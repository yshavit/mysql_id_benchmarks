package com.yuvalshavit.mib.run;

import com.yuvalshavit.mib.pk.AutoIncrementPkProvider;
import com.yuvalshavit.mib.result.TotalTimeResults;

import java.util.concurrent.TimeUnit;

public final class Main {
  private Main() {}

  public static void main(String[] args) throws Exception {
    TotalTimeResults resultHandler = new TotalTimeResults();
    Runner runner = new Runner(3, 4, "yuval", "", "foo_test", new AutoIncrementPkProvider(), resultHandler);
    runner.run(false);
    System.out.printf("%d ms%n", resultHandler.getTotalTime(TimeUnit.MILLISECONDS));
  }
}
