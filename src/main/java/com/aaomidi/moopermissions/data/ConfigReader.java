package com.aaomidi.moopermissions.data;

import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by Amir on 2015-04-03.
 */
public class ConfigReader {
    private static FileConfiguration config;

    private static String prefix;
    private static String host;
    private static Integer port;
    private static String database;
    private static String username;
    private static String password;

    public ConfigReader(FileConfiguration config) {
        ConfigReader.config = config;

        StringManager.setPrefix(getPrefix());
    }


    public static String getHost() {
        if (host != null) {
            return host;
        }
        return host = config.getString("MySQL-Settings.Hostname");
    }

    public static Integer getPort() {
        if (port != null) {
            return port;
        }
        return port = config.getInt("MySQL-Settings.Port");
    }

    public static String getDatabase() {
        if (database != null) {
            return database;
        }
        return database = config.getString("MySQL-Settings.Database");
    }

    public static String getUsername() {
        if (database != null) {
            return username;
        }
        return username = config.getString("MySQL-Settings.Username");
    }

    public static String getPassword() {
        if (password != null) {
            return password;
        }
        return password = config.getString("MySQL-Settings.Password");
    }


    public static String getPrefix() {
        if (prefix != null) {
            return prefix;
        }

        return prefix = config.getString("Prefix");
    }

    public static void reload() {
        prefix = null;
    }
}
