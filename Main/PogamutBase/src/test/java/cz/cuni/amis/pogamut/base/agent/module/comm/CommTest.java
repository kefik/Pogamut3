package cz.cuni.amis.pogamut.base.agent.module.comm;

import org.junit.Ignore;

import cz.cuni.amis.tests.BaseTest;

@Ignore
public class CommTest extends BaseTest {
	
	protected void checkInstanceCount(int timeoutSecs) {
		log.info("Waiting for GC() to clean up ObservingAgent instances (" + timeoutSecs + " seconds timeout)...");
		int instanceCount = ObservingAgent.instanceCount.getFlag();
		for (int i = 0; i < timeoutSecs; ++i) {
			if (instanceCount == 0) break;
			log.info("Waiting for GC() ... ObservingAgent.instanceCount = " + instanceCount);
			System.gc();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			instanceCount = ObservingAgent.instanceCount.getFlag();
			log.info((i+1) + " / " + timeoutSecs + " seconds passed...");
		}
		
		if (instanceCount != 0) {
			testFailed("ObservingAgent.instanceCount == " + instanceCount + " != 0 ... Memory leak!!!");
		}
		
		log.info("All ObservingAgent instances GC()ed OK!");
	}

}
