package com.aaomidi.moopermissions.data;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupFileRegistry;
import com.aaomidi.moopermissions.model.perms.server.Group;
import com.aaomidi.moopermissions.model.perms.server.PermissionFile;
import com.aaomidi.moopermissions.utils.StringManager;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by amir on 2015-12-13.
 */
public class DataManager {
    private final MooPermissions instance;
    private final Map<Integer, PermissionFile> perms = new TreeMap<>();
    @Getter
    private GroupFileRegistry groupFileRegistry;

    public DataManager(MooPermissions instance) {
        this.instance = instance;

        this.readFiles();
        this.createMPermissions();
    }

    public void reset() {
        perms.clear();
        this.readFiles();
        this.createMPermissions();
    }

    private void createMPermissions() {
        groupFileRegistry = new GroupFileRegistry();

        for (Map.Entry<Integer, PermissionFile> entry : perms.entrySet()) {
            Integer priority = entry.getKey();
            PermissionFile file = entry.getValue();

            groupFileRegistry.addFile(file);
        }
    }

    private void readFiles() {
        File dataFolder = instance.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
            this.createSampleFile();
        }

        for (File file : dataFolder.listFiles()) {
            if (!file.getName().contains("permission")) {
                continue;
            }

            readPermissionFile(file);
        }
    }


    private void readPermissionFile(File file) {
        try {
            Gson gson = new Gson();
            PermissionFile mpf = gson.fromJson(new FileReader(file), PermissionFile.class);

            perms.put(mpf.getPriority(), mpf);
        } catch (Exception e) {
            StringManager.log("Error when reading: " + file.getName());
            e.printStackTrace();
        }
    }

    private void createSampleFile() {
        File file = new File(instance.getDataFolder(), "sample.permission.json");
        Gson gson = new Gson();

        List<Group> mgroups = new ArrayList<>();
        LinkedHashMap<String, Boolean> permissions = new LinkedHashMap<>();

        permissions.put("random.permission", true);
        permissions.put("another.permission", false);
        Group mgroup1 = new Group("default", (LinkedHashMap<String, Boolean>) permissions.clone(), 1, "Default Group", true, new ArrayList<>());

        permissions.put("another.permission", true);
        permissions.put("my.rank.is.vip", true);

        List<String> parents = new ArrayList<>();
        parents.add("default");

        Group mgroup2 = new Group("rank1", permissions, 2, "CowGod", false, parents);

        mgroups.add(mgroup1);
        mgroups.add(mgroup2);

        PermissionFile permissionFile = new PermissionFile("Random", 1, mgroups);

        String json = gson.toJson(permissionFile);

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
