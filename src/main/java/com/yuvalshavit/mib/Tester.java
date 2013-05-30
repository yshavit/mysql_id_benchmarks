package com.yuvalshavit.mib;

import com.google.common.base.Supplier;
import net.jcip.annotations.ThreadSafe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public final class Tester implements Runnable {

  private final AtomicInteger sequencer;
  private final Supplier<? extends Connection> connector;
  private final Test test;
  private final TestResultHandler resultHandler;

  public Tester(Supplier<? extends Connection> connector,
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
      Connection connection = closer.register(connector.get());
      String sql = test.getPreparedStatementSql();
      PreparedStatement state;
      try {
        state = closer.register(connection.prepareStatement(sql));
      }
      catch (Exception e) {
        throw new TestException(e);
      }

      while (true) {
        int callSequence = sequencer.incrementAndGet();
        if (callSequence <= 0)
          return;
        try {
          test.setParameters(state);
          long start = System.nanoTime();
          state.executeUpdate();
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
