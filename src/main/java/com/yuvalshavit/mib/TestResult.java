package com.yuvalshavit.mib;

public class TestResult {
  private final int sequence;
  private final long time;
  private final Throwable exception;

  public TestResult(int sequence, long time, Throwable exception) {
    this.sequence = sequence;
    this.time = time;
    this.exception = exception;
  }
}
