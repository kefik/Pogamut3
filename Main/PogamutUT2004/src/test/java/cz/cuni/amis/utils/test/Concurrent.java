package cz.cuni.amis.utils.test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import java.util.logging.Level;

public class Concurrent<CONTEXT extends TestContext> implements ContextRunnable<CONTEXT> {
	
	private final int threadCount;
	protected final ContextRunnable tester;	
	
	private Object mutex = new Object();
	private List<Exception> list = new ArrayList<Exception>();
	
	public Concurrent(int threads, ContextRunnable tester) {
		this.tester = tester;
		NullCheck.check(this.tester, "tester");
		this.threadCount = threads;
		if (threadCount <= 0) throw new IllegalArgumentException("Number of threads must be > 0.");
	}
	
	protected Runnable newTest(final CONTEXT ctx) {
		return
			new Runnable() {
				@Override
				public void run() {
					tester.run(ctx);
				}
			};
	}
	
	private Runnable trueNewTest(final CONTEXT ctx) {
		final Runnable myRunnable = newTest(ctx); 
		return 
			new Runnable() {
				@Override
				public void run() {
					try {
						myRunnable.run();
					} catch (Exception e) {
						synchronized(mutex) {
							list.add(e);
						}
					}
				}
			}; 
	}

	@Override
	public void run(final CONTEXT ctx) {
		Logger log = ctx.getLog();
		Thread[] threads = new Thread[threadCount];
		for (int i = 0; i < threads.length; ++i) {
			threads[i] = new Thread(trueNewTest(ctx), "Test" + i);
		}
		StopWatch watch = new StopWatch();
		for (int i = 0; i < threads.length; ++i) {
			log.log(log.getLevel(), "Starting thread " + (i+1) + " / " + threads.length + ".");
			threads[i].start();
		}
		try {
			for (int i = 0; i < threads.length; ++i) {			
				threads[i].join();		
			}
			log.log(log.getLevel(), "All " + threads.length + " threads finished in " + watch.stopStr() + ".");			
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
		if (list.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(Const.NEW_LINE);
			sb.append("Exceptions:");
			for (Exception e : list) {
				sb.append(Const.NEW_LINE);
				sb.append("    " + e.getClass().getSimpleName() + ": " + e.getMessage());
			}
			if (log.isLoggable(Level.SEVERE)) log.severe(sb.toString());
			if (log.isLoggable(Level.SEVERE)) log.severe("Test failed due to previous errors...");
			throw new RuntimeException("Test failed due to previous errors...");
		}
	}
	
}
