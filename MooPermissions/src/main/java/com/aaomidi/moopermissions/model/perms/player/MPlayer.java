package com.aaomidi.moopermissions.model.perms.player;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupFileRegistry;
import com.aaomidi.moopermissions.engine.registeries.GroupIndexRegistry;
import com.aaomidi.moopermissions.model.perms.server.Group;
import com.aaomidi.moopermissions.utils.StringManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * Created by amir on 2015-12-13.
 */
@RequiredArgsConstructor
public class MPlayer {
    private final MooPermissions instance;
    @Getter
    private final int id;
    @Getter
    private final UUID uuid;
    @Getter
    private final String name;
    @Getter
    private final Map<String, PlayerGroup> groups;
    @Getter
    private final Map<String, PlayerPermission> permissionList;

    @Getter
    private final Lock lock = new ReentrantLock();

    private LinkedHashMap<String, Boolean> effectivePermissions;
    private PermissionAttachment permissionAttachment;

    public boolean addToGroup(String groupName, long expiry) {
        try {
            lock.lock();
            if (expiry < 0) {
                expiry = 0;
            }

            // Don't let someone be added to the default group.
            if (groupName.toLowerCase().equals(GroupFileRegistry.getDefaultGroup().getName())) {
                return false;
            }

            PlayerGroup pg = getGroup(groupName.toLowerCase());

            if (pg != null) {

                if (expiry == 0 && !pg.canExpire())  // If both of them can't expire.
                    return true; // Don't waste CPU Cycles.

                _removeFromGroup(groupName);
            }
            Integer gid = GroupIndexRegistry.get(groupName);

            if (gid == null) {
                gid = instance.getMySQL().registerNewGroup(groupName);
                if (gid == null) {
                    StringManager.log(Level.SEVERE, "ERROR WHEN CREATING A NEW GROUP.");
                    return false;
                }
            }
            pg = instance.getMySQL().addGroupToPlayer(this, gid, expiry);

            if (pg == null) return false;

            groups.put(pg.getName(), pg);
            this.reset();
            return this.apply();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    public Group getPrimaryGroup() {
        Group highestGroup = null;
        for (PlayerGroup playerGroup : getGroups().values()) {
            Group group = playerGroup.toGroup();
            if (group == null)
                continue;
            if (highestGroup == null) {
                highestGroup = group;
                continue;
            }
            if (highestGroup.getPriority() < group.getPriority()) {
                highestGroup = group;
                continue;
            }
        }
        return highestGroup;
    }

    public boolean setGroup(String groupName, long time) {
        try {
            lock.lock();

            boolean result1 = _removeAllGroups();
            return result1 && addToGroup(groupName, time);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Internal method to remove a player's group. This does not refresh permissions.
     *
     * @param groupName
     * @return
     */
    private boolean _removeFromGroup(String groupName) {
        groupName = groupName.toLowerCase();

        // Don't let someone be removed from the default group.
        if (groupName.equals(GroupFileRegistry.getDefaultGroup().getName())) {
            return false;
        }

        PlayerGroup pg = getGroup(groupName);

        if (pg == null)
            return false;

        boolean result = instance.getMySQL().removeGroupFromPlayer(this, pg);
        if (result) {
            groups.remove(groupName);
        }

        return result;
    }

    public boolean removeFromGroup(String groupName) {
        try {
            lock.lock();
            boolean result = _removeFromGroup(groupName);

            if (result) {
                this.reset();
                this.apply();
            }
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    private boolean _removeAllGroups() {
        boolean result = instance.getMySQL().removeAllGroups(this);

        if (result) {
            groups.clear();
        }

        return result;
    }

    public boolean removeAllGroups() {
        try {
            lock.lock();
            boolean result = _removeAllGroups();

            if (result) {
                this.reset();
                this.apply();
            }

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }


    public boolean addPermissionToPlayer(String permission, boolean give, long expiry) {
        try {
            lock.lock();
            PlayerPermission pp = getPermission(permission);

            if (pp != null) {
                if (expiry == 0 && !pp.canExpire() && pp.isGive() == give) {
                    return true;
                }

                if (!_removePermission(permission)) {
                    return false;
                }
            }

            pp = instance.getMySQL().addPermissionToPlayer(this, permission, give, expiry);

            if (pp == null) return false;

            permissionList.put(pp.getPermission(), pp);
            this.reset();
            this.apply();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    public boolean setPermission(String permission, boolean give, long expiry) {
        try {
            lock.lock();
            boolean result1 = _removeAllPermissions();
            return result1 && addPermissionToPlayer(permission, give, expiry);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    private boolean _removePermission(String permission) {
        permission = permission.toLowerCase();
        PlayerPermission pp = getPermission(permission);

        if (pp == null)
            return false;

        boolean result = instance.getMySQL().removePermissionFromPlayer(this, pp);
        if (result) {
            permissionList.remove(permission);
            return true;
        }

        return false;

    }

    public boolean removePermission(String permission) {
        try {
            lock.lock();

            boolean result = _removePermission(permission);
            if (result) {
                this.reset();
                this.apply();
            }

            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    private boolean _removeAllPermissions() {
        boolean result = instance.getMySQL().removeAllPermissions(this);

        if (result) {
            permissionList.clear();
        }

        return result;
    }

    public boolean removeAllPermissions() {
        try {
            lock.lock();

            boolean result = _removeAllPermissions();

            if (result) {
                this.reset();
                this.apply();
            }

            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    public PlayerGroup getGroup(String groupName) {
        groupName = groupName.toLowerCase();
        return groups.get(groupName);
    }

    public PlayerPermission getPermission(String permission) {
        permission = permission.toLowerCase();
        return permissionList.get(permission);
    }

    public Map<String, Boolean> getPlayerSpecificPermissions() {
        Map<String, Boolean> map = new LinkedHashMap<>();
        for (PlayerPermission playerPermission : permissionList.values()) {
            if (playerPermission.isExpired()) continue;
            map.put(playerPermission.getPermission(), playerPermission.isGive());
        }
        return map;
    }

    /**
     * This method returns the actual effective permissions of the player.
     *
     * @return
     */
    public LinkedHashMap<String, Boolean> getEffectivePermissions() {
        if (effectivePermissions != null)
            return effectivePermissions;

        effectivePermissions = new LinkedHashMap<>();
        effectivePermissions.putAll(GroupFileRegistry.getDefaultGroup().getPermissions());


        for (PlayerGroup playerGroup : groups.values()) {
            String groupName = playerGroup.getName();
            Group group = GroupFileRegistry.getGroup(groupName);
            if (group == null) {
                //StringManager.log(Level.WARNING, "Player %s had group %s but was not defined in server.", name, groupName);
                continue;
            }

            effectivePermissions.putAll(group.getPermissions());
        }

        effectivePermissions.putAll(getPlayerSpecificPermissions());

        return effectivePermissions;
    }

    public boolean apply() {
        try {
            lock.lock();
            Player player = getPlayer();
            if (player == null) return false;

            permissionAttachment = player.addAttachment(instance);
            for (Map.Entry<String, Boolean> effectivePerm : getEffectivePermissions().entrySet()) {
                permissionAttachment.setPermission(effectivePerm.getKey(), effectivePerm.getValue());
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        try {
            lock.lock();

            effectivePermissions = null;


            if (permissionAttachment != null) {
                Player player = getPlayer();
                if (player != null) {
                    player.removeAttachment(permissionAttachment);
                }
            }

            permissionAttachment = null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        } finally {
            lock.unlock();
        }
    }


    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }


    public void update(MPlayer mPlayer) {
        try {
            lock.lock();
            if (mPlayer.getGroups().values().containsAll(getGroups().values()) && mPlayer.getPermissionList().values().containsAll(getPermissionList().values())) {
                return;
            }

            this.groups.clear();
            this.groups.putAll(mPlayer.getGroups());

            this.permissionList.clear();
            this.permissionList.putAll(mPlayer.getPermissionList());

            this.reset();
            this.apply();

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        } finally {
            lock.unlock();
        }
    }
}
