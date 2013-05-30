package com.yuvalshavit.mib.pk;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class BinaryUuidPkProvider extends NonAutoIncrPkFieldProvider {
  @Override
  public String getSqlDefinition() {
    return "BINARY(16)";
  }

  @Override
  public void setNextValue(PreparedStatement statement, String uuid, int position)  throws SQLException {
    throw new UnsupportedOperationException(); // TODO!
  }
}
