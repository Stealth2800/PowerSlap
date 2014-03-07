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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlFileManager {

    private File file;
    private FileConfiguration config;

    public YamlFileManager(String filePath) {
        Validate.notNull(filePath, "FilePath cannot be null");

        file = new File(filePath);
        reloadConfig();
    }

    public YamlFileManager(File file) {
        Validate.notNull(file, "File cannot be null");

        this.file = file;
        reloadConfig();
    }

    public void reloadConfig() {
        if (!file.exists()) {
            try {
                file.createNewFile();
                saveFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void saveFile() {
        if (file == null || config == null) {
            reloadConfig();
        } else {
            try {
                config.save(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    public boolean isEmpty() {
        return config.getKeys(false).size() == 0;
    }

    public void copyDefaults(FileConfiguration otherConfig) {
        Validate.notNull(otherConfig, "OtherConfig cannot be null");

        config.addDefaults(otherConfig);
        config.options().copyDefaults(true);
        saveFile();
    }

}