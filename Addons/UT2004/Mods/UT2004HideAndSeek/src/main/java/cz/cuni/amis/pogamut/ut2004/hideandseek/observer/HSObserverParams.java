package cz.cuni.amis.pogamut.ut2004.hideandseek.observer;

import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;

public class HSObserverParams extends UT2004AgentParameters {
	
	private String botId;

	public String getBotIdToObserve() {
		return botId;
	}

	public HSObserverParams setBotIDToObserve(String botIdToObserve) {
		this.botId = botIdToObserve;
		return this;
	}

}
