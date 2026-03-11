package xyz.irondiscipline.api.rank;

import org.bukkit.ChatColor;

public interface IRank {

    String getId();

    String getDisplayRaw();

    default String getDisplay() {
        return ChatColor.translateAlternateColorCodes('&', getDisplayRaw());
    }

    int getWeight();

    default boolean isHigherThan(IRank other) {
        return this.getWeight() > other.getWeight();
    }

    default boolean isLowerThan(IRank other) {
        return this.getWeight() < other.getWeight();
    }

    default String getNamespace() {
        return "core";
    }
}