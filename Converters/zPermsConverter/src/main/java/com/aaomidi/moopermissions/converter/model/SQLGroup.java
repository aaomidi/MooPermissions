package com.aaomidi.moopermissions.converter.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Date;


/**
 * Created by amir on 2015-12-24.
 */
@EqualsAndHashCode
public class SQLGroup {
    @Getter
    private final int id;
    @Getter
    private final String name;
    @Getter
    private final Date creation;

    public SQLGroup(int id, String name, Date creation) {
        this.id = id;
        this.name = name;
        this.creation = creation;
    }
}
