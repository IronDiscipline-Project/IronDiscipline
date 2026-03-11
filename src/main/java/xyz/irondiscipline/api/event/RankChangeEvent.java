package xyz.irondiscipline.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.irondiscipline.api.rank.IRank;

public class RankChangeEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final IRank oldRank;
    private final IRank newRank;
    private final Cause cause;

    public RankChangeEvent(Player player, IRank oldRank, IRank newRank, Cause cause) {
        super(true);
        this.player = player;
        this.oldRank = oldRank;
        this.newRank = newRank;
        this.cause = cause;
    }

    public Player getPlayer() {
        return player;
    }

    public IRank getOldRank() {
        return oldRank;
    }

    public IRank getNewRank() {
        return newRank;
    }

    public Cause getCause() {
        return cause;
    }

    public boolean isPromotion() {
        return newRank.isHigherThan(oldRank);
    }

    public boolean isDemotion() {
        return newRank.isLowerThan(oldRank);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public enum Cause {
        PROMOTE,
        DEMOTE,
        SET,
        AUTO_PROMOTE,
        API,
        OTHER
    }
}