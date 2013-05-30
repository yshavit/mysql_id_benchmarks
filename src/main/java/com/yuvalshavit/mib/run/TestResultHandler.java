package com.yuvalshavit.mib.run;

public interface TestResultHandler {
  void onSuccess(int sequenceId, long time);
  void onFailure(int sequenceId, Throwable failure);
}
