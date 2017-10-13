package cz.cuni.amis.pogamut.multi.worldview;

import org.junit.Ignore;
import org.junit.Test;
	
/**
 * NOT WORKING				
 * @author Jimmy
 */
@Ignore
public class Test02_BatchAwareWorldViewTest extends BatchAwareWorldViewTest {

	@Test
	public void test() {
		runTest(
			1,    // int agents, 
			10,   // int logicCycles, 
			100,  // long logicDuration, 
			10,   // int objectsPerBatch, 
			50,   // long batchDuration, 
			60000,// long timeoutMillis
			true  // sync batches
		);
	}
	
}
 