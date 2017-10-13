package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PathFuture;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReact;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReactOnce;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.GetPath;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.Path;
import cz.cuni.amis.utils.future.FutureStatus;

/**
 * {@link IPathFuture} implementation that is using UT2004 inner AStar algorithm for finding the path inside UT2004
 * environment. 
 * <p><p>
 * <b>WARNING:</b> UT2004 has a limition set on the path length. It will return only the first
 * 16 navpoints that are leading to the path's target. Whenever path executor happens to tell you, that
 * the target is reached, you should compare your bot current location with {@link UT2004AStarPathFuture#getPathTo()}.
 * <p><p>
 * Note that the path that is produced by this future contains mix of {@link NavPoint} and {@link Location} objects.
 * Usually {@link Location} objects are only the first and last elements of the path and the rest are {@link NavPoint}s.
 * 
 * @author Jimmy
 */
public class UT2004AStarPathFuture extends PathFuture<ILocated> {

	private static final int PATH_TIMEOUT = 10;

	private static Object idMutex = new Object();
	
	private static long lastId = 0;
	
	private String pathId;
	
	private EventReactOnce<Path> pathReaction;
	
	private EventReact<EndMessage> endReaction;

	private IVisionWorldView worldView;
	
	private Logger log;
	
	private Double startTime;

	public UT2004AStarPathFuture(UT2004Bot bot, ILocated pathFrom, ILocated pathTo) {
		super(pathFrom, pathTo, bot.getEventBus(), bot.getWorldView());
		log = bot.getLogger().getCategory(this.getClass().getSimpleName());
		synchronized(idMutex) {
			pathId = "UT2004AStarPathFuture_" + (++lastId);
		}
		pathReaction = new EventReactOnce<Path>(Path.class, bot.getWorldView()){
			@Override
			protected void react(Path event) {
				if (pathId.equals(event.getPathId())) {
					eventPath(event);
				}
			}			
		};
		endReaction = new EventReact<EndMessage>(EndMessage.class, bot.getWorldView()) {
			@Override
			protected void react(EndMessage event) {
				eventEndMessage(event);
			}		
		};
		log.finer("Requesting path from '" + pathFrom + "' to '" + pathTo + "' under id '" + pathId + "'.");
		bot.getAct().act(new GetPath().setLocation(pathTo.getLocation()).setId(pathId));
		log.fine("Path requested, listening for the result (timeout " + PATH_TIMEOUT + "s)");		
		worldView = bot.getWorldView();
	}

	@Override
	protected boolean cancelComputation(boolean mayInterruptIfRunning) {
		pathReaction.disable();
		endReaction.disable();
		return getStatus() == FutureStatus.FUTURE_IS_BEING_COMPUTED;
	}
	
	protected void eventEndMessage(EndMessage event) {
		if (startTime == null) startTime = event.getTime();
		if (event.getTime() - startTime > PATH_TIMEOUT) {
			pathReaction.disable();
			endReaction.disable();
			if (getStatus() == FutureStatus.FUTURE_IS_BEING_COMPUTED) {
				computationException(new UT2004AStarPathTimeoutException("Path did not came from GB2004 in " + PATH_TIMEOUT + "s.", log, this));
			}
		}
	}

	protected void eventPath(Path event) {
		endReaction.disable();
		List<ILocated> result = new ArrayList<ILocated>(event.getPath().size());
		ILocated last = null;
		for (int i = 0; i < event.getPath().size(); ++i) {			
			UnrealId routeId = event.getPath().get(i).getRouteId();
			NavPoint nav = (NavPoint) worldView.get(routeId);
			if (nav == null) {
				result.add(last = event.getPath().get(i).getLocation());
			} else {
				result.add(last = nav);
			}			
		}
		
		double distance;
		if (last != null) {
			distance = getPathTo().getLocation().getDistance(last.getLocation());
			if (distance < 40) {
				// COMPLETE PATH, OK!
				setResult(result);
				return;
			}
		} else {
			last = worldView.getSingle(Self.class).getLocation();
			distance = getPathTo().getLocation().getDistance(last.getLocation());
		}
		
		double distance2D = getPathTo().getLocation().getDistance2D(last.getLocation());
		double distanceZ = getPathTo().getLocation().getDistanceZ(last.getLocation());
		
		// INCOMPLETE PATH !!! ... Path Planner inside UT2004 is SICK! 
		
		ILocated pathTo = getPathTo();
		NavPoint pathToNav = 
			pathTo instanceof NavPoint 
				? (NavPoint)getPathTo() 
				: DistanceUtils.getNearest(worldView.getAll(NavPoint.class).values(), pathTo, 20);
		if (pathToNav == null) {
			// check whether pathTo is not an Item
			Item pathToItem = DistanceUtils.getNearest(worldView.getAll(Item.class).values(), pathTo, 20);
			if (pathToItem != null) {
				pathToNav = pathToItem.getNavPoint();
			}
		}
		NavPoint lastNav = last instanceof NavPoint ? (NavPoint) last : DistanceUtils.getNearest(worldView.getAll(NavPoint.class).values(), last, 20);
		
		if (pathToNav != null) {
			// WE'RE RUNNING TO SOME NAVPOINT!
			if (lastNav != null) {
				// AND LAST POINT ON PATH IS NAVPOINT!
				NavPointNeighbourLink link = lastNav.getOutgoingEdges().get(pathToNav.getId());
				if (link != null) {
					// THERE EXIST LINK BETWEEN last AND pathTo !!!
					if (FloydWarshallMap.isWalkable(link.getFlags())) {
						// LINK IS WALKABLE !!!
						result.add(pathTo);
						setResult(result);
						return;
					} else {
						// LINK IS NOT WALKABLE
						// => just return the path we've got, it is probably because of GETPATH limit (max 16. navpoints returned)
						setResult(result);
						// MAY BE CALL: computationException(...); ?
						return;
					}
				} else {
					// LINK DOES NOT EXIST BETWEEN THESE TWO NAVPOINTS, THERE IS PROBABLY A GOOD REASON WHY NOT...
					// => just return the path we've got
					setResult(result);
					return;
				}
			}
		}
		
		// EITHER pathTo IS NOT NavPoint, OR last IS NOT A NAVPOINT
				
		if (distance2D < 100 &&
			distanceZ  < 200) {
			// SEEMS THAT WE CAN SIDE STEP TO pathTo
			result.add(pathTo);
			setResult(result);
			return;
		}
		
		// => just return the path we've got
		// MAY BE CALL: computationException(...); ?
		setResult(result);
	}

}
