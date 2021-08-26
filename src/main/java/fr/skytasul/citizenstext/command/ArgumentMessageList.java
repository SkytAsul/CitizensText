package fr.skytasul.citizenstext.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageList extends TextCommandArgument<OptionMessages> {
	
	public ArgumentMessageList() {
		super("list", "list", OptionMessages.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option) {
		String list = ChatColor.GREEN + "List of messages for §6" + option.getTextInstance().getNPCName() + " §a:\n§r" + option.listMessages();
		sender.sendMessage(list);
		return false;
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " : List all messages/IDs";
	}
	
}
