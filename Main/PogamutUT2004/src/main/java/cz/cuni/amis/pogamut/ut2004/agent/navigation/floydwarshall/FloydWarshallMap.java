package cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall;

import cz.cuni.amis.pogamut.base.agent.IGhostAgent;
import cz.cuni.amis.pogamut.base.agent.module.SensorModule;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004EdgeChecker;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import cz.cuni.amis.utils.IFilter;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.exception.PogamutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Point3d;

/**
 * Private map using Floyd-Warshall for path-finding.
 * <p>
 * <p>
 * It should be initialized upon receiving {@link MapPointListObtained} event.
 * It precomputes all the paths inside the environment using Floyd-Warshall
 * algorithm (O(n^3)). Use getReachable(), getDistance(), getPath() to obtain
 * the info about the path.
 * <p>
 * <p>
 * If needed you may call {@link FloydWarshallMap#refreshPathMatrix()} to
 * recompute Floyd-Warshall. Especially useful if you're using
 * {@link NavigationGraphBuilder} to modify the underlying navpoints/edges.
 * <p>
 * <p>
 * Based upon the implementation from Martin Krulis with his kind consent -
 * Thank you!
 * <p>
 * <p>
 * NOTE: requires O(navpoints.size^3) of memory ~ which is 10000^3 at max for
 * UT2004 (usually the biggest maps have 3000 navpoints max, but small maps,
 * e.g., DM-1on1-Albatross has 200 navpoints).
 * <p>
 * <p>
 * Because the algorithm is so space-hungery-beast, there is option to disable
 * it by calling {@link FloydWarshallMap#setEnabled(boolean)} with 'false'
 * argument. See the method for further documentation about the object behavior.
 *
 * @author Martin Krulis
 * @author Jimmy
 */
public class FloydWarshallMap extends SensorModule<IGhostAgent> implements IPathPlanner<NavPoint> {

    public static class PathMatrixNode {

        private float distance = Float.POSITIVE_INFINITY;
        private Integer viaNode = null;
        private List<NavPoint> path = null;

        /**
         * Doesn't leading anywhere
         */
        public PathMatrixNode() {
        }

        public PathMatrixNode(float distance) {
            this.distance = distance;
        }

        public float getDistance() {
            return distance;
        }

        public void setDistance(float distance) {
            this.distance = distance;
        }

        /**
         * Returns indice.
         *
         * @return
         */
        public Integer getViaNode() {
            return viaNode;
        }

        public void setViaNode(Integer indice) {
            this.viaNode = indice;
        }

        public List<NavPoint> getPath() {
            return path;
        }

        public void setPath(List<NavPoint> path) {
            this.path = path;
        }

    }

    private IWorldEventListener<MapPointListObtained> mapListener = new IWorldEventListener<MapPointListObtained>() {

        @Override
        public void notify(MapPointListObtained event) {
            if (log.isLoggable(Level.INFO)) {
                log.info("Map point list obtained.");
            }
            performFloydWarshall(event);
        }
    };

    /**
     * Flag mask representing unusable edge.
     */
    public static final int BAD_EDGE_FLAG = LinkFlag.FLY.get()
            | LinkFlag.LADDER.get() | LinkFlag.PROSCRIBED.get()
            | LinkFlag.SWIM.get() | LinkFlag.PLAYERONLY.get();

    public static boolean isWalkable(int flag) {
        return ((flag & BAD_EDGE_FLAG) == 0) && ((flag & LinkFlag.SPECIAL.get()) == 0);
    }

    /**
     * Prohibited edges.
     */
    protected int badEdgeFlag = 0;

    /**
     * Hash table converting navPoint IDs to our own indices.
     */
    protected Map<UnrealId, Integer> navPointIndices;

    /**
     * Mapping indices to nav points.
     * <p>
     * <p>
     * WILL BE NULL AFTER CONSTRUCTION! SERVES AS A TEMPORARY "GLOBAL VARIABLE"
     * DURING FLOYD-WARSHALL COMPUTATION AND PATH RECONSTRUCTION.
     */
    protected Map<Integer, NavPoint> indicesNavPoints;

