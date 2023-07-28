package dev.codedred.safedrop.data.database.manager;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.data.database.table.UsersTable;
import dev.codedred.safedrop.data.database.datasource.DataSource;
import dev.codedred.safedrop.data.database.datasource.impl.MySQL;
import lombok.Getter;

@Getter
public class DatabaseManager {

    private final DataSource dataSource;

    private UsersTable usersTable;

    public DatabaseManager(SafeDrop plugin) {
        this.dataSource = new MySQL(plugin);
    }

    public void load() {
        this.usersTable = new UsersTable(dataSource);
        this.usersTable.createTable();
    }

}
