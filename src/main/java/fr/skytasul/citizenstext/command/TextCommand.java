package fr.skytasul.citizenstext.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.command.message.ArgumentMessageSetCmd;
import fr.skytasul.citizenstext.command.message.ArgumentMessageSetDelay;
import fr.skytasul.citizenstext.command.message.ArgumentMessageSetSender;
import fr.skytasul.citizenstext.command.message.ArgumentMessageSetSound;

public class TextCommand implements TabExecutor {
	
	private List<CommandArgument> arguments = new ArrayList<>();
	private Map<String, CommandArgument> argumentsKeyMapping = new HashMap<>();
	
	public TextCommand() {
		registerArgument(new ArgumentMessageAdd());
		registerArgument(new ArgumentMessageEdit());
		registerArgument(new ArgumentMessageInsert());
		registerArgument(new ArgumentMessageRemove());
		registerArgument(new ArgumentMessageSetDelay());
		registerArgument(new ArgumentMessageSetSender());
		registerArgument(new ArgumentMessageSetCmd());
		registerArgument(new ArgumentMessageSetSound());
		registerArgument(new ArgumentTextName());
		registerArgument(new ArgumentTextRepeat());
		registerArgument(new ArgumentTextPlaybackTime());
		registerArgument(new ArgumentTextRandom());
		registerArgument(new ArgumentTextNear());
		registerArgument(new ArgumentMessageList());
		registerArgument(new ArgumentMessageClear());
		registerArgument(new ArgumentTextConvert());
		registerArgument(new ArgumentTextDelete());
		
		registerArgument(new ArgumentReload());
		registerArgument(new ArgumentHelp(this));
	}
	
	public void registerArgument(CommandArgument argument) {
		arguments.add(argument);
		argumentsKeyMapping.put(argument.getCmdArgument(), argument);
		for (String arg : argument.getCmdAliases()) argumentsKeyMapping.put(arg, argument);
	}
	
	public List<CommandArgument> getArguments() {
		return arguments;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Unknown command. Type /text help to get some help for this command.");
			return true;
		}
		
		for (int i = 0; i < args.length; i++){
			args[i] = net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', CitizensText.translateHexColorCodes("(&|§)#", "", args[i]));
		}
		
		CommandArgument argument = argumentsKeyMapping.get(args[0].toLowerCase());
		if (argument == null) {
			if (args[0].equals("save")) //TODO remove
				sender.sendMessage("§c§oThe save command has been disabled. Everything is now saved in real-time.");
			else
				sender.sendMessage(ChatColor.RED + "Unknown command. Type §o/text help §r§cto get some help.");
		}else {
			if (perm(sender, argument.getCmdPermission())) {
				argument.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
			}else sender.sendMessage(ChatColor.RED + "Sorry, but you don't have the permission to do this command. §oRequired permission : §lcitizenstext." + argument.getCmdPermission());
		}
		
		return true;
	}
	
	public boolean perm(CommandSender sender, String perm){
		return sender.hasPermission("citizenstext." + perm);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tmp = null;
		Collection<String> find = null;
		if (args.length == 1){
			find = sender.isOp() ? argumentsKeyMapping.keySet() : argumentsKeyMapping.entrySet().stream().filter(entry -> perm(sender, entry.getValue().getCmdPermission())).map(Entry::getKey).collect(Collectors.toList());
		}else if (args.length >= 2) {
			CommandArgument arg = argumentsKeyMapping.get(args[0].toLowerCase());
			if (arg != null && perm(sender, arg.getCmdPermission())) find = arg.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
		}
		
		if (find != null) {
			for (String arg : find) {
				if (arg.startsWith(args[args.length - 1])) {
					if (tmp == null) tmp = new ArrayList<>();
					tmp.add(arg);
				}
			}
		}
		return tmp == null ? Collections.emptyList() : tmp;
	}

}
