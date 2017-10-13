package cz.cuni.amis.pogamut.base.agent.leaktest;

import cz.cuni.amis.pogamut.base.agent.leaktest.test.LeakTestAgentFactory_01;
import cz.cuni.amis.pogamut.base.agent.leaktest.test.LeakTestAgent_01;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class Test01_LeakTestAgent extends AbstractLeakTest {

	@Override
	public IAgentFactory getFactory() {
		return new LeakTestAgentFactory_01();
	}

	@Override
	public ImmutableFlag<Integer> getInstances() {
		return LeakTestAgent_01.getInstances();
	}
	
}
