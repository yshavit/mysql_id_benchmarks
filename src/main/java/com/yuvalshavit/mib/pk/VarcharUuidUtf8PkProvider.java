package com.yuvalshavit.mib.pk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public final class VarcharUuidUtf8PkProvider extends NonAutoIncrPkFieldProvider {
  @Override
  public String getSqlDefinition() {
    return "VARCHAR(36) CHARACTER SET utf8 COLLATE utf8_bin";
  }

  @Override
  public void setNextValue(PreparedStatement statement, UUID uuid, int position)  throws SQLException {
    statement.setString(position, uuid.toString());
  }
}
