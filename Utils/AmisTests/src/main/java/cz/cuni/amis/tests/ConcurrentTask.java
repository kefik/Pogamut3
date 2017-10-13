package cz.cuni.amis.tests;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public abstract class ConcurrentTask implements Runnable {

	private Exception e;
	private CountDownLatch latch;
	private Logger log;
	
	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}
	
	public void setLogger(Logger log) {
		this.log = log;
	}
	
	public final void run() {
		try {
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
