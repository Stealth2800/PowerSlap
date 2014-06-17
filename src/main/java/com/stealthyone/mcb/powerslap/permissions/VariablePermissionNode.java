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

public enum VariablePermissionNode {

    SLAP,
    SLAP_COOLDOWN;

    private String permission;

    private VariablePermissionNode() {
        permission = "powerslap." + toString().toLowerCase().replace("_", ".");
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