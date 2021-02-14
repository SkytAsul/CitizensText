package fr.skytasul.citizenstext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import fr.skytasul.citizenstext.TextInstance.Message;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.text.Text;

@SuppressWarnings("serial")
public class TextCommand implements CommandExecutor, TabCompleter {

	private List<String> cmds = Arrays.asList("add", "edit", "insert", "remove", "player", "cmd", "list", "clear", "sound", "delay", "name", "repeat", "convert", "random", "help", "reload", "save", "delete");
	private Map<String, List<String>> subCmds = new HashMap<String, List<String>>() {{
		put("cmd", Arrays.asList("add", "remove", "auto", "console"));
		put("sound", Arrays.asList("add", "remove"));
	}};
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		for (int i = 0; i < args.length; i++){
			args[i] = CitizensText.translateHexColorCodes("(&|§)#", "", args[i]);
		}
		
		if (args.length == 0){
			sender.sendMessage(ChatColor.RED + "Unknown command. Type /text help to get some help for this command.");
			return false;
		}
		
		if (args[0].equals("reload")){
			if (!perm(sender, "reload")) return false;
			CitizensText.getInstance().onDisable();
			CitizensText.getInstance().loadConfig();
			sender.sendMessage("§aConfig reloaded !");
			CitizensText.getInstance().loadDatas();
			sender.sendMessage("§aDatas reloading...");
			return false;
		}else if (args[0].equals("save")){
			if (!perm(sender, "save")) return false;
			sender.sendMessage("§aConfig saved ! " + CitizensText.getInstance().saveDatas() + " texts saved.");
			return false;
		}
		
		NPC npc = ((Citizens) CitizensAPI.getPlugin()).getDefaultNPCSelector().getSelected(sender);
		if (npc == null){
			sender.sendMessage(ChatColor.RED + "You must have a NPC selected to do this command.");
			return false;
		}
		if (!TextInstance.npcs.containsKey(npc)){
			TextInstance.npcs.put(npc, new TextInstance(npc));
		}
		TextInstance txt = TextInstance.npcs.get(npc);
		
