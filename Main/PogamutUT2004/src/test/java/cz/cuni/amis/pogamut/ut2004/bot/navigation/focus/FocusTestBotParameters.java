package cz.cuni.amis.pogamut.ut2004.bot.navigation.focus;

import cz.cuni.amis.pogamut.base.agent.params.IAgentParameters;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;

public class FocusTestBotParameters extends UT2004BotParameters {

	private String startNavPointId;
	private String endNavPointId;
	private int numOfRepetitions;
	private boolean walkInCircles = false;
	
	private String focus;
	
	public FocusTestBotParameters(String startNavPointId, String endNavPointId) {
		this.startNavPointId = startNavPointId;
		this.endNavPointId = endNavPointId;
		numOfRepetitions = 1;
	}
	
	public FocusTestBotParameters(String startNavPointId, String endNavPointId, int numOfRepetitions) {
		this.startNavPointId = startNavPointId;
		this.endNavPointId = endNavPointId;
		this.numOfRepetitions = numOfRepetitions;
	}
	
	public FocusTestBotParameters(String startNavPointId, String endNavPointId, int numOfRepetitions, boolean walkInCircles) {
		this.startNavPointId = startNavPointId;
		this.endNavPointId = endNavPointId;
		this.numOfRepetitions = numOfRepetitions;
		this.walkInCircles  = walkInCircles;
	}
	
	public String getFocus() {
		return focus;
	}

	public FocusTestBotParameters setFocus(String focus) {
		this.focus = focus;
		return this;
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
		if (defaults instanceof FocusTestBotParameters) {
			if (startNavPointId == null) startNavPointId = ((FocusTestBotParameters)defaults).getStartNavPointId();
			if (endNavPointId == null) endNavPointId = ((FocusTestBotParameters)defaults).getEndNavPointId();
		}
	}
	
}
