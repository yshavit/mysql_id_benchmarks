package com.yuvalshavit.mib.teststeps;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class JoinTest implements Test {
  @Override
  public String getPreparedStatementSql() {
    return "SELECT count(*) FROM main JOIN secondary USING(mid) WHERE secondary.val3 <> 4";
  }

  @Override
  public void setParameters(PreparedStatement statement) throws SQLException {
    // nothing to do
  }
}
