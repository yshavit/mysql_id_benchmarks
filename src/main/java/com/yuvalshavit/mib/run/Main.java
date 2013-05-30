package com.yuvalshavit.mib.run;

import com.google.common.base.Supplier;
import com.yuvalshavit.mib.pk.AutoIncrementPkProvider;
import com.yuvalshavit.mib.result.Result;
import com.yuvalshavit.mib.result.TestResultHandler;
import com.yuvalshavit.mib.result.TotalTimeResults;
import com.yuvalshavit.mib.teststeps.Test;

import java.util.List;

public final class Main {
  private Main() {}

  public static void main(String[] args) throws Exception {
    Runner runner = new Runner(100, 4, "yuval", "", "foo_test", new AutoIncrementPkProvider());
    List<Result<?>> testResults = runner.run(false, new Supplier<TestResultHandler>() {
      @Override
      public TestResultHandler get() {
        return new TotalTimeResults();
      }
    });
    for (Result<?> result : testResults) {
      System.out.println(result);
    }
  }
}
