package fr.skytasul.citizenstext.command;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageInsert extends TextCommandArgument<OptionMessages> {
	
	public ArgumentMessageInsert() {
		super("insert", "insert", OptionMessages.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "You must specify an ID and a message.");
			return false;
		}
		
		try {
			int id = Integer.parseInt(args[0]);
			if (id < 0 || id > option.getValue().size()) {
				sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") must be between 0 and " + option.getValue().size() + ".");
				return false;
			}
			String msg = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			option.insertMessage(id, msg);
			sender.sendMessage(ChatColor.GREEN + "Succesfully inserted message \"" + msg + "\"§r§a at the position " + id + ".");
			return true;
		}catch (IllegalArgumentException ex) {
			sender.sendMessage(ChatColor.RED + "This is not a valid number.");
		}
		return false;
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <id> <message>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Insert a message";
	}
	
}
