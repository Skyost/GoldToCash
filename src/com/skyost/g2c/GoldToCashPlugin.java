/*
 * -------------------------------------------------------------------------------------------------------- *
 * 																											*
 * GoldToCash Plugin																						*
 * 																											*
 * -------------------------------------------------------------------------------------------------------- *
 * 																											*
 * Copyright © 2013 Skyost. All rights reserved.															*
 * 																											*
 * -------------------------------------------------------------------------------------------------------- *
 *																											*
 * Redistribution and use in source and binary forms, with or without modification, are						*
 * permitted provided that the following conditions are met:												*
 *																											*
 *    1. Redistributions of source code must retain the above copyright notice, this list of				*
 *       conditions and the following disclaimer.															*
 *																											*
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list				*
 *       of conditions and the following disclaimer in the documentation and/or other materials				*
 *       provided with the distribution.																	*
 *																											*
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED								*
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND					*
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR							*
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR						*
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR					*
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON					*
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING						*
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF						*
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.																*
 *																											*
 * The views and conclusions contained in the software and documentation are those of the					*
 * authors and contributors and should not be interpreted as representing official policies,				*
 * either expressed or implied, of anybody else.															*
 * 																											*
 * -------------------------------------------------------------------------------------------------------- *
 */

package com.skyost.g2c;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

import com.skyost.g2c.Metrics.Graph;

public class GoldToCashPlugin extends JavaPlugin implements Listener {
	public GoldToCashConfig config;
	public GoldToCashMessages messages;
	public double totalCashConverted;
	public static Economy economy = null;
	public static Date actual = new Date();
	public static DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	public ItemStack air = new ItemStack(Material.AIR);
	
	public void onEnable() {
		logToFile("[" + date() + "] [CONSOLE] Enabling GoldToCash v" + this.getDescription().getVersion() + "...");
		if(loadVault() == true) {
			System.out.println("[GoldToCash] Vault Economy loaded with success !");
			logToFile("[" + date() + "] [CONSOLE] Vault Economy loaded with success !");
		}
		else {
			getLogger().log(Level.SEVERE, "[GoldToCash] Vault Economy not loaded, disabling this plugin...");
			logToFile("[" + date() + "] [CONSOLE] Vault Economy not loaded, disabling this plugin...");
			getServer().getPluginManager().disablePlugin(this);
		}
		loadPlugin();
	}
	
