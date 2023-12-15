package dev.codedred.safedrop.data.database.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource {
  Connection getConnection();

  void closeConnection() throws SQLException;
}
