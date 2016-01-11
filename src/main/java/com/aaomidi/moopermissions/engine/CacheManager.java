package com.aaomidi.moopermissions.engine;

import com.aaomidi.moopermissions.MooPermissions;
import com.aaomidi.moopermissions.engine.registeries.GroupIndexRegistry;
import com.aaomidi.moopermissions.model.perms.player.MPlayer;
import com.aaomidi.moopermissions.utils.StringManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Created by amir on 2015-12-17.
 */
public class CacheManager {
    private final Map<Player, MPlayer> playerReferenceMap = new HashMap<>();
    private final Map<String, MPlayer> playerNameMap = new HashMap<>();
    private final Map<UUID, MPlayer> playerUUIDMap = new HashMap<>();

    private final Cache<String, MPlayer> playerNameCache = CacheBuilder
            .newBuilder()
            .maximumSize(100)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final LinkedList<MPlayer> queue = new LinkedList<>();
    private final MooPermissions instance;

    public CacheManager(MooPermissions instance) {
        this.instance = instance;
        consumeQueue();
    }

    public void resetAllPlayers() {
        for (MPlayer mPlayer : playerReferenceMap.values()) {
            mPlayer.reset();
            mPlayer.apply();
        }
    }

    /**
     * Checks for updates every 10 ticks.
     */
    private void consumeQueue() {
        new BukkitRunnable() {
            @Override
            public void run() {
                do {
                    if (queue.isEmpty())
                        break;

                    MPlayer mPlayer = queue.removeFirst();
                    if (mPlayer == null || mPlayer.getPlayer() == null) continue;
                    MPlayer newPlayer = instance.getMySQL().getPlayer(mPlayer.getId(), mPlayer.getName(), mPlayer.getUuid());
                    mPlayer.update(newPlayer);
                    queue.addLast(mPlayer);
                    break;
                } while (true);
            }
        }.runTaskTimerAsynchronously(instance, 100L, 10L);

        new BukkitRunnable() {
            @Override
            public void run() {
                GroupIndexRegistry.reset();
                instance.getMySQL().registerGroups();
            }
        }.runTaskTimer(instance, 95L, 200L);
    }

    public void initializePlayer(Player player) {
        if (playerUUIDMap.containsKey(player.getUniqueId())) {
            return;
        }

        String playerName = player.getName().toLowerCase();
        UUID uuid = player.getUniqueId();

        MPlayer mPlayer = instance.getMySQL().initPlayer(player);
        if (mPlayer == null) {
            throw new RuntimeException(new NoSuchElementException("MPlayer could not be initialized."));
        }
        StringManager.log(Level.INFO, "&bApplying perms.");
        mPlayer.apply();
        StringManager.log(Level.INFO, "&bApplied.");
        /* Put them inside a map */
        playerReferenceMap.put(player, mPlayer);
        playerNameMap.put(playerName, mPlayer);
        playerUUIDMap.put(uuid, mPlayer);

        queue.addLast(mPlayer);
    }

    public MPlayer getPlayer(UUID uuid) {
        return playerUUIDMap.get(uuid);
    }

    public MPlayer getPlayer(Player player) {
        return playerReferenceMap.get(player);
    }

    public MPlayer getPlayer(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return getPlayer(player);
        }
        return instance.getMySQL().getPlayer(playerName);
    }

    public void getPlayerAsync(String playerName, Consumer<MPlayer> consumer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                MPlayer mplayer = getPlayer(playerName);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        consumer.accept(mplayer);
                    }
                }.runTask(instance);
            }
        }.runTaskAsynchronously(instance);
    }

    public boolean isPlayerCached(String playerName) {
        return playerNameCache.getIfPresent(playerName.toLowerCase()) != null;
    }

    public void cleanUpPlayer(Player player) {
        MPlayer mPlayer = playerReferenceMap.get(player);

        playerReferenceMap.remove(player);
        playerNameMap.remove(player.getName());
        playerUUIDMap.remove(player.getUniqueId());

        if (mPlayer != null) {
            queue.remove(mPlayer);
        }
    }
}

