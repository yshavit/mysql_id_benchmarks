package com.yuvalshavit.mib.pk;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public final class BinaryUuidPkProvider extends NonAutoIncrPkFieldProvider {
  @Override
  public String getSqlDefinition() {
    return "BINARY(16)";
  }

  @Override
  public void setNextValue(PreparedStatement statement, UUID uuid, int position)  throws SQLException {
    ByteBuffer bb = ByteBuffer.allocate(16);
    LongBuffer longBuffer = bb.asLongBuffer();
    longBuffer.put(uuid.getMostSignificantBits());
    longBuffer.put(uuid.getLeastSignificantBits());
    byte[] bytes = bb.array();
    statement.setBytes(position, bytes);
  }
}
