package com.irondiscipline.util;

import com.irondiscipline.IronDiscipline;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

/**
 * PaperSpigot 向けタスクスケジューラー
 * BukkitScheduler ベースの実装（Folia非対応）
 */
public class TaskScheduler {

    private final IronDiscipline plugin;

    public TaskScheduler(IronDiscipline plugin) {
        this.plugin = plugin;
    }

    /**
     * 非同期タスクを実行
     */
    public BukkitTask runAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * グローバルタスクを実行（メインスレッド）
     */
    public BukkitTask runGlobal(Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    /**
     * 遅延タスク (グローバル)
     * @param ticks 遅延Tick
     */
    public BukkitTask runGlobalLater(Runnable runnable, long ticks) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, ticks);
    }

    /**
     * 定期タスク (グローバル)
     * @param delayTicks 開始遅延
     * @param periodTicks 周期
     */
    public BukkitTask runGlobalTimer(Runnable runnable, long delayTicks, long periodTicks) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks);
    }

    /**
     * 定期タスク (グローバル) - タスク自己参照可能
     */
    public void runGlobalTimer(Consumer<BukkitTask> task, long delayTicks, long periodTicks) {
        Bukkit.getScheduler().runTaskTimer(plugin, new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                task.accept(new BukkitTaskWrapper(this));
            }
        }, delayTicks, periodTicks);
    }

    /**
     * エンティティに関連付けられたタスクを実行（Paper: メインスレッドで実行）
     */
    public BukkitTask runEntity(Entity entity, Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    /**
     * エンティティに関連付けられた遅延タスクを実行（Paper: メインスレッドで実行）
     */
    public BukkitTask runEntityLater(Entity entity, Runnable runnable, long delayTicks) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
    }

    /**
     * エンティティに関連付けられた定期タスクを実行（Paper: メインスレッドで実行）
     */
    public BukkitTask runEntityTimer(Entity entity, Runnable runnable, long delayTicks, long periodTicks) {
        return Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks);
    }

    /**
     * エンティティに関連付けられた定期タスクを実行（自己参照可能）
     */
    public void runEntityTimer(Entity entity, Consumer<BukkitTask> task, long delayTicks, long periodTicks) {
        Bukkit.getScheduler().runTaskTimer(plugin, new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                task.accept(new BukkitTaskWrapper(this));
            }
        }, delayTicks, periodTicks);
    }

    /**
     * 特定の場所でタスクを実行（Paper: メインスレッドで実行）
     */
    public BukkitTask runRegion(Location location, Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    /**
     * タスクをキャンセル
     */
    public void cancel(BukkitTask task) {
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * BukkitRunnable を BukkitTask インターフェースとしてラップ
     * Consumer<BukkitTask> パターンで自己キャンセル可能にする
     */
    private static class BukkitTaskWrapper implements BukkitTask {
        private final org.bukkit.scheduler.BukkitRunnable runnable;

        BukkitTaskWrapper(org.bukkit.scheduler.BukkitRunnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public int getTaskId() {
            return -1;
        }

        @Override
        public org.bukkit.plugin.Plugin getOwner() {
            return null;
        }

        @Override
        public boolean isSync() {
            return true;
        }

        @Override
        public boolean isCancelled() {
            return runnable.isCancelled();
        }

        @Override
        public void cancel() {
            runnable.cancel();
        }
    }
}
