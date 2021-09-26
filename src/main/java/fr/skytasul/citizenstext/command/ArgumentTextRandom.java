package fr.skytasul.citizenstext.command;

import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.CitizensTextConfiguration;
import fr.skytasul.citizenstext.options.OptionNear;
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
		if (option.getTextInstance().getOption(OptionNear.class).getOrDefault()) {
			if (CitizensTextConfiguration.clickDisabled()) {
				sender.sendMessage("§cThe \"Cancel click\" config option is enabled. Please disable it if you want to enable the random mode, or use \"/text near\" to disable it for this NPC.");
			}else {
				sender.sendMessage("§cThe \"Near\" option is enabled. Please disable it by using \"/text near\" if you want to enable the random mode.");
			}
			return false;
		}
		sender.sendMessage("§aRandom mode is now §o" + (option.toggle() ? "enabled" : "disabled"));
		return true;
	}
	
	@Override
	public String getHelpDescription() {
		return "Toggle random mode";
	}
	
}
