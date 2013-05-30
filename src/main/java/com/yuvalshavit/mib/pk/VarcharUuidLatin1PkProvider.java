package com.yuvalshavit.mib.pk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public final class VarcharUuidLatin1PkProvider extends NonAutoIncrPkFieldProvider {
  @Override
  public String getSqlDefinition() {
    return "VARCHAR(36) CHARACTER SET latin1 COLLATE latin1_swedish_ci";
  }

  @Override
  public void setNextValue(PreparedStatement statement, String uuid, int position)  throws SQLException {
    statement.setString(position, uuid);
  }
}
