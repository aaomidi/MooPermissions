package com.aaomidi.moopermissions.utils;


import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by amir on 2015-11-29.
 */
public class StringManager {
    @Setter
    private static String prefix = "[MooPermissions] ";
    @Setter
    private static Logger logger;

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void log(Level level, String s, Object... format) {
        String msg = String.format("%s: %s", prefix, String.format(s, format));
        logger.log(level, msg);
    }

    public static void log(String s) {
        String msg = prefix + ": " + s;
        System.out.print(msg);
    }

    public static void sendMessage(CommandSender c, String s, Object... format) {
        String message = String.format(prefix + s, format);

        c.sendMessage(colorize(message));
    }
}
