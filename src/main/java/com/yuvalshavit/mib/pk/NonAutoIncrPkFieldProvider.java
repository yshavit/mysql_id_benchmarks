package com.yuvalshavit.mib.pk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public abstract class NonAutoIncrPkFieldProvider implements PkFieldInsertProvider {



  @Override
  public boolean isAutoIncrement() {
    return false;
  }

  @Override
  public PkFieldProvider replay() {
    return new PkFieldProvider() {
      @Override
      public void setNextValue(PreparedStatement statement, int position) throws SQLException {
        throw new UnsupportedOperationException(); // TODO
      }
    };
  }

  protected String getUuid() {
    String uuid = UUID.randomUUID().toString();
    assert false : "TODO: save this for replaying";
    return uuid;
  }
}
