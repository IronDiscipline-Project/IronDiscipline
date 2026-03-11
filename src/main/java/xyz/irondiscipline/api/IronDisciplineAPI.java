package xyz.irondiscipline.api;

import org.bukkit.Bukkit;
import xyz.irondiscipline.api.provider.IDivisionProvider;
import xyz.irondiscipline.api.provider.IEconomyProvider;
import xyz.irondiscipline.api.provider.IJailProvider;
import xyz.irondiscipline.api.provider.IKillLogProvider;
import xyz.irondiscipline.api.provider.IRankProvider;
import xyz.irondiscipline.api.provider.IStorageProvider;
import xyz.irondiscipline.api.provider.ITerritoryProvider;

public final class IronDisciplineAPI {

    private IronDisciplineAPI() {
    }

    public static IRankProvider getRankProvider() {
        return getService(IRankProvider.class);
    }

    public static IJailProvider getJailProvider() {
        return getService(IJailProvider.class);
    }

    public static IDivisionProvider getDivisionProvider() {
        return getService(IDivisionProvider.class);
    }

    public static IKillLogProvider getKillLogProvider() {
        return getService(IKillLogProvider.class);
    }

    public static IStorageProvider getStorageProvider() {
        return getService(IStorageProvider.class);
    }

    public static IEconomyProvider getEconomyProvider() {
        return getService(IEconomyProvider.class);
    }

    public static ITerritoryProvider getTerritoryProvider() {
        return getService(ITerritoryProvider.class);
    }

    public static boolean isAvailable() {
        return getService(IRankProvider.class) != null;
    }

    private static <T> T getService(Class<T> clazz) {
        return Bukkit.getServicesManager().load(clazz);
    }
}