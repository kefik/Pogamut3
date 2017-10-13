package cz.cuni.amis.pogamut.ut2004.observer.impl;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;

public class UT2004Observer extends AbstractUT2004Observer<UT2004WorldView, IAct> {
	
	/**
	 * Parameters passed into the constructor/factory/runner (by whatever means the agent has been started).
	 */
    private UT2004AgentParameters params;

	@Inject
    public UT2004Observer(UT2004AgentParameters params, IComponentBus bus, IAgentLogger agentLogger, UT2004WorldView worldView, IAct act) {
        super(params.getAgentId(), bus, agentLogger, worldView, act);
        this.params = params;
    }
    
    /**
     * Returns parameters that were passed into the agent during the construction. 
     * <p><p>
     * This is a great place to parametrize your agent. Note that you may pass arbitrary subclass of {@link UT2004AgentParameters}
     * to the constructor/factory/runner and pick them up here.
     * 
     * @return parameters
     */
    public UT2004AgentParameters getParams() {
		return params;
	}

    
}
