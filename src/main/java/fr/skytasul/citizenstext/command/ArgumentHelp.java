package fr.skytasul.citizenstext.command;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ArgumentHelp extends CommandArgument {
	
	private TextCommand command;
	
	public ArgumentHelp(TextCommand command) {
		super(null, "help", "?");
		this.command = command;
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args) {
		StringJoiner joiner = new StringJoiner("\n");
		joiner.add(ChatColor.GREEN + "§m---§r §2§lCitizensText §r§2help §a§m---§r§a");
		
		List<CommandArgument> arguments = new ArrayList<>(command.getArguments());
		List<CommandArgument> argumentsText = arguments.stream().filter(TextCommandArgument.class::isInstance).collect(Collectors.toList());
		argumentsText.forEach(x -> addCommandHelp(joiner, x));
		joiner.add("§a§l -- -   x   - --§a");
		arguments.removeAll(argumentsText);
		arguments.forEach(x -> addCommandHelp(joiner, x));
		
		joiner.add("§m--------------------");
		
		sender.sendMessage(joiner.toString());
	}
	
	private void addCommandHelp(StringJoiner joiner, CommandArgument argument) {
		joiner.add(" /text " + argument.getHelpString());
	}
	
	@Override
	public String getHelpString() {
		return super.getHelpString() + " : Display a list of commands";
	}
	
}
