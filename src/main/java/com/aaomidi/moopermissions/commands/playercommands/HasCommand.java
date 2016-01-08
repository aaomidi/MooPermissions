package com.aaomidi.moopermissions.commands.playercommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.model.commands.MPlayerCommand;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by amir on 2015-12-23.
 */
public class HasCommand extends MPlayerCommand {

    public HasCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public boolean execute(MooPermissions instance, String playerName, String[] args, CommandSender commandSender) {
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            StringManager.sendMessage(commandSender, "&cThat player was not found.");
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        String permission = "";
        for (String arg : args) {
            permission += arg + " ";
        }
        permission = permission.trim();

        boolean result = player.hasPermission(permission);

        StringManager.sendMessage(commandSender, "&3%s &b%s permission &3%s", player.getName(), result ? "has" : "does not have", permission);
        return true;
    }
}
