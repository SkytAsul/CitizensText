package fr.skytasul.citizenstext.command;

import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionNear;

public class ArgumentTextNear extends TextCommandArgument<OptionNear> {
	
	public ArgumentTextNear() {
		super("near", "near", OptionNear.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionNear option) {
		sender.sendMessage("§aNear mode is now §o" + (option.toggle() ? "enabled" : "disabled"));
		return true;
	}
	
	@Override
	public String getHelpDescription() {
		return "Toggle \"talk when nearby\"";
	}
	
}
