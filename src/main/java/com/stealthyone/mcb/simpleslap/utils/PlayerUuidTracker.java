package com.stealthyone.mcb.simpleslap.utils;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class PlayerUuidTracker {

    private SimpleSlap plugin;

    private YamlFileManager uuidFile;
    private Map<UUID, String> uuidsToNames = new HashMap<>();
    private Map<String, UUID> namesToUuids = new HashMap<>();

    public PlayerUuidTracker(SimpleSlap plugin) {
        this.plugin = plugin;
        uuidFile = new YamlFileManager(plugin.getDataFolder() + File.separator + "playerUUIDs.yml");
        reloadUuids();
    }

    public void reloadUuids() {
        uuidsToNames.clear();
        namesToUuids.clear();
        uuidFile.reloadConfig();

        ConfigurationSection config = uuidFile.getConfig().getConfigurationSection("players");
        if (config != null) {
            for (String rawUuid : config.getKeys(false)) {
                UUID uuid;
                try {
                    uuid = UUID.fromString(rawUuid);
                } catch (Exception ex) {
                    plugin.getLogger().warning("Unable to load player name/UUID mapping from playerUUIDs.yml - invalid UUID '" + rawUuid + "'");
                    continue;
                }

                String name = config.getString(rawUuid);
                uuidsToNames.put(uuid, name);
                namesToUuids.put(name.toLowerCase(), uuid);
            }
        }
    }

    public void saveUuids() {
        ConfigurationSection config = uuidFile.getConfig().createSection("players");

        for (Entry<UUID, String> entry : uuidsToNames.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }

        uuidFile.saveFile();
    }

    public void updatePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();

        if (uuidsToNames.containsKey(uuid)) {
            String oldName = uuidsToNames.get(uuid);
            namesToUuids.remove(oldName.toLowerCase());
        }

        uuidsToNames.put(uuid, name);
        namesToUuids.put(name.toLowerCase(), uuid);
    }

    public String getName(UUID uuid) {
        return uuidsToNames.get(uuid);
    }

    public UUID getUuid(String name) {
        return namesToUuids.get(name.toLowerCase());
    }

}