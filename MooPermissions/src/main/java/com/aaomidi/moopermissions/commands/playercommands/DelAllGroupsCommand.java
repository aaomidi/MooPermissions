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
public class DelAllGroupsCommand extends MPlayerCommand {

    public DelAllGroupsCommand(String name, String description, String... aliases) {
        super(name, description, aliases);
    }

    @Override
    public boolean execute(MooPermissions instance, String playerName, String[] args, CommandSender commandSender) {
        Player player = Bukkit.getPlayer(playerName);
        MPlayer mPlayer = null;


        if (player == null) {
            mPlayer = instance.getCacheManager().getPlayer(playerName);
        } else {
            mPlayer = instance.getCacheManager().getPlayer(player);
        }

        if (mPlayer == null) {
            StringManager.sendMessage(commandSender, "&cThat player was not recognized.");
            return true;
        }
        boolean result = mPlayer.removeAllGroups();

        if (result) {
            StringManager.sendMessage(commandSender, "&bSuccessfully removed all groups from &3%s", mPlayer.getName());
        } else {
            StringManager.sendMessage(commandSender, "&bProblem when removing all groups from &3%s", mPlayer.getName());
        }

        return true;
    }
}
