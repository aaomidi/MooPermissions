package com.aaomidi.moopermissions.engine;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.commands.groupcommands.GroupDelGroupCommand;
import com.aaomidi.moopermissions.commands.othercommands.ImportCommand;
import com.aaomidi.moopermissions.commands.othercommands.ListImplementedGroupsCommand;
import com.aaomidi.moopermissions.commands.othercommands.ListRegisteredGroupsCommand;
import com.aaomidi.moopermissions.commands.othercommands.ReloadGroupsCommand;
import com.aaomidi.moopermissions.commands.playercommands.*;
import com.aaomidi.moopermissions.model.commands.MCommand;
import com.aaomidi.moopermissions.model.commands.MGroupCommand;
import com.aaomidi.moopermissions.model.commands.MOtherCommand;
import com.aaomidi.moopermissions.model.commands.MPlayerCommand;
import com.aaomidi.moopermissions.utils.StringManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Created by amir on 2015-12-17.
 */
@RequiredArgsConstructor
public class CommandHandler implements CommandExecutor {
    private final static String NO_PERMISSION = "&cYou do not have permission to execute that command";
    private final MooPermissions instance;

    private final Map<String, MCommand> playerCommandMap = new HashMap<>();
    private final Map<String, MCommand> groupCommandMap = new HashMap<>();
    private final Map<String, MCommand> otherCommandMap = new HashMap<>();

    private void register(MCommand mCommand) {
        switch (mCommand.getCommandType()) {
            case GROUP:
                groupCommandMap.put(mCommand.getName().toLowerCase(), mCommand);
                for (String s : mCommand.getAliases()) {
                    groupCommandMap.put(s.toLowerCase(), mCommand);
                }
                break;
            case PLAYER:
                playerCommandMap.put(mCommand.getName().toLowerCase(), mCommand);
                for (String s : mCommand.getAliases()) {
                    playerCommandMap.put(s.toLowerCase(), mCommand);
                }
                break;
            case OTHER:
                otherCommandMap.put(mCommand.getName().toLowerCase(), mCommand);
                for (String s : mCommand.getAliases()) {
                    otherCommandMap.put(s.toLowerCase(), mCommand);
                }
                break;
            case NONE:
                break;
        }
    }

    public void registerCommands() {
        /** Group Commands **/
        register(new GroupDelGroupCommand("DeleteGroup", "Deletes a group entirely.", "dg", "delgroup"));

        /** Other Commands **/
        register(new ImportCommand("Import", "Imports group memberships."));
        register(new ListImplementedGroupsCommand("ListImplementedGroups", "Lists all the implemented groups", "lig", "ListImplemented", "ListIG", "ListImplementedG"));
        register(new ListRegisteredGroupsCommand("ListRegisteredGroups", "Lists all the registered groups", "lrg", "ListRegistered", "ListRG", "ListRegisteredG"));
        register(new ReloadGroupsCommand("ReloadGroups", "Reloads all the groups from file", "rg"));

        /** Player Commands **/
        register(new AddGroupIgnoreCommand("AddGroupIgnore", "Adds a group to a player. Adds even if its not implemented on server.", "agi"));
        register(new AddGroupCommand("AddGroup", "Adds a group to a player.", "ag"));
        register(new AddPermissionCommand("AddPermission", "Adds a permission to a player.", "addperm", "ap"));
        register(new DelAllGroupsCommand("DelAllGroups", "Removes a player's membership from all groups.", "dag"));
        register(new DelAllPermissionsCommand("DelAllPermissions", "Removes all of custom permissions from a player.", "dap"));
        register(new PlayerDelGroupCommand("DelGroup", "Removes a group from a player.", "dg"));
        register(new DelPermissionCommand("DelPermission", "Removes a permission from a player.", "delperm", "dp"));
        register(new HasCommand("Has", "Checks if a player has a permission.", "h"));
        register(new PlayerInfoCommand("Info", "Prints out information about the player.", "pi"));
        register(new SetGroupCommand("SetGroup", "Sets a player's group to the specified group.", "sg"));
        register(new SetPermissionCommand("SetPermission", "Sets a permission for a player.", "setperm", "sp"));
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!command.getName().equals("permissions")) {
            return false;
        }

