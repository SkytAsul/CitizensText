package fr.skytasul.citizenstext;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import fr.skytasul.citizenstext.command.TextCommand;
import fr.skytasul.citizenstext.options.TextOptionsRegistry;
import fr.skytasul.citizenstext.players.CTPlayersManager;
import fr.skytasul.citizenstext.texts.TextsManager;

import net.citizensnpcs.api.event.CitizensPreReloadEvent;
import net.citizensnpcs.api.event.CitizensReloadEvent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CitizensText extends JavaPlugin implements Listener{
	
	private static CitizensText instance;

	private boolean enabled = false;
	private boolean disabled = false;

	private TextsManager texts;
	private CTPlayersManager players;
	
	public boolean papi;
	
	private TextCommand command;
	private TextOptionsRegistry optionsRegistry;
	
	@Override
	public void onLoad() {
		instance = this;
	}
	
	@Override
	public void onEnable(){
		command = new TextCommand();
		getCommand("text").setExecutor(command);
		getCommand("text").setTabCompleter(command);
		
		saveDefaultConfig();

		CitizensTextConfiguration.loadConfig(getConfig());

		optionsRegistry = new TextOptionsRegistry();
		
		papi = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
		if (papi) getLogger().info("Hooked into PlaceholderAPI!");
		
		getServer().getScheduler().runTaskLater(this, this::loadDatas, 3L);
		
		Metrics metrics = new Metrics(this, 9557);
		metrics.addCustomChart(new Metrics.SingleLineChart("texts", () -> texts.getTexts().size()));
	}
	
	@Override
	public void onDisable(){
		if (disabled) return;
		disable();
		enabled = false;
	}
	
	public TextOptionsRegistry getOptionsRegistry() {
		return optionsRegistry;
	}
	
	public CTPlayersManager getPlayers() {
		return players;
	}
	
	public TextsManager getTexts() {
		return texts;
	}
	
	public boolean isPAPIEnabled() {
		return papi;
	}
	
	public void disable() {
		if (texts != null) {
			texts.disable();
			texts = null;
		}
		if (players != null) {
			players.disable();
			players = null;
		}
	}
	
	public void loadDatas() {
		if (getServer().getPluginManager().isPluginEnabled("Citizens")) {
			try {
				texts = new TextsManager(new File(getDataFolder(), "datas.yml"));
				texts.load(this);
				
				players = new CTPlayersManager(this, new File(getDataFolder(), "players.yml"));
				
				if (!enabled) getServer().getPluginManager().registerEvents(instance, instance);
				enabled = true;
			}catch (Throwable e) {
				e.printStackTrace();
				getLogger().severe("An error occurred during data loading. To preserve data integrity, the plugin will now stop.");
				disabled = true;
			}
		}else {
			getLogger().severe("Citizens has not started properly. CitizensText can not work without it, the plugin will now stop.");
			disabled = true;
		}
		
		if (disabled) getServer().getPluginManager().disablePlugin(instance);
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
			click.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CitizensTextConfiguration.getClickableMessage()).create()));
		}
		p.spigot().sendMessage(clicks);
	}
	
	public static String formatMessage(String format, String msg, String name, int id, int size) {
		return format(format(format(format(format, 0, name), 1, msg), 2, "" + id), 3, "" + size).replace("{nl}", "\n");
	}

	public static String format(String msg, int i, String replace){
		return msg.replace("{" + i + "}", replace);
	}
	
}