	public void onDisable() {
		logToFile("[" + date() + "] [CONSOLE] Disabling GoldToCash v" + this.getDescription().getVersion() + "...");
		try {
			config.save();
			messages.save();
			getServer().getPluginManager().disablePlugin(this);
		} 
		catch(InvalidConfigurationException ex) {
			getLogger().log(Level.SEVERE, "[ERROR]" + ex);
			logToFile("[" + date() + "] [CONSOLE] " + ex);
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	public void loadPlugin() {
		try {
			System.setOut(new PrintStream(System.out, true, "utf8"));
			config = new GoldToCashConfig(this);
			config.init();
			logToFile("[" + date() + "] [CONSOLE] Configuration file loaded with success !");
			messages = new GoldToCashMessages(this);
			messages.init();
			logToFile("[" + date() + "] [CONSOLE] Messages file loaded with success !");
			if(!(config.ConversionMethod.toUpperCase().contains("COMMAND") || config.ConversionMethod.toUpperCase().contains("SIGN"))) {
				getLogger().log(Level.SEVERE, "[GoldToCash] 'ConversionMethod' in config must have at least 'COMMAND' and / or 'SIGN' ! We will disable the plugin so you can edit the values and reload it.");
				logToFile("[" + date() + "] [CONSOLE] 'ConversionMethod' in config must have at least 'COMMAND' and / or 'SIGN' ! We will disable the plugin so you can edit the values and reload it.");
				getServer().getPluginManager().disablePlugin(this);
			}
			else {
				this.getServer().getPluginManager().registerEvents(this, this);
				startMetrics();
			}
		}
		catch(Exception ex) {
			getLogger().log(Level.SEVERE, "[ERROR] " + ex);
			logToFile("[" + date() + "] [CONSOLE] " + ex);
			getServer().getPluginManager().disablePlugin(this);
        return;
		}
	}
	
	public boolean loadVault() {
		if(getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
	}
	
	@SuppressWarnings("incomplete-switch")
	private void checkUpdate(CommandSender sender) {
		try {
			logToFile("[" + date() + "] [" + sender.getName() + "] Checking for updates...");
			Updater updater = new Updater(this, "goldtocash", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
			Updater.UpdateResult result = updater.getResult();
	        switch(result) {
	            case NO_UPDATE:
	            	sender.sendMessage(ChatColor.GREEN + "[GoldToCash] " + messages.Update_NOUPDATE);
	            	logToFile("[" + date() + "] [" + sender.getName() + "] " + messages.Update_NOUPDATE);
	            	break;
	            case FAIL_DBO:
	            	sender.sendMessage(ChatColor.RED + "[GoldToCash] " + messages.Update_FAILDBO);
	            	logToFile("[" + date() + "] [" + sender.getName() + "] " + messages.Update_FAILDBO);
	            	break;
	            case UPDATE_AVAILABLE:
	            	String message = messages.Update_AVAILABLE.replaceAll("/version/", updater.getLatestVersionString());
	            	sender.sendMessage(ChatColor.GREEN + "[GoldToCash] " + message);
	            	logToFile("[" + date() + "] [" + sender.getName() + "] " + message);
	            	break;
	        }
		}
		catch(Exception ex) {
			getLogger().log(Level.SEVERE, "[ERROR] " + ex);
			logToFile("[" + date() + "] [CONSOLE] " + ex);
		}
	}
	
	public void startMetrics() {
		final boolean MetricsAutoCheckForUpdates = config.AutoCheckForUpdates;
		final String MetricsConversionMethod = config.ConversionMethod.toUpperCase();
		try {
		    Metrics metrics = new Metrics(this);
		    Graph cashConvertedGraph = metrics.createGraph("GoldConvertedGraph");
		    cashConvertedGraph.addPlotter(new Metrics.Plotter("Total Cash converted") {
		    @Override
		    public int getValue() {
		        return round(totalCashConverted);
		       }
		    });
		    
    		Graph updateGraph = metrics.createGraph("UpdateGraph");
    		updateGraph.addPlotter(new Metrics.Plotter("Checking for Updates") {	
    			@Override
    			public int getValue() {	
    				return 1;
    			}
    			
    			@Override
    			public String getColumnName() {
    				if(MetricsAutoCheckForUpdates == true) {
    					return "Yes";
    				}
    				else if(MetricsAutoCheckForUpdates == false) {
    					return "No";
    				}
    				else {
    					return "Maybe";
    				}
    			}
    		});
    		
    		Graph conversionMethodGraph = metrics.createGraph("ConversionMethodGraph");
    		conversionMethodGraph.addPlotter(new Metrics.Plotter("Conversion Method") {	
    			@Override
    			public int getValue() {	
    				return 1;
    			}
    			
    			@Override
    			public String getColumnName() {
    				if(MetricsConversionMethod.contains("SIGN") && MetricsConversionMethod.contains("COMMAND")) {
    					return "Sign and Command";
    				}
    				else if(MetricsConversionMethod.contains("SIGN")) {
    					return "Sign";
    				}
    				else if(MetricsConversionMethod.contains("COMMAND")) {
    					return "Command";
    				}
    				else {
    					return "Other, maybe from the developper";
    				}
    			}
    		});
    		
    		Graph headerGraphGoldToCash = metrics.createGraph("HeaderGraphGoldToCash");
    		headerGraphGoldToCash.addPlotter(new Metrics.Plotter("Sign header GoldToCash") {	
    			@Override
    			public int getValue() {	
    				return 1;
    			}
    			
    			@Override
    			public String getColumnName() {
    				return config.SignHeaderGoldToCash;
    			}
    		});
		    metrics.start();
		    logToFile("[" + date() + "] [CONSOLE] Metrics started with success !");
		}
		catch (IOException ex) {
			getLogger().log(Level.SEVERE, "[GoldToCash] [CONSOLE] " + ex);
			logToFile("[" + date() + "] " + ex);
		}
	}
	
	public void logToFile(String message) {
        try {
            File dataFolder = getDataFolder();
            if(!(dataFolder.exists())) {
                dataFolder.mkdir();
            }
            File saveTo = new File(getDataFolder(), this.getDescription().getName() + " v" + this.getDescription().getVersion() + ".log");
            if(!(saveTo.exists())) {
                saveTo.createNewFile();
        		logToFile(this.getDescription().getName() + " v" + this.getDescription().getVersion() + " Logs :");
        		logToFile("[MM-DD-YYYY HH:MM:SS] [SENDER] Message");
        		logToFile("----------------------------------------");
            }
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush();
            pw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
	
	public static String date() {
		String dat = dateFormat.format(actual);
		return dat;
	}
	
	public int round(double d) {
		double dAbs = Math.abs(d);
		int i = (int) dAbs;
		double result = dAbs - (double) i;
		if(result < 0.5) {
			return d < 0 ? -i : i;
		}
		else {
			return d < 0 ? -(i+1) : i + 1;
		}
	}
    
    public void convertToCash(Player player, ItemStack inHand) {
    	if(economy.isEnabled() == true) {
            if(player.hasPermission("goldtocash.convert.goldtocash")) {
        		if(economy.hasAccount(player.getName()) == false) {
        			economy.createPlayerAccount(player.getName());
        		}
            	Double result = config.Prices.get("" + inHand.getTypeId());
            	double ecoAdd = result * inHand.getAmount();
        		economy.depositPlayer(player.getName(), ecoAdd);
        		totalCashConverted = totalCashConverted + ecoAdd;
        		player.setItemInHand(air);
        		String moneyName;
        		if(ecoAdd >= 1 && ecoAdd < 2) {
        			moneyName = economy.currencyNameSingular();
        		}
        		else {
        			moneyName = economy.currencyNamePlural();
        		}
        		String message = messages.Message_MoneyAdded.replaceAll("/money/", "" + ecoAdd);
        		message = message.replaceAll("/moneyname/", moneyName);
        		player.sendMessage(message);
        		logToFile("[" + date() + "] [" + player.getName() + "] " + message);
            }
            else {
            	player.sendMessage(ChatColor.RED + messages.Message_Permission);
            	logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_Permission);
            }
    	}
    	else {
    		player.sendMessage(ChatColor.RED + messages.Message_NoEconomy);
    		logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_NoEconomy);
        }
    }
    
    public void convertToGold(Player player, ItemStack toConvert) {
    	if(economy.isEnabled() == true) {
            if(player.hasPermission("goldtocash.convert.cashtogold")) {
            	Double result = config.Prices.get("" + toConvert.getTypeId());
            	if(economy.getBalance(player.getName()) < result) {
            		player.sendMessage(ChatColor.RED + messages.Message_NotEnoughtMoney);
            		logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_NotEnoughtMoney);
            	}
            	else {
                	if(economy.hasAccount(player.getName()) == false) {
            			economy.createPlayerAccount(player.getName());
            		}
	            	double ecoSub = economy.getBalance(player.getName()) / result;
	            	economy.withdrawPlayer(player.getName(),result * ecoSub);
	            	ItemStack toGive = new ItemStack(toConvert.getTypeId());
	            	toGive.setAmount(round(ecoSub));
	            	player.getInventory().addItem(toGive);
		    		String moneyName;
		    		if(ecoSub >= 1 && ecoSub < 2) {
		    			moneyName = economy.currencyNameSingular();
		    		}
		    		else {
		    			moneyName = economy.currencyNamePlural();
		    		}
		    		String message = messages.Message_ItemAdded.replaceAll("/money/", "" + result * ecoSub);
		    		message = message.replaceAll("/moneyname/", moneyName);
		    		message = message.replaceAll("/item/", "" + toConvert.getTypeId());
		    		player.sendMessage(message);
		    		logToFile("[" + date() + "] [" + player.getName() + "] " + message);
            	}
            }
            else {
            	player.sendMessage(ChatColor.RED + messages.Message_Permission);
            	logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_Permission);
            }
    	}
    	else {
    		player.sendMessage(ChatColor.RED + messages.Message_NoEconomy);
    		logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_NoEconomy);
        }
    }
    
    public void config(CommandSender sender, String arg1, String arg2, String arguments) {
    	if(arg1.equalsIgnoreCase("AutoCheckForUpdates")) {
    		if(arg2.equalsIgnoreCase("true")) {
    			config.AutoCheckForUpdates = true;
    			String message = messages.Message_ConfigChanged.replaceAll("/arg1/", "AutoCheckForUpdates");
    			message = message.replaceAll("/arg2/", "true");
    			sender.sendMessage(message);
    			logToFile("[" + date() + "] [" + sender.getName() + "] " + message);
    		}
    		else if(arg2.equalsIgnoreCase("false")) {
    			config.AutoCheckForUpdates = false;
    			String message = messages.Message_ConfigChanged.replaceAll("/arg1/", "AutoCheckForUpdates");
    			message = message.replaceAll("/arg2/", "false");
    			sender.sendMessage(message);
    			logToFile("[" + date() + "] [" + sender.getName() + "] " + message);
    		}
    		else {
    			sender.sendMessage(ChatColor.RED + messages.Message_Arguments);
				logToFile("[" + date() + "] [" + sender.getName() + "] " + messages.Message_Arguments);
    		}
		}
    	else if(arg1.equalsIgnoreCase("Prices")) {
    		try {
	    		String[] str = arguments.split(":");
	    		String one = str[0].substring(7);
	    		String two = str[1];
    			Integer.parseInt(one);
    			config.Prices.put(one, Double.parseDouble(two));
        		String message = messages.Message_ConfigAdded.replaceAll("/arg1/", "Prices");
        		message = message.replaceAll("/arg2/", one + ":" + two);
    			sender.sendMessage(message);
    			logToFile("[" + date() + "] [" + sender.getName() + "] " + message);
    		}
    		catch(Exception ex) {
    			sender.sendMessage(ChatColor.RED + messages.Message_Arguments);
				logToFile("[" + date() + "] [" + sender.getName() + "] " + messages.Message_Arguments);
    		}
    	}
    	else if(arg1.equalsIgnoreCase("ConversionMethod")) {
    		if(arguments.toUpperCase().contains("COMMAND") || arguments.toUpperCase().contains("SIGN")) {
    			arguments = arguments.toUpperCase();
    			arguments = arguments.substring(17, arguments.length());
    			config.ConversionMethod = arguments;
    			String message = messages.Message_ConfigChanged.replaceAll("/arg1/", "ConversionMethod");
    			message = message.replaceAll("/arg2/", arguments);
				sender.sendMessage(message);
				logToFile("[" + date() + "] [" + sender.getName() + "] " + message);
    		}
    		else {
    			sender.sendMessage(ChatColor.RED + messages.Message_Arguments);
				logToFile("[" + date() + "] [" + sender.getName() + "] " + messages.Message_Arguments);
    		}
    	}
    	else if(arg1.equalsIgnoreCase("SignHeaderGoldToCash")) {
    		arguments = arguments.substring(11, arguments.length());
    		config.SignHeaderGoldToCash = arguments;
    		String message = messages.Message_ConfigChanged.replaceAll("/arg1/", "SignHeaderGoldToCash");
    		message = message.replaceAll("/arg2/", arguments);
			sender.sendMessage(message);
			logToFile("[" + date() + "] [" + sender.getName() + "] " + message);
    	}
    	else {
    		sender.sendMessage(ChatColor.RED + messages.Message_Arguments);
			logToFile("[" + date() + "] [" + sender.getName() + "] " + messages.Message_Arguments);
    	}
		try {
			config.save();
		} 
		catch(InvalidConfigurationException ex) {
			getLogger().log(Level.SEVERE, "[ERROR] " + ex);
			logToFile("[" + date() + "] [CONSOLE] " + ex);
			getServer().getPluginManager().disablePlugin(this);
		}
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
    	Player player = e.getPlayer();
    	if(config.AutoCheckForUpdates == true) {
    		if(player.isOp() == true) {
    			checkUpdate(player);
    		}
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
    	Player player = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = e.getClickedBlock();
            if(b.getState() instanceof Sign) {
            	Sign s = (Sign)b.getState();
            	String[] lines = s.getLines().clone();
            	String prefix = arrayToString(lines);
        		if(config.ConversionMethod.toUpperCase().contains("SIGN")) {
        			if(prefix.toUpperCase().startsWith(config.SignHeaderGoldToCash.toUpperCase())) {
	        			Double result = config.Prices.get("" + player.getItemInHand().getTypeId());
			    	    try {
			    	    	if(!(result.equals(null))) {
			        			convertToCash(player, player.getItemInHand());
			    	    	}
			    	    	else {
			    	    		String message = messages.Message_ConvertItem.replaceAll("/moneyname/", economy.currencyNamePlural());
			            		player.sendMessage(ChatColor.RED + message);
			    				logToFile("[" + date() + "] [" + player.getName() + "] " + message);
			    	    	}
			    	    }
			    	    catch(NullPointerException ex) {
			    	    	String message = messages.Message_ConvertItem.replaceAll("/moneyname/", economy.currencyNamePlural());
		            		player.sendMessage(ChatColor.RED + message);
		    				logToFile("[" + date() + "] [" + player.getName() + "] " + message);
			    	    }
	            	}
    			}
        		else {
        			player.sendMessage(ChatColor.RED + messages.Message_MethodDisabled);
        			logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_MethodDisabled);
        		}
            }
        }
    }
    
