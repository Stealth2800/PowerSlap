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
package com.stealthyone.mcb.powerslap;

import com.stealthyone.mcb.powerslap.backend.SlapManager;
import com.stealthyone.mcb.powerslap.backend.cooldowns.CooldownManager;
import com.stealthyone.mcb.powerslap.commands.CmdPowerSlap;
import com.stealthyone.mcb.powerslap.commands.CmdSlap;
import com.stealthyone.mcb.powerslap.config.ConfigHelper;
import com.stealthyone.mcb.powerslap.listeners.PlayerListener;
import com.stealthyone.mcb.stbukkitlib.messages.MessageManager;
import com.stealthyone.mcb.stbukkitlib.players.PlayerUUIDTracker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PowerSlap extends JavaPlugin {

    private static PowerSlap instance;

    public static PowerSlap getInstance() {
        return instance;
    }

    private boolean hookVanish = false;

    private MessageManager messageManager;
    private PlayerUUIDTracker uuidTracker;

    private CooldownManager cooldownManager;
    private SlapManager slapManager;

    @Override
    public void onLoad() {
        instance = this;
        getDataFolder().mkdir();
        new File(getDataFolder() + File.separator + "data").mkdir();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(false);
        saveConfig();

        /* Check hooks */
        try {
            Class.forName("org.kitteh.vanish.VanishPlugin");
            Plugin vanishPlugin = Bukkit.getPluginManager().getPlugin("VanishNoPacket");
            if (vanishPlugin != null) {
                hookVanish = true;
                getLogger().info("Found dependency: VanishNoPacket v" + vanishPlugin.getDescription().getVersion());
            }
        } catch (Exception ex) {
            getLogger().info("Unable to find optional dependency: VanishNoPacket.");
        }

        if (ConfigHelper.PREVENT_VANISHED_PLAYER_SLAP.get()) {
            if (!hookVanish) {
                getLogger().warning("Config is set to prevent vanished players from being slapped but VanishNoPacket is not installed!");
            } else {
                getLogger().info("Preventing vanished players from being slapped: ENABLED.");
            }
        } else {
            getLogger().info("Preventing vanished players from being slapped: DISABLED.");
        }

        messageManager = new MessageManager(this);
        uuidTracker = new PlayerUUIDTracker(this);

        cooldownManager = new CooldownManager(this);
        slapManager = new SlapManager(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("powerslap").setExecutor(new CmdPowerSlap(this));
        getCommand("slap").setExecutor(new CmdSlap(this));

        getLogger().info(String.format("%s v%s by Stealth2800 ENABLED.", getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        saveAll();
        getLogger().info(String.format("%s v%s by Stealth2800 DISABLED.", getName(), getDescription().getVersion()));
        instance = null;
    }

    public void saveAll() {
        saveConfig();
        slapManager.save();
        cooldownManager.saveCooldowns();
        uuidTracker.save();
    }

    public void reloadAll() {
        reloadConfig();
        messageManager.reloadMessages();
        slapManager.reloadData();
        cooldownManager.reloadCooldowns();
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public PlayerUUIDTracker getUuidTracker() {
        return uuidTracker;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public SlapManager getSlapManager() {
        return slapManager;
    }

    public boolean isHookVanish() {
        return hookVanish;
    }

}