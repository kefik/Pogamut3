package cz.cuni.amis.pogamut.ut2004.agent.navigation.astar;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.NullCheck;

public class UT2004PFGoal extends UT2004PFTask {

	private NavPoint targetNode;

	public UT2004PFGoal(NavPoint startNode, NavPoint targetNode) {
		super(startNode);
		NullCheck.check(targetNode, "targetNode");
		this.targetNode = targetNode;
	}

	@Override
	public boolean isGoalReached(NavPoint actualNode) {
		return actualNode.getId() == targetNode.getId();
	}

}
