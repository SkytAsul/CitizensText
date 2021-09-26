package fr.skytasul.citizenstext.command.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.message.CTCommand;
import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageSetCmd extends MessageCommandArgument {
	
	private static final List<String> ARGUMENTS = Arrays.asList("add", "remove", "console", "auto");
	private static final String ARGUMENTS_STRING = String.join("|", ARGUMENTS);
	
	public ArgumentMessageSetCmd() {
		super("cmd", "cmd");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option, Message message) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify a command (" + ARGUMENTS_STRING + ")");
			return false;
		}
		String argument = args[0].toLowerCase();
		switch (argument) {
		case "add":
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "You must specify a command (without /). You can insert {PLAYER} which will be replaced by the player name.");
				return false;
			}
			message.addCommand(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
			sender.sendMessage(ChatColor.GREEN + "Successfully added command for message \"" + message.getText() + "\"§r§a. New commands: " + message.getCommandsList());
			break;
		case "remove":
			sender.sendMessage(ChatColor.GREEN + "Succesfully removed " + message.clearCommands() + " commands.");
			break;
		case "auto":
		case "console":
			if (message.getCommands().isEmpty()) {
				sender.sendMessage(ChatColor.RED + "There is no command on this message. Use /text cmd <msg id> add <cmd>");
				return false;
			}
			
			int id = 0;
			if (args.length > 0) {
				try {
					id = Integer.parseInt(args[1]);
				}catch (IllegalArgumentException ex) {
					sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" isn't a valid number.");
					return false;
				}
				
				if (id < 0 || id >= message.getCommands().size()) {
					sender.sendMessage(ChatColor.RED + "The command ID must be a number between 0 and " + (message.getCommands().size() - 1));
					return false;
				}
			}
			CTCommand command = message.getCommand(id);
			if (argument.equals("auto")) {
				command.auto = !command.auto;
				sender.sendMessage("§aAuto-dispatchment of commands is now §o" + (command.auto ? "enabled" : "disabled. §7Please note that clickable messages are not compatible with console dispatchment."));
			}else if (argument.equals("console")) {
				command.console = !command.console;
				sender.sendMessage("§aDispatchment by console of commands is now §o" + (command.console ? "enabled. §7Please note that this feature is not compatible with clickable messages." : "disabled"));
			}
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Unknown command. /text cmd <msg id> <" + ARGUMENTS_STRING + ">");
			return false;
		}
		return true;
	}
	
	@Override
	public List<String> onTabCompleteMessage(CommandSender sender, String[] args, OptionMessages option, String argCmdId) {
		if (args.length == 1) return ARGUMENTS;
		if (args.length == 2) {
			String operation = args[0].toLowerCase();
			if (operation.equals("auto") || operation.equals("console")) {
				try {
					Message message = option.getMessage(Integer.parseInt(argCmdId));
					return IntStream.range(0, message.getCommands().size()).mapToObj(Integer::toString).collect(Collectors.toList());
				}catch (Exception ex) {}
			}
		}
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <" + ARGUMENTS_STRING + "> ...";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Manage text commands";
	}
	
}
