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
package com.stealthyone.mcb.powerslap.messages;

import com.stealthyone.mcb.powerslap.PowerSlap;
import com.stealthyone.mcb.stbukkitlib.messages.Message;
import org.bukkit.command.CommandSender;

import java.util.Map;

public enum UsageMessage {

    SLAP;

    private String path;

    private UsageMessage() {
        this.path = "usages." + toString().toLowerCase();
    }

    public Message getMessage() {
        return PowerSlap.getInstance().getMessageManager().getMessage(path);
    }

    public void sendTo(CommandSender sender) {
        getMessage().sendTo(sender);
    }

    public void sendTo(CommandSender sender, Map<String, String> replacements) {
        getMessage().sendTo(sender, replacements);
    }

}