package com.stealthyone.mcb.simpleslap.permissions;

import com.stealthyone.mcb.simpleslap.messages.ErrorMessage;
import org.bukkit.command.CommandSender;

public enum VariablePermissionNode {

    SLAP,
    SLAP_COOLDOWN;

    private String permission;

    private VariablePermissionNode() {
        permission = "simpleslap." + toString().toLowerCase().replace("_", ".");
    }

    public boolean isAllowed(CommandSender sender, String variable) {
        return sender.hasPermission(permission + "." + variable.toLowerCase());
    }

    public boolean isAllowed(CommandSender sender, String variable, boolean alert) {
        boolean result = isAllowed(sender, variable);
        if (!result && alert) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        }
        return result;
    }

}