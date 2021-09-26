package fr.skytasul.citizenstext.command.message;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageSetDelay extends MessageCommandArgument {
	
	public ArgumentMessageSetDelay() {
		super("delay", "delay");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option, Message message) {
		try {
			int delay = args.length == 0 ? -1 : Integer.parseInt(args[0]);
			message.setDelay(delay);
			sender.sendMessage(ChatColor.GREEN + "Message now has " + (delay < 0 ? "default delay" : delay + " ticks delay") + ".");
		}catch (IllegalArgumentException ex) {
			sender.sendMessage(ChatColor.RED + "\"" + args[1] + "\" isn't a valid number.");
			return false;
		}
		return true;
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " [delay]";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Set the delay of a message";
	}
	
}