    public String arrayToString(String[] lines) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < lines.length; i++) {
		   result.append(lines[i]);
		}
		String arrayToString = result.toString();
		return arrayToString;
    }
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if(sender instanceof Player) {
            player = (Player) sender;
        }
        
        if(cmd.getName().equalsIgnoreCase("goldtocash")) {
        	if(sender instanceof Player) {
        		if(config.ConversionMethod.toUpperCase().contains("COMMAND")) {
        			Double result = config.Prices.get("" + player.getItemInHand().getTypeId());
		    	    try {
		    	    	if(!(result.equals(null))) {
		        			convertToCash(player, player.getItemInHand());
		    	    	}
		    	    	else {
		    	    		String message = messages.Message_ConvertItem.replaceAll("/moneyname/", economy.currencyNamePlural());
		            		sender.sendMessage(ChatColor.RED + message);
		    				logToFile("[" + date() + "] [" + player.getName() + "] " + message);
		    	    	}
		    	    }
		    	    catch(NullPointerException ex) {
		    	    	String message = messages.Message_ConvertItem.replaceAll("/moneyname/", economy.currencyNamePlural());
	            		sender.sendMessage(ChatColor.RED + message);
	    				logToFile("[" + date() + "] [" + player.getName() + "] " + message);
		    	    }
        		}
        		else {
        			sender.sendMessage(ChatColor.RED + messages.Message_MethodDisabled);
        			logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_MethodDisabled);
        		}
        	}
        	else {
        		sender.sendMessage(ChatColor.RED + "[GoldToCash] " + messages.Message_NoConsole);
        		logToFile("[" + date() + "] [CONSOLE] " + messages.Message_NoConsole);
        	}
        }
        if(cmd.getName().equalsIgnoreCase("cashtogold")) {
        	if(sender instanceof Player) {
        		if(config.ConversionMethod.toUpperCase().contains("COMMAND")) {
	        		if(args.length == 1) {
	        			Double result = config.Prices.get(args[0]);
			    	    try {
			    	    	if(!(result.equals(null))) {
			    	    		convertToGold(player, new ItemStack(Integer.parseInt(args[0])));
			    	    	}
			    	    	else {
			    	    		String message = messages.Message_ConvertMoney.replaceAll("/moneyname/", economy.currencyNamePlural());
			            		sender.sendMessage(ChatColor.RED + message);
			    				logToFile("[" + date() + "] [" + player.getName() + "] " + message);
			    	    	}
			    	    }
			    	    catch(NullPointerException ex) {
			    	    	String message = messages.Message_ConvertMoney.replaceAll("/moneyname/", economy.currencyNamePlural());
		            		sender.sendMessage(ChatColor.RED + message);
		    				logToFile("[" + date() + "] [" + player.getName() + "] " + message);
			    	    }
	        		}
	        		else {
	        			player.sendMessage(ChatColor.RED + messages.Message_Arguments);
	        			logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_Arguments);
	        		}
        		}
        		else {
        			sender.sendMessage(ChatColor.RED + messages.Message_MethodDisabled);
        			logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_MethodDisabled);
        		}
        	}
        	else {
        		sender.sendMessage(ChatColor.RED + "[GoldToCash] " + messages.Message_NoConsole);
        		logToFile("[" + date() + "] [CONSOLE] " + messages.Message_NoConsole);
        	}
        }
        if(cmd.getName().equalsIgnoreCase("goldtocashconfig")) {
        	if(sender instanceof Player) {
        		if(player.hasPermission("goldtocash.config")) {
        			if(!(args.length < 2)) {
        				String arguments;
        				arguments = Arrays.toString(args).substring(1,  Arrays.toString(args).length() - 1);
        				arguments = arguments.replaceAll(",", "");
        				config(sender, args[0], args[1], arguments);
        			}
        			else {
        				player.sendMessage(ChatColor.RED + messages.Message_Arguments);
        				logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_Arguments);
        			}
        		}
        		else {
        			player.sendMessage(ChatColor.RED + messages.Message_Permission);
                	logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_Permission);
        		}
        	}
        	else {
        		if(!(args.length < 2)) {
    				String arguments;
    				arguments = Arrays.toString(args).substring(1,  Arrays.toString(args).length() - 1);
    				arguments = arguments.replaceAll(",", "");
    				config(sender, args[0], args[1], arguments);
    			}
    			else {
    				sender.sendMessage(ChatColor.RED + "[GoldToCash] " + messages.Message_Arguments);
    				logToFile("[" + date() + "] [CONSOLE] " + messages.Message_Arguments);
    			}
        	}
        }
    	if(cmd.getName().equalsIgnoreCase("price")) {
    		if(sender instanceof Player) {
        		if(player.hasPermission("goldtocash.price")) {
        			if(args.length == 1) {
        	    		Double result = config.Prices.get(args[0]);
			    	    try {
			    	    	if(!(result.equals(null))) {
			    	    		String message = messages.Message_Price.replaceAll("/item/", args[0]);
			    	    		message = message.replaceAll("/price/", "" + result);
			    	    		sender.sendMessage(message);
			    	    		logToFile("[" + date() + "] [" + player.getName() + "] " + message);
			    	    	}
			    	    	else {
			    	    		String message = messages.Message_ConvertItem.replaceAll("/moneyname/", economy.currencyNamePlural());
			            		sender.sendMessage(ChatColor.RED + message);
			    				logToFile("[" + date() + "] [" + player.getName() + "] " + message);
			    	    	}
			    	    }
			    	    catch(NullPointerException ex) {
			    	    	String message = messages.Message_ConvertItem.replaceAll("/moneyname/", economy.currencyNamePlural());
		            		sender.sendMessage(ChatColor.RED + message);
		    				logToFile("[" + date() + "] [" + player.getName() + "] " + message);
			    	    }
	        		}
        			else {
        				sender.sendMessage(ChatColor.RED + messages.Message_Arguments);
        				logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_Arguments);
        			}
        		}
	        	else {
	        		player.sendMessage(ChatColor.RED + messages.Message_Permission);
	                logToFile("[" + date() + "] [" + player.getName() + "] " + messages.Message_Permission);
	        	}
    		}
    		else {
    			if(args.length == 1) {
    	    		Double result = config.Prices.get(args[0]);
	    			try {
	    				if(!(result.equals(null))) {
		    				String message = messages.Message_Price.replaceAll("/item/", args[0]);
			    	    	message = message.replaceAll("/price/", "" + result);
		            		sender.sendMessage("[GoldToCash] " + message);
		    				logToFile("[" + date() + "] [CONSOLE] " + message);
	    				}
		    	    	else {
		    	    		String message = messages.Message_ConvertItem.replaceAll("/moneyname/", economy.currencyNamePlural());
		            		sender.sendMessage(ChatColor.RED + message);
		    				logToFile("[" + date() + "] [CONSOLE] " + message);
		    	    	}
		    	    }
		    	    catch(NullPointerException ex) {
		    	    	String message = messages.Message_ConvertItem.replaceAll("/moneyname/", economy.currencyNamePlural());
	            		sender.sendMessage(ChatColor.RED + "[GoldToCash] " + message);
	    				logToFile("[" + date() + "] [CONSOLE] " + message);
		    	    }
    			}
    			else {
    				sender.sendMessage(ChatColor.RED + "[GoldToCash] " + messages.Message_Arguments);
    				logToFile("[" + date() + "] [CONSOLE] " + messages.Message_Arguments);
    			}
    		}
    	}
        return true;
    }

}
