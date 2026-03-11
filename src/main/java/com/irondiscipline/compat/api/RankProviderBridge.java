package com.irondiscipline.compat.api;

import com.irondiscipline.IronDiscipline;
import com.irondiscipline.model.Rank;
import org.bukkit.entity.Player;
import xyz.irondiscipline.api.provider.IRankProvider;
import xyz.irondiscipline.api.rank.IRank;
import xyz.irondiscipline.api.rank.RankRegistry;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class RankProviderBridge implements IRankProvider {

    private final IronDiscipline plugin;

    RankProviderBridge(IronDiscipline plugin) {
        this.plugin = plugin;
    }

    @Override
    public IRank getRank(Player player) {
        return ApiCompat.toApiRank(plugin.getRankManager().getRank(player));
    }

    @Override
    public CompletableFuture<IRank> getRankAsync(UUID playerId) {
        return plugin.getRankManager().getRankAsync(playerId).thenApply(ApiCompat::toApiRank);
    }

    @Override
    public CompletableFuture<Boolean> setRank(Player player, IRank newRank) {
        return plugin.getRankManager().setRank(player, Rank.fromId(newRank.getId()));
    }

    @Override
    public CompletableFuture<Boolean> setRankByUUID(UUID playerId, String playerName, IRank newRank) {
        return plugin.getRankManager().setRankByUUID(playerId, Rank.fromId(newRank.getId()));
    }

    @Override
    public CompletableFuture<IRank> promote(Player player) {
        return plugin.getRankManager().promote(player).thenApply(ApiCompat::toApiRank);
    }

    @Override
    public CompletableFuture<IRank> demote(Player player) {
        return plugin.getRankManager().demote(player).thenApply(ApiCompat::toApiRank);
    }

    @Override
    public boolean requiresPTS(Player player) {
        return plugin.getRankManager().requiresPTS(player);
    }

    @Override
    public boolean isHigherRank(Player officer, Player target) {
        return plugin.getRankManager().isHigherRank(officer, target);
    }
}