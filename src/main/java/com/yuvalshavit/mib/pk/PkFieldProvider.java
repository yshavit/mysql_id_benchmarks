package com.yuvalshavit.mib.pk;

import net.jcip.annotations.ThreadSafe;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@ThreadSafe
public interface PkFieldProvider {
  void setNextValue(PreparedStatement statement, int position) throws SQLException;
}
