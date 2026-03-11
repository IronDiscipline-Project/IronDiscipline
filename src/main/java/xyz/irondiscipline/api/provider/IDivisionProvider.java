package xyz.irondiscipline.api.provider;

import java.util.Set;
import java.util.UUID;

public interface IDivisionProvider {

    String getDivision(UUID playerId);

    String getDivisionDisplay(UUID playerId);

    void setDivision(UUID playerId, String division);

    void removeDivision(UUID playerId);

    boolean isMP(UUID playerId);

    boolean divisionExists(String division);

    Set<String> getAllDivisions();

    Set<UUID> getDivisionMembers(String division);
}