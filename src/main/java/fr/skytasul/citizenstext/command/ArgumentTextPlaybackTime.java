package fr.skytasul.citizenstext.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.options.OptionPlaybackTime;
import fr.skytasul.citizenstext.options.OptionRepeat;

public class ArgumentTextPlaybackTime extends TextCommandArgument<OptionPlaybackTime> {
	
	public ArgumentTextPlaybackTime() {
		super("playback", "playback", OptionPlaybackTime.class);
	}
	
	@Override
	public boolean createTextInstance() {
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionPlaybackTime option) {
		if (!option.getTextInstance().getOption(OptionRepeat.class).getOrDefault()) {
			sender.sendMessage("The \"no repeat\" option is enabled. You must first disable it using \"\text repeat\" before editing the playback time.");
			return false;
		}
		Integer oldTime = option.getValue();
		if (args.length == 0) {
			if (oldTime == null) {
				sender.sendMessage("§cNo custom playback time was set. Default value: " + option.getDefault() + "s");
				return false;
			}
			option.setValue(null);
			sender.sendMessage("§aCustom playback time removed. Coming back from old (" + oldTime + "s) to default value (" + option.getDefault() + "s)");
		}else {
			try {
				int time = Integer.parseInt(args[0]);
				if (time < 0) {
					sender.sendMessage(ChatColor.RED + args[0] + " is not a valid number. (must be positive)");
					return false;
				}
				option.setValue(time);
				sender.sendMessage("§aCustom playback time set to " + time + " seconds. (old: " + oldTime + "s)");
			}catch (IllegalArgumentException ex) {
				sender.sendMessage(ChatColor.RED + "\"" + args[0] + "\" is not a valid number.");
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <time in seconds>";
	}
	
	@Override
	protected String getHelpDescription() {
		return "Set the time before players can restart the conversation";
	}
	
}
