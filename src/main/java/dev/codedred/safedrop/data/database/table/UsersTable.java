package dev.codedred.safedrop.data.database.table;

import dev.codedred.safedrop.data.database.datasource.DataSource;
import dev.codedred.safedrop.data.database.datasource.impl.SQLite;
import dev.codedred.safedrop.model.User;
import dev.codedred.safedrop.utils.async.Async;
import java.sql.SQLException;
import java.util.UUID;
import lombok.val;

public class UsersTable {

  private static final String TABLE_NAME = "safedrop_users";

  private final DataSource dataSource;

  public UsersTable(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void createTable() {
    String createTableSql;

    if (dataSource instanceof SQLite) {
      createTableSql =
        String.format(
          "CREATE TABLE IF NOT EXISTS `%s` (" +
          "`uniqueId` TEXT NOT NULL," +
          "`enabled` INTEGER NOT NULL)",
          TABLE_NAME
        );
    } else {
      createTableSql =
        String.format(
          "CREATE TABLE IF NOT EXISTS `%s` (" +
          "`uniqueId` VARCHAR(36) NOT NULL," +
          "`enabled` BOOLEAN NOT NULL)",
          TABLE_NAME
        );
    }

    try (
      val preparedStatement = dataSource
        .getConnection()
        .prepareStatement(createTableSql)
    ) {
      preparedStatement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }

  public void insert(User user) {
    Async.run(() -> {
      try (
        val preparedStatement = dataSource
          .getConnection()
          .prepareStatement(
            String.format(
              "INSERT INTO `%s` " + "(`uniqueId`, `enabled`) VALUES (?, ?)",
              TABLE_NAME
            )
          )
      ) {
        preparedStatement.setString(1, user.getUniqueId().toString());

        if (dataSource instanceof SQLite) {
          preparedStatement.setInt(2, user.isEnabled() ? 1 : 0);
        } else {
          preparedStatement.setBoolean(2, user.isEnabled());
        }

        preparedStatement.executeUpdate();
      } catch (SQLException exception) {
        exception.printStackTrace();
      }
    });
  }

  public void update(User user) {
    Async.run(() -> {
      try (
        val preparedStatement = dataSource
          .getConnection()
          .prepareStatement(
            String.format(
              "UPDATE `%s` " + " SET `enabled` = ? WHERE `uniqueId` = ?",
              TABLE_NAME
            )
          )
      ) {
        if (dataSource instanceof SQLite) {
          preparedStatement.setInt(1, user.isEnabled() ? 1 : 0);
        } else {
          preparedStatement.setBoolean(1, user.isEnabled());
        }

        preparedStatement.setString(2, user.getUniqueId().toString());
        preparedStatement.executeUpdate();
      } catch (SQLException exception) {
        exception.printStackTrace();
      }
    });
  }

  public User getByUuid(UUID uuid) {
    try (
      val preparedStatement = dataSource
        .getConnection()
        .prepareStatement(
          String.format(
            "SELECT * FROM `%s` " + "WHERE `uniqueId` = ?",
            TABLE_NAME
          )
        )
    ) {
      preparedStatement.setString(1, uuid.toString());

      try (val resultSet = preparedStatement.executeQuery()) {
        if (resultSet.next()) {
          boolean enabled;
          if (dataSource instanceof SQLite) {
            enabled = resultSet.getInt("enabled") != 0;
          } else {
            enabled = resultSet.getBoolean("enabled");
          }

          return new User(
            UUID.fromString(resultSet.getString("uniqueId")),
            enabled
          );
        }
      }
    } catch (SQLException exception) {
      exception.printStackTrace();
    }

    return null;
  }
}
