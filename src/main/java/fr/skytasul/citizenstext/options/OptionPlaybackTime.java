package fr.skytasul.citizenstext.options;

import org.bukkit.configuration.ConfigurationSection;

import fr.skytasul.citizenstext.CitizensTextConfiguration;
import fr.skytasul.citizenstext.texts.TextInstance;

public class OptionPlaybackTime extends TextOption<Integer> {
	
	public OptionPlaybackTime(TextInstance txt) {
		super(txt);
	}
	
	@Override
	public Integer getDefault() {
		return CitizensTextConfiguration.getTimeToPlayback();
	}
	
	@Override
	protected void saveValue(ConfigurationSection config, String key) {
		config.set(key, getValue());
	}
	
	@Override
	protected Integer loadValue(ConfigurationSection config, String key) {
		return config.getInt(key);
	}
	
	@Override
	public void setDefaultValue() {}
	
}
