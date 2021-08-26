package fr.skytasul.citizenstext.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageAdd extends TextCommandArgument<OptionMessages> {
	
	public ArgumentMessageAdd() {
		super("add", "add", OptionMessages.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify a message.");
			return false;
		}
		String msg = String.join(" ", args);
		option.addMessage(msg);
		sender.sendMessage(ChatColor.GREEN + "Succesfully added message \"" + msg + "\".");
		return true;
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " <message> : Add a message (use {nl} to skip a line)";
	}
	
}
