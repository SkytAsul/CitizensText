package fr.skytasul.citizenstext.command;

import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentTextDelete extends TextCommandArgument<OptionMessages> {
	
	public ArgumentTextDelete() {
		super("delete", "delete", OptionMessages.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option) {
		if (option.messagesSize() > 0) {
			sender.sendMessage("§cFor security, please clear all messages before deleting.");
		}else {
			option.getTextInstance().delete();
			sender.sendMessage("§aText instance deleted.");
		}
		return false;
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " : Delete this text instance to free space";
	}
	
}
