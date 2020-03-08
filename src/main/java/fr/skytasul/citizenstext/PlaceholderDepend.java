package fr.skytasul.citizenstext;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class PlaceholderDepend {

	public static String format(String entryString, Player player) {
		return PlaceholderAPI.setPlaceholders(player, entryString);
	}
	
}
