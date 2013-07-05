package com.skyost.server;

import java.io.File;
import org.bukkit.plugin.Plugin;

public class GoldToCashConfig extends Config {
	public GoldToCashConfig(Plugin plugin) {
		CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n             GoldToCash Configuration                 #";
		CONFIG_HEADER += "\n See http://dev.bukkit.org/bukkit-plugins/goldtocash  #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n##################################################### #";
	}
	public boolean AutoCheckForUpdates = true;
	public String ConversionMethod = "COMMAND SIGN";
	public String SignHeaderGoldToCash = "[GoldToCash]";
	public double IronIngotPrice = 250.0;
	public double GoldIngotPrice = 500.0;
	public double DiamondPrice = 750.0;
	public double EmeraldPrice = 1000.0;
}
