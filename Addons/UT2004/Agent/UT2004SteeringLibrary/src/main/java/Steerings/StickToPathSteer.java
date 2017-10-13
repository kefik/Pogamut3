package Steerings;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import SocialSteeringsBeta.RefLocation;
import SteeringProperties.SteeringProperties;
import SteeringProperties.StickToPathProperties;
import SteeringStuff.ISteering;
import SteeringStuff.RefBoolean;
import SteeringStuff.SteeringManager;
import SteeringStuff.SteeringTools;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphHelper;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.utils.Tuple3;
import cz.cuni.amis.utils.future.FutureStatus;

/**
 * Steering that makes sure the bot won't get out of the "naviagation edge corridor".
 *
 * @author Jimmy
 */
public class StickToPathSteer implements ISteering {

    private UT2004Bot bot;

    private static int NEARLY_THERE_DISTANCE = 150;
    
    private static double NAVPOINT_MAX_DISTANCE = 100;
    
   	private StickToPathProperties properties;

   	/**
   	 * CURRENT path future.
   	 */
	private IPathFuture<ILocated> pathFuture;

	/**
	 * CURRENT path locations list.
	 */
	private List<ILocated> pathLocations;
	
	/**
	 * CURRENT navigation points, if applies (may contain NULLs!).
	 */
	private List<NavPoint> pathNavPoints;
	
	/**
	 * CURRENT navigation links, index 0 is for links between pathNavPoints.get(0) and pathNavPoints.get(1)
	 */
	private List<NavPointNeighbourLink> pathLinks;

    public StickToPathSteer(UT2004Bot bot) {
        this.bot = bot;
    }

    /**
     * Moves the bot around the given pathFuture.
     */
    @Override
    public Vector3d run(Vector3d scaledActualVelocity, RefBoolean wantsToGoFaster, RefBoolean wantsToStop, RefLocation focus) {
    	
    	// ENSURE PATH EXISTENCE
    	ensurePath();
    	
    	if (!hasPath()) {
    		// NO PATH
    		// => we cannot steer
    		return new Vector3d(0,0,0);
    	}
    	
    	Tuple3<NavPointNeighbourLink, NavPointNeighbourLink, NavPointNeighbourLink> links = getCurrentLinks();
    	if (links == null) {
    		// INVALID LINKS
    		// => we cannot steer
    		return new Vector3d(0,0,0);
    	}

    	NavPointNeighbourLink prevLink = links.getFirst();
    	NavPointNeighbourLink currLink = links.getSecond();
    	NavPointNeighbourLink nextLink = links.getThird();
    	
    	if (SteeringManager.DEBUG) {
    		System.out.println("StickToPath: prevLink = " + prevLink);
    		System.out.println("StickToPath: currLink = " + currLink);
    		System.out.println("StickToPath: nextLink = " + nextLink);
    	}
    	
    	if (currLink != null) {
    		return steerCurrLink(currLink, nextLink);
    	}
    	
    	// currLink == null
    	if (nextLink != null) {
    		return steerNextLink(nextLink);
    	}
    	
    	// NO LINK INFORMATION
    	// => we cannot steer
    	return new Vector3d(0,0,0);
    }

    private Vector3d steerCurrLink(NavPointNeighbourLink currLink, NavPointNeighbourLink nextLink) {
    	Location projection = NavigationGraphHelper.projectPointToLinkLine(currLink, bot.getLocation());
    	double distance = NavigationGraphHelper.NAV_LINK_GET_DISTANCE.getDistance(currLink, bot.getLocation());
    	
    	double ratio;
    	
    	if (currLink.getCollisionR() <= 0) {
    		ratio = 0;
    	} else {
    		ratio = distance / (double)currLink.getCollisionR();
    	}
    	
    	Location result = projection.sub(bot.getLocation()).scale(ratio).setZ(0);
    	
    	if (SteeringManager.DEBUG) {
    		System.out.println("StickToPathSteer: HAS LINK");
    		System.out.println("StickToPathSteer: collision radius = " + currLink.getCollisionR());
    		System.out.println("StickToPathSteer: distance         = " + distance);
    		System.out.println("StickToPathSteer: ratio            = " + ratio);
    		System.out.println("StickToPathSteer: result           = [" + result.x + ", " + result.y + ", " + result.z + "]");
    		System.out.println("StickToPathSteer: length           = " + result.getLength());
    	}
    	
    	return result.asVector3d();
	}

	private Vector3d steerNextLink(NavPointNeighbourLink nextLink) {
    	Location projection = nextLink.getFromNavPoint().getLocation();
    	
    	Location result = projection.sub(bot.getLocation()).getNormalized().scale(50).setZ(0);
    	
    	if (SteeringManager.DEBUG) {
    		System.out.println("StickToPathSteer: TARGETING NEXT LINK");
    		System.out.println("StickToPathSteer: result           = [" + result.x + ", " + result.y + ", " + result.z + "]");
    		System.out.println("StickToPathSteer: length           = " + result.getLength());
    	}
    	
    	return result.asVector3d();
	}

	private void reset() {
		this.pathFuture = null;
		this.pathLocations = null;
		this.pathNavPoints = null;
		this.pathLinks = null;
	}
    
    private boolean hasPath() {
		return this.pathFuture != null && this.pathLocations != null && this.pathNavPoints != null && this.pathLinks != null && this.pathLocations.size() > 0 && this.pathNavPoints.size() > 0;
	}
    
