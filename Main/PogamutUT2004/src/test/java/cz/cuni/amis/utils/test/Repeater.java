package cz.cuni.amis.utils.test;

import java.util.logging.Logger;

import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.StopWatch;

public class Repeater<CONTEXT extends TestContext> implements ContextRunnable<CONTEXT> {
	
	private int repetitions;
	private ContextRunnable test;	

	public Repeater(int repetitions, ContextRunnable test) {
		this.test = test;
		NullCheck.check(this.test, "test");
		this.repetitions = repetitions;
		if (this.repetitions <= 0) throw new IllegalArgumentException("repetitions must be > 0"); 
	}
	
	protected Runnable newTestIteration(final CONTEXT ctx) {
		return new Runnable() {
			@Override
			public void run() {
				test.run(ctx);
			}
		};
	}

	@Override
	public void run(CONTEXT ctx) {
		Logger log = ctx.getLog();
		double totalMillis = 0;
		StopWatch watch = new StopWatch();
    	for (int i = 0; i < repetitions; ++i) {	    		
    		log.log(log.getLevel(), "---((( Run " + (i+1) + " / " + repetitions + " )))---");
    		watch.start();
    		newTestIteration(ctx).run();
    		totalMillis += watch.stop();
    	}
    	log.log(log.getLevel(), String.format("All " + repetitions + " tests finished in %.3f ms.", totalMillis));
	}

}
