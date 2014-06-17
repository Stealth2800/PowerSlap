package com.stealthyone.mcb.simpleslap.commands;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import com.stealthyone.mcb.simpleslap.messages.ErrorMessage;
import com.stealthyone.mcb.simpleslap.messages.NoticeMessage;
import com.stealthyone.mcb.simpleslap.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSimpleSlap implements CommandExecutor {

    private SimpleSlap plugin;

    public CmdSimpleSlap(SimpleSlap plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            cmdVersion(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "bypass":
                cmdBypass(sender);
                return true;

            case "reload":
                cmdReload(sender);
                return true;

            case "save":
                cmdSave(sender);
                return true;

            case "toggle":
                cmdToggle(sender);
                return true;

            case "version":
                cmdVersion(sender);
                return true;

            default:
                ErrorMessage.UNKNOWN_COMMAND.sendTo(sender, new QuickMap<>("{COMMAND}", args[0]).build());

            case "help":
                sender.sendMessage(ChatColor.RED + "/" + label + " <help|reload|save|version>");
                return true;
        }
    }

    /*
     * Toggle admin bypass mode.
     */
    protected void cmdBypass(CommandSender sender) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
            return;
        } else if (!PermissionNode.SLAP_ADMIN_BYPASS.isAllowed(sender, true)) return;

        boolean newValue = plugin.getSlapManager().toggleAdminBypass((Player) sender);
        NoticeMessage.SLAP_BYPASS_TOGGLED.sendTo(sender, new QuickMap<>("{TOGGLE}", newValue ? (ChatColor.GREEN + "enabled") : (ChatColor.RED + "disabled")).build());
    }

    /*
     * Reload plugin data.
     */
    private void cmdReload(CommandSender sender) {
        if (!PermissionNode.ADMIN_RELOAD.isAllowed(sender, true)) return;

        plugin.reloadAll();
        NoticeMessage.PLUGIN_RELOADED.sendTo(sender);
    }

    /*
     * Toggle slap movement.
     */
    protected void cmdToggle(CommandSender sender) {
        if (!(sender instanceof Player)) {
            ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
            return;
        } else if (!PermissionNode.SLAP_TOGGLE.isAllowed(sender, true)) return;

        boolean newValue = plugin.getSlapManager().toggleMovementBlock((Player) sender);
        NoticeMessage.SLAP_MOVEMENT_TOGGLED.sendTo(sender, new QuickMap<>("{TOGGLE}", newValue ? (ChatColor.RED + "blocking") : (ChatColor.GREEN + "accepting")).build());
    }

    /*
     * Save plugin data.
     */
    private void cmdSave(CommandSender sender) {
        if (!PermissionNode.ADMIN_SAVE.isAllowed(sender, true)) return;

        plugin.saveAll();
        NoticeMessage.PLUGIN_SAVED.sendTo(sender);
    }

    /*
     * Show plugin version.
     */
    private void cmdVersion(CommandSender sender) {
        sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "SimpleSlap" + ChatColor.GOLD + " v" + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GOLD + "Created by Stealth2800");
        sender.sendMessage(ChatColor.BLUE + "http://stealthyone.com/bukkit");
    }

}