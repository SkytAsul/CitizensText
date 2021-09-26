package fr.skytasul.citizenstext.command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.TextComponent;

public class ArgumentHelp extends CommandArgument {
	
	private TextCommand command;
	
	public ArgumentHelp(TextCommand command) {
		super(null, "help", "?");
		this.command = command;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		TextComponent text = new TextComponent();
		text.setColor(net.md_5.bungee.api.ChatColor.GREEN);
		text.addExtra(new TextComponent("§m---§r §2§lCitizensText §r§2help §a§m---§r§a\n"));
		
		List<CommandArgument> arguments = new ArrayList<>(command.getArguments());
		List<CommandArgument> argumentsText = arguments.stream().filter(TextCommandArgument.class::isInstance).collect(Collectors.toList());
		argumentsText.forEach(x -> text.addExtra(x.getCachedHelpComponent()));
		text.addExtra("§l -- -   §2§lx§a§l   - --§a\n");
		arguments.removeAll(argumentsText);
		arguments.forEach(x -> text.addExtra(x.getCachedHelpComponent()));
		
		text.addExtra("§m------------------------");
		
		sender.spigot().sendMessage(text);
	}
	
	@Override
	public String getHelpDescription() {
		return "Display a list of commands";
	}
	
}
