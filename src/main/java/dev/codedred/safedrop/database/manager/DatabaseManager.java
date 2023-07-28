package dev.codedred.safedrop.database.manager;

import dev.codedred.safedrop.SafeDrop;
import dev.codedred.safedrop.database.datasource.DataSource;
import dev.codedred.safedrop.database.datasource.impl.MySQL;
import dev.codedred.safedrop.database.table.UsersTable;
import lombok.Getter;
import lombok.val;

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
