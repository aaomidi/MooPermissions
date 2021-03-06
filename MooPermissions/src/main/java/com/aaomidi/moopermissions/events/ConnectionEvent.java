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

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Created by amir on 2015-12-17.
 */
@RequiredArgsConstructor
public class ConnectionEvent implements Listener {
    private final MooPermissions instance;

    private final Set<Player> joinCalled = Collections.newSetFromMap(new WeakHashMap<>());

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        instance.getCacheManager().lockPlayer(player);

        instance.getServer().getScheduler().runTaskAsynchronously(instance, () -> {
            MPlayer mPlayer = instance.getCacheManager().initializePlayer(player);


            instance.getServer().getScheduler().runTask(instance, () -> {
                instance.getCacheManager().unlockPLayer(player);

                mPlayer.reset();
                mPlayer.apply();
            });
        });

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
