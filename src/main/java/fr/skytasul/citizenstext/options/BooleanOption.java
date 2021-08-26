package fr.skytasul.citizenstext.options;

import org.bukkit.configuration.ConfigurationSection;

import fr.skytasul.citizenstext.texts.TextInstance;

public abstract class BooleanOption extends TextOption<Boolean> {
	
	protected BooleanOption(TextInstance txt) {
		super(txt);
	}
	
	public boolean toggle() {
		setValue(!getOrDefault());
		return getOrDefault();
	}
	
	@Override
	protected void saveValue(ConfigurationSection config, String key) {
		config.set(key, getValue());
	}
	
	@Override
	protected Boolean loadValue(ConfigurationSection config, String key) {
		return config.getBoolean(key);
	}
	
}
