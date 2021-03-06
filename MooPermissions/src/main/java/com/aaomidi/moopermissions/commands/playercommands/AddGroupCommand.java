package com.aaomidi.moopermissions.commands.playercommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupFileRegistry;
import com.aaomidi.moopermissions.model.commands.MPlayerCommand;
import com.aaomidi.moopermissions.model.perms.player.MPlayer;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by amir on 2015-12-25.
 */
public class AddGroupCommand extends MPlayerCommand {
    public AddGroupCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public boolean execute(MooPermissions instance, String playerName, String[] args, CommandSender commandSender) {
        Player player = Bukkit.getPlayer(playerName);
        MPlayer mPlayer = null;

        if (args.length == 0) {
            return false;
        }

        if (GroupFileRegistry.getGroup(args[0]) == null) {
            StringManager.sendMessage(commandSender, "&c%s is not implemented on the server.", args[0]);
            return true;
        }

        if (player == null) {
            mPlayer = instance.getCacheManager().getPlayer(playerName);
        } else {
            mPlayer = instance.getCacheManager().getPlayer(player);
        }

        if (mPlayer == null) {
            StringManager.sendMessage(commandSender, "&cThat player was not recognized.");
            return true;
        }

        long time = -2;
        if (args.length >= 2) {
            try {
                time = Long.valueOf(args[1]);
            } catch (Exception ex) {
                time = -2;
            }
        }

        // If time was negative, make it unlimited.
        if (time <= 0) {
            time = 0;
        } else {
            time = (System.currentTimeMillis()) + (time * 1000); // Expiration time.
        }

        if (mPlayer.addToGroup(args[0], time)) {
            StringManager.sendMessage(commandSender, "&bSuccessfully added &3%s &bto &3%s", mPlayer.getName(), args[0]);
        } else {
            StringManager.sendMessage(commandSender, "&bProblem when adding &3%s &bto &3%s", mPlayer.getName(), args[0]);

        }

        return true;

    }
}
