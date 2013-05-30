package com.yuvalshavit.mib.schema;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.yuvalshavit.mib.pk.PkFieldInsertProvider;
import com.yuvalshavit.mib.run.ConnectionProvider;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaCreator {
  private final PkFieldInsertProvider pkFieldProvider;
  private final ConnectionProvider connector;
  private final boolean warmup;

  public SchemaCreator(PkFieldInsertProvider pkFieldProvider, ConnectionProvider connector, boolean warmup) {
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

  private String getSql(String resourceName) throws IOException {
    URL url = Resources.getResource(SchemaCreator.class, resourceName);
    String sql = Resources.toString(url, Charsets.UTF_8);
    String engine = warmup ? "BLACKHOLE" : "InnoDB";
    String autoInc = pkFieldProvider.isAutoIncrement() ? "AUTO_INCREMENT" : "";
    sql = sql.replace("$ENGINE$", engine);
    sql = sql.replace("$ID_TYPE$", pkFieldProvider.getSqlDefinition());
    sql = sql.replace("$AUTO_INCREMENT$", autoInc);
    return sql;
  }
}
