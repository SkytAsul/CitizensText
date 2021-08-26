package fr.skytasul.citizenstext.players;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import fr.skytasul.citizenstext.CitizensText;
import fr.skytasul.citizenstext.texts.TextInstance;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class CTPlayer {
	
	private final Map<TextInstance, CTPlayerText> texts = new HashMap<>();
	private final UUID uuid;
	
	public CTPlayer(UUID uuid) {
		this.uuid = uuid;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public CTPlayerText getText(TextInstance txt) {
		return texts.computeIfAbsent(txt, text -> new CTPlayerText(this, text));
	}
	
	public void setConfigObject(String key, Object value) {
		CitizensText.getInstance().getPlayers().setConfigObject("players." + uuid.toString() + "." + key, value);
	}
	
	public void load(ConfigurationSection config) {
		for (String key : config.getKeys(false)) {
			int id = Integer.parseInt(key);
			NPC npc = CitizensAPI.getNPCRegistry().getById(id);
			if (npc != null) {
				TextInstance txt = CitizensText.getInstance().getTexts().getText(npc);
				CTPlayerText text = new CTPlayerText(this, txt);
				text.load(config.getConfigurationSection(key));
				texts.put(txt, text);
			}
		}
	}
	
	public static synchronized CTPlayer getPlayer(Player player) {
		return CitizensText.getInstance().getPlayers().getPlayer(player);
	}
	
}
