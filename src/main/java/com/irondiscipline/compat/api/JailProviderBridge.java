package com.irondiscipline.compat.api;

import com.irondiscipline.IronDiscipline;
import org.bukkit.entity.Player;
import xyz.irondiscipline.api.provider.IJailProvider;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class JailProviderBridge implements IJailProvider {

    private final IronDiscipline plugin;

    JailProviderBridge(IronDiscipline plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isJailed(UUID playerId) {
        return plugin.getJailManager().isJailed(playerId);
    }

    @Override
    public CompletableFuture<Boolean> isJailedAsync(UUID playerId) {
        return CompletableFuture.completedFuture(plugin.getJailManager().isJailed(playerId));
    }

    @Override
    public boolean jail(Player target, Player jailer, String reason) {
        return plugin.getJailManager().jail(target, jailer, reason);
    }

    @Override
    public boolean unjail(Player target) {
        return plugin.getJailManager().unjail(target);
    }
}