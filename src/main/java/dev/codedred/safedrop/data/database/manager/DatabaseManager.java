package dev.codedred.safedrop.data.database.manager;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.database.datasource.impl.SQLite;
import dev.codedred.safedrop.data.database.table.UsersTable;
import dev.codedred.safedrop.data.database.datasource.DataSource;
import dev.codedred.safedrop.data.database.datasource.impl.MySQL;
import lombok.Getter;

import java.sql.SQLException;

@Getter
public class DatabaseManager {

    private final SafeDrop plugin;

    private DataSource dataSource;
    private UsersTable usersTable;

    public DatabaseManager(SafeDrop plugin) {
        this.plugin = plugin;
        setupDataSource();
    }

    private void setupDataSource() {
        String type = plugin.getConfig().getString("database-settings.type");

        switch (type.toLowerCase()) {
            case "mysql":
                this.dataSource = new MySQL(plugin);
                break;
            case "sqlite":
                this.dataSource = new SQLite(plugin);
                break;
            default:
                throw new IllegalStateException("Unexpected database type: " + type + ". Accepted Values: 'mysql', 'sqlite'");
        }
    }

    public void load() {
        this.usersTable = new UsersTable(dataSource);
        this.usersTable.createTable();
    }

    public void reload() {
        try {
            if (dataSource != null) {
                dataSource.closeConnection();
            }

            setupDataSource();
            load();
        } catch (SQLException e) {
            plugin.getLogger().warning("[SafeDrop] Error while trying to load database!");
        }
    }
}

