package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import math.geom3d.Point3D;
import math.geom3d.line.StraightLine3D;
import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 * This module helps you to obtain nearest {@link NavPoint} and {@link NavPointNeighbourLink} to your current location.
 * 
 * @author Jimmy
 */
public class NavigationGraphHelper extends SensorModule<UT2004Bot> {
	
	public static final DistanceUtils.IGetDistance<NavPointNeighbourLink> NAV_LINK_GET_DISTANCE = new DistanceUtils.IGetDistance<NavPointNeighbourLink>() {
		
		@Override
		public double getDistance(NavPointNeighbourLink object, ILocated point) {
			if (object == null) return Double.MAX_VALUE;
			if (point == null) return Double.MAX_VALUE;
			
			// SEE: http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
			Location x0 = point.getLocation();
			Location x1 = object.getFromNavPoint().getLocation();
			Location x2 = object.getToNavPoint().getLocation();
			double distance = x0.sub(x1).cross(x0.sub(x2)).getLength() / x2.sub(x1).getLength();
			
			return distance;
		}
		
	};
	
	public static final DistanceUtils.IGetDistance<NavLinkPair> NAV_LINK_PAIR_GET_DISTANCE = new DistanceUtils.IGetDistance<NavLinkPair>() {

		@Override
		public double getDistance(NavLinkPair object, ILocated target) {
			return object.getDistance(target.getLocation());
		}
		
	};
	
	/**
	 * Key: {@link NavPoint#getId()}
	 * Value: set of link (pairs) that either originate or ends in the corresponding navpoint, i.e. {@link NavLinkPair#isLinkNavPoint(UnrealId)} is true for the key.
	 */
	protected Map<UnrealId, Set<NavLinkPair>> navPointLinks = new LazyMap<UnrealId, Set<NavLinkPair>>() {

		@Override
		protected Set<NavLinkPair> create(UnrealId key) {
			return new HashSet<NavLinkPair>();
		}
		
	};
	
	/**
	 * List of ALL existing {@link NavLinkPair} in the map.
	 */
	protected Set<NavLinkPair> navLinkPairs = new HashSet<NavLinkPair>();
		
	//========================================================================
	// SENSORS
	//========================================================================
	
	/**
	 * Returns nearest {@link NavPoint} to current bot position.
	 */
	public NavPoint getNearestNavPoint() {
		return DistanceUtils.getNearest(agent.getWorldView().getAll(NavPoint.class).values(), agent.getLocation());
	}
	
	/**
	 * Returns nearest {@link NavPoint} to current bot position no further than 'maxDistance' from the bot.
	 */
	public NavPoint getNearestNavPoint(double maxDistance) {
		NavPoint result = DistanceUtils.getNearest(agent.getWorldView().getAll(NavPoint.class).values(), agent.getLocation());
		if (result == null) return null;
		if (agent.getLocation().getDistance(result.getLocation()) > maxDistance) return null;
		return result;
	}
	
	/**
	 * Returns nearest {@link NavLinkPair} to current bot position.
	 * @return
	 */
	public NavLinkPair getNearestNavLinkPair() {
		return DistanceUtils.getNearest(navLinkPairs, agent.getLocation(), NAV_LINK_PAIR_GET_DISTANCE);
	}
	
	/**
	 * Returns nearest {@link NavPoint} to some 'target'.
	 * @param target
	 * @return
	 */
	public NavPoint getNearestNavPoint(ILocated target) {
		return DistanceUtils.getNearest(agent.getWorldView().getAll(NavPoint.class).values(), target);
	}
	
	/**
	 * Returns nearest {@link NavPoint} to some 'target' no further than 'maxDistance' from the bot.
	 * @param target
	 * @param maxDistance
	 * @return
	 */
	public NavPoint getNearestNavPoint(ILocated target, double maxDistance) {
		if (target == null || target.getLocation() == null) return null;
		NavPoint result = DistanceUtils.getNearest(agent.getWorldView().getAll(NavPoint.class).values(), target);
		if (result == null) return null;
		if (target.getLocation().getDistance(result.getLocation()) > maxDistance) return null;
		return result;
	}
	
	/**
	 * Returns nearest {@link NavLinkPair} to some 'target'.
	 * @param target
	 * @return
	 */
	public NavLinkPair getNearestNavLinkPair(ILocated target) {
		return DistanceUtils.getNearest(navLinkPairs, target, NAV_LINK_PAIR_GET_DISTANCE);
	}
	
	// =================
	// ANALYTIC GEOMETRY
	// =================
	
	
	/**
	 * Projects 'point' to line formed by the 'link'.
	 * 
	 * @param link
	 * @param point	
	 */
	public static Location projectPointToLinkLine(NavPointNeighbourLink link, ILocated point) {
		if (link == null || point == null || point.getLocation() == null) return null;
		StraightLine3D line = new StraightLine3D(link.getFromNavPoint().getLocation().asPoint3D(), link.getToNavPoint().getLocation().asPoint3D());
		Point3D result = line.projectPoint(point.getLocation().asPoint3D());
		return new Location(result);
	}
	
