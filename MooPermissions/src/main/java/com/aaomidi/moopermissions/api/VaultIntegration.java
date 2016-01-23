package com.aaomidi.moopermissions.api;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupFileRegistry;
import com.aaomidi.moopermissions.model.perms.player.MPlayer;
import com.aaomidi.moopermissions.model.perms.server.Group;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.permission.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by amir on 2015-12-29.
 */
@RequiredArgsConstructor
public class VaultIntegration extends Permission {
    private final MooPermissions instance;

    @Override
    public String getName() {
        return "MooPermissions";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String worldName, String playerName, String permission) {
        MPlayer mPlayer = getPlayer(playerName);
        if (mPlayer == null)
            return false;
        return mPlayer.getEffectivePermissions().getOrDefault(permission, false);
    }

    @Override
    public boolean playerAdd(String worldName, String playerName, String permission) {
        MPlayer mPlayer = getPlayer(playerName);
        if (mPlayer == null)
            return false;
        return mPlayer.addPermissionToPlayer(permission, true, 0);
    }

    @Override
    public boolean playerRemove(String worldName, String playerName, String permission) {
        MPlayer mPlayer = getPlayer(playerName);
        if (mPlayer == null)
            return false;
        return mPlayer.removePermission(permission);

    }

    @Override
    public boolean groupHas(String worldName, String groupName, String permission) {
        Group group = getGroup(groupName);
        if (group == null)
            return false;
        return group.getPermissions().getOrDefault(permission, false);
    }

    @Override
    public boolean groupAdd(String worldName, String groupName, String permission) {
        throw new UnsupportedOperationException("This plugin does not support adding permissions to groups.");
    }

    @Override
    public boolean groupRemove(String worldName, String groupName, String permission) {
        throw new UnsupportedOperationException("This plugin does not support removing permissions from groups.");
    }

    @Override
    public boolean playerInGroup(String worldName, String playerName, String groupName) {
        MPlayer mPlayer = getPlayer(playerName);
        if (mPlayer == null)
            return false;
        return mPlayer.getGroups().containsKey(groupName);
    }

    @Override
    public boolean playerAddGroup(String worldName, String playerName, String groupName) {
        MPlayer mPlayer = getPlayer(playerName);
        if (mPlayer == null)
            return false;
        if (getGroup(groupName) == null)
            return false;
        return mPlayer.addToGroup(groupName, 0);
    }

    @Override
    public boolean playerRemoveGroup(String worldName, String playerName, String groupName) {
        MPlayer mPlayer = getPlayer(playerName);
        if (mPlayer == null)
            return false;
        if (getGroup(groupName) == null)
            return false;
        return mPlayer.removeFromGroup(groupName);
    }

    @Override
    public String[] getPlayerGroups(String worldName, String playerName) {
        MPlayer mPlayer = getPlayer(playerName);
        if (mPlayer == null)
            return null;
        String[] arr = new String[mPlayer.getGroups().keySet().size()];
        return mPlayer.getGroups().keySet().toArray(arr);
    }

    @Override
    public String getPrimaryGroup(String worldName, String playerName) {
        MPlayer mPlayer = getPlayer(playerName);
        if (mPlayer == null)
            return null;
        Group primary = mPlayer.getPrimaryGroup();
        if (primary == null) {
            return null;
        }
        return primary.getName();
    }

    @Override
    public String[] getGroups() {
        List<Group> groups = new ArrayList<>(GroupFileRegistry.getGroups().values());
        Collections.sort(groups, (o1, o2) -> ((Integer) o1.getPriority()).compareTo(o2.getPriority()));

        List<String> groupNames = new ArrayList<>();
        groups.stream().forEachOrdered(g -> groupNames.add(g.getName()));

        String[] arr = new String[groupNames.size()];
        return groupNames.toArray(arr);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }

    public MPlayer getPlayer(String playerName) {
        return instance.getCacheManager().getPlayer(playerName);
    }

    public Group getGroup(String name) {
        return GroupFileRegistry.getGroup(name);
    }
}
