/*
 * StBukkitLib
 * Copyright (C) 2014 Stealth2800 <stealth2800@stealthyone.com>
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
package com.stealthyone.mcb.simpleslap.utils;

import org.apache.commons.lang.Validate;

public class TimeUtils {

    public static enum TimeFormat {

        LONG,
        SHORT;

    }

    public static String translateSeconds(int seconds) {
        return translateSeconds(seconds, TimeFormat.LONG);
    }

    public static String translateSeconds(int seconds, TimeFormat format) {
        Validate.notNull(format);
        if (seconds < 0)
            throw new IllegalArgumentException("Seconds cannot be negative! (" + seconds + ")");
        StringBuilder returnString = new StringBuilder();
        int minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        int hours = minutes / 60;
        minutes = minutes - hours * 60;
        int days = hours / 24;
        hours = hours - days * 24;

        if (format == TimeFormat.LONG) {
            if (days > 0) {
                returnString.append(days).append(days == 1 ? " day" : " days");
            }
            if (hours > 0) {
                if (days > 0)
                    returnString.append(" ");
                returnString.append(hours).append(hours == 1 ? " hour" : " hours");
            }
            if (minutes > 0) {
                if (hours > 0)
                    returnString.append(" ");
                returnString.append(minutes).append(minutes == 1 ? " minute" : " minutes");
            }
            if (seconds > 0 || seconds == 0 && minutes == 0 && hours == 0 && days == 0) {
                if (minutes > 0)
                    returnString.append(" ");
                returnString.append(seconds).append(seconds == 1 ? " second" : " seconds");
            }

        } else if (format == TimeFormat.SHORT) {
            if (days > 0) {
                returnString.append(Integer.toString(days).length() == 1 ? "0" : "" + days);
            }
            if (hours > 0) {
                if (days > 0)
                    returnString.append(":");
                returnString.append(Integer.toString(hours).length() == 1 ? "0" : "" + hours);
            }

            if (hours > 0) {
                returnString.append(":").append(Integer.toString(minutes).length() == 1 ? "0" : "").append(minutes);
            } else {
                returnString.append(minutes);
            }

            returnString.append(":").append(Integer.toString(seconds).length() == 1 ? "0" : "").append(seconds);
        }

        return returnString.toString();
    }

}