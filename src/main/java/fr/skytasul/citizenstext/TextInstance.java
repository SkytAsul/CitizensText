package fr.skytasul.citizenstext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skytasul.citizenstext.event.TextSendEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class TextInstance implements Listener{

	public static Map<NPC, TextInstance> npcs = new HashMap<>();
	public static Map<Integer, TextInstance> dead = new HashMap<>();
	
	private Map<String, Integer> players = new HashMap<>();
	private Map<String, Long> times = new HashMap<>();
	private Map<UUID, Long> resetTimes = new HashMap<>();
	private Map<UUID, BukkitRunnable> runs = new HashMap<>();
	
	private List<Message> messages = new ArrayList<>();
	private boolean random = false;
	private boolean repeat = true;
	private boolean autoDispatch = true;
	private boolean console = false;
	private String customName;

	private NPC npc;
	private int id;
	
	public TextInstance(NPC npc){
		this.npc = npc;
		Bukkit.getPluginManager().registerEvents(this, CitizensText.getInstance());
	}
	
	private TextInstance(int id){
		this.id = id;
	}
	
	public void addMessage(String msg){
		messages.add(new Message(msg));
	}
	
	public String editMessage(int id, String msg) {
		return messages.get(id).setText(msg);
	}
	
	public void insertMessage(String msg, int id){
		messages.add(id, new Message(msg));
	}
	
	public String setCommand(int id, String command){
		return messages.get(id).setCommand(command);
	}
	
	public String removeCommand(int id){
		return messages.get(id).setCommand(null);
	}
	
	public String setSound(int id, String sound) {
		return messages.get(id).setSound(sound);
	}
	
	public String removeSound(int id){
		return messages.get(id).setSound(null);
	}
	
	public boolean toggleRandom(){
		random = !random;
		players.clear();
		return random;
	}

	public boolean toggleRepeatMode(){
		repeat = !repeat;
		times.clear();
		return repeat;
	}
	
	public boolean toggleAutodispatch(){
		return autoDispatch = !autoDispatch;
	}
	
	public boolean toggleConsole(){
		return console = !console;
	}
	
	public String getCustomName(){
		return customName;
	}

	public String setCustomName(String customName){
		String tmp = this.customName;
		this.customName = customName;
		return tmp;
	}
	
	public String getNPCName(){
		return customName == null ? npc.getName() : customName;
	}

	public int size(){
		return messages.size();
	}
	
	public int clear(){
		int i = messages.size();
		messages.clear();
		return i;
	}

	public Message getMessage(int id) {
		return messages.get(id);
	}
	
	public Message removeMessage(int id) {
		return messages.remove(id);
	}
	
	public List<Message> getMessages() {
		return new ArrayList<>(messages);
	}
	
	public String listMessages(){
		StringJoiner stb = new StringJoiner("\n");
		for (int i = 0; i < messages.size(); i++) {
			Message msg = messages.get(i);
			stb.add(ChatColor.AQUA + "" + i + " : " + ChatColor.GREEN + msg.text);
		}
		return stb.toString();
	}

	public NPC getNPC(){
		return npc;
	}
	
	public boolean isEmpty(){
		if (!messages.isEmpty()) return false;
		if (autoDispatch) return false;
		if (random) return false;
		if (console) return false;
		if (customName != null) return false;
		return true;
	}
	
	public void delete(){
		HandlerList.unregisterAll(this);
		npcs.remove(npc);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (!CitizensText.clickDisabled() || random) return;
		Player p = e.getPlayer();
		if (!npc.isSpawned() || npc.getEntity().getWorld() != e.getTo().getWorld()) return;
		if (runs.containsKey(p.getUniqueId())) return;
		if (e.getTo().distance(npc.getEntity().getLocation()) < CitizensText.getDistanceToContinue()){
			send(p);
		}
	}
	
	@EventHandler
	public void onRemove(NPCRemoveEvent e){
		if (e.getNPC() == npc){
			delete();
			id = npc.getId();
			npc = null;
			dead.put(id, this);
			CitizensText.getInstance().getLogger().info("Text instance of NPC " + id + " is now dead.");
		}
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRightClick(NPCRightClickEvent e){
		if (!CitizensText.isLeftClickNeeded()) click(e);
	}
	
	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLeftClick(NPCLeftClickEvent e){
		if (CitizensText.isLeftClickNeeded()) click(e);
	}
	
	private void click(NPCClickEvent e){
		if (CitizensText.clickDisabled()) return;
		if (e.getNPC() == npc){
			send(e.getClicker());
		}
	}
	
	public void send(Player p){
		if (messages.size() == 0) return;
		
		UUID uuid = p.getUniqueId();
		if (times.containsKey(uuid.toString())) {
			if (times.get(uuid.toString()) > System.currentTimeMillis()) {
				return;
			}else times.remove(uuid.toString());
		}
		if (CitizensText.getClickMinimumTime() > 0) times.put(uuid.toString(), System.currentTimeMillis() + CitizensText.getClickMinimumTime() * 1000);
		
		if (random){
			int id = ThreadLocalRandom.current().nextInt(messages.size());
			TextSendEvent event = new TextSendEvent(p, this, messages.get(id));
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) event.getMessage().send(p, id);
			return;
		}
		int id;
		if (players.containsKey(uuid.toString())) { // player has already started
			if (!resetTimes.containsKey(uuid) || resetTimes.get(uuid) > System.currentTimeMillis()) {
				id = players.get(uuid.toString());
				if (id >= messages.size()) id = 0;
			}else id = 0;
			players.remove(uuid.toString()); // remove from list
		}else { // never started
			id = 0;
		}
		Message message = messages.get(id);
		TextSendEvent event = new TextSendEvent(p, this, message);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		message = event.getMessage();
		message.send(p, id);
		
		id++;
		if (messages.size() == id){ // last message
			resetTimes.remove(uuid);
			if (runs.containsKey(uuid)){ // cancel and remove launch task
				runs.get(uuid).cancel();
				runs.remove(uuid);
			}
			if (CitizensText.getTimeToPlayback() > 0 || !repeat) times.put(uuid.toString(), repeat ? System.currentTimeMillis() + CitizensText.getTimeToPlayback() * 1000 : Long.MAX_VALUE);
			return;
		}
		// not last message
		if (CitizensText.getKeepTime() != -1) resetTimes.put(uuid, System.currentTimeMillis() + CitizensText.getKeepTime() * 1000);
		players.put(uuid.toString(), id); // add in list
		if (message.getDelay() >= 0) { // TASK SYSTEM
			if (runs.containsKey(uuid)){ // cancel and remove task in progress
				runs.get(uuid).cancel();
				runs.remove(uuid);
			}
			runs.put(uuid, new BukkitRunnable() { // create the task
				@Override
				public void run() {
					runs.remove(uuid);
					if (CitizensText.getDistanceToContinue() > 0){
						Entity entity = npc.getEntity();
						if (p == null || entity == null) return;
						if (p.getWorld() != entity.getWorld()) return;
						if (p.getLocation().distance(entity.getLocation()) > CitizensText.getDistanceToContinue()) return; // player too far
					}
					send(p);
				}
			});
			runs.get(uuid).runTaskLater(CitizensText.getInstance(), message.getDelay());
		}
	}
	
	public Map<String, Object> serialize(){
		if (messages.isEmpty()) return null;
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("npc", npc == null ? id : npc.getId());
		
		Map<Integer, Object> tmp = new HashMap<>();
		for (int i = 0; i < messages.size(); i++){
			tmp.put(i, messages.get(i).serialize());
		}
		map.put("messages", tmp);
		
		if (!players.isEmpty()) {
			if (!resetTimes.isEmpty()) {
				long time = System.currentTimeMillis();
				for (Iterator<Entry<UUID, Long>> iterator = resetTimes.entrySet().iterator(); iterator.hasNext();) {
					Entry<UUID, Long> entry = iterator.next();
					if (entry.getValue() <= time) {
						iterator.remove();
						players.remove(entry.getKey().toString());
					}
				}
			}
			map.put("players", players);
		}
		if (!times.isEmpty()) {
			long time = System.currentTimeMillis();
			for (Iterator<Entry<String, Long>> iterator = times.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Long> entry = iterator.next();
				if (entry.getValue() <= time) iterator.remove();
			}
			map.put("times", times);
		}
		if (random) map.put("random", true);
		if (!repeat) map.put("repeat", false);
		if (!autoDispatch) map.put("autoDispatch", false);
		if (customName != null) map.put("customName", customName);
		if (console) map.put("console", true);
		
		return map;
	}
	
	public static void load(Map<String, Object> map){
		int npcID = (int) map.get("npc");
		NPC npc = CitizensAPI.getNPCRegistry().getById(npcID);
		if (npc == null) CitizensText.getInstance().getLogger().info("NPC with the id " + map.get("npc") + " doesn't exist. Consider removing this text instance.");
		
		TextInstance ti = npc == null ? new TextInstance(npcID) : new TextInstance(npc);
		
		ti.messages = ((Map<Integer, Object>) map.get("messages")).entrySet().stream().sorted((x, y) -> Integer.compare(x.getKey(), y.getKey())).map(x -> {
			if (x.getValue() instanceof String) return ti.new Message((String) x.getValue());
			return ti.new Message((Map<String, Object>) x.getValue());
		}).collect(Collectors.toList());

		if (map.containsKey("commands")) ((Map<Integer, String>) map.get("commands")).forEach((id, command) -> {
			if (ti.messages.size() < id) ti.messages.get(id).command = command;
		}); // TODO remove (changed in 1.19)
		if (map.containsKey("sounds")) ((Map<Integer, String>) map.get("sounds")).forEach((id, sound) -> {
			if (ti.messages.size() < id) ti.messages.get(id).sound = sound;
		}); // TODO remove (changed in 1.19)
		if (map.containsKey("players")) ti.players = (Map<String, Integer>) map.get("players");
		if (map.containsKey("times")) ti.times = (Map<String, Long>) map.get("times");
		if (map.containsKey("random")) ti.random = (boolean) map.get("random");
		if (map.containsKey("repeat")) ti.repeat = (boolean) map.get("repeat");
		if (map.containsKey("autoDispatch")) ti.autoDispatch = (boolean) map.get("autoDispatch");
		if (map.containsKey("customName")) ti.customName = (String) map.get("customName");
		if (map.containsKey("console")) ti.console = (boolean) map.get("console");

		if (npc != null){
			npcs.put(npc, ti);
		}else dead.put(npcID, ti);
	}
	
	public class Message {
		private String text;
		private boolean player = false;
		private String command;
		private String sound;
		private int delay = -1;
		
		public Message(String text) {
			this.text = text;
		}
		
		public Message(Map<String, Object> serializedDatas) {
			text = (String) serializedDatas.get("text");
			if (serializedDatas.containsKey("player")) player = (boolean) serializedDatas.get("player");
			if (serializedDatas.containsKey("command")) command = (String) serializedDatas.get("command");
			if (serializedDatas.containsKey("sound")) sound = (String) serializedDatas.get("sound");
			if (serializedDatas.containsKey("delay")) delay = (int) serializedDatas.get("delay");
		}
		
		public String getText() {
			return text;
		}
		
		public String setText(String text) {
			String tmp = this.text;
			this.text = text;
			return tmp;
		}
		
		public String setCommand(String command) {
			String tmp = this.command;
			this.command = command;
			return tmp;
		}
		
		public String setSound(String sound) {
			String tmp = this.sound;
			this.sound = sound;
			return tmp;
		}
		
		public boolean togglePlayerMode() {
			return !(player = !player);
		}
		
		public void setDelay(int delay) {
			this.delay = delay;
		}
		
		public int getDelay() {
			return delay < 0 ? CitizensText.getTimeToContinue() * 20 : delay;
		}
		
		public void send(Player p, int id) {
			String msg = text;
			if (CitizensText.papi) msg = PlaceholderDepend.format(msg, p);
			msg = msg.replace("{PLAYER}", p.getName());
			msg = CitizensText.formatMessage(player, msg, player ? p.getName() : getNPCName(), id + 1, messages.size());
			if (command != null) {
				String cmd = command.replace("{PLAYER}", p.getName());
				if (!autoDispatch && !console) {
					CitizensText.sendCommand(p, msg, cmd); // clickable msg
					if (sound != null) p.playSound(p.getLocation(), sound, 1f, 1f);
					return;
				}
				if (!console) {
					p.performCommand(cmd);
				}else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
			}// normal message
			if (sound != null) p.playSound(p.getLocation(), sound, 1f, 1f);
			p.sendMessage(msg);
		}
		
		@Override
		public String toString() {
			return text;
		}
		
		public Object serialize() {
			if (!player && (command == null) && (sound == null) && delay == -1) return text;
			Map<String, Object> map = new HashMap<>();
			map.put("text", text);
			if (player) map.put("player", true);
			if (command != null) map.put("command", command);
			if (sound != null) map.put("sound", sound);
			if (delay != -1) map.put("delay", delay);
			return map;
		}
	}

}
