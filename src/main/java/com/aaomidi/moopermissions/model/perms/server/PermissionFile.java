package com.aaomidi.moopermissions.model.perms.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Created by amir on 2015-12-13.
 */
@RequiredArgsConstructor
public class PermissionFile {
    @Getter
    private final String name;
    @Getter
    private final int priority;
    @Getter
    private final List<Group> groups;
}
