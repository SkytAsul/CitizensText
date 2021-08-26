package fr.skytasul.citizenstext.options;

import org.bukkit.configuration.ConfigurationSection;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.texts.TextInstance;

public abstract class TextOption<T> {
	
	private final TextInstance txt;
	private final String configKey;
	
	private T value;
	
	protected TextOption(TextInstance txt) {
		this.txt = txt;
		this.configKey = CitizensText.getInstance().getOptionsRegistry().getOptionType(getClass()).getConfigKey();
	}
	
	public final TextInstance getTextInstance() {
		return txt;
	}
	
	public T getValue() {
		return value;
	}
	
	public final T getOrDefault() {
		return value == null ? getDefault() : value;
	}
	
	public boolean isEmpty() {
		return value == null;
	}
	
	public abstract T getDefault();
	
	public void setValue(T value) {
		if (value == getDefault()) {
			if (this.value == null) return;
			this.value = null;
		}else this.value = value;
		// save
	}
	
	public void saveValue() {
		if (txt.getConfigurationSection() != null)
			saveValue(txt.getConfigurationSection(), configKey);
	}
	
	protected abstract void saveValue(ConfigurationSection config, String key);
	
	public void loadValue(ConfigurationSection config) {
		value = loadValue(config, configKey);
	}
	
	protected abstract T loadValue(ConfigurationSection config, String key);
	
	public abstract void setDefaultValue();
	
}
