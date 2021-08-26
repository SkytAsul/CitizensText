package fr.skytasul.citizenstext.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.CitizensTextConfiguration;
import fr.skytasul.citizenstext.PlaceholderDepend;
import fr.skytasul.citizenstext.texts.TextInstance;

public class Message {
	private String text;
	private TextSender sender;
	private List<CTCommand> commands = new ArrayList<>();
	private String sound;
	private int delay = -1;
	
	public Message(String text) {
		this.text = text;
	}
	
	public Message(ConfigurationSection data) {
		text = data.getString("text");
		if (data.getBoolean("player", false)) {
			sender = TextSender.PLAYER_SENDER; // to remove
			data.set("player", null);
			data.set("sender", sender.toString());
		}
		if (data.contains("sender")) sender = TextSender.fromString(data.getString("sender"));
		if (data.contains("command")) {
			commands.add(new CTCommand(data.getString("command"), false, true));
		}
		if (data.contains("commands")) {
			this.commands = data.getList("commands").stream().map(CTCommand::deserialize).collect(Collectors.toList());
		}
		if (data.contains("sound")) sound = data.getString("sound");
		if (data.contains("delay")) delay = data.getInt("delay");
	}
	
	public String getText() {
		return text;
	}
	
	public String setText(String text) {
		String tmp = this.text;
		this.text = text;
		return tmp;
	}
	
	public void addCommand(String command) {
		commands.add(new CTCommand(command));
	}
	
	public int clearCommands() {
		int size = commands.size();
		commands.clear();
		return size;
	}
	
	public CTCommand getCommand(int id) {
		return commands.get(id);
	}
	
	public List<CTCommand> getCommands() {
		return commands;
	}
	
	public String getCommandsList() {
		return commands.stream().map(CTCommand::toString).collect(Collectors.joining("§7, "));
	}
	
	public String setSound(String sound) {
		String tmp = this.sound;
		this.sound = sound;
		return tmp;
	}
	
	public void setSender(TextSender sender) {
		this.sender = sender;
	}
	
	public TextSender getSender() {
		return sender == null ? TextSender.NPC_SENDER : sender;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public int getDelay() {
		return delay < 0 ? CitizensTextConfiguration.getTimeToContinue() * 20 : delay;
	}
	
	public void send(Player p, int id, TextInstance textInstance) {
		String msg = text;
		msg = msg.replace("{PLAYER}", p.getName());
		TextSender sender = getSender();
		if (sender == null || sender instanceof TextSender.NPCSender) {
			msg = TextSender.NPC_SENDER.format(msg, textInstance.getNPCName(), id + 1, textInstance.getMessages().messagesSize());
		}else if (sender instanceof TextSender.PlayerSender) {
			msg = TextSender.PLAYER_SENDER.format(msg, p.getName(), id + 1, textInstance.getMessages().messagesSize());
		}else if (sender instanceof TextSender.NoSender) {
			// no format here
		}else if (sender instanceof TextSender.CustomizedSender) {
			msg = ((TextSender.CustomizedSender) sender).format(msg, id + 1, textInstance.getMessages().messagesSize());
		}
		if (CitizensText.getInstance().isPAPIEnabled()) msg = PlaceholderDepend.format(msg, p);
		
		boolean cancelMsg = false;
		for (Iterator<CTCommand> iterator = commands.iterator(); iterator.hasNext();) {
			CTCommand cmd = iterator.next();
			cancelMsg = cmd.execute(p, msg) || cancelMsg;
		}
		if (!cancelMsg) p.sendMessage(msg);
		if (sound != null) p.playSound(p.getLocation(), sound, 1f, 1f);
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	public Object serialize() {
		if (getSender() == TextSender.NPC_SENDER && (commands.isEmpty()) && (sound == null) && delay == -1) return text;
		Map<String, Object> map = new HashMap<>();
		map.put("text", text);
		if (getSender() != TextSender.NPC_SENDER) map.put("sender", sender.toString());
		if (!commands.isEmpty()) map.put("commands", commands.stream().map(CTCommand::serialize).collect(Collectors.toList()));
		if (sound != null) map.put("sound", sound);
		if (delay != -1) map.put("delay", delay);
		return map;
	}
}