package fr.skytasul.citizenstext.players;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import fr.skytasul.citizenstext.CitizensText;

public class CTPlayersManager {
	
	private Map<UUID, CTPlayer> players = new HashMap<>();
	
	private File file;
	private YamlConfiguration config;
	
	private BukkitTask task;
	
	public CTPlayersManager(CitizensText plugin, File file) throws IOException {
		this.file = file;
		if (file.createNewFile()) {
			config = new YamlConfiguration();
			config.options().header("Do not edit anything here! Everything should be modified in-game.");
			config.options().copyHeader(true);
			config.createSection("players");
			config.save(file);
		}else {
			config = YamlConfiguration.loadConfiguration(file);
			
			ConfigurationSection playersSection = config.getConfigurationSection("players");
			for (String key : playersSection.getKeys(false)) {
				try {
					UUID uuid = UUID.fromString(key);
					CTPlayer player = new CTPlayer(uuid);
					player.load(playersSection.getConfigurationSection(key));
					players.put(uuid, player);
				}catch (Exception ex) {
					plugin.getLogger().severe("An error ocurred while loading " + key + " datas");
					ex.printStackTrace();
				}
			}
		}
	}
	
	public void setConfigObject(String key, Object value) {
		config.set(key, value);
		if (task == null) {
			task = Bukkit.getScheduler().runTaskLaterAsynchronously(CitizensText.getInstance(), () -> {
				task = null;
				save();
			}, 15 * 20);
		}
	}
	
	private void save() {
		try {
			config.save(file);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disable() {
		if (task != null) {
			task.cancel();
			task = null;
			save();
		}
	}
	
	public synchronized CTPlayer getPlayer(Player player) {
		CTPlayer ctPlayer = players.get(player.getUniqueId());
		if (ctPlayer == null) {
			ctPlayer = new CTPlayer(player.getUniqueId());
			players.put(player.getUniqueId(), ctPlayer);
		}
		return ctPlayer;
	}
	
}
