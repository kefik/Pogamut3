package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Finds the shortest through internal A* algorithm in the UT2004. The path info is send through GB2004 messages.
 * <p><p>
 * Returns {@link UT2004AStarPathFuture} that contains the logic for obtaining the path from UT2004 (note that it takes
 * some time then UT2004 sends you a path).
 * <p><p>
 * <b>IMPORTANT:</b> Due to restrictions of the UnrealScript path planner this implementation returns only paths of maximal length 16.
 * Therefore returned path may end on the half way trough. Therefore, whenever the {@link IPathExecutor} reports that
 * the bot has reached its target, you should compare bot's current position and the {@link UT2004AStarPathFuture#getPathTo()}.
 * 
 * @author Ik
 * @author Jimmy
 */
public class UT2004AStarPathPlanner implements IPathPlanner<ILocated> {

	private UT2004Bot bot;

	public UT2004AStarPathPlanner(UT2004Bot bot) {
		this.bot = bot;
	}
	
	@Override
	public IPathFuture<ILocated> computePath(ILocated from, ILocated to) {
		return new UT2004AStarPathFuture(bot, from, to);
	}
	
	public double getDistance(ILocated from, ILocated to) {
		throw new PogamutException("Could not compute path-distances on the fly!", this);
	}

}
