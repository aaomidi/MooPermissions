package com.aaomidi.moopermissions.commands.playercommands;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.model.SQLGroup;
import com.aaomidi.moopermissions.model.commands.MCommand;
import com.aaomidi.moopermissions.model.commands.MPlayerCommand;
import com.aaomidi.moopermissions.model.perms.player.MPlayer;
import com.aaomidi.moopermissions.model.perms.player.PlayerGroup;
import com.aaomidi.moopermissions.model.perms.player.PlayerPermission;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by amir on 2015-12-23.
 */
public class PlayerInfoCommand extends MPlayerCommand {

    public PlayerInfoCommand(String name, String description, String... aliases) {
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

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n&3%s &bis members of: ", mPlayer.getName()));

        List<PlayerGroup> playerGroups = new ArrayList<>(mPlayer.getGroups().values());
        // Show default group
        playerGroups.add(0, new PlayerGroup(-1, -1, new Date(System.currentTimeMillis()), null, new SQLGroup(-1, "Default", new Date(System.currentTimeMillis()))));

        int i = 1;
        for (PlayerGroup group : playerGroups) {
            StringManager.log(Level.INFO, "CREATION: " + group.getCreation().getTime() + "");

            String creation = MCommand.DATE_FORMAT.format(group.getCreation());
            sb
                    .append("\n&d")
                    .append(i++)
                    .append(". ")
                    .append("&3")
                    .append(group.getName())
                    .append(" &bSince ")
                    .append("&3")
                    .append(creation);
            if (group.canExpire()) {
                String expiration = MCommand.DATE_FORMAT.format(group.getExpiration());
                sb
                        .append(" &bTill ")
                        .append("&3")
                        .append(expiration);
            }
        }
        sb.append(String.format("\n&3%s &bhas the following permissions: ", mPlayer.getName()));

        i = 1;
        for (PlayerPermission playerPermission : mPlayer.getPermissionList().values()) {

            String creation = MCommand.DATE_FORMAT.format(playerPermission.getCreation());
            sb
                    .append("\n&d")
                    .append(i++)
                    .append(". ")
                    .append("&3")
                    .append(playerPermission.getPermission())
                    .append("&b:")
                    .append("&3")
                    .append(playerPermission.isGive())
                    .append(" &bSince ")
                    .append("&3")
                    .append(creation);
            if (playerPermission.canExpire()) {
                String expiration = MCommand.DATE_FORMAT.format(playerPermission.getExpiration());
                sb
                        .append(" &bTill ")
                        .append("&3")
                        .append(expiration);
            }
        }

        StringManager.sendMessage(commandSender, sb.toString());
        return true;
    }
}
