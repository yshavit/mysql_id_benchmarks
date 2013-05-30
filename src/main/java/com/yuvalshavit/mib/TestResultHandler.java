package com.yuvalshavit.mib;

public interface TestResultHandler {
  void onSuccess(int sequenceId, long time);
  void onFailure(int sequenceId, long time, Throwable failure);
}
