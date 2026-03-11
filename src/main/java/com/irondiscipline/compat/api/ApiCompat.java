package com.irondiscipline.compat.api;

import com.irondiscipline.IronDiscipline;
import com.irondiscipline.model.KillLog;
import com.irondiscipline.model.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import xyz.irondiscipline.api.event.RankChangeEvent;
import xyz.irondiscipline.api.rank.IRank;
import xyz.irondiscipline.api.rank.RankRegistry;
import xyz.irondiscipline.api.provider.IDivisionProvider;
import xyz.irondiscipline.api.provider.IJailProvider;
import xyz.irondiscipline.api.provider.IKillLogProvider;
import xyz.irondiscipline.api.provider.IRankProvider;
import xyz.irondiscipline.api.provider.IStorageProvider;

public final class ApiCompat {

    private ApiCompat() {
    }

    public static void registerServices(IronDiscipline plugin) {
        Bukkit.getServicesManager().register(IRankProvider.class, new RankProviderBridge(plugin), plugin, ServicePriority.Normal);
        Bukkit.getServicesManager().register(IJailProvider.class, new JailProviderBridge(plugin), plugin, ServicePriority.Normal);
        Bukkit.getServicesManager().register(IDivisionProvider.class, new DivisionProviderBridge(plugin), plugin, ServicePriority.Normal);
        Bukkit.getServicesManager().register(IKillLogProvider.class, new KillLogProviderBridge(plugin), plugin, ServicePriority.Normal);
        Bukkit.getServicesManager().register(IStorageProvider.class, new StorageProviderBridge(plugin), plugin, ServicePriority.Normal);
    }

    public static void unregisterServices(IronDiscipline plugin) {
        Bukkit.getServicesManager().unregisterAll(plugin);
    }

    public static IRank toApiRank(Rank rank) {
        return RankRegistry.fromId(rank != null ? rank.getId() : null);
    }

    public static xyz.irondiscipline.api.model.KillLog toApiKillLog(KillLog log) {
        if (log == null) {
            return null;
        }
        return xyz.irondiscipline.api.model.KillLog.builder()
                .id(log.getId())
                .timestamp(log.getTimestamp())
                .killer(log.getKillerId(), log.getKillerName())
                .victim(log.getVictimId(), log.getVictimName())
                .weapon(log.getWeapon())
                .distance(log.getDistance())
                .location(log.getWorld(), log.getX(), log.getY(), log.getZ())
                .build();
    }

    public static void fireRankChange(Player player, Rank oldRank, Rank newRank, RankChangeEvent.Cause cause) {
        if (player == null || oldRank == null || newRank == null || oldRank == newRank) {
            return;
        }
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager == null) {
            return;
        }
        pluginManager.callEvent(
                new RankChangeEvent(player, toApiRank(oldRank), toApiRank(newRank), cause)
        );
    }
}