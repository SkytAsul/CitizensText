package fr.skytasul.citizenstext.options;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import fr.skytasul.citizenstext.texts.TextInstance;

public class TextOptionsRegistry {
	
	private Map<Class<? extends TextOption<?>>, TextOptionType<?>> options = new HashMap<>();
	private Map<String, TextOptionType<?>> configMapping = new HashMap<>();
	
	public TextOptionsRegistry() {
		register(new TextOptionType<>(OptionMessages.class, OptionMessages::new, "messages"));
		register(new TextOptionType<>(OptionName.class, OptionName::new, "customName"));
		register(new TextOptionType<>(OptionRandom.class, OptionRandom::new, "random"));
		register(new TextOptionType<>(OptionRepeat.class, OptionRepeat::new, "repeat"));
	}
	
	public void register(TextOptionType<?> optionType) {
		options.put(optionType.clazz, optionType);
		configMapping.put(optionType.configKey, optionType);
	}
	
	public <T extends TextOption<?>> T createOption(Class<T> clazz, TextInstance txt) {
		return (T) options.get(clazz).createOption(txt);
	}
	
	public <T extends TextOption<?>> TextOptionType<T> getOptionType(Class<T> clazz) {
		return (TextOptionType<T>) options.get(clazz);
	}
	
	public TextOptionType<?> getOptionType(String configKey) {
		return configMapping.get(configKey);
	}
	
	public Collection<TextOptionType<?>> getOptionTypes() {
		return options.values();
	}
	
	public static class TextOptionType<T extends TextOption<?>> {
		
		private Class<T> clazz;
		private Function<TextInstance, T> optionSupplier;
		private String configKey;
		
		public TextOptionType(Class<T> clazz, Function<TextInstance, T> optionSupplier, String configKey) {
			this.clazz = clazz;
			this.optionSupplier = optionSupplier;
			this.configKey = configKey;
		}
		
		public Class<T> getOptionClass() {
			return clazz;
		}
		
		public T createOption(TextInstance txt) {
			return optionSupplier.apply(txt);
		}
		
		public String getConfigKey() {
			return configKey;
		}
		
	}
	
}
