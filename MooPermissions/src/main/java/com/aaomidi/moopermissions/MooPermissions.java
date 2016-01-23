package com.aaomidi.moopermissions;

import com.aaomidi.moopermissions.api.VaultIntegration;
import com.aaomidi.moopermissions.data.ConfigReader;
import com.aaomidi.moopermissions.data.DataManager;
import com.aaomidi.moopermissions.engine.CacheManager;
import com.aaomidi.moopermissions.engine.CommandHandler;
import com.aaomidi.moopermissions.events.ConnectionEvent;
import com.aaomidi.moopermissions.sql.MySQL;
import com.aaomidi.moopermissions.utils.StringManager;
import lombok.Getter;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by amir on 2015-12-13.
 */
public class MooPermissions extends JavaPlugin {
    @Getter
    private DataManager dataManager;
    @Getter
    private MySQL mySQL;
    @Getter
    private CacheManager cacheManager;
    @Getter
    private CommandHandler commandHandler;
    @Getter
    private VaultIntegration vaultIntegration;
    @Getter
    private Vault vault;

    @Override
    public void onLoad() {
        StringManager.setLogger(this.getLogger());
        dataManager = new DataManager(this);

        File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists()) {
            this.saveDefaultConfig();
        }

        new ConfigReader(this.getConfig());
    }

    @Override
    public void onEnable() {
        this.setupSQL();
        this.setupEvents();
        this.setupCommands();

        cacheManager = new CacheManager(this);
        this.hookToVault();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void setupSQL() {
        mySQL = new MySQL(this, ConfigReader.getHost(), ConfigReader.getPort(), ConfigReader.getUsername(), ConfigReader.getPassword(), ConfigReader.getDatabase());
    }

    private void setupCommands() {
        commandHandler = new CommandHandler(this);

        commandHandler.registerCommands();
        this.getCommand("permissions").setExecutor(commandHandler);
    }

    private void setupEvents() {
        registerEvent(new ConnectionEvent(this));
    }

    private void registerEvent(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    private void hookToVault() {
        try {
            vault = (Vault) getServer().getPluginManager().getPlugin("Vault");
            if (vault == null) {
                throw new Error("Vault not found!");
            }
            vaultIntegration = new VaultIntegration(this);
            getServer().getServicesManager().register(Permission.class, vaultIntegration, vault, ServicePriority.Highest);
            StringManager.log(Level.INFO, "Hooked into vault!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
