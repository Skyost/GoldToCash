package com.skyost.g2c;

import java.io.File;

import org.bukkit.plugin.Plugin;

public class GoldToCashMessages extends Config {
	public GoldToCashMessages(Plugin plugin) {
		CONFIG_FILE = new File(plugin.getDataFolder(), "messages.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n               GoldToCash Messages                    #";
		CONFIG_HEADER += "\n See http://dev.bukkit.org/bukkit-plugins/goldtocash  #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n##################################################### #";
	}
	public String Update_NOUPDATE = "No Update: The updater did not find an update, you have the latest version.";
	public String Update_FAILDBO = "dev.bukkit.org Failed: For some reason, the updater was unable to contact DBO to download the file.";
	public String Update_AVAILABLE = "Update found: There was an update found : /version/.";
	
	public String Message_MoneyAdded = "Added /money/ /moneyname/ to your balance.";
	public String Message_ItemAdded = "Converted /money/ /moneyname/ to item id /item/.";
	public String Message_Permission = "You don't have permission to do this !";
	public String Message_ConvertItem = "Can't convert this item to /moneyname/ !";
	public String Message_ConvertMoney = "Can't convert /moneyname/ to this unknown item id !";
	public String Message_ConfigChanged = "'/arg1/' set to '/arg2/' !";
	public String Message_ConfigAdded = "'/arg2/' added to '/arg1/' !";
	public String Message_Price = "The price of item id /item/ is /price/ /moneyname/.";
	public String Message_NoEconomy = "Economy was not detected / not enabled on this server !";
	public String Message_NotEnoughtMoney = "You don't have enough money to convert your money into this item ! Check /price <ID> for the price of this item.";
	public String Message_Arguments = "Incorrects aguments !";
	public String Message_MethodDisabled = "This conversion method has been disabled !";
	public String Message_NoConsole = "You can't do this from the console !";
}
