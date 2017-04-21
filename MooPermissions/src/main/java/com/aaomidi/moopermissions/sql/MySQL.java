package com.aaomidi.moopermissions.sql;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupIndexRegistry;
import com.aaomidi.moopermissions.model.perms.player.MPlayer;
import com.aaomidi.moopermissions.model.perms.player.PlayerGroup;
import com.aaomidi.moopermissions.model.perms.player.PlayerPermission;
import com.aaomidi.moopermissions.utils.StringManager;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by amir on 2015-12-14.
 */
public class MySQL extends SQLConnector {
    public final Deadbabe timestampDeadbabe = new Deadbabe(Types.TIMESTAMP);

    public MySQL(MooPermissions instance, String host, int port, String username, String password, String database) {
        super(instance, host, port, username, password, database);
        this.biteMyShinyMetalAss();
        this.registerGroups();
    }

    /**
     * This method actually does not have anything to do with asses.
     * It creates tables.
     */
    private void biteMyShinyMetalAss() {
        String query1 = "CREATE TABLE IF NOT EXISTS mooperms_index(id INT NOT NULL AUTO_INCREMENT, uuidH BIGINT NOT NULL, uuidL BIGINT NOT NULL, name VARCHAR(16), ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(id), UNIQUE KEY uuid(uuidH, uuidL), INDEX(name))Engine=InnoDB DEFAULT CHARSET=utf8mb4;";

        String query2 = "CREATE TABLE IF NOT EXISTS mooperms_gindex(id INT NOT NULL AUTO_INCREMENT, name VARCHAR(32) UNIQUE NOT NULL, creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(id))Engine=InnoDB DEFAULT CHARSET=utf8mb4;";

        String query3 = "CREATE TABLE IF NOT EXISTS mooperms_groups(id INT NOT NULL AUTO_INCREMENT, pid INT NOT NULL, gid INT NOT NULL, creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, expiration TIMESTAMP DEFAULT NULL, PRIMARY KEY(id), UNIQUE KEY info(pid, gid), FOREIGN KEY(pid) REFERENCES mooperms_index(id), FOREIGN KEY(gid) REFERENCES mooperms_gindex(id) ON DELETE CASCADE, INDEX(expiration))Engine=InnoDB DEFAULT CHARSET=utf8mb4;";

        String query4 = "CREATE TABLE IF NOT EXISTS mooperms_perms(id INT NOT NULL AUTO_INCREMENT, pid INT NOT NULL, permission VARCHAR(127) NOT NULL, give BOOLEAN NOT NULL, creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, expiration TIMESTAMP DEFAULT NULL, PRIMARY KEY(id), UNIQUE KEY info(pid, permission), FOREIGN KEY(pid) REFERENCES mooperms_index(id), INDEX (expiration)) Engine=InnoDB DEFAULT CHARSET=utf8mb4;";

        executeUpdate(query1);
        executeUpdate(query2);
        executeUpdate(query3);
        executeUpdate(query4);
    }

