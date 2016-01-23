package com.aaomidi.moopermissions.engine.registeries;

import com.aaomidi.moopermissions.model.perms.server.Group;
import com.aaomidi.moopermissions.model.perms.server.PermissionFile;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amir on 2015-12-13.
 */
public class GroupFileRegistry {
    @Getter
    private static final Map<String, Group> groups = new HashMap<>();
    private static Group defaultGroup;

    public static Group getDefaultGroup() {
        if (defaultGroup != null)
            return defaultGroup;

        try {
            Group group = groups.values().stream().filter(Group::isDefault).findAny().get();
            defaultGroup = group;
            if (defaultGroup == null) {
                throw new Error("NO DEFAULT GROUP SPECIFIED.");
            }
            return group;
        } catch (Exception ex) {
            throw new Error("NO DEFAULT GROUP SPECIFIED.");
        }
    }

    public static Group getGroup(String name) {
        return groups.get(name.toLowerCase());
    }

    public static void addFile(PermissionFile file) {
        for (Group group : file.getGroups()) {
            addGroup(group);
        }
    }

    public static void addGroup(Group input) {
        Group group = groups.get(input.getName().toLowerCase());

        if (group == null) {
            groups.put(input.getName().toLowerCase(), input);
            return;
        }

        group.addPermissions(input);
    }

    public static void reset() {
        groups.clear();
        defaultGroup = null;
    }
}
