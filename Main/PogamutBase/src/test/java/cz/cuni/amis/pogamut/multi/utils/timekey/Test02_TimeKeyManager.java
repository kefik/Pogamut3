package cz.cuni.amis.pogamut.multi.utils.timekey;

import org.junit.Test;

import cz.cuni.amis.pogamut.multi.worldview.objects.CheckInstances;
import cz.cuni.amis.utils.StopWatch;
				
public class Test02_TimeKeyManager extends TimeKeyManagerTest {
	
	protected ITimeKeyManager timeKeyManager = TimeKeyManager.get();
	
//	@Test(timeout=60000)
//	public void lockUnlockTest() 
//	{
//		testTimeKeyManager(timeKeyManager, 1, 10, 10);
//		CheckInstances.waitGCTotal();
//		testOk();
//	}
//	
//	@Test(timeout=300000)
//	public void lockUnlockTest2() 
//	{
//		testTimeKeyManager(timeKeyManager, 20, 10, 10);
//		CheckInstances.waitGCTotal();
//		testOk();
//	}
	
	@Test(timeout=300000)
	public void lockUnlockTest3() 
	{
		StopWatch watches = new StopWatch();
		for (int i = 0; i < 20; ++i) {
			log.info("RUN " + (i+1) + " / 20");
			testTimeKeyManager(timeKeyManager, 20, 10, 200); // 20 * 10 * 200 operations == 40000 operations
		}
		log.info("TOTAL TIME: " + watches.stop() + " ms");
		CheckInstances.waitGCTotal();
		testOk();
	}

}
