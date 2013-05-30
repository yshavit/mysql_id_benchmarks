package com.yuvalshavit.mib.run;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
  Connection get() throws SQLException;
}
