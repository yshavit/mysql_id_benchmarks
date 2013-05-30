package com.yuvalshavit.mib.pk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

public abstract class UuidPkFieldProvider implements PkFieldInsertProvider {

  private final long seed;
  private final Random random;

  protected UuidPkFieldProvider() {
    seed = new Random().nextLong();
    random = new Random(seed);
  }

  protected abstract void setNextValue(PreparedStatement statement, UUID uuid, int position) throws SQLException;

  @Override
  public boolean isAutoIncrement() {
    return false;
  }

  @Override
  public final void setNextValue(PreparedStatement statement, int position) throws SQLException {
    UUID uuid = nextUuid(random);
    setNextValue(statement, uuid, position);
  }

  @Override
  public PkFieldProvider replay() {
    return new PkFieldProvider() {
      private final Random replayRandom = new Random(seed);
      @Override
      public void setNextValue(PreparedStatement statement, int position) throws SQLException {
        UUID uuid = nextUuid(replayRandom);
        UuidPkFieldProvider.this.setNextValue(statement, uuid, position);
      }
    };
  }

  private static UUID nextUuid(Random random) {
    // Copy this part from UUID.randomUUID(), because we need control over the PRNG
    byte[] uuidBytes = new byte[16];
    random.nextBytes(uuidBytes);
    uuidBytes[6]  &= 0x0f;  /* clear version        */
    uuidBytes[6]  |= 0x40;  /* set to version 4     */
    uuidBytes[8]  &= 0x3f;  /* clear variant        */
    uuidBytes[8]  |= 0x80;  /* set to IETF variant  */
    // Copy this part from the private UUID.<init>(byte[])
    long msb = 0;
    long lsb = 0;
    assert uuidBytes.length == 16 : "data must be 16 bytes in length";
    for (int i=0; i<8; i++)
      msb = (msb << 8) | (uuidBytes[i] & 0xff);
    for (int i=8; i<16; i++)
      lsb = (lsb << 8) | (uuidBytes[i] & 0xff);

    // Now just get the high and low bytes
    return new UUID(msb, lsb);
  }
}
