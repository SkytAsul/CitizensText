package fr.skytasul.citizenstext.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageClear extends TextCommandArgument<OptionMessages> {
	
	public ArgumentMessageClear() {
		super("clear", "clear", OptionMessages.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option) {
		sender.sendMessage(ChatColor.GREEN.toString() + option.clear() + " messages removed.");
		return true;
	}
	
	@Override
	public String getHelpDescription() {
		return "Clear all messages";
	}
	
}
