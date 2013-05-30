package com.yuvalshavit.mib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class MainInsertTest implements Test {
  private final Random random = new Random();
  private final PkFieldProvider pkFieldProvider;

  public MainInsertTest(PkFieldProvider pkFieldProvider) {
    this.pkFieldProvider = pkFieldProvider;
  }

  @Override
  public PreparedStatement setUp(Connection connection) throws SQLException {
    String sql = pkFieldProvider.isAutoIncrement()
        ? "INSERT INTO main (val1, val2) VALUES (?,?)"
        : "INSERT INTO main (mid, val1, val2) VALUES (?,?,?)";
    return connection.prepareCall(sql);
  }

  @Override
  public void run(PreparedStatement statement) throws Exception {
    int pos = pkFieldProvider.isAutoIncrement() ? 1 : 0;
    pkFieldProvider.setNextValue(statement, pos);
    statement.setString(++pos, UUID.randomUUID().toString()); // val1
    statement.setInt(++pos, random.nextInt()); // val2
    statement.executeUpdate();
  }
}
