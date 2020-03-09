package fr.skytasul.citizenstext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

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
	
	private List<String> messages = new ArrayList<>();
	private Map<Integer, String> commands = new HashMap<>();
	private Map<Integer, String> sounds = new HashMap<>();
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
		messages.add(msg);
	}
	
	public void insertMessage(String msg, int id){
		messages.add(id, msg);
	}
	
	public String setCommand(int id, String command){
		String tmp = "";
		if (commands.containsKey(id)) tmp = commands.remove(id);
		commands.put(id, command);
		return tmp;
	}
	
	public String removeCommand(int id){
		return commands.remove(id);
	}
	
	public String setSound(int id, String command){
		String tmp = "";
		if (sounds.containsKey(id)) tmp = sounds.remove(id);
		sounds.put(id, command);
		return tmp;
	}
	
	public String removeSound(int id){
		return sounds.remove(id);
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

	public String getMessage(int id){
		return messages.get(id);
	}
	
	public String removeMessage(int id){
		return messages.remove(id);
	}
	
	public List<String> getMessages(){
		return new ArrayList<>(messages);
	}
	
	public String listMessages(){
		StringBuilder stb = new StringBuilder();
		for (String msg : messages){
			stb.append(ChatColor.AQUA + "" + messages.indexOf(msg) + " : " + ChatColor.GREEN + msg + "\n");
		}
		return stb.toString();
	}

	public NPC getNPC(){
		return npc;
	}
	
	public boolean isEmpty(){
		if (!messages.isEmpty()) return false;
		if (!commands.isEmpty()) return false;
		if (!sounds.isEmpty()) return false;
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
			sendText(p, new Random().nextInt(messages.size()));
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
		sendText(p, id);
		
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
		if (CitizensText.getTimeToContinue() >= 0){ // TASK SYSTEM
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
			runs.get(uuid).runTaskLater(CitizensText.getInstance(), CitizensText.getTimeToContinue() * 20);
		}
	}
	
	private void sendText(Player p, int id){
		String msg = messages.get(id); 
		if (CitizensText.papi) msg = PlaceholderDepend.format(msg, p);
		msg = msg.replace("{PLAYER}", p.getName());
		String sound = sounds.get(id);
		if (commands.containsKey(id)){ // clickable message
			String cmd = commands.get(id);
			cmd = cmd.replace("{PLAYER}", p.getName());
			if (!autoDispatch && !console){
				CitizensText.sendRawNPC(p, msg, npc.getName(), cmd, id + 1, messages.size());
				if (sound != null) p.playSound(p.getLocation(), sound, 1f, 1f);
				return;
			}
			if (!console){
				p.performCommand(cmd);
			}else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}// normal message
		if (sound != null) p.playSound(p.getLocation(), sound, 1f, 1f);
		CitizensText.sendNPCMessage(p, msg, getNPCName(), id + 1, messages.size());
	}
	
	public Map<String, Object> serialize(){
		if (messages.isEmpty()) return null;
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("npc", npc == null ? id : npc.getId());
		
		Map<Integer, String> tmp = new HashMap<>();
		for (int i = 0; i < messages.size(); i++){
			String msg = messages.get(i);
			index: for (int j = i; j < messages.size(); j++){
				if (msg.equals(messages.get(j))){
					tmp.put(j, msg);
					break index;
				}
			}
		}
		map.put("messages", tmp);
		
		if (!sounds.isEmpty()) map.put("sounds", sounds);
		if (!commands.isEmpty()) map.put("commands", commands);
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
		int id = (int) map.get("npc");
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if (npc == null) CitizensText.getInstance().getLogger().info("NPC with the id " + map.get("npc") + " doesn't exist. Consider removing this text instance.");
		
		TextInstance ti = npc == null ? new TextInstance(id) : new TextInstance(npc);
		
		List<Entry<Integer, String>> strings = new ArrayList<>();
		for (Entry<Integer, String> en : ((Map<Integer, String>) map.get("messages")).entrySet()){
			strings.add(en);
		}
		strings.sort((x, y) -> {
			if (x.getKey() < y.getKey()) return -1;
			if (x.getKey() > y.getKey()) return 1;
			return 0;
		});
		strings.forEach((x) -> ti.messages.add(x.getValue()));

		if (map.containsKey("commands")) ti.commands = (Map<Integer, String>) map.get("commands");
		if (map.containsKey("players")) ti.players = (Map<String, Integer>) map.get("players");
		if (map.containsKey("times")) ti.times = (Map<String, Long>) map.get("times");
		if (map.containsKey("sounds")) ti.sounds = (Map<Integer, String>) map.get("sounds");
		if (map.containsKey("random")) ti.random = (boolean) map.get("random");
		if (map.containsKey("repeat")) ti.repeat = (boolean) map.get("repeat");
		if (map.containsKey("autoDispatch")) ti.autoDispatch = (boolean) map.get("autoDispatch");
		if (map.containsKey("customName")) ti.customName = (String) map.get("customName");
		if (map.containsKey("console")) ti.console = (boolean) map.get("console");

		if (npc != null){
			npcs.put(npc, ti);
		}else dead.put(id, ti);
	}

}
