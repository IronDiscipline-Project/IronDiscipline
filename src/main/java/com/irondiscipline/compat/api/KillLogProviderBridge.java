package com.irondiscipline.compat.api;

import com.irondiscipline.IronDiscipline;
import com.irondiscipline.model.KillLog;
import xyz.irondiscipline.api.provider.IKillLogProvider;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class KillLogProviderBridge implements IKillLogProvider {

    private final IronDiscipline plugin;

    KillLogProviderBridge(IronDiscipline plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<Void> saveKillLogAsync(xyz.irondiscipline.api.model.KillLog log) {
        KillLog internal = KillLog.builder()
                .id(log.getId())
                .timestamp(log.getTimestamp())
                .killer(log.getKillerId(), log.getKillerName())
                .victim(log.getVictimId(), log.getVictimName())
                .weapon(log.getWeapon())
                .distance(log.getDistance())
                .location(log.getWorld(), log.getX(), log.getY(), log.getZ())
                .build();
        return plugin.getStorageManager().saveKillLogAsync(internal);
    }

    @Override
    public CompletableFuture<List<xyz.irondiscipline.api.model.KillLog>> getKillLogsAsync(UUID playerId, int limit) {
        return plugin.getStorageManager().getKillLogsAsync(playerId, limit)
                .thenApply(logs -> logs.stream().map(ApiCompat::toApiKillLog).toList());
    }

    @Override
    public CompletableFuture<List<xyz.irondiscipline.api.model.KillLog>> getAllKillLogsAsync(int limit) {
        return plugin.getStorageManager().getAllKillLogsAsync(limit)
                .thenApply(logs -> logs.stream().map(ApiCompat::toApiKillLog).toList());
    }
}