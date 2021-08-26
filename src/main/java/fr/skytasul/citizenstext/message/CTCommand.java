package fr.skytasul.citizenstext.message;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.skytasul.citizenstext.CitizensText;

public class CTCommand {
	private String command;
	public boolean console = false;
	public boolean auto = true;
	
	public CTCommand(String command) {
		this.command = command;
	}
	
	public CTCommand(String command, boolean console, boolean auto) {
		this.command = command;
		this.console = console;
		this.auto = auto;
	}
	
	public boolean execute(Player p, String msg) {
		String cmd = command.replace("{PLAYER}", p.getName());
		if (!auto && !console) {
			CitizensText.sendCommand(p, msg, cmd); // clickable msg
			return true;
		}
		if (!console) {
			p.performCommand(cmd);
		}else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		return false;
	}
	
	@Override
	public String toString() {
		return "§b/" + command + " §7(" + (console ? "console" : "player") + ", " + (auto ? "auto" : "clickable") + ")";
	}
	
	public Object serialize() {
		if (!console && auto) return command;
		Map<String, Object> map = new HashMap<>();
		map.put("command", command);
		if (console) map.put("console", true);
		if (!auto) map.put("auto", false);
		return map;
	}
	
	public static CTCommand deserialize(Object serialized) {
		CTCommand cmd;
		if (serialized instanceof String) {
			cmd = new CTCommand((String) serialized);
		}else {
			Map<String, Object> map = (Map<String, Object>) serialized;
			cmd = new CTCommand((String) map.get("command"));
			if (map.containsKey("console")) cmd.console = (boolean) map.get("console");
			if (map.containsKey("auto")) cmd.auto = (boolean) map.get("auto");
		}
		return cmd;
	}
	
}