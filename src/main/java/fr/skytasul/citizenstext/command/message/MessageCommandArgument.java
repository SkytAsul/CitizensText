package fr.skytasul.citizenstext.command.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.command.TextCommandArgument;
import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.options.OptionMessages;

public abstract class MessageCommandArgument extends TextCommandArgument<OptionMessages> {
	
	protected MessageCommandArgument(String cmdArgument, String cmdPermission) {
		super(cmdArgument, cmdPermission, OptionMessages.class);
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
			Message message = option.getMessage(id); // call before to not trigger Arrays.copyOfRange uselessly
			return onCommand(sender, Arrays.copyOfRange(args, 1, args.length), option, message);
		}catch (IndexOutOfBoundsException ex) {
			sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + (option.messagesSize() - 1) + ".");
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args, OptionMessages option) {
		if (option == null) return Collections.emptyList();
		if (args.length == 1) {
			return IntStream.range(0, option.messagesSize()).mapToObj(Integer::toString).collect(Collectors.toList());
		}
		return onTabCompleteMessage(sender, Arrays.copyOfRange(args, 1, args.length), option, args[0]);
	}
	
	public abstract boolean onCommand(CommandSender sender, String[] args, OptionMessages option, Message message);
	
	public List<String> onTabCompleteMessage(CommandSender sender, String[] args, OptionMessages option, String argMsgId) {
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " <msg id>";
	}
	
}
