package com.yuvalshavit.mib.teststeps;

import net.jcip.annotations.ThreadSafe;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@ThreadSafe
public interface Test {
  String getPreparedStatementSql();
  void setParameters(PreparedStatement statement) throws SQLException;
}
