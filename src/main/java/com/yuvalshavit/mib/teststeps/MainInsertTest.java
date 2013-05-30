package com.yuvalshavit.mib.teststeps;

import com.yuvalshavit.mib.pk.PkFieldProvider;

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
  public String getPreparedStatementSql() {
    return pkFieldProvider.isAutoIncrement()
        ? "INSERT INTO main (val1, val2) VALUES (?,?)"
        : "INSERT INTO main (mid, val1, val2) VALUES (?,?,?)";
  }

  @Override
  public void setParameters(PreparedStatement statement) throws SQLException {
    int pos = pkFieldProvider.isAutoIncrement() ? 1 : 0;
    pkFieldProvider.setNextValue(statement, pos);
    statement.setString(++pos, UUID.randomUUID().toString()); // val1
    statement.setInt(++pos, random.nextInt()); // val2
  }
}
