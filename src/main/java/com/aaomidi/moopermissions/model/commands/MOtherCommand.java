package com.aaomidi.moopermissions.model.commands;

import com.aaomidi.moopermissions.MooPermissions;
import org.bukkit.command.CommandSender;

/**
 * Created by amir on 2015-12-24.
 */
public abstract class MOtherCommand extends MCommand {
    public MOtherCommand(String name, String description, String... aliases) {
        super(name, String.format("perms.other.%s", name), description, Type.OTHER, aliases);
    }

    public abstract boolean execute(MooPermissions instance, String[] args, CommandSender commandSender);

}
