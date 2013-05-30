package com.yuvalshavit.mib.result;

public interface TestResultHandler {
  void onSuccess(int sequenceId, long time);
  void onFailure(int sequenceId, Throwable failure);
}
