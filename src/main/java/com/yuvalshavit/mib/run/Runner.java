package com.yuvalshavit.mib.run;

import com.yuvalshavit.mib.pk.PkFieldInsertProvider;
import com.yuvalshavit.mib.result.TestResultHandler;
import com.yuvalshavit.mib.schema.SchemaCreator;
import com.yuvalshavit.mib.teststeps.JoinTest;
import com.yuvalshavit.mib.teststeps.MainDeleteTest;
import com.yuvalshavit.mib.teststeps.MainInsertTest;
import com.yuvalshavit.mib.teststeps.MainUpdateTest;
import com.yuvalshavit.mib.teststeps.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Runner {
  private final int nRows;
  private final int nThreads;
  private final String myqlUser;
  private final String myqlPassword;
  private final String myqlSchema;
  private final PkFieldInsertProvider pkFieldProvider;
  private final TestResultHandler resultHandler;

  public Runner(int nRows,
                int nThreads,
                String myqlUser,
                String myqlPassword,
                String myqlSchema,
                PkFieldInsertProvider pkFieldProvider,
                TestResultHandler resultHandler)
  {
    this.nRows = nRows;
    this.nThreads = nThreads;
    this.myqlUser = myqlUser;
    this.myqlPassword = myqlPassword;
    this.myqlSchema = myqlSchema;
    this.pkFieldProvider = pkFieldProvider;
    this.resultHandler = resultHandler;
  }

  private void run(boolean warmup) throws Exception {
    ConnectionProvider connector = new ConnectionProvider() {
      @Override
      public Connection get() throws SQLException {
        String connectString = String.format("jdbc:mysql://localhost/%s?user=%s&password=%s",
          myqlSchema, myqlUser, myqlPassword);
        return DriverManager.getConnection(connectString);
      }
    };
    new SchemaCreator(pkFieldProvider, connector, warmup).run();

    List<Test> tests = new ArrayList<Test>(4);
    tests.add(new MainInsertTest(pkFieldProvider));
    tests.add(new MainUpdateTest(pkFieldProvider.replay()));
    tests.add(new JoinTest());
    tests.add(new MainDeleteTest(pkFieldProvider.replay()));

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
