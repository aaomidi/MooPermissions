package com.aaomidi.moopermissions.converter.model.zperms;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by amir on 2016-01-21.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class ZPermPlayer {
    private static Pattern uuidPattern = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
    @Getter
    private final long id;
    private final String uuid;
    @Getter
    private final String name;
    @Getter
    private final List<ZPermGroup> groups = new ArrayList<>();
    @Getter
    private final Map<String, Boolean> permissions = new HashMap<>();
    @Getter
    @Setter
    private int mooID;

    public UUID getUUID() {
        if (uuid.length() < 32 || uuid.length() > 32) {
            return null;
        }
        Matcher matcher = uuidPattern.matcher(uuid);
        if (!matcher.matches()) {
            return null;
        }
        String uuidString = matcher.replaceAll("$1-$2-$3-$4-$5");
        return UUID.fromString(uuidString);
    }

    public void addPermissions(String permission, boolean value) {
        this.permissions.put(permission, value);
    }

    public void addGroup(ZPermGroup group) {
        this.groups.add(group);
    }
}
