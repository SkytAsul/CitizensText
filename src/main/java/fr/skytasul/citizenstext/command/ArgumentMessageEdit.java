package fr.skytasul.citizenstext.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageEdit extends TextCommandArgument<OptionMessages> {
	
	public ArgumentMessageEdit() {
		super("edit", "edit", OptionMessages.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "You must specify an ID and a message.");
			return false;
		}
		
		try {
			int id = Integer.parseInt(args[0]);
			if (id < 0) {
				sender.sendMessage(ChatColor.RED + "This is not a valid number.");
				return false;
			}
			String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			sender.sendMessage(ChatColor.GREEN + "Succesfully edited message \"" + option.editMessage(id, msg) + "\"§r§a at the position " + id + ".");
			return true;
		}catch (IllegalArgumentException ex) {
			sender.sendMessage(ChatColor.RED + "This is not a valid number.");
		}catch (IndexOutOfBoundsException ex) {
			sender.sendMessage(ChatColor.RED + "The number you have entered (" + args[0] + ") is too big. It must be between 0 and " + (option.messagesSize() - 1) + ".");
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args, OptionMessages option) {
		if (option != null && args.length == 1) {
			return IntStream.range(0, option.messagesSize()).mapToObj(Integer::toString).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <id> <message>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Edit a previously created message";
	}
	
}