    // Warshall's matrix of distances.
    protected PathMatrixNode[][] pathMatrix;

    /**
     * Whether the this object is enabled and the path is going to be actually
     * computed.
     * <p>
     * <p>
     * Enabled as default.
     */
    private boolean enabled = true;

    /**
     * Synchronizing access to object with respect to
     * {@link FloydWarshallMap#enabled}.
     */
    protected Object mutex = new Object();

    public FloydWarshallMap(IGhostAgent bot) {
        this(bot, BAD_EDGE_FLAG, null);
    }

    public FloydWarshallMap(IGhostAgent bot, Logger log) {
        this(bot, BAD_EDGE_FLAG, log);
    }

    public FloydWarshallMap(IGhostAgent bot, int badEdgeFlag, Logger log) {
        super(bot, log);
        this.badEdgeFlag = badEdgeFlag;
        worldView.addEventListener(MapPointListObtained.class, mapListener);
    }

    /**
     * Whether the object is active, see
     * {@link FloydWarshallMap#setEnabled(boolean)} for more documentation.
     * <p>
     * <p>
     * Default: true
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables/disables object. As default, object is initialized as 'enabled'.
     * <p>
     * <p>
     * If you "disable" the object (passing 'false' as an argument), it will
     * {@link FloydWarshallMap#cleanUp()} itself dropping any info it has about
     * paths, i.e., method
     * {@link FloydWarshallMap#computePath(NavPoint, NavPoint)} will start
     * throwing exceptions at you.
     * <p>
     * <p>
     * Note that if you "enable" the object (passing 'true' as an argument), it
     * won't AUTOMATICALLY trigger the computation of the algorithm, you should
     * manually {@link FloydWarshallMap#refreshPathMatrix()} when it is
     * appropriate (unless you enable it before list of navpoints is received,
     * in that case the path will get computed automatically).
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        synchronized (mutex) {
            this.enabled = enabled;
            if (!enabled) {
                cleanUp();
            }
        }
    }

    /**
     * Force FloydWarshall to run again, useful if you modify navpoints using
     * {@link NavigationGraphBuilder}.
     */
    public void refreshPathMatrix() {
        synchronized (mutex) {
            if (!enabled) {
                if (log.isLoggable(Level.FINE)) {
                    log.fine(this + ": Won't refresh path matrix, object is disabled.");
                }
                return;
            }
            if (log.isLoggable(Level.FINE)) {
                log.fine(this + ": Refreshing path matrix...");
            }
            List<NavPoint> navPoints = MyCollections.asList(agent.getWorldView().getAll(NavPoint.class).values());
            performFloydWarshall(navPoints);
            if (log.isLoggable(Level.WARNING)) {
                log.warning(this + ": Path matrix refreshed!");
            }
        }
    }

    protected void performFloydWarshall(MapPointListObtained map) {
        List<NavPoint> navPoints = MyCollections.asList(worldView.getAll(NavPoint.class).values());
        performFloydWarshall(navPoints);
    }

