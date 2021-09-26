package fr.skytasul.citizenstext.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public abstract class CommandArgument {
	
	private final String cmdPermission;
	private final String cmdArgument;
	private final String[] aliases;
	
	TextComponent helpComponent;
	
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
	 * Returns a string made of the command syntax and a brief description.
	 * @return a string helping to understand the command
	 */
	public final String getHelpString() {
		return getHelpSyntax() + "§7:§f " + getHelpDescription();
	}
	
	/**
	 * Returns the syntax of this command.
	 * @return syntax
	 */
	protected String getHelpSyntax() {
		return cmdArgument;
	}
	
	/**
	 * Returns a brief description of the command.
	 * @return command description
	 */
	protected abstract String getHelpDescription();
	
	TextComponent getCachedHelpComponent() {
		if (helpComponent == null) {
			helpComponent = new TextComponent(" /text " + getHelpString() + "\n");
			helpComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/text " + getCmdArgument()));
			helpComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to suggest §a§l/text " + getCmdArgument())));
		}
		return helpComponent;
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
