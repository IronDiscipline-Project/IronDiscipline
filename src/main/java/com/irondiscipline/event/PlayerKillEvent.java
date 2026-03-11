package com.irondiscipline.event;

import com.irondiscipline.model.KillLog;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKillEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player killer;
    private final Player victim;
    private final KillLog killLog;

    public PlayerKillEvent(Player killer, Player victim, KillLog killLog) {
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
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}