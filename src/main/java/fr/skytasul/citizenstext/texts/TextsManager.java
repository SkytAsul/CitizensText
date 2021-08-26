package fr.skytasul.citizenstext.texts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.skytasul.citizenstext.CitizensText;

import net.citizensnpcs.api.npc.NPC;

public class TextsManager {
	
	private static final int DATA_VERSION = 2;
	
	private File dataFile;
	private FileConfiguration data;
	private ConfigurationSection textsSection;
	
	public Map<NPC, TextInstance> texts = new HashMap<>();
	
	public TextsManager(File dataFile) {
		this.dataFile = dataFile;
	}
	
	public void load(CitizensText plugin) throws IOException {
		boolean exists = true;
		if (dataFile.createNewFile()) {
			exists = false;
			plugin.getLogger().info("Data file (datas.yml) created");
		}
		data = YamlConfiguration.loadConfiguration(dataFile);
		data.options().header("Do not edit anything here! Everything should be modified in-game.");
		data.options().copyHeader(true);
		int version = 0;
		if (exists) {
			version = data.getInt("dataVersion");
			
			if (data.contains("data")) { // pre-2.0
				plugin.getLogger().warning("Migrating old datas from " + data.getString("lastVersion", null) + " to post-2.0 data format.");
				plugin.getLogger().warning("WARNING - You will loose all player text datas during migration. (text times, etc.)");
				
				for (Map<?, ?> m : data.getMapList("data")) {
					ConfigurationSection section = data.createSection("texts." + Integer.toString((int) m.get("npc")), m);
					section.set("times", null);
					section.set("players", null);
				}
				data.set("data", null);
				data.set("lastVersion", null);
			}
			textsSection = data.getConfigurationSection("texts");
			for (String key : textsSection.getKeys(false)) {
				TextInstance.load(textsSection.getConfigurationSection(key));
			}
			
			plugin.getLogger().info(texts.size() + " texts loaded");
		}
		
		if (version != DATA_VERSION) {
			plugin.getLogger().info("Upgrading data version " + version + " to " + DATA_VERSION);
			data.set("dataVersion", DATA_VERSION);
			data.save(dataFile);
		}
	}
	
	public void disable() {
		new ArrayList<>(texts.values()).forEach(TextInstance::unload);
	}
	
	public Map<NPC, TextInstance> getTexts() {
		return texts;
	}
	
	public TextInstance getText(NPC npc) {
		return texts.get(npc);
	}
	
	public void addText(TextInstance text) {
		texts.put(text.getNPC(), text);
	}
	
	public void removeText(TextInstance text) {
		texts.remove(text.getNPC());
	}
	
	protected void createConfig(TextInstance text) {
		String id = Integer.toString(text.getNPC().getId());
		ConfigurationSection config;
		if (textsSection.isConfigurationSection(id)) {
			CitizensText.getInstance().getLogger().severe("datas.yml already contains a section for newly created NPC instance " + id);
			config = textsSection.getConfigurationSection(id);
		}else config = textsSection.createSection(id);
		
		text.createConfig(config);
		
		try {
			save();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void save() throws IOException {
		data.save(dataFile);
	}
	
}
