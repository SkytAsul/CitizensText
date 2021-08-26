package fr.skytasul.citizenstext;

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
	private static boolean leftClick;
	
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
		leftClick = config.getBoolean("leftClick");
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
	
	public static boolean isLeftClickNeeded() {
		return leftClick;
	}
	
}
