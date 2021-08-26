package fr.skytasul.citizenstext.options;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.texts.TextInstance;

public class OptionMessages extends TextOption<List<Message>> {
	
	public OptionMessages(TextInstance txt) {
		super(txt);
	}
	
	@Override
	public List<Message> getDefault() {
		return null;
	}
	
	@Override
	public void setDefaultValue() {
		setValue(new ArrayList<>());
	}
	
	@Override
	public boolean isEmpty() {
		return getValue() == null || getValue().isEmpty();
	}
	
	public void addMessage(String msg) {
		getValue().add(new Message(msg));
	}
	
	public String editMessage(int id, String msg) {
		return getValue().get(id).setText(msg);
	}
	
	public void insertMessage(int id, String msg) {
		getValue().add(id, new Message(msg));
	}
	
	public String removeMessage(int id) {
		return getValue().remove(id).getText();
	}
	
	public int messagesSize() {
		return getValue().size();
	}
	
	public Message getMessage(int id) {
		return getValue().get(id);
	}
	
	public String listMessages() {
		StringJoiner stb = new StringJoiner("\n");
		for (int i = 0; i < getValue().size(); i++) {
			Message msg = getValue().get(i);
			stb.add(ChatColor.AQUA + "" + i + " : "
					+ ChatColor.GREEN + msg.getText()
					+ (msg.getCommands().isEmpty() ? "" : ChatColor.GRAY + " (" + msg.getCommands().size() + " command(s): " + msg.getCommandsList() + "§7)"));
		}
		return stb.toString();
	}
	
	public int clear() {
		int i = getValue().size();
		getValue().clear();
		return i;
	}
	
	@Override
	protected void saveValue(ConfigurationSection config, String key) {
		Map<String, Object> tmp = new HashMap<>();
		for (int i = 0; i < messagesSize(); i++) {
			tmp.put(Integer.toString(i), getMessage(i).serialize());
		}
		config.set(key, tmp);
	}
	
	@Override
	protected List<Message> loadValue(ConfigurationSection config, String key) {
		ConfigurationSection messagesSection = config.getConfigurationSection(key);
		
		return messagesSection.getKeys(false).stream().map(mkey -> {
			Message msg;
			if (messagesSection.isConfigurationSection(mkey)) {
				msg = new Message(messagesSection.getConfigurationSection(mkey));
			}else msg = new Message(messagesSection.getString(mkey));
			return new AbstractMap.SimpleEntry<>(Integer.valueOf(mkey), msg);
		}).sorted((x, y) -> Integer.compare(x.getKey(), y.getKey())).map(Entry::getValue).collect(Collectors.toList());
	}
	
}