	/**
	 * Tells whether "point" projection to "link" is inside the "link segment".
	 * @param link
	 * @param point
	 * @return
	 */
	public static Boolean isPointProjectionOnLinkSegment(NavPointNeighbourLink link, ILocated point) {
		if (link == null || point == null || point.getLocation() == null) return null;
		StraightLine3D line = new StraightLine3D(link.getFromNavPoint().getLocation().asPoint3D(), link.getToNavPoint().getLocation().asPoint3D());
		double u = line.project(point.getLocation().asPoint3D());
		return u >= 0 && u <= 1;
	}
	
	/**
	 * Tells whether "point" projection to "link" is inside the "link segment".
	 * @param link
	 * @param point
	 * @return
	 */
	public static Boolean isPointProjectionBeforeLinkSegment(NavPointNeighbourLink link, ILocated point) {
		if (link == null || point == null || point.getLocation() == null) return null;
		StraightLine3D line = new StraightLine3D(link.getFromNavPoint().getLocation().asPoint3D(), link.getToNavPoint().getLocation().asPoint3D());
		double u = line.project(point.getLocation().asPoint3D());
		return u < 0;
	}
	
	/**
	 * Tells whether "point" projection to "link" is inside the "link segment".
	 * @param link
	 * @param point
	 * @return
	 */
	public static Boolean isPointProjectionAfterLinkSegment(NavPointNeighbourLink link, ILocated point) {
		if (link == null || point == null || point.getLocation() == null) return null;
		StraightLine3D line = new StraightLine3D(link.getFromNavPoint().getLocation().asPoint3D(), link.getToNavPoint().getLocation().asPoint3D());
		double u = line.project(point.getLocation().asPoint3D());
		return u > 1;
	}
	
	/*========================================================================*/
	
	/**
	 * Initialization method called from {@link MapPointListObtainedListener}.
	 */
	protected void init(Collection<NavPoint> navPoints) {
		if (log != null && log.isLoggable(Level.FINE)) log.fine("Computing nav-link pairs...");
		
		navPointLinks.clear();
		navLinkPairs.clear();
		
		if (navPoints.size() == 0) return;
		
		Set<NavPointNeighbourLink> finishedLinks = new HashSet<NavPointNeighbourLink>();
		Set<NavPoint> finished = new HashSet<NavPoint>();
		Set<NavPoint> pending = new HashSet<NavPoint>();
		
		pending.add(navPoints.iterator().next());
		
		while (pending.size() > 0) {
			NavPoint nav1;
			
			Iterator<NavPoint> pendingIterator = pending.iterator();
			nav1 = pendingIterator.next();
			pendingIterator.remove();
			
			for (NavPointNeighbourLink outgoing : nav1.getOutgoingEdges().values()) {
				if (finishedLinks.contains(outgoing)) continue;
				NavPoint nav2 = outgoing.getToNavPoint();
				NavPointNeighbourLink incoming = outgoing.getToNavPoint().getOutgoingEdges().get(nav2.getId());
				NavLinkPair pair = new NavLinkPair(outgoing, incoming);
				
				navPointLinks.get(nav1.getId()).add(pair);
				navPointLinks.get(nav2.getId()).add(pair);
				navLinkPairs.add(pair);
				
				finishedLinks.add(outgoing);
				if (incoming != null) {
					finishedLinks.add(incoming);
				}
				
				if (!finished.contains(nav2)) {
					pending.add(nav2);
				}
			}
			
			finished.add(nav1);
		}
		
		if (log != null && log.isLoggable(Level.INFO)) log.info("Computed nav-link pairs.");
	}
	
	/*========================================================================*/
	
	/**
	 * {@link MapPointListObtained} listener.
	 */
	protected class MapPointListObtainedListener implements IWorldEventListener<MapPointListObtained>
	{
		/**
		 * Constructor. Registers itself on the given WorldView object.
		 * @param worldView WorldView object to listen to.
		 */
		public MapPointListObtainedListener(IWorldView worldView)
		{
			worldView.addEventListener(ItemPickedUp.class, this);
		}

		@Override
		public void notify(MapPointListObtained event)
		{
			if (event.getNavPoints() != null) {
				init(event.getNavPoints().values());
			}
		}

	}
	
	protected MapPointListObtainedListener mapPointListObtainedListener;

	/*========================================================================*/

	public NavigationGraphHelper(UT2004Bot bot) {
		super(bot);
		
		mapPointListObtainedListener = new MapPointListObtainedListener(bot.getWorldView());
		
		// IN CASE OF LATE-INITIALIZATION
		if (bot.getWorldView().getAll(NavPoint.class).size() > 0) {
			init(bot.getWorldView().getAll(NavPoint.class).values());
		} 
	}
		
}
