/*
 * PowerSlap - Slapping plugin for players to abuse each other with
 * Copyright (C) 2013 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com/bukkit>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stealthyone.mcb.powerslap.backend.cooldowns;

import com.stealthyone.mcb.powerslap.PowerSlap;
import com.stealthyone.mcb.powerslap.config.ConfigHelper;
import com.stealthyone.mcb.powerslap.messages.NoticeMessage;
import com.stealthyone.mcb.powerslap.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.storage.YamlFileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class CooldownManager {

    private PowerSlap plugin;

    private YamlFileManager cooldownFile;
    private Map<UUID, Cooldown> playerCooldowns = new HashMap<>();

    private List<Integer> cooldownIntervals = new ArrayList<>();

    private boolean reloading = false;
    private boolean offlinePause;

    public CooldownManager(PowerSlap plugin) {
        this.plugin = plugin;
        cooldownFile = new YamlFileManager(plugin.getDataFolder() + File.separator + "data" + File.separator + "cooldowns.yml");
        reloadCooldowns();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new CooldownCounter(), 20L, 20L);
    }

    public void reloadCooldowns() {
        reloading = true;
        playerCooldowns.clear();
        cooldownFile.reloadConfig();

        offlinePause = ConfigHelper.SLAP_COOLDOWN_PAUSE_OFFLINE.get();
        cooldownIntervals = plugin.getConfig().getIntegerList("Slap cooldown.Intervals");

        FileConfiguration config = cooldownFile.getConfig();
        for (String rawUuid : config.getKeys(false)) {
            UUID uuid;
            try {
                uuid = UUID.fromString(rawUuid);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Unable to load cooldown from Cooldown.yml, invalid UUID (" + rawUuid + ")");
                continue;
            }

            Cooldown cooldown = new Cooldown(config.getInt(rawUuid));
            playerCooldowns.put(uuid, cooldown);
        }
        reloading = false;
    }

    public void saveCooldowns() {
        FileConfiguration cdConfig = cooldownFile.getConfig();
        for (String oldKey : cdConfig.getKeys(false)) {
            cdConfig.set(oldKey, null);
        }

        for (Entry<UUID, Cooldown> entry : playerCooldowns.entrySet()) {
            cdConfig.set(entry.getKey().toString(), entry.getValue().getTime());
        }

        cooldownFile.saveFile();
    }

    public int getCooldownInterval(Player player) {
        if (cooldownIntervals.size() == 0) {
            return -1;
        } else if (PermissionNode.SLAP_COOLDOWN.isAllowed(player, "exempt")) {
            return -1;
        }

        for (Integer cooldown : cooldownIntervals) {
            if (PermissionNode.SLAP_COOLDOWN.isAllowed(player, Integer.toString(cooldown))) {
                return cooldown;
            }
        }
        return cooldownIntervals.get(cooldownIntervals.size() - 1);
    }

    public boolean handleSlap(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (getCooldownTime(sender) > -1) {
            return false;
        }

        int cdTime = getCooldownInterval((Player) sender);
        if (cdTime != -1) {
            playerCooldowns.put(((Player) sender).getUniqueId(), new Cooldown(cdTime));
        }
        return true;
    }

    public int getCooldownTime(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return -1;
        }

        Cooldown cooldown = playerCooldowns.get(((Player) sender).getUniqueId());
        if (cooldown == null) {
            return -1;
        } else {
            return cooldown.getTime();
        }
    }

    private class CooldownCounter implements Runnable {

        @Override
        public void run() {
            if (reloading) return;

            Iterator<Entry<UUID, Cooldown>> it = playerCooldowns.entrySet().iterator();
            while (it.hasNext()) {
                Entry<UUID, Cooldown> entry = it.next();
                Player player = Bukkit.getPlayerExact(plugin.getUuidTracker().getName(entry.getKey()));
                if (player == null && offlinePause) {
                    continue;
                }

                if (entry.getValue().countdown() == -1) {
                    if (player != null && ConfigHelper.SLAP_COOLDOWN_ALERT.get()) {
                        NoticeMessage.SLAP_COOLDOWN_ENDED.sendTo(player);
                    }
                    it.remove();
                }
            }
        }

    }

}