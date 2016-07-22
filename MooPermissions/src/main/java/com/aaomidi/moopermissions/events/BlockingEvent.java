package com.aaomidi.moopermissions.events;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.utils.StringManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class BlockingEvent implements Listener {
    private final MooPermissions instance;
    private final Cache<Player, Boolean> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).weakKeys().build();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Boolean exists = cache.getIfPresent(player);
        if (exists == null) {
            exists = false;
        }

        if (instance.getCacheManager().isLocked(player) && !player.isOp()) {
            event.setTo(event.getFrom());
            if (!exists) {
                cache.put(player, true);
                StringManager.sendMessage(player, "&cPlease wait a few seconds before moving, we're just gathering your data!.");
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (instance.getCacheManager().isLocked(player) && !player.isOp()) {
            StringManager.sendMessage(player, "&cPlease wait a few seconds before entering a command, we're just gathering your data!.");

            event.setMessage("");
            event.setCancelled(true);
        }
    }

}
