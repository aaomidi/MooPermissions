package com.aaomidi.moopermissions.model.perms;

import java.util.Date;

/**
 * Created by amir on 2015-12-14.
 */
public interface Timed {
    Date getCreation();

    Date getExpiration();

    default boolean isExpired() {
        if (!canExpire()) return false;
        long now = System.currentTimeMillis();

        return now > getExpiration().getTime();
    }

    default boolean canExpire() {
        return getExpiration() != null && getExpiration().getTime() != 0;
    }
}
