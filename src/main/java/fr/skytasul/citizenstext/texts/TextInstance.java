package fr.skytasul.citizenstext.texts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.CitizensTextConfiguration;
import fr.skytasul.citizenstext.CitizensTextConfiguration.ClickType;
import fr.skytasul.citizenstext.event.TextSendEvent;
import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.options.OptionMessages;
import fr.skytasul.citizenstext.options.OptionName;
import fr.skytasul.citizenstext.options.OptionNear;
import fr.skytasul.citizenstext.options.OptionPlaybackTime;
import fr.skytasul.citizenstext.options.OptionRandom;
import fr.skytasul.citizenstext.options.OptionRepeat;
import fr.skytasul.citizenstext.options.TextOption;
import fr.skytasul.citizenstext.options.TextOptionsRegistry.TextOptionType;
import fr.skytasul.citizenstext.players.CTPlayer;
import fr.skytasul.citizenstext.players.CTPlayerText;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class TextInstance implements Listener{

	private Map<Class<?>, TextOption<?>> options = new HashMap<>();
	
	private NPC npc;
	private int id;
	private boolean created = false;
	private ConfigurationSection config;
	
	public TextInstance(NPC npc){
		this(npc, null);
	}
	
	public TextInstance(NPC npc, ConfigurationSection config) {
		this.id = npc.getId();
		this.npc = npc;
		this.config = config;
	}
	
	public void create() {
		if (!created) {
			Bukkit.getPluginManager().registerEvents(this, CitizensText.getInstance());
			CitizensText.getInstance().getTexts().addText(this);
			created = true;
			if (config == null) {
				CitizensText.getInstance().getTexts().createConfig(this);
			}else {
				for (String key : config.getKeys(false)) {
					TextOptionType<?> optionType = CitizensText.getInstance().getOptionsRegistry().getOptionType(key);
					if (optionType == null) {
						if (!key.equalsIgnoreCase("npc")) CitizensText.getInstance().getLogger().warning("Unknown key " + key + " in " + id + " data section");
					}else {
						TextOption<?> option = optionType.createOption(this);
						option.loadValue(config);
						options.put(optionType.getOptionClass(), option);
					}
				}
			}
		}
	}
	
	protected void createConfig(ConfigurationSection config) {
		this.config = config;
		for (TextOption<?> option : options.values()) {
			option.saveValue();
		}
		config.set("npc", id);
	}
	
	public void saveOption(TextOption<?> option) throws IOException {
		if (config == null) return;
		option.saveValue();
		CitizensText.getInstance().getTexts().save();
	}
	
	public <O extends TextOption<?>> O getOption(Class<O> optionClass) {
		return (O) options.computeIfAbsent(optionClass, x -> {
			O option = CitizensText.getInstance().getOptionsRegistry().createOption(optionClass, this);
			option.setDefaultValue();
			return option;
		});
	}

	public NPC getNPC(){
		return npc;
	}
	
	public ConfigurationSection getConfigurationSection() {
		return config;
	}
	
	public boolean isEmpty(){
		return options.values().stream().allMatch(TextOption::isEmpty);
	}
	
	public boolean isRandom() {
		return getOption(OptionRandom.class).getOrDefault();
	}
	
	public boolean isRepeat() {
		return getOption(OptionRepeat.class).getOrDefault();
	}
	
	public String getNPCName() {
		return getOption(OptionName.class).getOrDefault();
	}
	
	public OptionMessages getMessages() {
		return getOption(OptionMessages.class);
	}
	
	public void unload(){
		if (!created) return;
		HandlerList.unregisterAll(this);
		CitizensText.getInstance().getTexts().removeText(this);
	}
	
	public void delete() {
		unload();
		config.getParent().set(config.getName(), null);
		try {
			CitizensText.getInstance().getTexts().save();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getFrom().getBlockZ()) return;
		if (!getOption(OptionNear.class).getOrDefault() || isRandom()) return;
		Player p = e.getPlayer();
		if (!npc.isSpawned() || npc.getEntity().getWorld() != e.getTo().getWorld()) return;
		CTPlayerText playerText = CTPlayer.getPlayer(p).getText(this);
		if (playerText.hasNextMessageTask()) return;
		if (e.getTo().distance(npc.getEntity().getLocation()) < CitizensTextConfiguration.getDistanceToContinue()) {
			send(p, playerText);
		}
	}
	
	@EventHandler
	public void onRemove(NPCRemoveEvent e){
		if (e.getNPC() == npc){
			unload();
			npc = null;
			CitizensText.getInstance().getLogger().info("Text instance of NPC " + id + " is now dead.");
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClick(NPCRightClickEvent e){
		click(e, ClickType.RIGHT);
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeftClick(NPCLeftClickEvent e){
		click(e, ClickType.LEFT);
	}
	
	private void click(NPCClickEvent e, ClickType click) {
		if (!CitizensTextConfiguration.getClicks().contains(click)) return;
		if (getOption(OptionNear.class).getOrDefault()) return;
		if (e.getNPC() == npc){
			send(e.getClicker(), CTPlayer.getPlayer(e.getClicker()).getText(this));
		}
	}
	
	public void send(Player p, CTPlayerText playerText) {
		OptionMessages messages = getMessages();
		if (messages.messagesSize() == 0) return;
		
		if (playerText.hasTime()) {
			if (playerText.getTime() > System.currentTimeMillis() || (!playerText.canRepeat() && !isRepeat())) return;
			playerText.removeTime();
		}
		if (CitizensTextConfiguration.getClickMinimumTime() > 0) playerText.setTime(System.currentTimeMillis() + CitizensTextConfiguration.getClickMinimumTime() * 1000);
		
		if (isRandom()) {
			int id = ThreadLocalRandom.current().nextInt(messages.messagesSize());
			TextSendEvent event = new TextSendEvent(p, this, messages.getMessage(id));
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) event.getMessage().send(p, id, this);
			return;
		}
		int id;
		if (playerText.hasStarted()) { // player has already started
			if (!playerText.hasResetTime() || playerText.getResetTime() > System.currentTimeMillis()) {
				id = playerText.getMessageIndex();
				if (id >= messages.messagesSize()) id = 0;
			}else id = 0;
			playerText.removeNextMessageTask();
		}else { // never started
			id = 0;
		}
		Message message = messages.getMessage(id);
		TextSendEvent event = new TextSendEvent(p, this, message);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		message = event.getMessage();
		message.send(p, id, this);
		
		id++;
		if (messages.messagesSize() == id) { // last message
			playerText.removeMessage();
			if (isRepeat()) {
				int playback = getOption(OptionPlaybackTime.class).getOrDefault();
				if (playback > 0) playerText.setTime(System.currentTimeMillis() + playback * 1000);
			}else {
				playerText.setNoRepeat();
			}
			return;
		}
		
		// not last message
		if (CitizensTextConfiguration.getKeepTime() != -1) playerText.setResetTime(System.currentTimeMillis() + CitizensTextConfiguration.getKeepTime() * 1000);
		playerText.setMessage(id);
		if (message.getDelay() >= 0) { // TASK SYSTEM
			playerText.setNextMessageTask(Bukkit.getScheduler().runTaskLater(CitizensText.getInstance(), () -> { // create the task
				playerText.removeNextMessageTask();
				if (CitizensTextConfiguration.getDistanceToContinue() > 0) {
					Entity entity = npc.getEntity();
					if (p == null || entity == null) return;
					if (p.getWorld() != entity.getWorld()) return;
					if (p.getLocation().distance(entity.getLocation()) > CitizensTextConfiguration.getDistanceToContinue()) return; // player too far
				}
				send(p, playerText);
			}, message.getDelay()));
		}
	}
	
	public static boolean load(ConfigurationSection data) {
		int npcID = data.getInt("npc");
		NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
		if (npc == null) {
			CitizensText.getInstance().getLogger().warning("NPC with the id " + npcID + " doesn't exist. Consider removing this text instance.");
			return false;
		}
		
		new TextInstance(npc, data).create();
		return true;
	}

}
