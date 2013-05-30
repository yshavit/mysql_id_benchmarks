package com.yuvalshavit.mib.result;

import com.yuvalshavit.mib.teststeps.Test;

public final class Result<H extends TestResultHandler> {
  private final Class<? extends Test> testClass;
  private final H handler;

  public Result(Class<? extends Test> testClass, H handler) {
    this.testClass = testClass;
    this.handler = handler;
  }

  public Class<? extends Test> getTestClass() {
    return testClass;
  }

  public H getHandler() {
    return handler;
  }

  @Override
  public String toString() {
    return testClass.getSimpleName() + ": " + handler;
  }
}