    protected void performFloydWarshall(List<NavPoint> navPoints) {
        if (!enabled) {
            if (log.isLoggable(Level.WARNING)) {
                log.fine(this + ": Should not be running Floyd-Warshall, object disabled.");
            }
            return;
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine(this + ": Beginning Floyd-Warshall algorithm...");
        }
        long start = System.currentTimeMillis();

        // prepares data structures
        int size = navPoints.size();
        navPointIndices = new HashMap<UnrealId, Integer>(size);
        indicesNavPoints = new HashMap<Integer, NavPoint>(size);
        pathMatrix = new PathMatrixNode[size][size];

        // Initialize navPoint indices mapping.
        for (int i = 0; i < navPoints.size(); ++i) {
            navPointIndices.put(navPoints.get(i).getId(), i);
            indicesNavPoints.put(i, navPoints.get(i));
        }

        // Initialize distance matrix.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                pathMatrix[i][j] = new PathMatrixNode((i == j) ? 0.0f
                        : Float.POSITIVE_INFINITY);
            }
        }

        // Set edge lengths into distance matrix.
        for (int i = 0; i < size; i++) {
            Point3d location = navPoints.get(i).getLocation().getPoint3d();
            for (NavPointNeighbourLink link : navPoints.get(i)
                    .getOutgoingEdges().values()) {
                if (UT2004EdgeChecker.checkLink(link)) {
                    
                    //Fill 0 as length for teleport edges.
                    if (link.getFromNavPoint().isTeleporter() && link.getToNavPoint().isTeleporter()) {
                        pathMatrix[i][navPointIndices.get(link.getToNavPoint()
                                .getId())].setDistance(0);
                    } else {
                    	UnrealId toNavPointId = link.getToNavPoint().getId();
                    	Integer toNavPointIndex = navPointIndices.get(toNavPointId);
                        pathMatrix[i][toNavPointIndex].setDistance((float) location.distance(link.getToNavPoint().getLocation().getPoint3d()));
                    }
                }
            }
        }

        // Perform Floyd-Warshall.
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    float newLen = pathMatrix[i][k].getDistance()
                            + pathMatrix[k][j].getDistance();
                    if (pathMatrix[i][j].getDistance() > newLen) {
                        pathMatrix[i][j].setDistance(newLen);
                        pathMatrix[i][j].setViaNode(k);
                    }
                }
            }
        }

        // Construct paths + log unreachable paths.
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (pathMatrix[i][j].getDistance() == Float.POSITIVE_INFINITY) {
                    if (log.isLoggable(Level.FINER)) {
                        log.finer("Unreachable path from " + navPoints.get(i).getId().getStringId()
                                + " -> " + navPoints.get(j).getId().getStringId());
                    }
                    count++;
                } else {
                    // path exists ... retrieve it
                    pathMatrix[i][j].setPath(retrievePath(i, j));
                }
            }
        }

        if (count > 0) {
            if (log.isLoggable(Level.WARNING)) {
                log.warning(this + ": There are " + count + " unreachable nav point pairs (if you wish to see more, set logging to Level.FINER).");
            }
        }

        if (log.isLoggable(Level.INFO)) {
            log.info(this + ": computation for " + size + " navpoints took " + (System.currentTimeMillis() - start) + " millis");
        }

        // null unneeded field to free some memory
        indicesNavPoints = null;
    }

    /**
     * Checks whether the edge is usable.
     *
     * @param from Starting nav point.
     * @param edge NeighNav object representing the edge.
     * @return boolean
     */
    public boolean checkLink(NavPointNeighbourLink edge) {
        // Bad flags (prohibited edges, swimming, flying...).
        if ((edge.getFlags() & badEdgeFlag) != 0) {
            return false;
        }

        // Lift flags.
        if ((edge.getFlags() & LinkFlag.SPECIAL.get()) != 0) {
            return true;
        }

		// Check whether the climbing angle is not so steep.
//		if ((edge.getFromNavPoint().getLocation().getPoint3d().distance(
//				edge.getToNavPoint().getLocation().getPoint3d()) < (edge
//				.getToNavPoint().getLocation().z - edge.getFromNavPoint()
//				.getLocation().z))
//				&& (edge.getFromNavPoint().getLocation().getPoint3d().distance(
//						edge.getToNavPoint().getLocation().getPoint3d()) > 100)) {
//			return false;
//		}
        // Check whether the jump is not so high.
//		if (((edge.getFlags() & LinkFlag.JUMP.get()) != 0)
//				&& (edge.getToNavPoint().getLocation().z
//						- edge.getFromNavPoint().getLocation().z > 80)) {
//			return false;
//		}
        //Check whether there is NeededJump attribute set - this means the bot has to 
        //provide the jump himself - if the Z of the jump is too high it means he
        //needs to rocket jump or ShieldGun jump - we will erase those links
        //as our bots are not capable of this
        if (edge.getNeededJump() != null && edge.getNeededJump().z > 680) {
            return false;
        }

        //This is a navpoint that requires lift jump - our bots can't do this - banning the link!
        if (edge.getToNavPoint().isLiftJumpExit()) {
            return false;
        }

        return true;
    }

    /**
     * Sub-routine of retrievePath - do not use! ... Well you may, it returns
     * path without 'from', 'to' or null if such path dosn't exist.
     * <p>
     * <p>
     * DO NOT USE OUTSIDE CONSTRUCTOR (relies on indicesNavPoints).
     *
     * @param from
     * @param to
     * @return
     */
    private List<NavPoint> retrievePathInner(Integer from, Integer to) {
        PathMatrixNode node = pathMatrix[from][to];
        if (node.getDistance() == Float.POSITIVE_INFINITY) {
            return null;
        }
        if (node.getViaNode() == null) {
            return new ArrayList<NavPoint>(0);
        }
        if (node.getViaNode() == null) {
            return new ArrayList<NavPoint>(0);
        }

        List<NavPoint> path = new ArrayList<NavPoint>();
        path.addAll(retrievePathInner(from, node.getViaNode()));
        path.add(indicesNavPoints.get(node.getViaNode()));
        path.addAll(retrievePathInner(node.getViaNode(), to));

        return path;
    }

    /**
     * Returns path between from-to or null if path doesn't exist. Path begins
     * with 'from' and ends with 'to'.
     * <p>
     * <p>
     * DO NOT USE OUTSIDE CONSTRUCTOR (relies on indicesNavPoints).
     *
     * @param from
     * @param to
     * @return
     */
    private List<NavPoint> retrievePath(Integer from, Integer to) {
        List<NavPoint> path = new ArrayList<NavPoint>();
        path.add(indicesNavPoints.get(from));
        path.addAll(retrievePathInner(from, to));
        path.add(indicesNavPoints.get(to));
        return path;
    }

    protected PathMatrixNode getPathMatrixNode(NavPoint np1, NavPoint np2) {
        return pathMatrix[navPointIndices.get(np1.getId())][navPointIndices
                .get(np2.getId())];
    }

    /**
     * Whether navpoint 'to' is reachable from the navpoint 'from'.
     * <p>
     * <p>
     * Throws exception if object is disabled, see
     * {@link FloydWarshallMap#setEnabled(boolean)}. Note that the object is
     * enabled by default.
     *
     * @param from
     * @param to
     * @return
     */
    public boolean reachable(NavPoint from, NavPoint to) {
        if ((from == null) || (to == null)) {
            return false;
        }
        return getPathMatrixNode(from, to).getDistance() != Float.POSITIVE_INFINITY;
    }

    /**
     * Calculate's distance between two nav points (using pathfinding).
     * <p>
     * <p>
     * Throws exception if object is disabled, see
     * {@link FloydWarshallMap#setEnabled(boolean)}. Note that the object is
     * enabled by default.
     *
     * @return Distance or POSITIVE_INFINITY if there's no path.
     */
    @Override
    public double getDistance(NavPoint from, NavPoint to) {
        if ((from == null) || (to == null)) {
            return Double.POSITIVE_INFINITY;
        }
        return getPathMatrixNode(from, to).getDistance();
    }

    /**
     * Returns path between navpoints 'from' -> 'to'. The path begins with
     * 'from' and ends with 'to'. If such path doesn't exist, returns null.
     * <p>
     * <p>
     * Throws exception if object is disabled, see
     * {@link FloydWarshallMap#setEnabled(boolean)}. Note that the object is
     * enabled by default.
     *
     * @param from
     * @param to
     * @return
     */
    public List<NavPoint> getPath(NavPoint from, NavPoint to) {
        synchronized (mutex) {
            if (!enabled) {
                throw new PogamutException("Can't return path as the object is disabled, call .setEnabled(true) & .refreshPathMatrix() first!", log, this);
            }
            if ((from == null) || (to == null)) {
                return null;
            }
            if (log.isLoggable(Level.FINE)) {
                log.fine("Retrieving path: " + from.getId().getStringId() + "[" + from.getLocation() + "] -> " + to.getId().getStringId() + "[" + to.getLocation() + "]");
            }
            List<NavPoint> path = getPathMatrixNode(from, to).getPath();
            if (path == null) {
                if (log.isLoggable(Level.WARNING)) {
                    log.warning("PATH NOT EXIST: " + from.getId().getStringId() + "[" + from.getLocation() + "] -> " + to.getId().getStringId() + "[" + to.getLocation() + "]");
                }
            } else {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Path exists - " + path.size() + " navpoints.");
                }
            }
            return path;
        }
    }

    @Override
    protected void cleanUp() {
        super.cleanUp();
        pathMatrix = null;
        navPointIndices = null;
    }

    @Override
    public String toString() {
        return "FloydWarshallMap";
    }

    /**
     * Returns path between navpoints 'from' -> 'to'. The path begins with
     * 'from' and ends with 'to'. If such path does not exist, it returns
     * zero-sized path.
     * <p>
     * <p>
     * Throws exception if object is disabled, see
     * {@link FloydWarshallMap#setEnabled(boolean)}. Note that the object is
     * enabled by default.
     *
     * @param from
     * @param to
     * @return
     */
    @Override
    public IPathFuture<NavPoint> computePath(NavPoint from, NavPoint to) {
        return new PrecomputedPathFuture<NavPoint>(from, to, getPath(from, to));
    }

	// ===============================
    // PATH-DISTANCE FILTERING METHODS
    // ===============================
    /**
     * Returns the nearest target (distance == path distance between 'from' and
     * 'target').
     * <p>
     * <p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from collection of objects
     */
    public <T extends NavPoint> T getNearestNavPoint(Collection<T> locations, T target) {
        if (locations == null) {
            return null;
        }
        if (target == null) {
            return null;
        }
        T nearest = null;
        double minDistance = Double.MAX_VALUE;
        double d;
        for (T l : locations) {
            if (l.getLocation() == null) {
                continue;
            }
            d = getDistance(target, l);
            if (d < minDistance) {
                minDistance = d;
                nearest = l;
            }
        }
        return nearest;
    }

    /**
     * Returns the nearest target (distance == path distance between 'from' and
     * 'target') that is not further than 'maxDistance'.
     * <p>
     * <p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return nearest object from collection of objects that is not further
     * than 'maxDistance'.
     */
    public <T extends NavPoint> T getNearestNavPoint(Collection<T> locations, NavPoint target, double maxDistance) {
        if (locations == null) {
            return null;
        }
        if (target == null) {
            return null;
        }
        T nearest = null;
        double minDistance = Double.MAX_VALUE;
        double d;
        for (T l : locations) {
            d = getDistance(target, l);
            if (d > maxDistance) {
                continue;
            }
            if (d < minDistance) {
                minDistance = d;
                nearest = l;
            }
        }
        return nearest;
    }

    /**
     * Returns the nearest target (distance == path distance between 'from' and
     * 'target').
     * <p>
     * <p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from collection of objects
     */
    public <T extends NavPoint> T getNearestFilteredNavPoint(Collection<T> locations, NavPoint target, IFilter<T> filter) {
        if (locations == null) {
            return null;
        }
        if (target == null) {
            return null;
        }
        T nearest = null;
        double minDistance = Double.MAX_VALUE;
        double d;
        for (T l : locations) {
            if (!filter.isAccepted(l)) {
                continue;
            }
            d = getDistance(target, l);
            if (d < minDistance) {
                minDistance = d;
                nearest = l;
            }
        }
        return nearest;
    }

    /**
     * Returns the second nearest target (distance == path distance between
     * 'from' and 'target').
     * <p>
     * <p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from collection of objects
     */
    public <T extends NavPoint> T getSecondNearestNavPoint(Collection<T> locations, NavPoint target) {
        if (locations == null) {
            return null;
        }
        if (target == null) {
            return null;
        }
        T secondNearest = null;
        T nearest = null;
        double closestDistance = Double.MAX_VALUE;
        double secondClosestDistance = Double.MAX_VALUE;

        for (T l : locations) {
            double distance = getDistance(target, l);
            if (distance < closestDistance) {
                secondClosestDistance = closestDistance;
                secondNearest = nearest;

                closestDistance = distance;
                nearest = l;
            } else {
                if (distance < secondClosestDistance) {
                    secondClosestDistance = distance;
                    secondNearest = l;
                }
            }
        }
        return secondNearest;
    }

    /**
     * Returns the nearest target (distance == path distance between 'from' and
     * 'target').
     * <p>
     * <p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from collection of objects
     */
    public <T extends Item> T getNearestItem(Collection<T> locations, NavPoint target) {
        if (locations == null) {
            return null;
        }
        if (target == null) {
            return null;
        }
        T nearest = null;
        double minDistance = Double.MAX_VALUE;
        double d;
        for (T l : locations) {
            if (l.getLocation() == null) {
                continue;
            }
            d = getDistance(target, l.getNavPoint());
            if (d < minDistance) {
                minDistance = d;
                nearest = l;
            }
        }
        return nearest;
    }

    /**
     * Returns the nearest target (distance == path distance between 'from' and
     * 'target') that is not further than 'maxDistance'.
     * <p>
     * <p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return nearest object from collection of objects that is not further
     * than 'maxDistance'.
     */
    public <T extends Item> T getNearestItem(Collection<T> locations, NavPoint target, double maxDistance) {
        if (locations == null) {
            return null;
        }
        if (target == null) {
            return null;
        }
        T nearest = null;
        double minDistance = Double.MAX_VALUE;
        double d;
        for (T l : locations) {
            d = getDistance(target, l.getNavPoint());
            if (d > maxDistance) {
                continue;
            }
            if (d < minDistance) {
                minDistance = d;
                nearest = l;
            }
        }
        return nearest;
    }

    /**
     * Returns the nearest target (distance == path distance between 'from' and
     * 'target').
     * <p>
     * <p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from collection of objects
     */
    public <T extends Item> T getNearestFilteredItem(Collection<T> locations, NavPoint target, IFilter<T> filter) {
        if (locations == null) {
            return null;
        }
        if (target == null) {
            return null;
        }
        T nearest = null;
        double minDistance = Double.MAX_VALUE;
        double d;
        for (T l : locations) {
            if (!filter.isAccepted(l)) {
                continue;
            }
            d = getDistance(target, l.getNavPoint());
            if (d < minDistance) {
                minDistance = d;
                nearest = l;
            }
        }
        return nearest;
    }

    /**
     * Returns the second nearest target (distance == path distance between
     * 'from' and 'target').
     * <p>
     * <p>
     * WARNING: O(n) complexity!
     *
     * @param <T>
     * @param targets
     * @param from
     * @return nearest object from collection of objects
     */
    public <T extends Item> T getSecondNearestItem(Collection<T> targets, NavPoint from) {
        if (targets == null) {
            return null;
        }
        if (from == null) {
            return null;
        }
        T secondNearest = null;
        T nearest = null;
        double closestDistance = Double.MAX_VALUE;
        double secondClosestDistance = Double.MAX_VALUE;

        for (T l : targets) {
            double distance = getDistance(from, l.getNavPoint());
            if (distance < closestDistance) {
                secondClosestDistance = closestDistance;
                secondNearest = nearest;

                closestDistance = distance;
                nearest = l;
            } else {
                if (distance < secondClosestDistance) {
                    secondClosestDistance = distance;
                    secondNearest = l;
                }
            }
        }
        return secondNearest;
    }

}
