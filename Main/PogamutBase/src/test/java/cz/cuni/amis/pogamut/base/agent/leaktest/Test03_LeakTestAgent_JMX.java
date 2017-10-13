package cz.cuni.amis.pogamut.base.agent.leaktest;

import cz.cuni.amis.pogamut.base.agent.leaktest.test.LeakTestAgentFactory_03;
import cz.cuni.amis.pogamut.base.agent.leaktest.test.LeakTestAgent_03;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class Test03_LeakTestAgent_JMX extends AbstractLeakTest {

	@Override
	public IAgentFactory getFactory() {
		return new LeakTestAgentFactory_03();
	}

	@Override
	public ImmutableFlag<Integer> getInstances() {
		return LeakTestAgent_03.getInstances();
	}
	
}
