package com.yuvalshavit.mib;

import net.jcip.annotations.NotThreadSafe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@NotThreadSafe
public interface Test {
  String getPreparedStatementSql();
  void setParameters(PreparedStatement statement) throws SQLException;
}
