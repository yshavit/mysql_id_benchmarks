package com.yuvalshavit.mib.run;

import com.yuvalshavit.mib.pk.PkFieldInsertProvider;
import com.yuvalshavit.mib.schema.SchemaCreator;
import com.yuvalshavit.mib.teststeps.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class Runner {
  private final int nRows;
  private final int nThreads;
  private final String connectionString;
  private final PkFieldInsertProvider pkFieldProvider;
  private final TestResultHandler resultHandler;
  private final Collection<? extends Test> tests;

  public Runner(int nRows,
                int nThreads,
                String connectionString,
                PkFieldInsertProvider pkFieldProvider,
                TestResultHandler resultHandler,
                Collection<? extends Test> tests) {
    this.nRows = nRows;
    this.nThreads = nThreads;
    this.connectionString = connectionString;
    this.pkFieldProvider = pkFieldProvider;
    this.resultHandler = resultHandler;
    this.tests = tests;
  }

  private void run(boolean warmup) throws Exception {
    ConnectionProvider connector = new ConnectionProvider() {
      @Override
      public Connection get() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost/foo_test?user=yuval&password=");
      }
    };
    new SchemaCreator(pkFieldProvider, connector, warmup).run();

    for (Test test : tests) {
      SimpleExecutor executor = new SimpleExecutor();
      AtomicInteger sequencer = new AtomicInteger(nRows);
      for (int i = 0; i < nThreads; ++i) {
        Tester tester = new Tester(connector, test, resultHandler, sequencer);
        executor.addRunnable(tester);
        executor.run();
      }
    }
  }
}
