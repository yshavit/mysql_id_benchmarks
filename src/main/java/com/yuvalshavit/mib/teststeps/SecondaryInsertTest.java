package com.yuvalshavit.mib.teststeps;

import com.yuvalshavit.mib.pk.PkFieldProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public final class SecondaryInsertTest implements Test {
  private final Random random = new Random();
  private final PkFieldProvider pkFieldProvider;

  public SecondaryInsertTest(PkFieldProvider pkFieldProvider) {
    this.pkFieldProvider = pkFieldProvider;
  }

  @Override
  public String getPreparedStatementSql() {
    return "INSERT INTO secondary(val3, mid) VALUES (?,?)";
  }

  @Override
  public void setParameters(PreparedStatement statement) throws SQLException {
    statement.setInt(1, random.nextInt());
    pkFieldProvider.setNextValue(statement, 2);
  }
}
