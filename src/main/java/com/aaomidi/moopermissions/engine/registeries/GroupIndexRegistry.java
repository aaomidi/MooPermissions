package com.aaomidi.moopermissions.engine.registeries;

import com.aaomidi.moopermissions.model.SQLGroup;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amir on 2015-12-17.
 */
@RequiredArgsConstructor
public class GroupIndexRegistry {
    private static final Map<Integer, String> map1 = new HashMap<>();
    private static final Map<String, Integer> map2 = new HashMap<>();
    private static final Map<String, SQLGroup> map3 = new HashMap<>();
    private static final Map<Integer, SQLGroup> map4 = new HashMap<>();

    public static void register(Integer id, String group, Date creation) {
        group = group.toLowerCase();
        SQLGroup sqlGroup = new SQLGroup(id, group, creation);

        map1.put(id, group);
        map2.put(group, id);
        map3.put(group, sqlGroup);
        map4.put(id, sqlGroup);
    }

    public static String get(Integer id) {
        if (id < 0) return GroupFileRegistry.getDefaultGroup().getName();

        return map1.get(id);
    }

    public static SQLGroup getSQLGroup(Integer id) {
        return map4.get(id);
    }

    public static Integer get(String name) {
        name = name.toLowerCase();
        return map2.get(name);
    }

    public static Collection<SQLGroup> getRegisteredGroups() {
        return map3.values();
    }

    public static void reset() {
        map1.clear();
        map2.clear();
        map3.clear();
        map4.clear();
    }
}
