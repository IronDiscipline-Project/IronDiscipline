package xyz.irondiscipline.api.provider;

import xyz.irondiscipline.api.model.KillLog;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IKillLogProvider {

    CompletableFuture<Void> saveKillLogAsync(KillLog log);

    CompletableFuture<List<KillLog>> getKillLogsAsync(UUID playerId, int limit);

    CompletableFuture<List<KillLog>> getAllKillLogsAsync(int limit);
}