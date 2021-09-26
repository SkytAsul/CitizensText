package fr.skytasul.citizenstext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.configuration.file.FileConfiguration;

public class CitizensTextConfiguration {
	
	private static String npcFormat, playerFormat;
	private static String clickableMsg = "§eClickable Message !";
	private static int continueAfter;
	private static int continueDistance;
	private static int playbackTime;
	private static int clickMinTime;
	private static int keepTime;
	private static boolean disableClick;
	private static List<ClickType> clicks;
	
	public static void loadConfig(FileConfiguration config) {
		npcFormat = config.getString("npcTexts");
		playerFormat = config.getString("playerTexts");
		clickableMsg = config.getString("clickableMessage");
		continueAfter = config.getInt("continueAfter");
		continueDistance = config.getInt("continueDistance");
		playbackTime = config.getInt("playbackSeconds");
		clickMinTime = config.getInt("clickMinTime");
		keepTime = config.getInt("keepTime");
		disableClick = config.getBoolean("disableClick");
		
		clicks = config.getStringList("clicks").stream().flatMap(x -> {
			try {
				ClickType click = ClickType.valueOf(x.toUpperCase());
				return Stream.of(click);
			}catch (IllegalArgumentException ex) {
				CitizensText.getInstance().getLogger().warning("Unknown click type: " + x);
				return Stream.empty();
			}
		}).collect(Collectors.toList());
		if (clicks.isEmpty()) {
			CitizensText.getInstance().getLogger().warning("No click specified. Default to RIGHT.");
			clicks.add(ClickType.RIGHT);
		}
		if (config.contains("leftClick")) {
			CitizensText.getInstance().getLogger().warning("The config option \"leftClick\" is no longer valid. Please remove it and use the new \"clicks\" options.");
			clicks.clear();
			clicks.add(config.getBoolean("leftClick") ? ClickType.LEFT : ClickType.RIGHT);
		}
	}
	
	public static String getNPCFormat() {
		return npcFormat;
	}
	
	public static String getPlayerFormat() {
		return playerFormat;
	}
	
	public static String getClickableMessage() {
		return clickableMsg;
	}
	
	public static int getTimeToContinue() {
		return continueAfter;
	}
	
	public static int getDistanceToContinue() {
		return continueDistance;
	}
	
	public static int getTimeToPlayback() {
		return playbackTime;
	}
	
	public static int getClickMinimumTime() {
		return clickMinTime;
	}
	
	public static int getKeepTime() {
		return keepTime;
	}
	
	public static boolean clickDisabled() {
		return disableClick;
	}
	
	public static List<ClickType> getClicks() {
		return clicks;
	}
	
	public enum ClickType {
		LEFT, RIGHT;
	}
	
}
