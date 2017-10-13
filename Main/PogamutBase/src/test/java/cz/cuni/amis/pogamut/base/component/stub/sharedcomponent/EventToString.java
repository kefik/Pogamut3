package cz.cuni.amis.pogamut.base.component.stub.sharedcomponent;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.component.IComponent;

public class EventToString {
	
	public static String eventToString(IAgentId agentId, IComponent component,  Class eventClass) {
		return agentId + " " + component.getComponentId().getToken() + " " + eventClass;
	}

}
