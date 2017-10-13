package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;

public class Navigation2TestBotParameters extends UT2004BotParameters {

	private String startNavPointId;
	private String endNavPointId;
	private int numOfRepetitions;
	private boolean walkInCircles = false;
	
	public Navigation2TestBotParameters(String startNavPointId, String endNavPointId) {
		this.startNavPointId = startNavPointId;
		this.endNavPointId = endNavPointId;
		numOfRepetitions = 1;
	}
	
	public Navigation2TestBotParameters(String startNavPointId, String endNavPointId, int numOfRepetitions) {
		this.startNavPointId = startNavPointId;
		this.endNavPointId = endNavPointId;
		this.numOfRepetitions = numOfRepetitions;
	}
	
	public Navigation2TestBotParameters(String startNavPointId, String endNavPointId, int numOfRepetitions, boolean walkInCircles) {
		this.startNavPointId = startNavPointId;
		this.endNavPointId = endNavPointId;
		this.numOfRepetitions = numOfRepetitions;
		this.walkInCircles  = walkInCircles;
	}

	public String getStartNavPointId() {
		return startNavPointId;
	}

	public String getEndNavPointId() {
		return endNavPointId;
	}

	public int getNumOfRepetitions() {
		return numOfRepetitions;
	}
	
	public boolean isWalkInCircles() {
		return walkInCircles;
	}
	
	@Override
	public void assignDefaults(IAgentParameters defaults) {
		super.assignDefaults(defaults);
		if (defaults instanceof Navigation2TestBotParameters) {
			if (startNavPointId == null) startNavPointId = ((Navigation2TestBotParameters)defaults).getStartNavPointId();
			if (endNavPointId == null) endNavPointId = ((Navigation2TestBotParameters)defaults).getEndNavPointId();
		}
	}
	
}
