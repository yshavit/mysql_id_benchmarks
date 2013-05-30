package com.yuvalshavit.mib;

import java.sql.PreparedStatement;

public interface PreparedStatementSetter {
  void setField(PreparedStatement statement, int position);
}
