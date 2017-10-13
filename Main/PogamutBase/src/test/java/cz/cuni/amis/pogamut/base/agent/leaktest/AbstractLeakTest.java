package cz.cuni.amis.pogamut.base.agent.leaktest;

import org.junit.Assert;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.agent.params.impl.AgentParameters;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public abstract class AbstractLeakTest {

	public abstract IAgentFactory getFactory();
	
	public abstract ImmutableFlag<Integer> getInstances();
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
	}
	
	protected int getNumAgentsToInstantiate() {
		return 10;
	}
	
	@Test
	public void test() {
		IAgentFactory factory = getFactory();
		
		StopWatch watch = new StopWatch();
		
		for (int i = 0; i < getNumAgentsToInstantiate(); ++i) {
			IAgent agent = factory.newAgent(new AgentParameters().setAgentId(new AgentId("TestAgent1" + (i+1))));
			System.out.println("/In test/ Agent("+(i+1)+"/"+getNumAgentsToInstantiate()+") started.");
			agent.start();
			sleep(10);
			agent.pause();
			System.out.println("/In test/ " + getClass().getSimpleName() + "'s instances count = " + getInstances().getFlag());
			Assert.assertTrue(getClass().getSimpleName() + "'s instances must be > 0", getInstances().getFlag() > 0);
			sleep(10);
			agent.resume();
			sleep(10);
			agent.stop();
			System.out.println("/In test/ Agent stopped.");
			sleep(10);
		}
		
		System.out.println("Agents finished in " + watch.stopStr() + ".");
		
		factory = null;
		
		try {
			for (int i = 0; i < 30; ++i) {
	    		System.gc();
	    		Integer value = getInstances().waitFor((long)1000, 0);
	    		if (value != null && value == 0) {
	    			System.out.println("All instances of " + getClass().getSimpleName() + " has been gc()ed, " + getClass().getSimpleName() + "'s instances = " + getInstances().getFlag() + ".");
	    			System.out.println("---/// TEST OK ///---");    				    			
	    			return;
	    		} else {
	    			System.out.println("/" + (i+1) + " sec" + (i != 0 ? "s" : "") + "/ " + getClass().getSimpleName() + "'s instances count = " + getInstances().getFlag());
	    		}
			}
		} finally {
			Pogamut.getPlatform().close();			
		}
		
    	String str = "Not all " + getClass().getSimpleName() + " instances were gc()ed in 30secs after the test end, " + getClass().getSimpleName() + "'s instances = " + getInstances().getFlag() + ".";
    	System.out.println(str);
    	Assert.fail(str);
	}
	
}
