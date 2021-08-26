package fr.skytasul.citizenstext.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageRemove extends TextCommandArgument<OptionMessages> {
	
	public ArgumentMessageRemove() {
		super("remove", "remove", OptionMessages.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify an ID.");
			return false;
		}
		int id;
		try {
			id = Integer.parseInt(args[0]);
		}catch (IllegalArgumentException ex) {
			sender.sendMessage(ChatColor.RED + "\"" + args[0] + "\" isn't a valid number.");
			return false;
		}
		try {
			sender.sendMessage(ChatColor.GREEN + "Succesfully removed message \"" + option.removeMessage(id) + "\".");
			return true;
		}catch (IndexOutOfBoundsException ex) {
			sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + (option.messagesSize() - 1) + ".");
		}
		return false;
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " <id> : Remove a message";
	}
	
}
