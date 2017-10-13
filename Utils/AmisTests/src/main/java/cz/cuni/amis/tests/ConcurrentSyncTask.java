package cz.cuni.amis.tests;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public abstract class ConcurrentSyncTask implements Runnable {

	private Exception e;
	private CountDownLatch latch;
	private Logger log;
	
	private CountDownLatch startLatch;
	
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	
	void setStartLatch(CountDownLatch startLatch) {
		this.startLatch = startLatch;
	}
	
	public void setLogger(Logger log) {
		this.log = log;
	}
	
	public final void run() {
		try {
			if (startLatch != null) {
				startLatch.countDown();
				startLatch.await();
			}
			runImpl();
		} catch (Exception e) {
			this.e = e;
		} finally {
			if (log != null) {
				synchronized(latch) {
					latch.countDown();
					log.info("Jobs remaining: " + latch.getCount());
				}
			} else {
				latch.countDown();
			}
		}
	}
	
	public boolean isException() {
		return e != null;
	}
	
	public Exception getException() {
		return e;
	}

	protected abstract void runImpl();
	
}
