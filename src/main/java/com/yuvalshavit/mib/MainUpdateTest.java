package com.yuvalshavit.mib;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public class MainUpdateTest implements Test {
  private final Random random = new Random();
  private final PkFieldProvider pkFieldProvider;

  public MainUpdateTest(PkFieldProvider pkFieldProvider) {
    this.pkFieldProvider = pkFieldProvider;
  }

  @Override
  public String getPreparedStatementSql() {
    return "UPDATE main set val1 = ?, val2 = ? WHERE mid = ?";
  }

  @Override
  public void setParameters(PreparedStatement statement) throws SQLException {
    statement.setString(1, UUID.randomUUID().toString()); // val1
    statement.setInt(2, random.nextInt()); // val2
    pkFieldProvider.setNextValue(statement, 3);
  }
}
