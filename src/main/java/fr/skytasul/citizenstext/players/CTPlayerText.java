package fr.skytasul.citizenstext.players;

import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import fr.skytasul.citizenstext.texts.TextInstance;

public class CTPlayerText {
	
	private final CTPlayer player;
	private final TextInstance txt;
	
	private int msgIndex = -1;
	private long time = 0;
	private long resetTime = 0;
	private BukkitTask next = null;
	
	public CTPlayerText(CTPlayer player, TextInstance txt) {
		this.player = player;
		this.txt = txt;
	}
	
	public int getMessageIndex() {
		return msgIndex;
	}
	
	public boolean hasStarted() {
		return msgIndex >= 0;
	}
	
	public void setMessage(int msgIndex) {
		this.msgIndex = msgIndex;
		setConfigObject("msgId", msgIndex, -1);
	}
	
	public void removeMessage() {
		setMessage(-1);
	}
	
	public long getTime() {
		return time;
	}
	
	public boolean hasTime() {
		return time != 0;
	}
	
	public void setTime(long time) {
		this.time = time;
		setConfigObject("time", time, 0);
	}
	
	public void removeTime() {
		setTime(0);
	}
	
	public void setNoRepeat() {
		setTime(-1);
	}
	
	public boolean canRepeat() {
		return time != -1;
	}
	
	public long getResetTime() {
		return resetTime;
	}
	
	public boolean hasResetTime() {
		return resetTime != 0;
	}
	
	public void setResetTime(long resetTime) {
		this.resetTime = resetTime;
		setConfigObject("resetTime", resetTime, 0);
	}
	
	public void removeResetTime() {
		setResetTime(0);
	}
	
	public BukkitTask getNextMessageTask() {
		return next;
	}
	
	public boolean hasNextMessageTask() {
		return next != null;
	}
	
	public void setNextMessageTask(BukkitTask next) {
		this.next = next;
	}
	
	public void removeNextMessageTask() {
		if (next != null) {
			if (!next.isCancelled()) next.cancel();
			next = null;
		}
	}

	private void setConfigObject(String key, Object object, Object defaultObject) {
		player.setConfigObject(txt.getNPC().getId() + "." + key, Objects.equals(object, defaultObject) ? null : object);
	}
	
	public void load(ConfigurationSection config) {
		if (config.contains("msgId")) msgIndex = config.getInt("msgId");
		if (config.contains("time")) time = config.getLong("time");
		if (config.contains("resetTime")) resetTime = config.getLong("resetTime");
	}
	
}
