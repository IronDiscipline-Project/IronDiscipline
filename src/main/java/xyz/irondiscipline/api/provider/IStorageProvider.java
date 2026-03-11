package xyz.irondiscipline.api.provider;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;

public interface IStorageProvider {

    Connection getConnection();

    ExecutorService getDbExecutor();

    String getDatabaseType();
}