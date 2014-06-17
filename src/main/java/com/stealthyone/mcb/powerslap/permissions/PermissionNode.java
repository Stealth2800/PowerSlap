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
package com.stealthyone.mcb.powerslap.permissions;

import com.stealthyone.mcb.powerslap.messages.ErrorMessage;
import org.bukkit.command.CommandSender;

public enum PermissionNode {

    ADMIN_RELOAD,
    ADMIN_SAVE,

    SLAP,
    SLAP_ADMIN_BYPASS,
    SLAP_SELF,
    SLAP_TOGGLE;

    private String permission;

    private PermissionNode() {
        permission = "powerslap." + toString().toLowerCase().replace("_", ".");
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

    public final static VariablePermissionNode SLAP_COOLDOWN = VariablePermissionNode.SLAP_COOLDOWN;
    public final static VariablePermissionNode SLAP_POWER = VariablePermissionNode.SLAP;

}