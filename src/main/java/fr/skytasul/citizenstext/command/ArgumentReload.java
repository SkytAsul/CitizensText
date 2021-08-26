package fr.skytasul.citizenstext.command;

import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.CitizensTextConfiguration;

public class ArgumentReload extends CommandArgument {
	
	public ArgumentReload() {
		super("reload", "reload");
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		CitizensText.getInstance().disable();
		sender.sendMessage("§aPlugin disabled.");
		CitizensText.getInstance().reloadConfig();
		CitizensTextConfiguration.loadConfig(CitizensText.getInstance().getConfig());
		sender.sendMessage("§aConfiguration reloaded !");
		sender.sendMessage("§7Reloading datas...");
		CitizensText.getInstance().loadDatas();
		sender.sendMessage("§a§lReload complete!");
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " : Reload config and datas";
	}
	
}
