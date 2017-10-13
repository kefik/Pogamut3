package cz.cuni.amis.pogamut.ut2004.vip.observer;

import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;

public class CSObserverParams extends UT2004AgentParameters {
	
	private String botId;

	public String getBotIdToObserve() {
		return botId;
	}

	public CSObserverParams setBotIDToObserve(String botIdToObserve) {
		this.botId = botIdToObserve;
		return this;
	}

}
