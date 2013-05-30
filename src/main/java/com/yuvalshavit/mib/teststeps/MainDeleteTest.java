package com.yuvalshavit.mib.teststeps;

import com.yuvalshavit.mib.pk.PkFieldProvider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class MainDeleteTest implements Test {
  private final Random random = new Random();
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
