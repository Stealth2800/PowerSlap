package com.stealthyone.mcb.simpleslap.permissions;

import com.stealthyone.mcb.simpleslap.messages.ErrorMessage;
import org.bukkit.command.CommandSender;

public enum PermissionNode {

    SLAP,
    SLAP_SELF;

    private String permission;

    private PermissionNode() {
        permission = "simpleslap." + toString().toLowerCase().replace("_", ".");
    }

    public boolean isAllowed(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public boolean isAllowed(CommandSender sender, boolean alert) {
        boolean result = isAllowed(sender);
        if (!result && alert) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        }
        return result;
    }

    public final static VariablePermissionNode SLAP_POWER = VariablePermissionNode.SLAP;

}