package com.aaomidi.moopermissions.commands.othercommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupFileRegistry;
import com.aaomidi.moopermissions.model.commands.MOtherCommand;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.command.CommandSender;

/**
 * Created by amir on 2015-12-25.
 */
public class ReloadGroupsCommand extends MOtherCommand {
    public ReloadGroupsCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public boolean execute(MooPermissions instance, String[] args, CommandSender commandSender) {
        GroupFileRegistry.reset();
        instance.getDataManager().reset();
        instance.getCacheManager().resetAllPlayers();
        StringManager.sendMessage(commandSender, "&bReloaded group files.");
        return true;
    }
}
