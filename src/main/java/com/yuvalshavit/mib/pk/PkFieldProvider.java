package com.yuvalshavit.mib.pk;

import net.jcip.annotations.ThreadSafe;

import java.sql.PreparedStatement;

@ThreadSafe
public interface PkFieldProvider {
  String getSqlDefinition();
  boolean isAutoIncrement();
  void setNextValue(PreparedStatement statement, int position);
}
