package fr.skytasul.citizenstext.command;

import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionRepeat;

public class ArgumentTextRepeat extends TextCommandArgument<OptionRepeat> {
	
	public ArgumentTextRepeat() {
		super("repeat", "repeat", OptionRepeat.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionRepeat option) {
		sender.sendMessage("§aRepeat mode is now §o" + (option.toggle() ? "enabled" : "disabled"));
		return true;
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " : Block the player from talking to the NPC again";
	}
	
}
