package fr.skytasul.citizenstext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.event.CitizensPreReloadEvent;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CitizensText extends JavaPlugin implements Listener{

	private static CitizensText instance;
	private static String npcFormat, playerFormat;
	private static String clMsg = "§eClickable Message !";
	private static int continueAfter;
	private static int continueDistance;
	private static int playbackTime;
	private static int clickMinTime;
	private static int keepTime;
	private static boolean disableClick;
	private static boolean leftClick;

	private static boolean enabled = false;
	private static boolean disabled = false;
	private static File dataFile;
	public static FileConfiguration data;

	public static boolean papi;
	
	public void onLoad(){instance = this;}
	
	public void onEnable(){
		TextCommand cmd = new TextCommand();
		getCommand("text").setExecutor(cmd);
		getCommand("text").setTabCompleter(cmd);
		
		saveDefaultConfig();

		loadConfig();

		papi = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
		if (papi) getLogger().info("PlaceholderAPI hooked !");
		loadDatas();
		
		Metrics metrics = new Metrics(this, 9557);
		metrics.addCustomChart(new Metrics.SingleLineChart("texts", () -> TextInstance.npcs.size()));
	}
	
	public void onDisable(){
		if (disabled) return;
		disable();
		HandlerList.unregisterAll((JavaPlugin) this);
		enabled = false;
	}
	
	private void disable(){
		saveDatas();
		TextInstance.npcs.clear();
		TextInstance.dead.clear();
	}
	
	public void loadDatas(){
		new BukkitRunnable() {
			@Override
			public void run() {
				if (getServer().getPluginManager().isPluginEnabled("Citizens")){
					try {
						dataFile = new File(getDataFolder(), "datas.yml");
						boolean exists = true;
						if (!dataFile.exists()){
							exists = false;
							dataFile.createNewFile();
							getLogger().info("Data file (data.yml) created"); 
						}
						data = YamlConfiguration.loadConfiguration(dataFile);
						data.options().header("Do not edit anything here! Everything should be modified in-game.");
						data.options().copyHeader(true);
						if (exists){
							for (Map<?, ?> m : data.getMapList("data")){
								TextInstance.load((Map<String, Object>) m);
							}
							getLogger().info((TextInstance.npcs.size() + TextInstance.dead.size()) + " texts loadeds" + (TextInstance.dead.isEmpty() ? "" : " (including " + TextInstance.dead.size() + " dead ones)"));
						}
						if (!enabled) getServer().getPluginManager().registerEvents(instance, instance);
						enabled = true;
					} catch (Exception e) {
						e.printStackTrace();
						getLogger().severe("An error occurred during data loading. To preserve data integrity, the plugin will now stop.");
						disabled = true;
					}
				}else{
					getLogger().severe("Citizens has not started properly. CitizensText can not work without it, the plugin will now stop.");
					disabled = true;
				}
				
				if (disabled) getServer().getPluginManager().disablePlugin(instance);
			}
		}.runTaskLater(this, 5L);
	}
	
	public void loadConfig(){
		reloadConfig();
		npcFormat = getConfig().getString("npcTexts");
		playerFormat = getConfig().getString("playerTexts");
		clMsg = getConfig().getString("clickableMessage");
		continueAfter = getConfig().getInt("continueAfter");
		continueDistance = getConfig().getInt("continueDistance");
		playbackTime = getConfig().getInt("playbackSeconds");
		clickMinTime = getConfig().getInt("clickMinTime");
		keepTime = getConfig().getInt("keepTime");
		disableClick = getConfig().getBoolean("disableClick");
		leftClick = getConfig().getBoolean("leftClick");
	}
	
	public int saveDatas(){
		List<Map<String, Object>> ls = new ArrayList<>();
		
		List<TextInstance> todo = new ArrayList<>(TextInstance.npcs.values());
		todo.addAll(TextInstance.dead.values());
		
		if (data != null) {
			for (TextInstance ti : todo) {
				if (ti.isEmpty()) getLogger().info("Text instance of NPC " + ti.getNPC().getId() + " is empty - consider removing to free space.");
				Map<String, Object> map = ti.serialize();
				if (map != null) {
					ls.add(map);
				}
			}
			data.set("data", ls);
			data.set("lastVersion", getDescription().getVersion());
			try {
				data.save(dataFile);
				getLogger().info(ls.size() + " texts saved");
				return ls.size();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	@EventHandler
	public void onCitizensPreReload(CitizensPreReloadEvent e){
		getLogger().info("Citizens is reloading - CitizensText datas are saving");
		disable();
	}
	
	@EventHandler
	public void onCitizensReload(CitizensReloadEvent e){
		getLogger().info("Citizens has reloaded - trying to reload CitizensText");
		loadDatas();
	}
	
	
	public static CitizensText getInstance(){
		return instance;
	}
	
	private static final char COLOR_CHAR = '\u00A7';
	
	public static String translateHexColorCodes(String startTag, String endTag, String message) {
		final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
		Matcher matcher = hexPattern.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find()) {
			String group = matcher.group(2);
			matcher.appendReplacement(buffer, COLOR_CHAR + "x"
					+ COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1)
					+ COLOR_CHAR + group.charAt(2) + COLOR_CHAR + group.charAt(3)
					+ COLOR_CHAR + group.charAt(4) + COLOR_CHAR + group.charAt(5));
		}
		return matcher.appendTail(buffer).toString();
	}
	
	public static void sendCommand(Player p, String text, String command) {
		BaseComponent[] clicks = TextComponent.fromLegacyText(text);
		for (BaseComponent click : clicks) {
			click.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/" + command));
			click.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(clMsg).create()));
		}
		p.spigot().sendMessage(clicks);
	}
	
	public static String formatMessage(boolean player, String msg, String name, int id, int max) {
		return format(format(format(format(player ? playerFormat : npcFormat, 0, name), 1, msg), 2, "" + id), 3, "" + max).replace("{nl}", "\n");
	}

	public static String format(String msg, int i, String replace){
		String tmp = new String(msg);
		tmp = tmp.replace("{" + i + "}", replace);
		return tmp;
	}
	
	public static String getNPCFormat() {
		return npcFormat;
	}
	
	public static int getTimeToContinue(){
		return continueAfter;
	}
	
	public static int getDistanceToContinue(){
		return continueDistance;
	}
	
	public static int getTimeToPlayback() {
		return playbackTime;
	}
	
	public static int getClickMinimumTime(){
		return clickMinTime;
	}
	
	public static int getKeepTime() {
		return keepTime;
	}

	public static boolean clickDisabled(){
		return disableClick;
	}
	
	public static boolean isLeftClickNeeded(){
		return leftClick;
	}
	
}
