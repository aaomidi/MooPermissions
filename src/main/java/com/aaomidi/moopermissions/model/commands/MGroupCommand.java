package com.aaomidi.moopermissions.model.commands;

import com.aaomidi.moopermissions.MooPermissions;
import org.bukkit.command.CommandSender;

/**
 * Created by amir on 2015-12-18.
 */
public abstract class MGroupCommand extends MCommand {

    public MGroupCommand(String name, String description, String... aliases) {
        super(name, String.format("perms.group.%s", name), description, Type.GROUP, aliases);
    }

    public abstract boolean execute(MooPermissions instance, String groupName, String[] args, CommandSender commandSender);
}