		switch (args[0]){
		
		case "add":
			if (!perm(sender, "add")) return false;
			if (args.length == 1){
				sender.sendMessage(ChatColor.RED + "You must specify a message.");
				return false;
			}
			StringBuilder stb = new StringBuilder();
			for (int i = 1; i < args.length; i++){
				stb.append(args[i] + " ");
			}
			String msg = stb.toString();
			msg = msg.substring(0, msg.length() - 1);
			txt.addMessage(msg);
			sender.sendMessage(ChatColor.GREEN + "Succesfully added message \"" + msg + "\".");
			break;
		
		case "edit":
			if (!perm(sender, "edit")) return false;
			if (args.length < 3) {
				sender.sendMessage(ChatColor.RED + "You must specify an ID and a message.");
				return false;
			}
			stb = new StringBuilder();
			for (int i = 2; i < args.length; i++) {
				stb.append(args[i] + " ");
			}
			msg = stb.toString();
			msg = msg.substring(0, msg.length() - 1);
			try {
				int id = Integer.parseInt(args[1]);
				if (id < 0) {
					sender.sendMessage(ChatColor.RED + "This is not a valid number.");
					return false;
				}
				sender.sendMessage(ChatColor.GREEN + "Succesfully edited message \"" + txt.editMessage(id, msg) + "\"§r§a at the position " + id + ".");
			}catch (IllegalArgumentException ex) {
				sender.sendMessage(ChatColor.RED + "This is not a valid number.");
			}catch (IndexOutOfBoundsException ex) {
				sender.sendMessage(ChatColor.RED + "The number you have entered (" + args[1] + ") is too big. It must be between 0 and " + txt.size() + ".");
			}
			break;

		case "insert":
			if (!perm(sender, "insert")) return false;
			if (args.length < 3){
				sender.sendMessage(ChatColor.RED + "You must specify an ID and a message.");
				return false;
			}
			stb = new StringBuilder();
			for (int i = 2; i < args.length; i++){
				stb.append(args[i] + " ");
			}
			msg = stb.toString();
			msg = msg.substring(0, msg.length() - 1);
			try{
				int id = Integer.parseInt(args[1]);
				if (id < 0){
					sender.sendMessage(ChatColor.RED + "This is not a valid number.");
					return false;
				}
				txt.insertMessage(msg, id);
				sender.sendMessage(ChatColor.GREEN + "Succesfully inserted message \"" + msg + "\"§r§a at the position " + id + ".");
			}catch (IllegalArgumentException ex){
				sender.sendMessage(ChatColor.RED + "This is not a valid number.");
			}
			break;
			
		case "remove":
			if (!perm(sender, "remove")) return false;
			if (args.length == 1){
				sender.sendMessage(ChatColor.RED + "You must specify an ID.");
				return false;
			}
			int id;
			try {
				id = Integer.parseInt(args[1]);
			}catch (IllegalArgumentException ex){
				sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" isn't a valid number.");
				return false;
			}
			try{
				sender.sendMessage(ChatColor.GREEN + "Succesfully removed message \"" + txt.removeMessage(id) + "\".");
			}catch (IndexOutOfBoundsException ex){
				sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + txt.size() + ".");
			}
			break;
			
		case "player":
			if (!perm(sender, "player")) return false;
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "You must specify an ID.");
				return false;
			}
			try {
				id = Integer.parseInt(args[1]);
			}catch (IllegalArgumentException ex) {
				sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" isn't a valid number.");
				return false;
			}
			try {
				sender.sendMessage(ChatColor.GREEN + "Message at position " + id + " is now sent by the " + (txt.getMessage(id).togglePlayerMode() ? "player" : "NPC") + ".");
			}catch (IndexOutOfBoundsException ex) {
				sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + txt.size() + ".");
			}
			break;
		
		case "delay":
			if (!perm(sender, "delay")) return false;
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "You must specify an ID.");
				return false;
			}
			try {
				id = Integer.parseInt(args[1]);
				int delay = args.length == 2 ? -1 : Integer.parseInt(args[2]);
				try {
					txt.getMessage(id).setDelay(delay);
					sender.sendMessage(ChatColor.GREEN + "Message at position " + id + " now has " + (delay < 0 ? "default delay" : delay + " ticks delay") + ".");
				}catch (IndexOutOfBoundsException ex) {
					sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + txt.size() + ".");
				}
			}catch (IllegalArgumentException ex) {
				sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" isn't a valid number.");
				return false;
			}
			break;
		
		case "cmd":
			if (!perm(sender, "cmd")) return false;
			if (args.length < 2){
				sender.sendMessage(ChatColor.RED + "You must specify a command. (add/remove/auto/console)");
				return false;
			}

			switch(args[1]){
			case "add":
				if (args.length < 4){
					sender.sendMessage(ChatColor.RED + "You must specify an ID and a command (without /). You can insert {PLAYER} who will be remplaced by the player name.");
					return false;
				}
				id = 0;
				try {
					id = Integer.parseInt(args[2]);
				}catch (IllegalArgumentException ex){
					sender.sendMessage(ChatColor.RED + "\"" + args[2] + "\" isn't a valid number.");
					return false;
				}
				stb = new StringBuilder();
				for (int i = 3; i < args.length; i++){
					stb.append(args[i] + " ");
				}
				String command = stb.toString();
				command = command.substring(0, command.length() - 1);
				try{
					String lcmd = txt.setCommand(id, command);
					sender.sendMessage(ChatColor.GREEN + "Successfully added command for message \"" + txt.getMessage(id) + "\"§r§a." + ((!StringUtils.isEmpty(lcmd)) ? " Old command : \"" + lcmd : ""));
				}catch (IndexOutOfBoundsException ex){
					sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + txt.size() + ".");
				}
				break;
				
			case "remove":
				if (args.length < 3){
					sender.sendMessage(ChatColor.RED + "You must specify an ID.");
					return false;
				}
				id = 0;
				try {
					id = Integer.parseInt(args[2]);
				}catch (IllegalArgumentException ex){
					sender.sendMessage(ChatColor.RED + "\"" + args[2] + "\" isn't a valid number.");
					return false;
				}
				sender.sendMessage(ChatColor.GREEN + "Succesfully removed command \"" + txt.removeCommand(id) + "\".");
				break;
				
			case "auto":
				sender.sendMessage("§aAuto-dispatchment of commands is now §o" + (txt.toggleAutodispatch() ? "enabled" : "disabled. Please note that clickable messages are not compatible with console dispatchment."));
				break;
				
			case "console":
				sender.sendMessage("§aDispatchment by console of commands is now §o" + (txt.toggleConsole() ? "enabled. Please note that this feature is not compatible with clickable messages." : "disabled"));
				break;
				
			default:
				sender.sendMessage(ChatColor.RED + "Unknown command. /text cmd <add|remove|auto|console>");
				break;
				
				
			}
			break;
			
		case "list":
			if (!perm(sender, "list")) return false;
			String list = ChatColor.GREEN + "List of messages for §6" + npc.getName() + " §a:\n§r" + txt.listMessages();
			sender.sendMessage(list);
			break;
			
		case "clear":
			if (!perm(sender, "clear")) return false;
			sender.sendMessage(ChatColor.GREEN.toString() + txt.clear() + " messages removed.");
			break;
			
		case "sound":
			if (!perm(sender, "sound")) return false;
			if (args.length < 2){
				sender.sendMessage(ChatColor.RED + "You must specify a command. (add/remove)");
				return false;
			}

			switch(args[1]){
			case "add":
				if (args.length < 4){
					sender.sendMessage(ChatColor.RED + "You must specify an ID and a sound.");
					return false;
				}
				id = 0;
				try {
					id = Integer.parseInt(args[2]);
				}catch (IllegalArgumentException ex){
					sender.sendMessage(ChatColor.RED + "\"" + args[2] + "\" isn't a valid number.");
					return false;
				}
				try{
					String lsound = txt.setSound(id, args[3]);
					sender.sendMessage(ChatColor.GREEN + "Successfully added command for message \"" + txt.getMessage(id).toString() + "§a.\"" + (StringUtils.isEmpty(lsound) ? " Last sound : \"" + lsound : ""));
				}catch (IndexOutOfBoundsException ex){
					sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + txt.size() + ".");
				}
				break;
				
			case "remove":
				if (args.length < 3){
					sender.sendMessage(ChatColor.RED + "You must specify an ID.");
					return false;
				}
				id = 0;
				try {
					id = Integer.parseInt(args[2]);
				}catch (IllegalArgumentException ex){
					sender.sendMessage(ChatColor.RED + "\"" + args[2] + "\" isn't a valid number.");
					return false;
				}
				sender.sendMessage(ChatColor.GREEN + "Succesfully removed sound \"" + txt.removeSound(id) + "\".");
				break;
				
			}
			break;
			
		case "name":
			if (!perm(sender, "name")) return false;
			if (args.length == 1){
				sender.sendMessage("§aCustom name removed. (old : \"" + txt.setCustomName(null) + "§r§a\")");
			}else {
				stb = new StringBuilder();
				for (int i = 1; i < args.length; i++){
					stb.append(args[i] + (i == args.length - 1 ? "" : " "));
				}
				sender.sendMessage("§aCustom name edited. (old : \"" + txt.setCustomName(stb.toString()) + "§r§a\")");
			}
			break;
			
		case "repeat":
			if (!perm(sender, "repeat")) return false;
			sender.sendMessage("§aRepeat mode is now §o" + (txt.toggleRepeatMode() ? "enabled" : "disabled"));
			break;
			
		case "convert":
			if (!perm(sender, "convert")) return false;
			if (!npc.hasTrait(Text.class)){
				sender.sendMessage(ChatColor.RED + "This NPC does not have the Text trait.");
				return false;
			}
			try {
				Text trait = npc.getTraitNullable(Text.class);
				Field f = trait.getClass().getDeclaredField("text");
				f.setAccessible(true);
				List<String> ls = new ArrayList<>((List<String>) f.get(trait));
				for (Message s : txt.getMessages()){
					if (ls.contains(s.getText())) ls.remove(s.getText());
				}
				for (String s : ls){
					txt.addMessage(s);
				}
				sender.sendMessage(ChatColor.GREEN.toString() + ls.size() + " messages added.");
				npc.removeTrait(Text.class);
			}catch (ReflectiveOperationException e) {
				sender.sendMessage("§cError while conversion. Please contact an administrator.");
				e.printStackTrace();
			}
			break;
			
		case "random":
			if (!perm(sender, "random")) return false;
			if (CitizensText.clickDisabled()){
				sender.sendMessage("§cThe \"Cancel click\" option is enabled. Please disable it if you want to enable the random mode.");
				break;
			}
			sender.sendMessage("§aRandom mode is now §o" + (txt.toggleRandom() ? "enabled" : "disabled"));
			break;
			
		case "delete":
			if (!perm(sender, "delete")) return false;
			if (txt.size() > 0){
				sender.sendMessage("§cFor security, please clear all messages before deleting.");
			}else {
				txt.delete();
				sender.sendMessage("§aText instance deleted.");
			}
			break;
			
		case "?":
		case "help":
			sender.sendMessage(ChatColor.GREEN + "§m--§r §2§lCitizensText §r§2help §a§m--§r§a\n"
					+ " /text add <message> : Add a message (to skip a line use {nl})\n"
					+ " /text edit <id> <message> : Edit a previously created message\n"
					+ " /text insert <id> <message> : Insert a message\n"
					+ " /text remove <id> : Remove a message\n"
					+ " /text delay <id> [delay] : Set the delay of a message\n"
					+ " /text player <id> : Make the message sent by the player\n"
					+ " /text cmd <add|remove|auto> ... : Manage text commands\n"
					+ " /text sound <add|remove> <id> ... : Manage text sounds\n"
					+ " /text name <name> : Set the custom name of the NPC\n"
					+ " /text repeat : Block the player from talking to the NPC again\n"
					+ " /text random : Toggle random mode\n"
					+ " /text list : List all messages/IDs\n"
					+ " /text clear : Clear all messages\n"
					+ " /text convert : Add all messages from default NPC Text trait\n"
					+ " /text delete : Delete text's instance to free space\n"
					+ "  §a§l -- -   x   - --§r§a\n"
					+ " /text reload : Reload config and datas\n"
					+ " /text save : Save all NPCs datas\n"
					+ "§m--------------------");
			break;
			
			default:
				sender.sendMessage(ChatColor.RED + "Unknown command. Type §o/text §r§chelp to get some help.");
				break;
		
		}
		
		return false;
	}
	
	public boolean perm(CommandSender sender, String perm){
		if (!sender.hasPermission("citizenstext." + perm)){
			sender.sendMessage(ChatColor.RED + "Sorry, but you don't have the permission to do this command. §oRequired permission : §l" + perm);
			return false;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tmp = new ArrayList<>();
		List<String> find;
		String sel = args[0];
		if (args.length == 1){
			find = cmds;
		}else if (subCmds.containsKey(sel) && args.length == 2){
			find = subCmds.get(sel);
			sel = args[1];
		}else return new ArrayList<>();
		for (String arg : find){
			if (arg.startsWith(sel)) tmp.add(arg);
		}
		return tmp;
	}

}
