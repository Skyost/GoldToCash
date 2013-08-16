package com.skyost.g2c;

import java.io.File;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;

public class GoldToCashConfig extends Config {
	public GoldToCashConfig(Plugin plugin) {
		CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
		CONFIG_HEADER = "##################################################### #";
		CONFIG_HEADER += "\n             GoldToCash Configuration                 #";
		CONFIG_HEADER += "\n See http://dev.bukkit.org/bukkit-plugins/goldtocash  #";
		CONFIG_HEADER += "\n              for more informations.                  #";
		CONFIG_HEADER += "\n##################################################### #";
        
        Prices.put("265", 250.0);
        Prices.put("266", 500.0);
        Prices.put("264", 750.0);
        Prices.put("388", 1000.0);
	}
	public boolean AutoCheckForUpdates = true;
	public String ConversionMethod = "COMMAND SIGN";
	public String SignHeaderGoldToCash = "[GoldToCash]";
	public HashMap<String, Double> Prices = new HashMap<String, Double>();
}
