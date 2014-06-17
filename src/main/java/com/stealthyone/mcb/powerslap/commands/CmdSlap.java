package com.stealthyone.mcb.powerslap.commands;

import com.stealthyone.mcb.powerslap.PowerSlap;
import com.stealthyone.mcb.powerslap.config.ConfigHelper;
import com.stealthyone.mcb.powerslap.messages.ErrorMessage;
import com.stealthyone.mcb.powerslap.messages.UsageMessage;
import com.stealthyone.mcb.powerslap.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSlap implements CommandExecutor {

    private PowerSlap plugin;

    public CmdSlap(PowerSlap plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.SLAP.isAllowed(sender, true)) return true;

        if (args.length < 1) {
            UsageMessage.SLAP.sendTo(sender, new QuickMap<>("{LABEL}", label).build());
            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "bypass":
                    ((CmdPowerSlap) plugin.getCommand("powerslap").getExecutor()).cmdBypass(sender);
                    return true;

                case "toggle":
                    ((CmdPowerSlap) plugin.getCommand("powerslap").getExecutor()).cmdToggle(sender);
                    return true;
            }
        }

        int power;
        try {
            power = Integer.parseInt(args[1]);
        } catch (IndexOutOfBoundsException ex) {
            power = ConfigHelper.SLAP_DEFAULT_POWER.get();
        } catch (NumberFormatException ex) {
            ErrorMessage.SLAP_POWER_MUST_BE_INT.sendTo(sender);
            return true;
        }

        String targetName = args[0];
        Player player = Bukkit.getPlayerExact(targetName);
        if (player == null || plugin.getSlapManager().checkVanish(player)) {
            ErrorMessage.UNABLE_TO_FIND_PLAYER.sendTo(sender, new QuickMap<>("{PLAYER}", targetName).build());
            return true;
        }

        plugin.getSlapManager().handleSlap(sender, player, power);

        return true;
    }

}