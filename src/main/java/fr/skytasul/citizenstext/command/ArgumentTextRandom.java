package fr.skytasul.citizenstext.command;

import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.CitizensTextConfiguration;
import fr.skytasul.citizenstext.options.OptionRandom;

public class ArgumentTextRandom extends TextCommandArgument<OptionRandom> {
	
	public ArgumentTextRandom() {
		super("random", "random", OptionRandom.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionRandom option) {
		if (CitizensTextConfiguration.clickDisabled()) {
			sender.sendMessage("§cThe \"Cancel click\" option is enabled. Please disable it if you want to enable the random mode.");
			return false;
		}
		sender.sendMessage("§aRandom mode is now §o" + (option.toggle() ? "enabled" : "disabled"));
		return true;
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " : Toggle random mode";
	}
	
}
