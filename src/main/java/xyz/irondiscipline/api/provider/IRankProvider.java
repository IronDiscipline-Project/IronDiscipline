package xyz.irondiscipline.api.provider;

import org.bukkit.entity.Player;
import xyz.irondiscipline.api.rank.IRank;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IRankProvider {

    IRank getRank(Player player);

    CompletableFuture<IRank> getRankAsync(UUID playerId);

    CompletableFuture<Boolean> setRank(Player player, IRank newRank);

    CompletableFuture<Boolean> setRankByUUID(UUID playerId, String playerName, IRank newRank);

    CompletableFuture<IRank> promote(Player player);

    CompletableFuture<IRank> demote(Player player);

    boolean requiresPTS(Player player);

    boolean isHigherRank(Player officer, Player target);
}