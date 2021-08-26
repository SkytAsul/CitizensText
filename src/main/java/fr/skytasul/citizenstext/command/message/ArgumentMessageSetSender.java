package fr.skytasul.citizenstext.command.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.message.TextSender;
import fr.skytasul.citizenstext.options.OptionMessages;

public class ArgumentMessageSetSender extends MessageCommandArgument {
	
	private static final List<String> SENDERS = Arrays.asList("npc", "player", "noSender", "othernpc", "otherformat");
	
	public ArgumentMessageSetSender() {
		super("sender", "sender");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option, Message message) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You must specify a sender. Available: " + String.join(" ", SENDERS));
			return false;
		}
		TextSender newSender;
		String senderArg = args[0].toLowerCase();
		if (senderArg.equals("npc")) {
			newSender = TextSender.NPC_SENDER;
		}else if (senderArg.equals("player")) {
			newSender = TextSender.PLAYER_SENDER;
		}else if (senderArg.equals("nosender")) {
			newSender = TextSender.NO_SENDER;
		}else if (senderArg.equals("othernpc")) {
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "You must specify a NPC name.");
				return false;
			}
			newSender = new TextSender.FixedNPCSender(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
		}else if (senderArg.equals("otherformat")) {
			if (args.length == 1) {
				sender.sendMessage(ChatColor.RED + "You must specify a format. Placeholders: {1} = message, {2} = message id, {3} = messages size");
				return false;
			}
			newSender = new TextSender.FixedFormatSender(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
		}else {
			sender.sendMessage(ChatColor.RED + "This sender does not exist. Available: " + String.join(" ", SENDERS));
			return false;
		}
		message.setSender(newSender);
		sender.sendMessage(ChatColor.GREEN + "You have modified the sender of the message.");
		return true;
	}
	
	@Override
	public List<String> onTabCompleteMessage(CommandSender sender, String[] args, OptionMessages option, String argCmdId) {
		if (args.length == 1) return SENDERS;
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " <sender type> ... : Set the sender of a message";
	}
	
}
