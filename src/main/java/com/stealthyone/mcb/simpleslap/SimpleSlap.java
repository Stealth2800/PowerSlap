package com.stealthyone.mcb.simpleslap;

import com.stealthyone.mcb.simpleslap.backend.SlapManager;
import com.stealthyone.mcb.simpleslap.commands.CmdSlap;
import com.stealthyone.mcb.simpleslap.config.ConfigHelper;
import com.stealthyone.mcb.simpleslap.listeners.PlayerListener;
import com.stealthyone.mcb.simpleslap.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleSlap extends JavaPlugin {

    private static SimpleSlap instance;

    public static SimpleSlap getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        getDataFolder().mkdir();
    }

    private boolean hookVanish = false;

    private MessageManager messageManager;
    private SlapManager slapManager;

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
        slapManager = new SlapManager(this);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        getCommand("slap").setExecutor(new CmdSlap(this));

        getLogger().info(String.format("%s v%s by Stealth2800 ENABLED.", getName(), getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("%s v%s by Stealth2800 DISABLED.", getName(), getDescription().getVersion()));
        instance = null;
    }

    public void reloadAll() {
        reloadConfig();

    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public SlapManager getSlapManager() {
        return slapManager;
    }

}