    public void registerGroups() {
        String query = "SELECT * FROM mooperms_gindex";
        try (ResultSet rs = executeQuery(query)) {
            if (!rs.next()) {
                StringManager.log(Level.WARNING, "No groups defined");
                return;
            }

            do {
                GroupIndexRegistry.register(rs.getInt("id"), rs.getString("name"), rs.getTimestamp("creation"));
            } while (rs.next());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public MPlayer initPlayer(Player player) {
        return this.initPlayer(player, true);
    }

    public MPlayer getPlayer(String playerName) {
        playerName = playerName.toLowerCase();
        String selectID = "SELECT * FROM mooperms_index WHERE name=? ORDER BY ts DESC LIMIT 1";

        try (ResultSet rs = executeQuery(selectID, playerName)) {
            if (!rs.next()) {
                StringManager.log(Level.WARNING, "No player found with the name " + playerName);
                return null;
            }

            int id = rs.getInt("id");
            long uuidH = rs.getLong("uuidH");
            long uuidL = rs.getLong("uuidL");

            UUID uuid = new UUID(uuidH, uuidL);
            return this.getPlayer(id, playerName, uuid);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Initializes a player
     *
     * @param player
     * @param indexFirst first entrance to function.
     * @return
     */
    private MPlayer initPlayer(Player player, boolean indexFirst) {
        long uuidH = player.getUniqueId().getMostSignificantBits();
        long uuidL = player.getUniqueId().getLeastSignificantBits();
        // Store names as lower case characters.
        String playerName = player.getName().toLowerCase();

        String selectID = "SELECT id, name FROM mooperms_index WHERE uuidH=? AND uuidL=?";
        String updateTimeStamp = "UPDATE mooperms_index SET ts=CURRENT_TIMESTAMP where id=?";
        String updateOtherNames = "UPDATE mooperms_index SET name=null WHERE name=?";
        String updateName = "UPDATE mooperms_index SET name=? WHERE id=?";
        String initPlayer = "INSERT IGNORE INTO mooperms_index(uuidH, uuidL, name) VALUES (?, ?, ?)";

        try (ResultSet rs = executeQuery(selectID, uuidH, uuidL)) {
            if (!rs.next()) {
                if (!indexFirst) {
                    StringManager.log(Level.SEVERE, "Issue when initializing a player. -Index");
                    return null;
                }
                executeUpdate(initPlayer, uuidH, uuidL, playerName);
                return initPlayer(player, false);
            }

            int id = rs.getInt("id");
            String databaseName = rs.getString("name");

            // Update name if needed.
            if (!playerName.equalsIgnoreCase(databaseName)) {
                executeUpdate(updateOtherNames, playerName);
                executeUpdate(updateName, playerName, id);
            } else {
                // Self mending database.
                executeUpdate(updateTimeStamp, id);
            }

            return getPlayer(id, playerName, player.getUniqueId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public MPlayer getPlayer(int id, String playerName, UUID uuid) {
        Map<String, PlayerGroup> groups = new HashMap<>();
        String selectGroups = "SELECT * FROM mooperms_groups WHERE pid=?";
        String selectPermissions = "SELECT * FROM mooperms_perms WHERE pid=?";

        String cleanGroups = "DELETE FROM mooperms_groups WHERE pid=? AND expiration < NOW()";
        String cleanPermissions = "DELETE FROM mooperms_perms WHERE pid=? AND expiration < NOW()";

        try {
             /* Remove expired permissions */
            executeUpdate(cleanGroups, id);
            executeUpdate(cleanPermissions, id);
            /* End removing expired permissions */
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        try (ResultSet rs = executeQuery(selectGroups, id)) {
            if (!rs.next()) {
                // StringManager.log(Level.WARNING, "No groups found for %s.", playerName);
            } else {
                do {
                    Date expiry = rs.getTimestamp("expiration");
                    if (rs.wasNull())
                        expiry = null;

                    Date creation = rs.getTimestamp("creation");
                    if (rs.wasNull())
                        creation = null;

                    int gid = rs.getInt("gid");

                    if (GroupIndexRegistry.get(gid) == null) {
                        StringManager.log(Level.SEVERE, "No group with associated gid " + gid);
                        return null;
                    }

                    PlayerGroup playerGroup = new PlayerGroup(rs.getInt("id"), gid, creation, expiry, GroupIndexRegistry.getSQLGroup(gid));
                    groups.put(playerGroup.getName(), playerGroup);
                } while (rs.next());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        try (ResultSet rs = executeQuery(selectPermissions, id)) {

            Map<String, PlayerPermission> permissions = new HashMap<>();
            if (!rs.next()) {
                //StringManager.log(Level.WARNING, "No permissions found for %s.", playerName);
            } else {
                do {
                    Date expiry = rs.getTimestamp("expiration");
                    if (rs.wasNull())
                        expiry = null;
                    Date creation = rs.getTimestamp("creation");
                    if (rs.wasNull())
                        creation = null;
                    
                    PlayerPermission playerPermission = new PlayerPermission(rs.getInt("id"), rs.getString("permission"), rs.getBoolean("give"), creation, expiry);
                    permissions.put(playerPermission.getPermission(), playerPermission);
                } while (rs.next());
            }


            return new MPlayer(getInstance(), id, uuid, playerName, groups, permissions);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Integer registerNewGroup(String groupName) {
        groupName = groupName.toLowerCase();

        if (GroupIndexRegistry.get(groupName) != null) {
            StringManager.log(Level.SEVERE, "GROUP ALREADY REGISTERED");
            return null;
        }

        String query1 = "INSERT INTO mooperms_gindex(name) VALUES(?);";
        String query2 = "SELECT id, creation FROM mooperms_gindex WHERE name=?;";

        try {
            executeUpdate(query1, groupName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        try (ResultSet rs = executeQuery(query2, groupName)) {
            if (!rs.next()) {
                StringManager.log(Level.SEVERE, "No group registered.");
                return null;
            }
            int gid = rs.getInt("id");
            Date creation = rs.getTimestamp("creation");

            if (rs.wasNull())
                creation = null;

            GroupIndexRegistry.register(gid, groupName, creation);
            return gid;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("Duplicates")
    public boolean removeGroupFromPlayer(MPlayer mPlayer, int gid) {
        String query = "DELETE FROM mooperms_groups WHERE pid=? and gid=?";

        try {
            executeUpdate(query, mPlayer.getId(), gid);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("Duplicates")
    public boolean removeGroupFromPlayer(MPlayer mPlayer, PlayerGroup group) {
        String query = "DELETE FROM mooperms_groups WHERE id=?";
        try {
            executeUpdate(query, group.getId());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("Duplicates")
    public boolean removeAllGroups(MPlayer mPlayer) {
        String query = "DELETE FROM mooperms_groups WHERE pid=?";
        try {
            executeUpdate(query, mPlayer.getId());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("Duplicates")
    public PlayerGroup addGroupToPlayer(MPlayer mPlayer, int gid, long expiration) {
        String query1 = "INSERT INTO mooperms_groups(pid, gid, expiration) VALUES (?, ?, ?);";
        String query2 = "SELECT * FROM mooperms_groups WHERE pid=? AND gid=?";
        Date ts = new Date(expiration);
        try {
            if (expiration <= 0) {
                executeUpdate(query1, mPlayer.getId(), gid, timestampDeadbabe);

            } else {
                executeUpdate(query1, mPlayer.getId(), gid, ts);
            }
        } catch (Exception ex) {
            StringManager.log(Level.SEVERE, "Could not add group to player");
            ex.printStackTrace();
            return null;
        }

        try (ResultSet rs = executeQuery(query2, mPlayer.getId(), gid)) {
            if (!rs.next()) {
                StringManager.log(Level.SEVERE, "AddGroup ID returned null");
                return null;
            }

            int id = rs.getInt("id");
            Date expiryRS = rs.getTimestamp("expiration");
            if (rs.wasNull())
                expiryRS = null;

            Date creationRS = rs.getTimestamp("creation");
            if (rs.wasNull())
                creationRS = null;

            if (expiration > 0) {
                if (expiryRS == null) {
                    StringManager.log(Level.SEVERE, "TIMESTAMP SHOULD NOT HAVE RETURNED NULL");
                    return null;
                }

                if (expiration / 1000 != expiryRS.getTime() / 1000) {
                    StringManager.log(Level.SEVERE, "Time: " + ts.getTime() + " RSTime:" + expiryRS.getTime());
                    StringManager.log(Level.SEVERE, "Expiration date mismatch");
                    return null;
                }
            }
            return new PlayerGroup(id, gid, creationRS, expiryRS, GroupIndexRegistry.getSQLGroup(gid));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("Duplicates")
    public boolean removePermissionFromPlayer(MPlayer mPlayer, String permission) {
        permission = permission.toLowerCase();
        String query = "DELETE FROM mooperms_perms WHERE pid=? and permission=?";

        try {
            executeUpdate(query, mPlayer.getId(), permission);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("Duplicates")
    public boolean removePermissionFromPlayer(MPlayer mPlayer, PlayerPermission permission) {
        String query = "DELETE FROM mooperms_perms WHERE id=?;";

        try {
            executeUpdate(query, permission.getId());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    @SuppressWarnings("Duplicates")
    public boolean removeAllPermissions(MPlayer mPlayer) {
        String query = "DELETE FROM mooperms_perms WHERE pid=?";
        try {
            executeUpdate(query, mPlayer.getId());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("Duplicates")
    public PlayerPermission addPermissionToPlayer(MPlayer mPlayer, String permission, boolean give, long expiration) {
        permission = permission.toLowerCase();
        String query1 = "INSERT INTO mooperms_perms(pid, permission, give, expiration) VALUES (?, ?, ?, ?);";
        String query2 = "SELECT * FROM mooperms_perms WHERE pid=? AND permission=?";
        Date ts = new Date(expiration);

        try {
            if (expiration <= 0) {
                executeUpdate(query1, mPlayer.getId(), permission, give, timestampDeadbabe);
            } else {
                executeUpdate(query1, mPlayer.getId(), permission, give, ts);
            }
        } catch (Exception ex) {
            StringManager.log(Level.SEVERE, "Could not add permission to player");
            ex.printStackTrace();
            return null;
        }

        try (ResultSet rs = executeQuery(query2, mPlayer.getId(), permission)) {
            if (!rs.next()) {
                StringManager.log(Level.SEVERE, "AddPermission ID returned null");
                return null;
            }

            int id = rs.getInt("id");
            Date expiryRS = rs.getTimestamp("expiration");
            if (rs.wasNull())
                expiryRS = null;
            Date creationRS = rs.getTimestamp("creation");
            if (rs.wasNull())
                creationRS = null;

            if (expiration > 0) {
                if (expiryRS == null) {
                    StringManager.log(Level.SEVERE, "TIMESTAMP SHOULD NOT HAVE RETURNED NULL");
                    return null;
                }

                if (expiration / 1000 != expiryRS.getTime() / 1000) {
                    StringManager.log(Level.SEVERE, "Time: " + ts.getTime() + " RSTime:" + expiryRS.getTime());
                    StringManager.log(Level.SEVERE, "Expiration date mismatch");
                    return null;
                }
            }
            return new PlayerPermission(id, permission, give, creationRS, expiryRS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean deleteGroup(int gid) {
        String query = "DELETE FROM mooperms_gindex WHERE id=?";
        try {
            executeUpdate(query, gid);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
