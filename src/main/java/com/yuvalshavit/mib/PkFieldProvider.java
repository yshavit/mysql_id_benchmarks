package com.yuvalshavit.mib;

import net.jcip.annotations.ThreadSafe;

import java.sql.PreparedStatement;

@ThreadSafe
public interface PkFieldProvider {
  String getSqlDefinition();
  boolean isAutoIncrement();
  void setNextValue(PreparedStatement statement, int position);
}
