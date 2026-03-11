package xyz.irondiscipline.api.provider;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ITerritoryProvider {

    CompletableFuture<UUID> getOwner(Chunk chunk);

    CompletableFuture<Boolean> claim(Player player, Chunk chunk);

    CompletableFuture<Boolean> unclaim(Chunk chunk);

    CompletableFuture<Integer> getClaimCount(UUID playerId);

    CompletableFuture<Set<Long>> getClaimedChunks(UUID playerId);

    CompletableFuture<Boolean> isClaimed(Chunk chunk);
}