        if (args.length == 0) {
            int i = 1;
            StringBuilder sb = new StringBuilder("&bPossible commands are: ");
            sb.append(String.format("\n &d%d. &b/perms player", i++));
            sb.append(String.format("\n &d%d. &b/perms group", i++));
            sb.append(String.format("\n &d%d. &b/perms other", i++));

            StringManager.sendMessage(commandSender, sb.toString());
            return false;
        }

        String commandTypeString = args[0];
        MCommand.Type type = MCommand.Type.getType(commandTypeString);

        switch (type) {
            case GROUP: {
                if (args.length < 3) {
                    StringBuilder sb = new StringBuilder("&bThe possible commands are: ");
                    int i = 1;
                    List<String> stuffDone = new ArrayList<>();
                    for (MCommand mCommand : groupCommandMap.values()) {
                        if (stuffDone.contains(mCommand.getName())) continue;
                        stuffDone.add(mCommand.getName());

                        sb.append(String.format("\n &d%d. &3/perms group &b[group] &d%s &7- %s", i++, mCommand.getName(), mCommand.getDescription()));
                    }
                    StringManager.sendMessage(commandSender, sb.toString());
                    return true;
                }
                String groupName = args[1];
                String cmdLabel = args[2];
                MCommand mCommand = groupCommandMap.get(cmdLabel.toLowerCase());

                if (mCommand == null || mCommand.getCommandType() != type) {
                    return false;
                }

                if (!commandSender.hasPermission(mCommand.getPermission())) {
                    StringManager.sendMessage(commandSender, NO_PERMISSION);
                    return true;
                }

                String[] newArray;

                if (args.length > 3) {
                    newArray = Arrays.copyOfRange(args, 3, args.length);
                } else {
                    newArray = new String[0];
                }

                ((MGroupCommand) mCommand).execute(instance, groupName, newArray, commandSender);

                break;
            }
            case PLAYER: {
                if (args.length < 3) {
                    StringBuilder sb = new StringBuilder("&bThe possible commands are: ");
                    int i = 1;
                    List<String> stuffDone = new ArrayList<>();
                    for (MCommand mCommand : playerCommandMap.values()) {
                        if (stuffDone.contains(mCommand.getName())) continue;
                        stuffDone.add(mCommand.getName());

                        sb.append(String.format("\n &d%d. &3/perms player &b[player] &d%s &7- %s", i++, mCommand.getName(), mCommand.getDescription()));
                    }
                    StringManager.sendMessage(commandSender, sb.toString());
                    return false;
                }
                String playerName = args[1];
                String cmdLabel = args[2];
                MCommand mCommand = playerCommandMap.get(cmdLabel.toLowerCase());

                if (mCommand == null || mCommand.getCommandType() != type) {
                    return false;
                }

                if (!commandSender.hasPermission(mCommand.getPermission())) {
                    StringManager.sendMessage(commandSender, NO_PERMISSION);
                    return true;
                }

                String[] newArray;

                if (args.length > 3) {
                    newArray = Arrays.copyOfRange(args, 3, args.length);
                } else {
                    newArray = new String[0];
                }

                ((MPlayerCommand) mCommand).execute(instance, playerName, newArray, commandSender);
                break;
            }
            case OTHER: {
                if (args.length < 2) {
                    StringBuilder sb = new StringBuilder("&bThe possible commands are: ");
                    int i = 1;

                    List<String> stuffDone = new ArrayList<>();
                    for (MCommand mCommand : otherCommandMap.values()) {
                        if (stuffDone.contains(mCommand.getName())) continue;
                        stuffDone.add(mCommand.getName());

                        sb.append(String.format("\n &d%d. &3/perms other &d%s &7- %s", i++, mCommand.getName(), mCommand.getDescription()));
                    }
                    StringManager.sendMessage(commandSender, sb.toString());
                    return false;
                }
                String cmdLabel = args[1];
                MCommand mCommand = otherCommandMap.get(cmdLabel.toLowerCase());

                if (mCommand == null || mCommand.getCommandType() != type) {
                    return false;
                }

                if (!commandSender.hasPermission(mCommand.getPermission())) {
                    StringManager.sendMessage(commandSender, NO_PERMISSION);
                    return true;
                }

                String[] newArray;

                if (args.length > 2) {
                    newArray = Arrays.copyOfRange(args, 2, args.length);
                } else {
                    newArray = new String[0];
                }

                ((MOtherCommand) mCommand).execute(instance, newArray, commandSender);
                break;
            }
            case NONE:
                return false;
        }
        return true;

    }
}
