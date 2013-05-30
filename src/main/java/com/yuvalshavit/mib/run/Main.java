package com.yuvalshavit.mib.run;

import com.google.common.base.Supplier;
import com.yuvalshavit.mib.pk.AutoIncrementPkProvider;
import com.yuvalshavit.mib.pk.BinaryUuidPkProvider;
import com.yuvalshavit.mib.pk.CharUuidLatin1PkProvider;
import com.yuvalshavit.mib.pk.PkFieldInsertProvider;
import com.yuvalshavit.mib.pk.VarcharUuidLatin1PkProvider;
import com.yuvalshavit.mib.pk.VarcharUuidUtf8PkProvider;
import com.yuvalshavit.mib.result.TestResult;
import com.yuvalshavit.mib.result.TestResultHandler;
import com.yuvalshavit.mib.result.TotalTimeResults;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
  public static final Supplier<TestResultHandler> testResultHandlerSupplier = new Supplier<TestResultHandler>() {
    @Override
    public TestResultHandler get() {
      return new TotalTimeResults();
    }
  };
  private final int nRows;
  private final int nThreads;
  private final String user;
  private final String password;
  private final String schema;

  public Main(int nRows, int nThreads, String user, String password, String schema) {
    this.nRows = nRows;
    this.nThreads = nThreads;
    this.user = user;
    this.password = password;
    this.schema = schema;
  }

  private enum PkProvider implements Supplier<PkFieldInsertProvider> {
    AUTO_INC {
      @Override
      public PkFieldInsertProvider get() {
        return new AutoIncrementPkProvider();
      }
    },
    VARCHAR_UTF8 {
      @Override
      public PkFieldInsertProvider get() {
        return new VarcharUuidUtf8PkProvider();
      }
    },
    VARCHAR_LATIN1 {
      @Override
      public PkFieldInsertProvider get() {
        return new VarcharUuidLatin1PkProvider();
      }
    },
    CHAR_LATIN1 {
      @Override
      public PkFieldInsertProvider get() {
        return new CharUuidLatin1PkProvider();
      }
    },
    BINARY {
      @Override
      public PkFieldInsertProvider get() {
        return new BinaryUuidPkProvider();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length > 0) {
      System.err.println("parameters are controled via -D switches");
      System.exit(1);
    }
    int nRows = getInt("rows");
    int nThreads = getInt("threads");
    String user = getString("user");
    String schema = getString("schema");
    String password = System.getProperty("password", "");
    String providers = getString("provider");

    List<PkProvider> pkProviders = new ArrayList<PkProvider>(PkProvider.values().length);
    if ("all".equalsIgnoreCase(providers)) {
      pkProviders.addAll(Arrays.asList(PkProvider.values()));
    }
    else {
      for (String provider : providers.split(",")) {
        provider = provider.trim().toUpperCase();
        PkProvider pkProvider = PkProvider.valueOf(provider);
        pkProviders.add(pkProvider);
      }
    }
    if (pkProviders.isEmpty()) {
      System.err.println("no providers requested");
      System.exit(1);
    }

    Main main = new Main(nRows, nThreads, user, password, schema);

    for (PkProvider pkProvider : pkProviders) {
      System.out.print("Warming up: " + pkProvider + "... ");
      main.run(pkProvider.get(), testResultHandlerSupplier, 10000);
      System.out.println("Warmed. Testing...");
      List<TestResult> testTestResults = main.run(pkProvider.get(), testResultHandlerSupplier, -1);
      for (TestResult testResult : testTestResults) {
        System.out.println(testResult);
      }
      System.out.println();
    }
  }

  private static int getInt(String property) {
    Integer i = Integer.getInteger(property);
    if (i == null) {
      System.err.println("property not set: -D" + property);
      System.exit(1);
    }
    return i;
  }

  private static String getString(String property) {
    String s = System.getProperty(property);
    if (s == null) {
      System.err.println("property not set: -D" + property);
      System.exit(1);
    }
    return s;
  }

  public List<TestResult> run(PkFieldInsertProvider pkFieldProvider,
                              Supplier<? extends TestResultHandler> resultsHandlerSupplier,
                              int warmup) throws Exception
  {
    ConnectionProvider connector = new ConnectionProvider() {
      @Override
      public Connection get() throws SQLException {
        String connectString = String.format("jdbc:mysql://localhost/%s?user=%s&password=%s",
          schema, user, password);
        return DriverManager.getConnection(connectString);
      }
    };
    new SchemaCreator(pkFieldProvider, connector, warmup > 0).run();

    List<Test> tests = new ArrayList<Test>(4);
    tests.add(new MainInsertTest(pkFieldProvider));
    tests.add(new MainUpdateTest(pkFieldProvider.replay()));
    tests.add(new JoinTest());
    tests.add(new MainDeleteTest(pkFieldProvider.replay()));

    List<TestResult> testResults = new ArrayList<TestResult>(tests.size());
    for (Test test : tests) {
      SimpleExecutor executor = new SimpleExecutor();
      AtomicInteger sequencer = new AtomicInteger(warmup > 0 ? warmup : nRows);
      TestResultHandler handler = resultsHandlerSupplier.get();
      testResults.add(new TestResult(test.getClass(), handler));
      for (int i = 0; i < nThreads; ++i) {
        Tester tester = new Tester(connector, test, handler, sequencer);
        executor.addRunnable(tester);
      }
      executor.run();
    }
    return testResults;
  }
}
