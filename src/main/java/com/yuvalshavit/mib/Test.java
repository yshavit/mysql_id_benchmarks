package com.yuvalshavit.mib;

import net.jcip.annotations.NotThreadSafe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@NotThreadSafe
public interface Test {
  PreparedStatement setUp(Connection connection) throws SQLException;
  void run(PreparedStatement statement) throws Exception;
}
