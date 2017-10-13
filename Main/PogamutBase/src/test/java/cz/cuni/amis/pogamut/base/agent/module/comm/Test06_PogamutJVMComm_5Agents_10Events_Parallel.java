package cz.cuni.amis.pogamut.base.agent.module.comm;

import org.junit.Test;

public class Test06_PogamutJVMComm_5Agents_10Events_Parallel extends ParallelTest {

	public static final int EVENTS_COUNT = 10;
	
	public static final int AGENTS_COUNT = 5;

	public static final int CHANNEL = 10;
	
	@Test
	public void test1() {
		innerImpl(EVENTS_COUNT, AGENTS_COUNT, CHANNEL);
		
		testOk();
	}
	
	@Test
	public void test2() {
		int COUNT = 5;
		for (int j = 0; j < COUNT; ++j) {
			log.info("TEST " + (j+1) + " / " + COUNT);			
			innerImpl(EVENTS_COUNT, AGENTS_COUNT, CHANNEL);					
		}
		
		testOk();
	}
	
}
