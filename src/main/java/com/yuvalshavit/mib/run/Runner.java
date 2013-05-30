package com.yuvalshavit.mib.run;

import com.google.common.base.Supplier;
import com.yuvalshavit.mib.pk.PkFieldProvider;
import com.yuvalshavit.mib.schema.SchemaCreator;
import com.yuvalshavit.mib.teststeps.Test;

import java.sql.Connection;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class Runner {
  private final int nRows;
  private final int nThreads;
  private final Supplier<? extends Connection> connector;
  private final PkFieldProvider pkFieldProvider;
  private final TestResultHandler resultHandler;
  private final Collection<? extends Supplier<? extends Test>> tests;

  public Runner(int nRows,
                int nThreads,
                Supplier<? extends Connection> connector,
                PkFieldProvider pkFieldProvider,
                TestResultHandler resultHandler,
                Collection<? extends Supplier<? extends Test>> tests) {
    this.nRows = nRows;
    this.nThreads = nThreads;
    this.connector = connector;
    this.pkFieldProvider = pkFieldProvider;
    this.resultHandler = resultHandler;
    this.tests = tests;
  }

  private void run(boolean warmup) throws Exception {
    new SchemaCreator(pkFieldProvider, connector, warmup).run();

    for (Supplier<? extends Test> test : tests) {
      SimpleExecutor executor = new SimpleExecutor();
      AtomicInteger sequencer = new AtomicInteger(nRows);
      for (int i = 0; i < nThreads; ++i) {
        Tester tester = new Tester(connector, test.get(), resultHandler, sequencer);
        executor.addRunnable(tester);
        executor.run();
      }
    }
  }
}
