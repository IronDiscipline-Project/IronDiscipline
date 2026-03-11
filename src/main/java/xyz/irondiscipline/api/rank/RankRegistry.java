package xyz.irondiscipline.api.rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class RankRegistry {

    private static final Map<String, IRank> REGISTRY = new ConcurrentHashMap<>();
    private static volatile List<IRank> sortedRanks = Collections.emptyList();

    static {
        for (IRank rank : CoreRanks.values()) {
            registerInternal(rank);
        }
        rebuildSortedList();
    }

    private RankRegistry() {
    }

    public static void register(IRank rank) {
        if (rank == null) {
            throw new IllegalArgumentException("Rank cannot be null");
        }
        if (rank.getId() == null || rank.getId().isEmpty()) {
            throw new IllegalArgumentException("Rank ID cannot be null or empty");
        }
        registerInternal(rank);
        rebuildSortedList();
    }

    public static void registerAll(IRank... ranks) {
        for (IRank rank : ranks) {
            if (rank != null) {
                registerInternal(rank);
            }
        }
        rebuildSortedList();
    }

    public static IRank unregister(String id) {
        IRank removed = REGISTRY.remove(id.toUpperCase());
        if (removed != null) {
            rebuildSortedList();
        }
        return removed;
    }

    public static IRank fromId(String id) {
        if (id == null) {
            return CoreRanks.PRIVATE;
        }
        IRank rank = REGISTRY.get(id.toUpperCase());
        return rank != null ? rank : CoreRanks.PRIVATE;
    }

    public static IRank fromIdOrNull(String id) {
        if (id == null) {
            return null;
        }
        return REGISTRY.get(id.toUpperCase());
    }

    public static IRank fromWeight(int weight) {
        IRank result = CoreRanks.PRIVATE;
        for (IRank rank : sortedRanks) {
            if (rank.getWeight() <= weight) {
                result = rank;
            } else {
                break;
            }
        }
        return result;
    }

    public static IRank getNextRank(IRank current) {
        List<IRank> sorted = sortedRanks;
        for (int index = 0; index < sorted.size() - 1; index++) {
            if (sorted.get(index).getId().equals(current.getId())) {
                return sorted.get(index + 1);
            }
        }
        return null;
    }

    public static IRank getPreviousRank(IRank current) {
        List<IRank> sorted = sortedRanks;
        for (int index = 1; index < sorted.size(); index++) {
            if (sorted.get(index).getId().equals(current.getId())) {
                return sorted.get(index - 1);
            }
        }
        return null;
    }

    public static List<IRank> values() {
        return sortedRanks;
    }

    public static List<IRank> valuesByNamespace(String namespace) {
        List<IRank> result = new ArrayList<>();
        for (IRank rank : sortedRanks) {
            if (rank.getNamespace().equals(namespace)) {
                result.add(rank);
            }
        }
        return Collections.unmodifiableList(result);
    }

    public static boolean isRegistered(String id) {
        return id != null && REGISTRY.containsKey(id.toUpperCase());
    }

    public static int size() {
        return REGISTRY.size();
    }

    private static void registerInternal(IRank rank) {
        REGISTRY.put(rank.getId().toUpperCase(), rank);
    }

    private static void rebuildSortedList() {
        List<IRank> list = new ArrayList<>(REGISTRY.values());
        list.sort(Comparator.comparingInt(IRank::getWeight));
        sortedRanks = Collections.unmodifiableList(list);
    }
}