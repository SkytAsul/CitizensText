package fr.skytasul.citizenstext.command;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.options.TextOption;
import fr.skytasul.citizenstext.texts.TextInstance;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public abstract class TextCommandArgument<O extends TextOption<?>> extends CommandArgument {
	
	private final Class<O> optionClass;
	
	public TextCommandArgument(String cmdArgument, String cmdPermission, Class<O> optionClass) {
		super(cmdPermission, cmdArgument);
		this.optionClass = optionClass;
	}
	
	public boolean createTextInstance() {
		return false;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
		if (npc == null) {
			sender.sendMessage(ChatColor.RED + "You must have a NPC selected to do this command.");
			return;
		}
		boolean created = false;
		TextInstance txt = CitizensText.getInstance().getTexts().getText(npc);
		if (txt == null) {
			if (!createTextInstance()) {
				sender.sendMessage(ChatColor.RED + "This NPC does not have texts. Use §o/text add <message>");
				return;
			}
			txt = new TextInstance(npc);
			created = true;
		}
		O option = txt.getOption(optionClass);
		if (onCommand(sender, args, option)) {
			if (created) {
				txt.create();
			}else {
				try {
					txt.saveOption(option);
				}catch (IOException e) {
					sender.sendMessage(ChatColor.RED + "An error ocurred while saving this change to the data file.");
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		O option = null;
		
		NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
		if (npc != null) {
			TextInstance txt = CitizensText.getInstance().getTexts().getText(npc);
			if (txt != null) {
				option = txt.getOption(optionClass);
			}
		}
		
		return onTabComplete(sender, args, option);
	}
	
	public abstract boolean onCommand(CommandSender sender, String[] args, O option);
	
	public List<String> onTabComplete(CommandSender sender, String[] args, O option) {
		return Collections.emptyList();
	}
	
}
