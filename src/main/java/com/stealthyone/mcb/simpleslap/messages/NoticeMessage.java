/*
 * Chatomizer - Advanced chat plugin with endless possibilities
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
package com.stealthyone.mcb.simpleslap.messages;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import org.bukkit.command.CommandSender;

public enum NoticeMessage {

    FORMAT_DEFAULT_NOTICE,
    FORMAT_DEFAULT_SET,
    FORMAT_NOTICE,
    FORMAT_SET,

    PLUGIN_RELOADED,
    PLUGIN_SAVED;

    private String path;

    private NoticeMessage() {
        this.path = "notices." + toString().toLowerCase();
    }

    public String getMessagePath() {
        return path;
    }

    public String getMessage() {
        return SimpleSlap.getInstance().getMessageManager().getMessage(path);
    }

    public String getMessage(String... replacements) {
        return SimpleSlap.getInstance().getMessageManager().getMessage(path, replacements);
    }

    public void sendTo(CommandSender sender) {
        sender.sendMessage(getMessage().split("\n"));
    }

    public void sendTo(CommandSender sender, String... replacements) {
        sender.sendMessage(getMessage(replacements).split("\n"));
    }

}