package cz.cuni.amis.pogamut.multi.worldview;

import org.junit.Ignore;
import org.junit.Test;

/**
 * NOT WORKING				
 * @author Jimmy
 */
@Ignore				
public class Test05_BatchAwareWorldView_2Agents_LotsTimeKeys extends BatchAwareWorldViewTest {

	@Test
	public void test1() {
		// WILL WORK OK
		runTest(
			2,     // int agents, 
			10000, // int logicCycles, 
			5,     // long logicDuration, 
			50,    // int objectsPerBatch, 
			10,    // long batchDuration, 
			600000,// long timeoutMillis
			true   // sync batches
		);
	}
	
}
