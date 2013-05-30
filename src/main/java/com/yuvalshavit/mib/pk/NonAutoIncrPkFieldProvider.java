package com.yuvalshavit.mib.pk;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class NonAutoIncrPkFieldProvider implements PkFieldInsertProvider {

  protected abstract void setNextValue(PreparedStatement statement, String uuid, int position) throws SQLException;

  private static Queue<String> uuids = new ConcurrentLinkedQueue<String>();

  @Override
  public boolean isAutoIncrement() {
    return false;
  }

  @Override
  public final void setNextValue(PreparedStatement statement, int position) throws SQLException {
    String uuid = UUID.randomUUID().toString();
    uuids.add(uuid);
    setNextValue(statement, uuid, position);
  }

  @Override
  public PkFieldProvider replay() {
    return new PkFieldProvider() {
      private final Queue<String> replayUuids = new ConcurrentLinkedQueue<String>(uuids);
      @Override
      public void setNextValue(PreparedStatement statement, int position) throws SQLException {
        String next = replayUuids.remove();
        NonAutoIncrPkFieldProvider.this.setNextValue(statement, next, position);
      }
    };
  }
}
