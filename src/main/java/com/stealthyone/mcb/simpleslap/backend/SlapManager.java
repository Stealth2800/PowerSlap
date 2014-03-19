package com.stealthyone.mcb.simpleslap.backend;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import com.stealthyone.mcb.simpleslap.config.ConfigHelper;
import com.stealthyone.mcb.simpleslap.messages.ErrorMessage;
import com.stealthyone.mcb.simpleslap.permissions.PermissionNode;
import com.stealthyone.mcb.simpleslap.utils.TimeUtils;
import com.stealthyone.mcb.simpleslap.utils.YamlFileManager;
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
import java.util.logging.Level;

public class SlapManager {

    private SimpleSlap plugin;

    private Map<Integer, String> slapMessages = new HashMap<>();

    private String equationXvel;
    private String equationYvel;
    private String equationZvel;

    private YamlFileManager tempData;
    private Set<UUID> damageCancellers = new HashSet<>();

    public SlapManager(SimpleSlap plugin) {
        this.plugin = plugin;
        tempData = new YamlFileManager(plugin.getDataFolder() + File.separator + "data" + File.separator + "damageCancellers.yml");
        reloadData();
    }

    public void save() {
        List<String> list = new ArrayList<>();
        for (UUID uuid : damageCancellers) {
            list.add(uuid.toString());
        }
        tempData.getConfig().set("players", list);
        tempData.saveFile();
    }

    public void reloadData() {
        slapMessages.clear();
        damageCancellers.clear();

        tempData.reloadConfig();
        for (String uuid : tempData.getConfig().getStringList("players")) {
            try {
                damageCancellers.add(UUID.fromString(uuid));
            } catch (Exception ex) {
                plugin.getLogger().warning("Invalid UUID in tempData (" + uuid + ")");
            }
        }
        tempData.getConfig().set("players", null);

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

    public void handleSlap(CommandSender sender, CommandSender target, int power) {
        if (power < 0) {
            ErrorMessage.SLAP_INVALID_POWER.sendTo(sender, Integer.toString(power));
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

        if (target instanceof Player) {
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

            if (!plugin.getCooldownManager().handleSlap(sender)) {
                ErrorMessage.SLAP_COOLING_DOWN.sendTo(sender, TimeUtils.translateSeconds(plugin.getCooldownManager().getCooldownTime(sender)));
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