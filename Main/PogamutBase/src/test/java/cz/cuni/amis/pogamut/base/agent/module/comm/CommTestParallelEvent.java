package cz.cuni.amis.pogamut.base.agent.module.comm;

import cz.cuni.amis.pogamut.base.agent.IObservingAgent;

public class CommTestParallelEvent extends CommEvent {

	public IObservingAgent origin;

	public CommTestParallelEvent(IObservingAgent origin) {
		this.origin = origin;
	}
	
}
