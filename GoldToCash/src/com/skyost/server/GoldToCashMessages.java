package com.skyost.server;

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
	public String Update_SUCCESS = "Update found: The updater found an update, and has readied it to be loaded the next time the server restarts/reloads.";
	public String Update_NOUPDATE = "No Update: The updater did not find an update, and nothing was downloaded.";
	public String Update_FAILDOWNLOAD = "Download Failed: The updater found an update, but was unable to download it.";
	public String Update_FAILDBO = "dev.bukkit.org Failed: For some reason, the updater was unable to contact DBO to download the file.";
	public String Update_FAILNOVERSION = "No version found: When running the version check, the file on DBO did not contain the a version in the format 'vVersion' such as 'v1.0'.";
	public String Update_FAILBADSLUG = "Bad slug: The slug provided by the plugin running the updater was invalid and doesn't exist on DBO.";
	public String Update_AVAILABLE = "Update found: There was an update found : /version/";
	
	public String Message_MoneyAdded = "Added /money/ /moneyname/ to your balance.";
	public String Message_Permission = "You don't have permission to do this !";
	public String Message_Convert = "Can't convert this item to /moneyname/ !";
	public String Message_ConfigChanged = "'/arg1/' set to '/arg2/' !";
	public String Message_NoEconomy = "Economy was not detected / not enabled on this server !";
	public String Message_Arguments = "Incorrects aguments !";
	public String Message_MethodDisabled = "This conversion method has been disabled !";
	public String Message_NoConsole = "You can't do this from the console !";
}
