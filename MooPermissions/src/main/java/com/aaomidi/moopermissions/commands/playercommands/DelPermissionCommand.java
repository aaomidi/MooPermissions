package com.aaomidi.moopermissions.commands.playercommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.model.commands.MPlayerCommand;
import com.aaomidi.moopermissions.model.perms.player.MPlayer;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by amir on 2015-12-23.
 */
public class DelPermissionCommand extends MPlayerCommand {

    public DelPermissionCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public boolean execute(MooPermissions instance, String playerName, String[] args, CommandSender commandSender) {
        Player player = Bukkit.getPlayer(playerName);
        MPlayer mPlayer = null;

        if (args.length == 0) {
            return false;
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

        if (mPlayer.getPermission(args[0]) == null) {
            StringManager.sendMessage(commandSender, "&cThat player does not have that permission.");
            return true;
        }

        if (mPlayer.removePermission(args[0])) {
            StringManager.sendMessage(commandSender, "&bSuccessfully removed &3%s &bfrom &3%s", args[0], mPlayer.getName());
        } else {
            StringManager.sendMessage(commandSender, "&bProblem when removing &3%s &bfrom &3%s", args[0], mPlayer.getName());

        }

        return true;

    }
}
