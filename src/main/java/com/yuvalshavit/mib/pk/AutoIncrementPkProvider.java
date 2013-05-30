package com.yuvalshavit.mib.pk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public final class AutoIncrementPkProvider implements PkFieldInsertProvider {
  @Override
  public String getSqlDefinition() {
    return "INTEGER AUTO_INCREMENT";
  }

  @Override
  public boolean isAutoIncrement() {
    return true;
  }

  @Override
  public void setNextValue(PreparedStatement statement, int position) {
    // nothing to do
  }

  @Override
  public PkFieldProvider replay() {
    final AtomicInteger counter = new AtomicInteger(1);
    return new PkFieldProvider() {
      @Override
      public void setNextValue(PreparedStatement statement, int position) throws SQLException {
        statement.setInt(position, counter.incrementAndGet());
      }
    };
  }
}
