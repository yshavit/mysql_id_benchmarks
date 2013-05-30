package com.yuvalshavit.mib.pk;

import net.jcip.annotations.ThreadSafe;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@ThreadSafe
public interface PkFieldInsertProvider extends PkFieldProvider {
  String getSqlDefinition();
  boolean isAutoIncrement();
  PkFieldProvider replay();
}
