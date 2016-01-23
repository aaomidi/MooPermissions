package com.aaomidi.moopermissions.converter;

import com.aaomidi.moopermissions.converter.data.DataManager;
import com.aaomidi.moopermissions.converter.model.ConfigFile;
import com.aaomidi.moopermissions.converter.model.SQLGroup;
import com.aaomidi.moopermissions.converter.model.zperms.ZPermGroup;
import com.aaomidi.moopermissions.converter.model.zperms.ZPermPlayer;
import com.aaomidi.moopermissions.converter.sql.SQLConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by amir on 2016-01-21.
 */
public class ZPermsConverter {
    private final HashMap<UUID, ZPermPlayer> uuidzPermMap = new HashMap<>();
    private final HashMap<Long, ZPermPlayer> idzPermMap = new HashMap<>();
    private final HashMap<Long, ZPermGroup> idzGroupMap = new HashMap<>();

    private final HashMap<String, SQLGroup> groups = new HashMap<>();
    private SQLConnector zPermSQL;
    private SQLConnector mooPermSQL;
    private DataManager dataManager;

    public ZPermsConverter() {
        dataManager = new DataManager();
        dataManager.readConfigFile();

        ConfigFile configFile = dataManager.getConfigFile();
        zPermSQL = new SQLConnector(this, configFile.getHost(), configFile.getPort(), configFile.getUsername(), configFile.getPassword(), configFile.getZpermDatabase());
        mooPermSQL = new SQLConnector(this, configFile.getHost(), configFile.getPort(), configFile.getUsername(), configFile.getPassword(), configFile.getMooDatabase());
        this.readAllPlayers();
        logn("-----------");
        this.readAllPermissions();
        logn("-----------");
        this.readAllMemberships();
        logn("-----------");

        this.saveInformation();
    }

    public static void main(String... args) {
        new ZPermsConverter();
    }

    public static void log(Object s, Object... args) {
        System.out.printf(s.toString(), args);
    }

    public static void logn(Object s, Object... args) {
        log(s + "\n", args);
    }

    public void saveInformation() {
        try {
            String query1 = "INSERT INTO mooperms_index(uuidH, uuidL, name) VALUES (?, ?, ?)";
            for (ZPermPlayer player : uuidzPermMap.values()) {
                UUID uuid = player.getUUID();
                mooPermSQL.executeUpdate(query1, uuid.getMostSignificantBits(), uuid.getLeastSignificantBits(), player.getName());
            }
            String query2 = "SELECT * FROM mooperms_index";
            ResultSet resultSet = mooPermSQL.executeQuery(query2);

            if (!resultSet.next()) {
                throw new Error("Empty");
            }

            do {
                int id = resultSet.getInt("id");
                long uuidH = resultSet.getLong("uuidH");
                long uuidL = resultSet.getLong("uuidL");
                UUID uuid = new UUID(uuidH, uuidL);
                ZPermPlayer player = uuidzPermMap.get(uuid);
                player.setMooID(id);
            } while (resultSet.next());

            String query3 = "INSERT INTO mooperms_perms(pid, permission, give) VALUES (?, ?, ?);";
            String query4 = "INSERT INTO mooperms_groups(pid, gid) VALUES (?, ?);";
            for (ZPermPlayer player : uuidzPermMap.values()) {
                for (Map.Entry<String, Boolean> perms : player.getPermissions().entrySet()) {
                    mooPermSQL.executeUpdate(query3, player.getMooID(), perms.getKey(), perms.getValue());
                }

                for (ZPermGroup group : player.getGroups()) {
                    int gid = registerNewGroup(group.getName());
                    mooPermSQL.executeUpdate(query4, player.getMooID(), gid);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Integer registerNewGroup(String groupName) {
        groupName = groupName.toLowerCase();

        if (groups.get(groupName) != null) {
            return groups.get(groupName).getId();
        }

        String query1 = "INSERT INTO mooperms_gindex(name) VALUES(?);";
        String query2 = "SELECT id, creation FROM mooperms_gindex WHERE name=?;";

        try {
            mooPermSQL.executeUpdate(query1, groupName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            ResultSet rs = mooPermSQL.executeQuery(query2, groupName);
            if (!rs.next()) {
                logn(Level.SEVERE, "No group registered.");
                return null;
            }
            int gid = rs.getInt("id");
            Date creation = rs.getTimestamp("creation");

            groups.put(groupName, new SQLGroup(gid, groupName, creation));
            return gid;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public void readAllPlayers() {
        String query1 = "SELECT * FROM entities";
        ResultSet resultSet = zPermSQL.executeQuery(query1);

        try {
            if (!resultSet.next()) {
                throw new Error("Empty entities");
            }
            do {
                long id = resultSet.getLong("id");
                String uuidString = resultSet.getString("name");
                String name = resultSet.getString("display_name");
                boolean isGroup = resultSet.getBoolean("is_group");
                if (isGroup) {
                    ZPermGroup zPermGroup = new ZPermGroup(id, uuidString);
                    this.store(zPermGroup);
                } else {
                    ZPermPlayer zPermPlayer = new ZPermPlayer(id, uuidString, name);
                    this.store(zPermPlayer);
                }
            } while (resultSet.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readAllPermissions() {
        String query1 = "SELECT * FROM entries";
        ResultSet resultSet = zPermSQL.executeQuery(query1);
        try {
            if (!resultSet.next()) {
                throw new Error("Empty entries");
            }
            do {
                long entity_id = resultSet.getLong("entity_id");
                String permission = resultSet.getString("permission");
                boolean give = resultSet.getBoolean("value");
                ZPermPlayer player = idzPermMap.get(entity_id);
                if (player == null) {
                    logn("%s was not a player permission (%d)", permission, entity_id);
                    continue;
                }
                player.addPermissions(permission, give);
            } while (resultSet.next());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void readAllMemberships() {
        String query1 = "SELECT * FROM memberships";
        ResultSet resultSet = zPermSQL.executeQuery(query1);
        try {
            if (!resultSet.next()) {
                throw new Error("Empty entries");
            }
            do {
                String uuidString = resultSet.getString("member");
                long groupID = resultSet.getInt("group_id");
                String name = resultSet.getString("display_name");
                ZPermPlayer tmp = new ZPermPlayer(0, uuidString, "");
                ZPermPlayer player = uuidzPermMap.get(tmp.getUUID());
                if (player == null) {
                    player = new ZPermPlayer(-1, uuidString, name);
                    store(player);
                }
                ZPermGroup group = idzGroupMap.get(groupID);
                if (group == null) {
                    logn("Group was null! (%d)", groupID);
                    continue;
                }
                player.addGroup(group);
            } while (resultSet.next());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void store(ZPermPlayer zPermPlayer) {
        if (zPermPlayer.getId() > 0) {
            this.idzPermMap.put(zPermPlayer.getId(), zPermPlayer);
        }
        this.uuidzPermMap.put(zPermPlayer.getUUID(), zPermPlayer);
    }

    public void store(ZPermGroup zPermGroup) {
        this.idzGroupMap.put(zPermGroup.getId(), zPermGroup);
    }
}
