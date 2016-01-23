package com.aaomidi.moopermissions.converter.model;

import lombok.Data;

/**
 * Created by amir on 2016-01-21.
 */
@Data
public class ConfigFile {
    private final String host;
    private final int port;
    private final String mooDatabase;
    private final String zpermDatabase;
    private final String username;
    private final String password;
}
