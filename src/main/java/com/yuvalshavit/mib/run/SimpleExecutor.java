package com.yuvalshavit.mib.run;

import net.jcip.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NotThreadSafe
public final class SimpleExecutor {
  private final Collection<Runnable> runnables = new ArrayList<Runnable>();

  public void addRunnable(Runnable runnable) {
    runnables.add(runnable);
  }

  public void run() throws InterruptedException {
    List<Thread> threads = new ArrayList<Thread>(runnables.size());
    for (Runnable runnable : runnables) {
      Thread thread = new Thread(runnable);
      threads.add(thread);
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
  }
}
