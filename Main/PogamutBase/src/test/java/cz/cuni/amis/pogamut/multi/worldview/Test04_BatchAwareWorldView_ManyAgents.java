package cz.cuni.amis.pogamut.multi.worldview;

import org.junit.Ignore;
import org.junit.Test;
		
/**
 * NOT WORKING				
 * @author Jimmy
 */
@Ignore
public class Test04_BatchAwareWorldView_ManyAgents extends BatchAwareWorldViewTest {

	@Test
	public void test4() {
		runTest(
			100,   // int agents, 
			20,    // int logicCycles, 
			10,    // long logicDuration, 
			10,    // int objectsPerBatch, 
			2,     // long batchDuration, 
			300000,// long timeoutMillis
			true   // sync batches
		);
	}
	
//	@Test
//	public void testThatFails() {
//		// WILL FAIL
//		runTest(
//			20,   // int agents, 
//			100,  // int logicCycles, 
//			10,   // long logicDuration, 
//			10,   // int objectsPerBatch, 
//			2,    // long batchDuration, 
//			60000, // long timeoutMillis
//			false // DO NOT sync batches
//		);
//	}
	
}
