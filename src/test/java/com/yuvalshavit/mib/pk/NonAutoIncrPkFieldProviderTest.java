package com.yuvalshavit.mib.pk;

import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public final class NonAutoIncrPkFieldProviderTest {
  private static class DummyPkFieldProvider extends NonAutoIncrPkFieldProvider {
    private final List<String> uuids = new ArrayList<String>();

    @Override
    protected void setNextValue(PreparedStatement statement, UUID uuid, int position) throws SQLException {
      uuids.add(uuid.toString());
    }

    @Override
    public String getSqlDefinition() {
      throw new UnsupportedOperationException();
    }
  }

  @Test
  public void replay() throws SQLException {
    DummyPkFieldProvider provider = new DummyPkFieldProvider();
    provider.setNextValue(null, 0);
    provider.setNextValue(null, 0);
    provider.setNextValue(null, 0);
    List<String> firstRun = new ArrayList<String>(provider.uuids);

    provider.uuids.clear();
    PkFieldProvider replay = provider.replay();
    replay.setNextValue(null, 0);
    replay.setNextValue(null, 0);
    replay.setNextValue(null, 0);
    List<String> secondRun = new ArrayList<String>(provider.uuids);

    assertEquals(firstRun, secondRun);
  }
}
