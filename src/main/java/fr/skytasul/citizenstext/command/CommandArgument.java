package fr.skytasul.citizenstext.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class CommandArgument {
	
	private final String cmdPermission;
	private final String cmdArgument;
	private final String[] aliases;
	
	protected CommandArgument(String cmdPermission, String cmdArgument, String... aliases) {
		this.cmdPermission = cmdPermission;
		this.cmdArgument = cmdArgument;
		this.aliases = aliases;
	}
	
	/**
	 * Called when the sender executes the command starting with {@link getCmdArgument}.
	 * @param sender Sender which executes the command
	 * @param args Arguments passed
	 */
	public abstract void onCommand(CommandSender sender, String[] args);
	
	/**
	 * Called when the sender attempts to tab-complete the command starting with {@link getCmdArgument}.
	 * @param sender Sender which tab-completes the command
	 * @param args Arguments passed
	 */
	public List<String> onTabComplete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}
	
	/**
	 * Returns a help string starting with the command arguments.
	 * @return a string hepling to understand the command
	 */
	public String getHelpString() {
		return cmdArgument;
	}
	
	public String getCmdArgument() {
		return cmdArgument;
	}
	
	public String[] getCmdAliases() {
		return aliases;
	}
	
	public String getCmdPermission() {
		return cmdPermission;
	}
	
}
