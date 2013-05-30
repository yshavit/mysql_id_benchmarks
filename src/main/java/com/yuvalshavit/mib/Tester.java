package com.yuvalshavit.mib;

import com.google.common.base.Supplier;
import com.google.common.io.Closeables;
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
      PreparedStatement state;
      try {
        state = closer.register(test.setUp(connection));
      }
      catch (Exception e) {
        throw new TestException(e);
      }

      while (true) {
        int callSequence = sequencer.incrementAndGet();
        if (callSequence <= 0)
          return;
        long start = System.nanoTime();
        try {
          test.run(state);
          resultHandler.onSuccess(callSequence, System.nanoTime() - start);
        }
        catch (Exception failure) {
          resultHandler.onFailure(callSequence, System.nanoTime() - start, failure);
        }
        catch (AssertionError error) {
          resultHandler.onFailure(callSequence, System.nanoTime() - start, error);
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
