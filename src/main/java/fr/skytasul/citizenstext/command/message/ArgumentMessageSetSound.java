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
			if (args.length < 2) {
				sender.sendMessage(ChatColor.RED + "You must specify a sound and optionally pitch, minVolume and maxVolume. Usage: /text sound <msg id> set <sound> [pitch] [minVolume] [maxVolume]");
				return false;
			}
			String sound = args[1];
			float pitch = 1.0f;
			float minVolume = 1.0f;
			float maxVolume = 1.0f;

			try {
				if (args.length > 2) {
					pitch = Float.parseFloat(args[2]);
				}
				if (args.length > 3) {
					minVolume = Float.parseFloat(args[3]);
				}
				if (args.length > 4) {
					maxVolume = Float.parseFloat(args[4]);
				}
			if (minVolume < 0.0f || maxVolume < 0.0f) {
				sender.sendMessage(ChatColor.RED + "Volume values must be positive numbers.");
				return false;
			}
			if (minVolume > maxVolume) {
				sender.sendMessage(ChatColor.RED + "minVolume cannot be greater than maxVolume.");
				return false;
			}
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Pitch, minVolume and maxVolume must be valid numbers.");
			return false;
		}
			
			message.setSound(sound);
			message.setPitch(pitch);
			message.setMinVolume(minVolume);
			message.setMaxVolume(maxVolume);
			
			sender.sendMessage(ChatColor.GREEN + "Successfully added sound to message \"" + message.getText() + "\". Previous: " + message.setSound(args[1]));
			break;
		case "remove":
			sender.sendMessage(ChatColor.GREEN + "Successfully removed sound \"" + message.setSound(null) + "\".");
			message.setPitch(1.0f);
			message.setMinVolume(1.0f);
			message.setMaxVolume(1.0f);
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Unknown command. /text sound <msg id> <" + ARGUMENTS_STRING + ">");
			return false;
		}
		return true;
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <" + ARGUMENTS_STRING + "> <sound> [pitch] [minVolume] [maxVolume]";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Manage text sounds";
	}
}
