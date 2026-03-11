package xyz.irondiscipline.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import xyz.irondiscipline.api.model.KillLog;

public class PlayerKillEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player killer;
    private final Player victim;
    private final KillLog killLog;

    public PlayerKillEvent(Player killer, Player victim, KillLog killLog) {
        super(true);
        this.killer = killer;
        this.victim = victim;
        this.killLog = killLog;
    }

    public Player getKiller() {
        return killer;
    }

    public Player getVictim() {
        return victim;
    }

    public KillLog getKillLog() {
        return killLog;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}