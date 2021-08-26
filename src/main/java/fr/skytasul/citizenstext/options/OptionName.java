package fr.skytasul.citizenstext.options;

import org.bukkit.configuration.ConfigurationSection;

import fr.skytasul.citizenstext.texts.TextInstance;

public class OptionName extends TextOption<String> {
	
	public OptionName(TextInstance txt) {
		super(txt);
	}
	
	@Override
	public String getDefault() {
		return getTextInstance().getNPC().getName();
	}
	
	@Override
	public void setDefaultValue() {}
	
	@Override
	protected void saveValue(ConfigurationSection config, String key) {
		config.set(key, getValue());
	}
	
	@Override
	protected String loadValue(ConfigurationSection config, String key) {
		return config.getString(key);
	}
	
}
