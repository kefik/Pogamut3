package cz.cuni.amis.pogamut.ut2004multi.communication.module;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;

public abstract class AgentSpecificObjectEventListener<OBJECT extends IWorldObject,EVENT extends IWorldObjectEvent<OBJECT>> implements
		IWorldObjectEventListener<OBJECT, EVENT> 
{
	protected IAgentId agentId;
	
	public AgentSpecificObjectEventListener(IAgentId agentId)
	{
		this.agentId = agentId;
	}

}
