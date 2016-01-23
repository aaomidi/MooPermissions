package com.aaomidi.moopermissions.model.perms.player;

import com.aaomidi.moopermissions.model.perms.Timed;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * Created by amir on 2015-12-14.
 */
@RequiredArgsConstructor
@EqualsAndHashCode
public class PlayerPermission implements Timed {
    @Getter
    private final int id;
    @Getter
    private final String permission;
    @Getter
    private final boolean give;
    @Getter
    private final Date creation;
    @Getter
    private final Date expiration;
}
