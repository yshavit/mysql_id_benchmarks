package com.yuvalshavit.mib.result;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class TotalTimeResults implements TestResultHandler {
  private final AtomicLong totalTime = new AtomicLong();
  @Override
  public void onSuccess(int sequenceId, long time) {
    totalTime.addAndGet(time);
  }

  @Override
  public void onFailure(int sequenceId, Throwable failure) {
    failure.printStackTrace();
  }

  public long getTotalTime(TimeUnit unit) {
    return unit.convert(totalTime.get(), TimeUnit.NANOSECONDS);
  }

  @Override
  public String toString() {
    return getTotalTime(TimeUnit.MILLISECONDS) + " ms";
  }
}
