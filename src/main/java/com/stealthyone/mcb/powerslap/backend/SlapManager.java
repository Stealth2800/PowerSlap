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
package com.stealthyone.mcb.powerslap.backend;

import com.stealthyone.mcb.powerslap.PowerSlap;
import com.stealthyone.mcb.powerslap.config.ConfigHelper;
import com.stealthyone.mcb.powerslap.messages.ErrorMessage;
import com.stealthyone.mcb.powerslap.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.storage.YamlFileManager;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import com.stealthyone.mcb.stbukkitlib.utils.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.kitteh.vanish.VanishPlugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class SlapManager {

    private PowerSlap plugin;

    private Map<Integer, String> slapMessages = new HashMap<>();

    private String equationXvel;
    private String equationYvel;
    private String equationZvel;

    private YamlFileManager tempData;
    private Set<UUID> damageCancellers = new HashSet<>();

    private YamlFileManager playerData;
    private Map<UUID, Boolean> adminBypasses = new HashMap<>();
    private Map<UUID, Boolean> movementCancellers = new HashMap<>();

    public SlapManager(PowerSlap plugin) {
        this.plugin = plugin;
        playerData = new YamlFileManager(plugin.getDataFolder() + File.separator + "data" + File.separator + "playerData.yml");
        tempData = new YamlFileManager(plugin.getDataFolder() + File.separator + "data" + File.separator + "damageCancellers.yml");
        loadData();
    }

    public void save() {
        List<String> list = new ArrayList<>();
        for (UUID uuid : damageCancellers) {
            list.add(uuid.toString());
        }
        tempData.getConfig().set("players", list);
        tempData.saveFile();

        ConfigurationSection moveToggleConf = playerData.getConfig().createSection("movementToggles");
        for (Entry<UUID, Boolean> entry : movementCancellers.entrySet()) {
            moveToggleConf.set(entry.getKey().toString(), entry.getValue());
        }

        ConfigurationSection adminBypassConf = playerData.getConfig().createSection("adminBypasses");
        for (Entry<UUID, Boolean> entry : adminBypasses.entrySet()) {
            adminBypassConf.set(entry.getKey().toString(), entry.getValue());
        }

        playerData.saveFile();
    }

    public void loadData() {
        for (String uuid : tempData.getConfig().getStringList("players")) {
            try {
                damageCancellers.add(UUID.fromString(uuid));
            } catch (Exception ex) {
                plugin.getLogger().warning("Invalid UUID in tempData (" + uuid + ")");
            }
        }
        tempData.getConfig().set("players", null);

        ConfigurationSection moveToggleConf = playerData.getConfig().getConfigurationSection("movementToggles");
        if (moveToggleConf != null) {
            for (String uuid : moveToggleConf.getKeys(false)) {
                try {
                    movementCancellers.put(UUID.fromString(uuid), moveToggleConf.getBoolean(uuid, false));
                } catch (Exception ex) {
                    plugin.getLogger().warning("Invalid UUID in playerData.yml (" + uuid + ")");
                }
            }
        }

        ConfigurationSection adminBypassConf = playerData.getConfig().getConfigurationSection("adminBypasses");
        if (adminBypassConf != null) {
            for (String uuid : adminBypassConf.getKeys(false)) {
                try {
                    adminBypasses.put(UUID.fromString(uuid), adminBypassConf.getBoolean(uuid, false));
                } catch (Exception ex) {
                    plugin.getLogger().warning("Invalid UUID in playerData.yml (" + uuid + ")");
                }
            }
        }

        reloadData();
    }

    public void reloadData() {
        slapMessages.clear();

        FileConfiguration config = plugin.getConfig();

        equationXvel = config.getString("Slap effect.Xvel");
        equationYvel = config.getString("Slap effect.Yvel");
        equationZvel = config.getString("Slap effect.Zvel");

        ScriptEngine js = new ScriptEngineManager().getEngineByName("JavaScript");

        try {
            js.eval(equationXvel.replace("{RANDOM}", Double.toString(1D)).replace("{FORCE}", Integer.toString(1)));
            js.eval(equationYvel.replace("{RANDOM}", Double.toString(1D)).replace("{FORCE}", Integer.toString(1)));
            js.eval(equationZvel.replace("{RANDOM}", Double.toString(1D)).replace("{FORCE}", Integer.toString(1)));
        } catch (Exception ex) {
            plugin.getLogger().severe("Error loading equations for slap effect from config.");
            ex.printStackTrace();
        }

        ConfigurationSection messageSec = config.getConfigurationSection("Slap messages");
        for (String power : messageSec.getKeys(false)) {
            int powerNum;
            try {
                powerNum = Integer.parseInt(power);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "Invalid power '" + power + "' in config. Not an integer.");
                continue;
            }

            String message = messageSec.getString(power);
            slapMessages.put(powerNum, message);
        }
    }

    public boolean checkVanish(Player player) {
        if (!plugin.isHookVanish()) {
            return false;
        } else if (!ConfigHelper.PREVENT_VANISHED_PLAYER_SLAP.get()) {
            return false;
        } else {
            return ((VanishPlugin) Bukkit.getPluginManager().getPlugin("VanishNoPacket")).getManager().isVanished(player);
        }
    }

    private boolean checkMovementBlock(Player player) {
        Boolean value = movementCancellers.get(player.getUniqueId());
        return value == null ? false : value;
    }

    private boolean checkAdminBypass(Player player) {
        Boolean value = adminBypasses.get(player.getUniqueId());
        return value == null ? false : value;
    }

    public boolean toggleMovementBlock(Player player) {
        boolean value = checkMovementBlock(player);
        movementCancellers.put(player.getUniqueId(), !value);
        return !value;
    }

    public boolean toggleAdminBypass(Player player) {
        boolean value = checkAdminBypass(player);
        adminBypasses.put(player.getUniqueId(), !value);
        return !value;
    }

    public void handleSlap(CommandSender sender, CommandSender target, int power) {
        if (power < 0) {
            ErrorMessage.SLAP_INVALID_POWER.sendTo(sender, new QuickMap<>("{POWER}", Integer.toString(power)).build());
            return;
        } else if (!PermissionNode.SLAP_POWER.isAllowed(sender, Integer.toString(power), true)) {
            return;
        } else if (!PermissionNode.SLAP_SELF.isAllowed(sender) && sender.getName().equals(target.getName())) {
            ErrorMessage.SLAP_CANNOT_SLAP_SELF.sendTo(sender);
            return;
        }

        boolean preventFallDmg = ConfigHelper.PREVENT_FALL_DAMAGE.get();
        if (!ConfigHelper.PREVENT_FALL_DAMAGE_SELF.get() && sender.getName().equals(target.getName())) {
            preventFallDmg = false;
        }

        String message = slapMessages.get(power);
        while (message == null && power >= 0) {
            power--;
            message = slapMessages.get(power);
        }

        if (message == null) {
            sender.sendMessage(ChatColor.RED + "An error occurred while fetching the slap message. Please contact an administrator and notify them of this!");
            return;
        }

        if (!plugin.getCooldownManager().handleSlap(sender)) {
            ErrorMessage.SLAP_COOLING_DOWN.sendTo(sender, new QuickMap<>("{TIME}", TimeUtils.translateSeconds(plugin.getCooldownManager().getCooldownTime(sender))).build());
            return;
        }

        if (target instanceof Player && (!checkMovementBlock((Player) target) || checkMovementBlock((Player) target) && sender instanceof Player && checkAdminBypass((Player) sender))) {
            ScriptEngine js = new ScriptEngineManager().getEngineByName("JavaScript");
            Random random = new Random();

            double xVel;
            double yVel;
            double zVel;
            try {
                xVel = (double) js.eval(equationXvel.replace("{RANDOM}", Double.toString(random.nextDouble())).replace("{FORCE}", Integer.toString(power)));
                yVel = (double) js.eval(equationYvel.replace("{RANDOM}", Double.toString(random.nextDouble())).replace("{FORCE}", Integer.toString(power)));
                zVel = (double) js.eval(equationZvel.replace("{RANDOM}", Double.toString(random.nextDouble())).replace("{FORCE}", Integer.toString(power)));
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + "An error occurred while calculating the slap velocity. Please contact an administrator and notify them of this!");
                ex.printStackTrace();
                return;
            }

            Player playerCast = (Player) target;
            playerCast.playSound(playerCast.getLocation(), Sound.HURT_FLESH, 1F, 1.25F);
            playerCast.setVelocity(new Vector(xVel, yVel, zVel));

            if (preventFallDmg && playerCast.getGameMode() != GameMode.CREATIVE) {
                damageCancellers.add(((Player) target).getUniqueId());
            }
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message.replace("{SENDER}", sender.getName()).replace("{TARGET}", target.getName())));
    }

    public boolean isDamageCancelled(Player player) {
        return damageCancellers.remove(player.getUniqueId());
    }

}