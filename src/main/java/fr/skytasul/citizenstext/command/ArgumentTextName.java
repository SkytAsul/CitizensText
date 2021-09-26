package fr.skytasul.citizenstext.command;

import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionName;

public class ArgumentTextName extends TextCommandArgument<OptionName> {
	
	public ArgumentTextName() {
		super("name", "name", OptionName.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionName option) {
		String oldName = option.getValue();
		if (args.length == 0) {
			if (oldName == null) {
				sender.sendMessage("§cNo custom NPC name was set.");
				return false;
			}
			option.setValue(null);
			sender.sendMessage("§aCustom name removed. (old: \"" + oldName + "§r§a\")");
		}else {
			option.setValue(String.join(" ", args));
			sender.sendMessage("§aCustom name edited. (old: \"" + oldName + "§r§a\")");
		}
		return true;
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <name>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Set the custom name of the NPC";
	}
	
}
