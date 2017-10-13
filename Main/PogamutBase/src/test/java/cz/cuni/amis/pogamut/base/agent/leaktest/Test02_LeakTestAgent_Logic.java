package cz.cuni.amis.pogamut.base.agent.leaktest;

import cz.cuni.amis.pogamut.base.agent.leaktest.test.LeakTestAgentFactory_02;
import cz.cuni.amis.pogamut.base.agent.leaktest.test.LeakTestAgent_02;
import cz.cuni.amis.pogamut.base.factory.IAgentFactory;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class Test02_LeakTestAgent_Logic extends AbstractLeakTest {

	@Override
	public IAgentFactory getFactory() {
		return new LeakTestAgentFactory_02();
	}

	@Override
	public ImmutableFlag<Integer> getInstances() {
		return LeakTestAgent_02.getInstances();
	}
	
}
