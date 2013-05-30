package com.yuvalshavit.mib.run;

import com.google.common.base.Supplier;
import com.yuvalshavit.mib.pk.PkFieldInsertProvider;
import com.yuvalshavit.mib.result.Result;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Runner {
  private final int nRows;
  private final int nThreads;
  private final String myqlUser;
  private final String myqlPassword;
  private final String myqlSchema;
  private final PkFieldInsertProvider pkFieldProvider;

  public Runner(int nRows,
                int nThreads,
                String myqlUser,
                String myqlPassword,
                String myqlSchema,
                PkFieldInsertProvider pkFieldProvider)
  {
    this.nRows = nRows;
    this.nThreads = nThreads;
    this.myqlUser = myqlUser;
    this.myqlPassword = myqlPassword;
    this.myqlSchema = myqlSchema;
    this.pkFieldProvider = pkFieldProvider;
  }

  public <H extends TestResultHandler>
  List<Result<? extends H>> run(boolean warmup, Supplier<? extends H> resultsHandlerSupplier) throws Exception
  {
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

    List<Result<? extends H>> results = new ArrayList<Result<? extends H>>(tests.size());
    for (Test test : tests) {
      SimpleExecutor executor = new SimpleExecutor();
      AtomicInteger sequencer = new AtomicInteger(nRows);
      H handler = resultsHandlerSupplier.get();
      results.add(new Result<H>(test.getClass(), handler));
      for (int i = 0; i < nThreads; ++i) {
        Tester tester = new Tester(connector, test, handler, sequencer);
        executor.addRunnable(tester);
      }
      executor.run();
    }
    return results;
  }
}