    private void ensurePath() {
    	if (this.pathFuture == properties.getPath()) {
    		// ALREADY INITIALIZED TO CORRECT PATH
    		return;
    	}
    	
    	// CHECK PATH EXISTENCE
    	if (properties.getPath() == null) {
    		System.out.println("PATH IS NULL! Use stickToPathProperties.setPath() to provide a path before you use the steering!");
    		reset();
    		return;
    	}
    	if (!properties.getPath().isDone()) {
    		System.out.println("Waiting for the path...");
    		reset();
    		return;
    	}
    	if (properties.getPath().get() == null) {
    		System.out.println("Provided PATH is null, path does not exist? Or incorrect path future provided?");
    		reset();
    		return;
    	}
    	if (properties.getPath().get().size() == 0) {
    		System.out.println("Provided PATH is 0-sized! Path does not exist? Or incorrect path future provided?");
    		reset();
    		return;
    	}
    	
    	// PATH EXIST
    	// => initialize
    	this.pathFuture = properties.getPath();
    	this.pathLocations = getPath(this.pathFuture);
    	this.pathNavPoints = getNavPoints(this.pathLocations);
    	this.pathLinks = getLinks(this.pathNavPoints);
    }
    
	private List<NavPoint> getNavPoints(List<ILocated> pathLocations) {
    	if (pathLocations == null) {
    		return null;
    	}
    	List<NavPoint> result = new ArrayList<NavPoint>(pathLocations.size());
    	for (ILocated location : pathLocations) {
    		result.add(getNearestNavPoint(location, NAVPOINT_MAX_DISTANCE));
    	}
		return result;
	}
	
	private List<NavPointNeighbourLink> getLinks(List<NavPoint> pathNavPoints) {
    	if (pathNavPoints == null) {
    		return null;
    	}
    	if (pathNavPoints.size() == 1) {
    		List<NavPointNeighbourLink> result = new ArrayList<NavPointNeighbourLink>(1);
    		result.add(null);
    		return result;
    	}
    	
    	List<NavPointNeighbourLink> result = new ArrayList<NavPointNeighbourLink>();
    	NavPoint previous = pathNavPoints.get(0);
    	for (int i = 1; i < pathNavPoints.size(); ++i) {
    		NavPoint curr = pathNavPoints.get(i);
    		if (previous != null && curr != null) {
    			result.add(previous.getOutgoingEdges().get(curr.getId()));
    		} else {
    			result.add(null);
    		}
    		previous = curr;
    	}
    	
    	return result;
	}
    
    /**
	 * Returns nearest {@link NavPoint} to some 'target' no further than 'maxDistance' from the bot.
	 * @param target
	 * @param maxDistance
	 * @return
	 */
	public NavPoint getNearestNavPoint(ILocated target, double maxDistance) {
		if (target == null || target.getLocation() == null) return null;
		NavPoint result = DistanceUtils.getNearest(bot.getWorldView().getAll(NavPoint.class).values(), target);
		if (result == null) return null;
		if (target.getLocation().getDistance(result.getLocation()) > maxDistance) return null;
		return result;
	}

    
	/**
     * Returns PREVIOUS, CURRENT, NEXT neighbour links we're following.
     * @return
     */
    private Tuple3<NavPointNeighbourLink, NavPointNeighbourLink, NavPointNeighbourLink> getCurrentLinks() {
    	if (!hasPath()) return null;
    	
    	NavPointNeighbourLink nearest = DistanceUtils.getNearest(pathLinks, bot.getLocation(), NavigationGraphHelper.NAV_LINK_GET_DISTANCE);
    	
    	if (nearest == null) return null;
    	
    	int index = pathLinks.indexOf(nearest);
    	
    	if (NavigationGraphHelper.isPointProjectionOnLinkSegment(nearest, bot.getLocation())) {
    		return new Tuple3<NavPointNeighbourLink, NavPointNeighbourLink, NavPointNeighbourLink>(
    				  getPathLink(index-1), getPathLink(index), getPathLink(index+1)
    			   );
    	} else
		if (NavigationGraphHelper.isPointProjectionBeforeLinkSegment(nearest, bot.getLocation())) {
    		return new Tuple3<NavPointNeighbourLink, NavPointNeighbourLink, NavPointNeighbourLink>(
    				  getPathLink(index-2), getPathLink(index-1), getPathLink(index)
    			   );
    	} else {
    		// after
    		return new Tuple3<NavPointNeighbourLink, NavPointNeighbourLink, NavPointNeighbourLink>(
  				      getPathLink(index), getPathLink(index+1), getPathLink(index+2)
  			       );
    	}
    		
	}
	private NavPointNeighbourLink getPathLink(int index) {
		if (pathLinks == null) return null;
		if (index < 0 || index >= pathLinks.size()) return null;
		return pathLinks.get(index);
	}

	private List<ILocated> getPath(IPathFuture<ILocated> path) {
    	List<ILocated> locations = path.get();
    	if (locations == null) {
    		System.out.println("Provided PATH is null, have you provided correct path via stickToPathProperties.setPath() ?");
    		return null;
    	}
    	if (locations.size() == 0) {
    		System.out.println("Provided PATH has no elements, have you provided correct path via stickToPathProperties.setPath() ?");
    		return null;
    	}
    	return locations;
	}

	public void setProperties(SteeringProperties newProperties) {
    	if (!(newProperties instanceof StickToPathProperties)) throw new RuntimeException("newProperties is not of class StickToPathProperties, but " + newProperties.getClass());
    	this.properties = new StickToPathProperties((StickToPathProperties)newProperties);         
    }

    public StickToPathProperties getProperties() {        
        return new StickToPathProperties(properties);
    }
    
}
        