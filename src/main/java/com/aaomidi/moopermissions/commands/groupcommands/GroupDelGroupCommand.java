package com.aaomidi.moopermissions.commands.groupcommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupIndexRegistry;
import com.aaomidi.moopermissions.model.commands.MGroupCommand;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.command.CommandSender;

/**
 * Created by amir on 2015-12-25.
 */
public class GroupDelGroupCommand extends MGroupCommand {
    public GroupDelGroupCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public boolean execute(MooPermissions instance, String groupName, String[] args, CommandSender commandSender) {
        Integer gid = GroupIndexRegistry.get(groupName);
        if (gid == null) {
            StringManager.sendMessage(commandSender, "&cThat group was not recognized!");
            return true;
        }

        if (instance.getMySQL().deleteGroup(gid)) {
            StringManager.sendMessage(commandSender, "&bSuccessfully removed that group.");
        } else {
            StringManager.sendMessage(commandSender, "&cProblem when removing group!");
        }
        return true;
    }
}
