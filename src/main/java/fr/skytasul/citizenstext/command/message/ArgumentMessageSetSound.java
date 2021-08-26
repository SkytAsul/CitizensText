package fr.skytasul.citizenstext.command.message;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageSetSound extends MessageCommandArgument {
	
	private static final List<String> ARGUMENTS = Arrays.asList("set", "remove");
	private static final String ARGUMENTS_STRING = String.join("|", ARGUMENTS);
	
	public ArgumentMessageSetSound() {
		super("sound", "sound");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option, Message message) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify a command (" + ARGUMENTS_STRING + ")");
			return false;
		}
		switch (args[0].toLowerCase()) {
		case "set":
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "You must specify a sound, in the format xxx.yyy.zzz...");
				return false;
			}
			sender.sendMessage(ChatColor.GREEN + "Successfully added sound to message \"" + message.getText() + "\". Previous: " + message.setSound(args[1]));
			break;
		case "remove":
			sender.sendMessage(ChatColor.GREEN + "Succesfully removed sound \"" + message.setSound(null) + "\".");
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Unknown command. /text sound <msg id> <" + ARGUMENTS_STRING + ">");
			return false;
		}
		return true;
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " <" + ARGUMENTS_STRING + "> ... : Manage text sounds";
	}
	
}
