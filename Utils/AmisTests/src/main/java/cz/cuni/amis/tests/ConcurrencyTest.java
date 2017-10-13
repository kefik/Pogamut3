package cz.cuni.amis.tests;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConcurrencyTest extends BaseTest {

	protected void runConcurrent(Collection<ConcurrentTask> tasks, int threads) {
		
		CountDownLatch latch = new CountDownLatch(tasks.size());
		
		for (ConcurrentTask task : tasks) {
			task.setLatch(latch);
			task.setLogger(log);
		}

		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		
		long time = System.currentTimeMillis();
		
		try {
			for (ConcurrentTask task : tasks) {
				executor.execute(task);
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
									
		} finally {
			executor.shutdownNow();
		}
		
		log.info("All tasks finished, time: " + (System.currentTimeMillis() - time) + " ms");
		
		log.info("Checking thread exceptions...");
		
		for (ConcurrentTask task : tasks) {
			if (task.getException() != null) {
				if (task.getException() instanceof RuntimeException) {
					throw (RuntimeException)task.getException();
				} else {
					throw new RuntimeException("At least one task has finished with an exception.", task.getException());
				}
			}
		}
		
		log.info("All tasks executed OK");
		
	}
	
	protected void runConcurrentSyncStart(Collection<ConcurrentSyncTask> tasks) {
		
		if (tasks.size() > 50) {
			throw new RuntimeException("tasks.size() == " + tasks.size() + " > 50 ... unsupported!");
		}
		
		CountDownLatch startLatch = new CountDownLatch(tasks.size());
		
		CountDownLatch latch = new CountDownLatch(tasks.size());
		
		Thread[] threads = new Thread[tasks.size()];
		
		int i = 0;
		for (ConcurrentSyncTask task : tasks) {
			threads[i++] = new Thread(task, "Task" + i);
			task.setStartLatch(startLatch);
			task.setLatch(latch);
			task.setLogger(log);
		}

		long time = System.currentTimeMillis();
		
		try {
			for (Thread thread : threads) {
				thread.start();
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				throw new RuntimeException("Interrupted.", e);
			}
									
		} finally {
			for (Thread thread : threads) {
				if (thread.isAlive()) thread.interrupt();
			}
		}
		
		log.info("All tasks finished, time: " + (System.currentTimeMillis() - time) + " ms");
		
		log.info("Checking thread exceptions...");
		
		for (ConcurrentSyncTask task : tasks) {
			if (task.getException() != null) {
				if (task.getException() instanceof RuntimeException) {
					throw (RuntimeException)task.getException();
				} else {
					throw new RuntimeException("At least one task has finished with an exception.", task.getException());
				}
			}
		}
		
		log.info("All tasks executed OK");
		
	}
	
}
