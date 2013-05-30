package com.yuvalshavit.mib.pk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public final class CharUuidLatin1PkProvider extends UuidPkFieldProvider {
  @Override
  public String getSqlDefinition() {
    return "CHAR(36) CHARACTER SET latin1 COLLATE latin1_swedish_ci";
  }

  @Override
  public void setNextValue(PreparedStatement statement, UUID uuid, int position)  throws SQLException {
    statement.setString(position, uuid.toString());
  }
}
