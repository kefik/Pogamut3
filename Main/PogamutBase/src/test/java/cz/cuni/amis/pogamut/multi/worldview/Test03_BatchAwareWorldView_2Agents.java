package cz.cuni.amis.pogamut.multi.worldview;

import org.junit.Ignore;
import org.junit.Test;

/**
 * NOT WORKING				
 * @author Jimmy
 */
@Ignore		
public class Test03_BatchAwareWorldView_2Agents extends BatchAwareWorldViewTest {

	@Test
	public void test1() {
		// WILL WORK OK
		runTest(
			2,     // int agents, 
			100,   // int logicCycles, 
			10,    // long logicDuration, 
			10,    // int objectsPerBatch, 
			50,    // long batchDuration, 
			300000,// long timeoutMillis
			true   // sync batches
		);
	}
	
	@Test
	public void test2() {
		// WILL WORK OK
		runTest(
			2,     // int agents, 
			100,   // int logicCycles, 
			50,    // long logicDuration, 
			10,    // int objectsPerBatch, 
			100,   // long batchDuration, 
			300000,// long timeoutMillis
			true   // sync batches
		);
	}
	
	@Test
	public void test3() {
		// WILL WORK OK
		runTest(
			2,     // int agents, 
			100,   // int logicCycles, 
			100,   // long logicDuration, 
			10,    // int objectsPerBatch, 
			240,   // long batchDuration, 
			300000,// long timeoutMillis
			true   // sync batches
		);
	}
	
	@Test
	public void test4() {
		runTest(
			2,     // int agents, 
			100,   // int logicCycles, 
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
//			2,     // int agents, 
//			100,   // int logicCycles, 
//			10,    // long logicDuration, 
//			10,    // int objectsPerBatch, 
//			2,     // long batchDuration, 
//			300000,// long timeoutMillis
//          false  // sync batches
//		);
//	}

	
}
