package com.irondiscipline.compat.api;

import com.irondiscipline.IronDiscipline;
import xyz.irondiscipline.api.provider.IDivisionProvider;

import java.util.Set;
import java.util.UUID;

final class DivisionProviderBridge implements IDivisionProvider {

    private final IronDiscipline plugin;

    DivisionProviderBridge(IronDiscipline plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getDivision(UUID playerId) {
        return plugin.getDivisionManager().getDivision(playerId);
    }

    @Override
    public String getDivisionDisplay(UUID playerId) {
        return plugin.getDivisionManager().getDivisionDisplay(playerId);
    }

    @Override
    public void setDivision(UUID playerId, String division) {
        plugin.getDivisionManager().setDivision(playerId, division);
    }

    @Override
    public void removeDivision(UUID playerId) {
        plugin.getDivisionManager().removeDivision(playerId);
    }

    @Override
    public boolean isMP(UUID playerId) {
        return plugin.getDivisionManager().isMP(playerId);
    }

    @Override
    public boolean divisionExists(String division) {
        return plugin.getDivisionManager().divisionExists(division);
    }

    @Override
    public Set<String> getAllDivisions() {
        return plugin.getDivisionManager().getAllDivisions();
    }

    @Override
    public Set<UUID> getDivisionMembers(String division) {
        return plugin.getDivisionManager().getDivisionMembers(division);
    }
}