package com.stealthyone.mcb.simpleslap.commands;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import com.stealthyone.mcb.simpleslap.config.ConfigHelper;
import com.stealthyone.mcb.simpleslap.messages.ErrorMessage;
import com.stealthyone.mcb.simpleslap.messages.UsageMessage;
import com.stealthyone.mcb.simpleslap.permissions.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSlap implements CommandExecutor {

    private SimpleSlap plugin;

    public CmdSlap(SimpleSlap plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.SLAP.isAllowed(sender, true)) return true;

        if (args.length < 1) {
            UsageMessage.SLAP.sendTo(sender, label);
            return true;
        } else {
            switch (args[0].toLowerCase()) {
                case "bypass":
                    ((CmdSimpleSlap) plugin.getCommand("simpleslap").getExecutor()).cmdBypass(sender, command, label, args);
                    return true;

                case "toggle":
                    ((CmdSimpleSlap) plugin.getCommand("simpleslap").getExecutor()).cmdToggle(sender, command, label, args);
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
        if (player == null) {
            ErrorMessage.UNABLE_TO_FIND_PLAYER.sendTo(sender, targetName);
            return true;
        }

        if (plugin.getSlapManager().checkVanish(player)) {
            ErrorMessage.UNABLE_TO_FIND_PLAYER.sendTo(sender, targetName);
            return true;
        }

        plugin.getSlapManager().handleSlap(sender, player, power);

        return true;
    }

}