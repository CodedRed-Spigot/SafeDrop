package dev.codedred.safedrop.data.database.table;

import dev.codedred.safedrop.data.database.datasource.DataSource;
import dev.codedred.safedrop.model.User;
import dev.codedred.safedrop.utils.async.Async;
import lombok.val;

import java.sql.SQLException;
import java.util.UUID;

public class UsersTable {

    private static final String TABLE_NAME = "safedrop_users";

    private final DataSource dataSource;

    public UsersTable(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createTable() {
        try (val preparedStatement = dataSource.getConnection().prepareStatement(String.format("CREATE TABLE IF NOT EXISTS `%s` (" +
                "`uniqueId` VARCHAR(36) NOT NULL," +
                "`enabled` BOOLEAN NOT NULL)", TABLE_NAME))) {
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void insert(User user) {
        Async.run(() -> {
            try (val preparedStatement = dataSource.getConnection().prepareStatement(String.format("INSERT INTO `%s` " +
                    "(`uniqueId`, `enabled`) VALUES (?, ?)", TABLE_NAME))) {
                preparedStatement.setString(1, user.getUniqueId().toString());
                preparedStatement.setBoolean(2, user.isEnabled());

                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void update(User user) {
        Async.run(() -> {
            try (val preparedStatement = dataSource.getConnection().prepareStatement(String.format("UPDATE `%s` " +
                    " SET `enabled` = ? WHERE `uniqueId` = ?", TABLE_NAME))) {
                preparedStatement.setBoolean(1, user.isEnabled());
                preparedStatement.setString(2, user.getUniqueId().toString());
                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }


    public User getByUuid(UUID uuid) {
        try (val preparedStatement = dataSource.getConnection().prepareStatement(String.format("SELECT * FROM `%s` " +
                "WHERE `uniqueId` = ?", TABLE_NAME))) {
            preparedStatement.setString(1, uuid.toString());

            try (val resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next())
                    return new User(
                            UUID.fromString(resultSet.getString("uniqueId")),
                            resultSet.getBoolean("enabled")
                    );

            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
