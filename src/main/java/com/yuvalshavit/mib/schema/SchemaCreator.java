package com.yuvalshavit.mib.schema;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.io.Resources;
import com.yuvalshavit.mib.pk.PkFieldProvider;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaCreator {
  private final PkFieldProvider pkFieldProvider;
  private final Supplier<? extends Connection> connector;
  private final boolean warmup;

  public SchemaCreator(PkFieldProvider pkFieldProvider, Supplier<? extends Connection> connector, boolean warmup) {
    this.pkFieldProvider = pkFieldProvider;
    this.connector = connector;
    this.warmup = warmup;
  }

  public void run() throws IOException, SQLException {
    Connection connection = connector.get();
    try {
      runSql(connection, "DROP TABLE IF EXISTS `main`");
      runSql(connection, "DROP TABLE IF EXISTS `secondary`");
      runSql(connection, getSql("main.sql"));
      runSql(connection, getSql("secondary.sql"));
    }
    finally {
      if (!connection.isClosed())
        connection.close();
    }
  }

  private void runSql(Connection connection, String sql) throws SQLException {
    Statement statement = connection.createStatement();
    try {
      statement.execute(sql);
    }
    finally {
      if (!statement.isClosed())
        statement.close();
    }
  }

  public String getSql(String resourceName) throws IOException {
    URL url = Resources.getResource(resourceName);
    String sql = Resources.toString(url, Charsets.UTF_8);
    String engine = warmup ? "BLACKHOLE" : "InnoDB";
    sql = sql.replace("$ENGINE$", engine);
    sql = sql.replace("ID_TYPE$", pkFieldProvider.getSqlDefinition());
    return sql;
  }
}