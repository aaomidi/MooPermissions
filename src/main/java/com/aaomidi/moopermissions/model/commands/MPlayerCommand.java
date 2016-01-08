package com.aaomidi.moopermissions.model.commands;

import com.aaomidi.moopermissions.MooPermissions;
import org.bukkit.command.CommandSender;

/**
 * Created by amir on 2015-12-17.
 */
public abstract class MPlayerCommand extends MCommand {

    public MPlayerCommand(String name, String description, String... aliases) {
        super(name, String.format("perms.player.%s", name), description, Type.PLAYER, aliases);
    }

    public abstract boolean execute(MooPermissions instance, String playerName, String[] args, CommandSender commandSender);
}
