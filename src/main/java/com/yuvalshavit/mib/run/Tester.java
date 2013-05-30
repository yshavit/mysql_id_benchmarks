package com.yuvalshavit.mib.run;

import com.yuvalshavit.mib.result.TestResultHandler;
import com.yuvalshavit.mib.util.SqlCloser;
import com.yuvalshavit.mib.teststeps.Test;
import net.jcip.annotations.ThreadSafe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public final class Tester implements Runnable {

  private final AtomicInteger sequencer;
  private final ConnectionProvider connector;
  private final Test test;
  private final TestResultHandler resultHandler;

  public Tester(ConnectionProvider connector,
                Test test,
                TestResultHandler resultHandler,
                AtomicInteger sequencer)
  {
    this.connector = connector;
    this.test = test;
    this.resultHandler = resultHandler;
    this.sequencer = sequencer;
  }

  @Override
  public void run() {
    SqlCloser closer = new SqlCloser();
    try {
      String sql = test.getPreparedStatementSql();
      PreparedStatement state;
      try {
        Connection connection = closer.register(connector.get());
        state = closer.register(connection.prepareStatement(sql));
      }
      catch (Exception e) {
        throw new TestException(e);
      }

      while (true) {
        int callSequence = sequencer.decrementAndGet();
        if (callSequence <= 0)
          return;
        try {
          test.setParameters(state);
          long start = System.nanoTime();
          state.execute();
          resultHandler.onSuccess(callSequence, System.nanoTime() - start);
        }
        catch (Exception failure) {
          resultHandler.onFailure(callSequence, failure);
        }
        catch (AssertionError error) {
          resultHandler.onFailure(callSequence, error);
        }
      }
    }
    finally {
      closer.close();
    }
  }

  private static class TestException extends RuntimeException {
    private TestException(Throwable cause) {
      super(cause);
    }
  }
}
