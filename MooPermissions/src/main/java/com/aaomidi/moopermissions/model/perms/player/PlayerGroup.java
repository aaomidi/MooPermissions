package com.aaomidi.moopermissions.model.perms.player;

import com.aaomidi.moopermissions.engine.registeries.GroupFileRegistry;
import com.aaomidi.moopermissions.model.SQLGroup;
import com.aaomidi.moopermissions.model.perms.Timed;
import com.aaomidi.moopermissions.model.perms.server.Group;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * Created by amir on 2015-12-14.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class PlayerGroup implements Timed {
    @Getter
    private final int id;
    @Getter
    private final int gid;
    @Getter
    private final Date creation;
    @Getter
    private final Date expiration;
    @Getter
    @NonNull
    private final SQLGroup sqlGroup;


    public String getName() {
        return sqlGroup.getName();
    }

    public Group toGroup() {
        return GroupFileRegistry.getGroup(getName());
    }

}
