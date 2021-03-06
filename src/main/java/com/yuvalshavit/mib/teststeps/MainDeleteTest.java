package com.yuvalshavit.mib.teststeps;

import com.yuvalshavit.mib.pk.PkFieldProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MainDeleteTest implements Test {
  private final PkFieldProvider pkFieldProvider;

  public MainDeleteTest(PkFieldProvider pkFieldProvider) {
    this.pkFieldProvider = pkFieldProvider;
  }

  @Override
  public String getPreparedStatementSql() {
    return "DELETE FROM main WHERE mid = ?";
  }

  @Override
  public void setParameters(PreparedStatement statement) throws SQLException {
    pkFieldProvider.setNextValue(statement, 1);
  }
}
