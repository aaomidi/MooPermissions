package com.aaomidi.moopermissions.events;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.model.perms.player.MPlayer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by amir on 2015-12-17.
 */
@RequiredArgsConstructor
public class ConnectionEvent implements Listener {
    private final MooPermissions instance;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        instance.getCacheManager().initializePlayer(player);

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MPlayer mPlayer = instance.getCacheManager().getPlayer(player);
        mPlayer.apply();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        instance.getCacheManager().cleanUpPlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        instance.getCacheManager().cleanUpPlayer(player);
    }
}
