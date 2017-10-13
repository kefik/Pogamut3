package cz.cuni.amis.pogamut.base.agent.component.event;

import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.ComponentBusEvents;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.PausingEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumedEvent;
import cz.cuni.amis.pogamut.base.component.bus.event.impl.ResumingEvent;

public class AgentEvents extends ComponentBusEvents {

	public AgentEvents(IComponentBus bus, IAgent component, Logger log) {
		super(bus, component, log);
	}
	
	public String toString() {
		return "AgentEvents[bus=" + bus + "]";
	}
	
}
