package com.yuvalshavit.mib.result;

import com.yuvalshavit.mib.teststeps.Test;

public final class TestResult {
  private final Class<? extends Test> testClass;
  private final TestResultHandler handler;

  public TestResult(Class<? extends Test> testClass, TestResultHandler handler) {
    this.testClass = testClass;
    this.handler = handler;
  }

  public Class<? extends Test> getTestClass() {
    return testClass;
  }

  public TestResultHandler getHandler() {
    return handler;
  }

  @Override
  public String toString() {
    return testClass.getSimpleName() + ": " + handler;
  }
}
