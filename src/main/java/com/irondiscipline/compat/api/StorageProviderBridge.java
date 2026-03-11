package com.irondiscipline.compat.api;

import com.irondiscipline.IronDiscipline;
import xyz.irondiscipline.api.provider.IStorageProvider;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;

final class StorageProviderBridge implements IStorageProvider {

    private final IronDiscipline plugin;

    StorageProviderBridge(IronDiscipline plugin) {
        this.plugin = plugin;
    }

    @Override
    public Connection getConnection() {
        return plugin.getStorageManager().getConnection();
    }

    @Override
    public ExecutorService getDbExecutor() {
        return plugin.getStorageManager().getDbExecutor();
    }

    @Override
    public String getDatabaseType() {
        return plugin.getStorageManager().getDatabaseType();
    }
}