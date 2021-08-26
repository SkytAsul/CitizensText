package fr.skytasul.citizenstext.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.texts.TextInstance;

public class TextSendEvent extends PlayerEvent implements Cancellable {
	
	private TextInstance text;
	private Message message;
	
	private boolean cancelled = false;
	
	public TextSendEvent(Player receiver, TextInstance text, Message message) {
		super(receiver);
		this.text = text;
		this.message = message;
	}
	
	public TextInstance getText() {
		return text;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public void setMessage(Message message) {
		this.message = message;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	private static HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public HandlerList getHandlerList() {
		return handlers;
	}
	
}
