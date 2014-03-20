package com.stealthyone.mcb.simpleslap;

import com.stealthyone.mcb.simpleslap.backend.SlapManager;
import com.stealthyone.mcb.simpleslap.backend.cooldowns.CooldownManager;
import com.stealthyone.mcb.simpleslap.commands.CmdSimpleSlap;
import com.stealthyone.mcb.simpleslap.commands.CmdSlap;
import com.stealthyone.mcb.simpleslap.config.ConfigHelper;
import com.stealthyone.mcb.simpleslap.listeners.PlayerListener;
import com.stealthyone.mcb.simpleslap.utils.MessageManager;
import com.stealthyone.mcb.simpleslap.utils.PlayerUuidTracker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SimpleSlap extends JavaPlugin {

    private static SimpleSlap instance;

    public static SimpleSlap getInstance() {
        return instance;
    }

    private boolean hookVanish = false;

    private MessageManager messageManager;
    private PlayerUuidTracker uuidTracker;

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
        uuidTracker = new PlayerUuidTracker(this);

        cooldownManager = new CooldownManager(this);
        slapManager = new SlapManager(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("simpleslap").setExecutor(new CmdSimpleSlap(this));
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
        uuidTracker.saveUuids();
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

    public PlayerUuidTracker getUuidTracker() {
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