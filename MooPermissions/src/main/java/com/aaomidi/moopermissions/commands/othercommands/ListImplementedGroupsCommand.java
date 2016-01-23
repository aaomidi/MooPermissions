package com.aaomidi.moopermissions.commands.othercommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupFileRegistry;
import com.aaomidi.moopermissions.model.commands.MOtherCommand;
import com.aaomidi.moopermissions.model.perms.server.Group;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by amir on 2015-12-24.
 */
public class ListImplementedGroupsCommand extends MOtherCommand {
    public ListImplementedGroupsCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public boolean execute(MooPermissions instance, String[] args, CommandSender commandSender) {
        StringBuilder sb = new StringBuilder("&bThe list of implemented groups are as following: ");
        int i = 1;
        ArrayList<Group> groups = new ArrayList<>(GroupFileRegistry.getGroups().values());
        Collections.sort(groups, (o1, o2) -> ((Integer) o1.getPriority()).compareTo(o2.getPriority()));

        for (Group group : groups) {
            sb.append(String.format("\n&5%d. &3%s&b(&3%s&b) - &3%d %s", i++, group.getName(), group.getDisplayName(), group.getPriority(), group.isDefault() ? "&b- &3Default" : ""));
        }

        StringManager.sendMessage(commandSender, sb.toString());
        return true;
    }
}
