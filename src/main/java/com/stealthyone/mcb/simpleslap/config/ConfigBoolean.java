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
package com.stealthyone.mcb.simpleslap.config;

import com.stealthyone.mcb.simpleslap.SimpleSlap;

public enum ConfigBoolean {

    DEBUG("Debug"),

    SLAP_COOLDOWN_ALERT("Slap cooldown.Cooldown alert", true),
    SLAP_COOLDOWN_PAUSE_OFFLINE("Slap cooldown.Pause while offline", true),
    SLAP_COOLDOWN_PAUSE_SERVER_DOWN("Slap cooldown.Pause while server is down", true),

    PREVENT_FALL_DAMAGE("Prevent fall damage", true),
    PREVENT_FALL_DAMAGE_SELF("Prevent fall damage from self"),
    PREVENT_VANISHED_PLAYER_SLAP("Prevent vanished player slap", true);

    private String path;
    private boolean defaultValue;

    private ConfigBoolean(String path) {
        this(path, false);
    }

    private ConfigBoolean(String path, boolean defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public boolean get() {
        return SimpleSlap.getInstance().getConfig().getBoolean(path, defaultValue);
    }

}