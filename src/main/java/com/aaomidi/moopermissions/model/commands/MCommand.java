package com.aaomidi.moopermissions.model.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by amir on 2015-12-14.
 */
@RequiredArgsConstructor
public class MCommand {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    @Getter
    private final String name;
    @Getter
    private final String permission;
    @Getter
    private final String description;
    @Getter
    private final Type commandType;
    @Getter
    private final String[] aliases;

    public enum Type {
        GROUP,
        PLAYER,
        OTHER,
        NONE;

        public static Type getType(String s) {
            switch (s.toLowerCase()) {
                case "group":
                    return GROUP;
                case "player":
                    return PLAYER;
                case "other":
                    return OTHER;
                default:
                    return NONE;
            }
        }
    }
}
