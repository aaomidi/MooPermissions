package com.aaomidi.moopermissions.commands.othercommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupIndexRegistry;
import com.aaomidi.moopermissions.model.SQLGroup;
import com.aaomidi.moopermissions.model.commands.MCommand;
import com.aaomidi.moopermissions.model.commands.MOtherCommand;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.command.CommandSender;

/**
 * Created by amir on 2015-12-24.
 */
public class ListRegisteredGroupsCommand extends MOtherCommand {
    public ListRegisteredGroupsCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public boolean execute(MooPermissions instance, String[] args, CommandSender commandSender) {
        StringBuilder sb = new StringBuilder("&bThe list of registered groups are as following: ");
        int i = 1;

        for (SQLGroup group : GroupIndexRegistry.getRegisteredGroups()) {
            String creation = MCommand.DATE_FORMAT.format(group.getCreation());
            sb.append(String.format("\n&5%d. &3%s&b(&3%d&b) - Created on &3%s", i++, group.getName(), group.getId(), creation));
        }

        StringManager.sendMessage(commandSender, sb.toString());
        return true;
    }
}
