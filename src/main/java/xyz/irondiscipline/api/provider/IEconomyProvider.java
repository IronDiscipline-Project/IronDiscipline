package xyz.irondiscipline.api.provider;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IEconomyProvider {

    CompletableFuture<Double> getBalance(UUID playerId);

    CompletableFuture<Boolean> withdraw(UUID playerId, double amount);

    CompletableFuture<Boolean> deposit(UUID playerId, double amount);

    CompletableFuture<Boolean> transfer(UUID fromId, UUID toId, double amount);

    CompletableFuture<Boolean> has(UUID playerId, double amount);
}