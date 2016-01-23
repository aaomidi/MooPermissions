package com.aaomidi.moopermissions.converter.data;

import com.aaomidi.moopermissions.converter.model.ConfigFile;
import com.google.gson.Gson;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by amir on 2016-01-21.
 */
public class DataManager {
    private static Gson gson = new Gson();
    @Getter
    private ConfigFile configFile;

    public void readConfigFile() {
        try {

            String currentPath = System.getProperty("user.dir");
            currentPath = String.format("%s%sconfig.json", currentPath, File.separator);

            File file = new File(currentPath);

            if (!file.exists()) {
                this.writeDefaultConfig(file);
            }

            FileReader reader = new FileReader(file);
            configFile = gson.fromJson(reader, ConfigFile.class);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void writeDefaultConfig(File file) throws IOException {

        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        ConfigFile configFile = new ConfigFile("127.0.0.1", 3306, "perms", "newperms", "root", "");
        String json = gson.toJson(configFile, ConfigFile.class);

        fileWriter.write(json);
        fileWriter.flush();
        fileWriter.close();
    }
}
