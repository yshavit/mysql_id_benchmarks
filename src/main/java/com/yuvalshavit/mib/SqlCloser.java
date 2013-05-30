package com.yuvalshavit.mib;

import com.google.common.io.Closer;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SqlCloser {

  private final Closer closer = Closer.create();

  public Connection register(final Connection connection) {
    closer.register(new SqlCloseable() {
      @Override
      public void closeSql() throws SQLException {
        connection.close();
      }
    });
    return connection;
  }

  public PreparedStatement register(final PreparedStatement statement) {
    closer.register(new SqlCloseable() {
      @Override
      public void closeSql() throws SQLException {
        statement.close();
      }
    });
    return statement;
  }

  public void close() {
    try {
      closer.close();
    }
    catch (IOException e) {
      e.printStackTrace(); // TODO log this properly
    }
  }

  private static abstract class SqlCloseable implements Closeable {
    protected abstract void closeSql() throws SQLException;

    @Override
    public void close() throws IOException {
      try {
        closeSql();
      }
      catch (SQLException e) {
        throw new IOException(e);
      }
    }
  }
}
