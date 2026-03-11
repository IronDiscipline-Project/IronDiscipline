package xyz.irondiscipline.api.provider;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IJailProvider {

    boolean isJailed(UUID playerId);

    CompletableFuture<Boolean> isJailedAsync(UUID playerId);

    boolean jail(Player target, Player jailer, String reason);

    boolean unjail(Player target);
}