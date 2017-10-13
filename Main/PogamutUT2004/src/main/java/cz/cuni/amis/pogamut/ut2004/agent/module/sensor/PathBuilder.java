package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.TabooSet;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.IFilter;

/**
 * The PathBuilder can be used to incrementally build the path the bot should run over.
 * <p><p>
 * Begin the building by calling {@link #newPath()}.
 * <p><p>
 * Then you can sequentially call {@link #planTo(ILocated)} and {@link #appendPath(IPathFuture)} or {@link #appendPath(List)}
 * to add more path segment into the result.
 * <p><p>
 * Obtain path via {@link #getPath()} or (specifically for {@link IUT2004Navigation#navigate(IPathFuture)} via {@link #getPathFuture()}).
 * <p><p>
 * You can probe path via methods like {@link #getPathLength()}, {@link #isItemOnPath(Item)} or {@link #getItemsOnPathSequence()}.
 * 
 * @author Jimmy
 */
public class PathBuilder {
	
	/**
	 * Double-linked-list of items that happens to be on the path.
	 * <p><p>
	 * Every instance knows which item is following and how far it is.
	 * <p><p>
	 * There is also an information about {@link #totalDistanceFromStart}, which can be used to compute whether
	 * the item is going to be present there.
	 * 
	 * @author Jimmy
	 */
	public static class ItemOnPath {
				
		public final Item item;
		public final int pathIndex;
		
		public ItemOnPath previous = null;
		public double previousDistance = 0;
		
		public ItemOnPath next = null;
		public double nextDistance = 0;
		
		public double totalDistanceFromStart = 0; 
		
		public ItemOnPath(Item item, int pathIndex) {
			this.item = item;
			this.pathIndex = pathIndex;
		}
		
	}
	
	private AgentInfo info;
	
	private IPathPlanner<ILocated> planner;

	private ILocated start;
	
	private List<ILocated> path = new ArrayList<ILocated>();
	
	private List<ItemOnPath> itemsOnPathSequence = new ArrayList<ItemOnPath>();
	
	private Set<Item> itemsOnPath = new HashSet<Item>();
	
	private TabooSet<Item> itemsOnPathTaboo;
	
	private ILocated end;
	
	private double pathLength;
	
	private LogCategory log;
	
	public PathBuilder(UT2004Bot bot, AgentInfo info, IPathPlanner<ILocated> pathPlanner) {
		this.info = info;
		this.log = bot.getLogger().getCategory("PathBuilder");
		
		this.planner = pathPlanner;
		
		itemsOnPathTaboo = new TabooSet<Item>(bot);
	}
	
	public LogCategory getLog() {
		return log;
	}
	
	/**
	 * Resets current path and start building a new one.
	 * <p><p>
	 * START point is taken as current bot's location.
	 */
	public void newPath() {
		start = info.getLocation();		
		if (info.getNearestNavPoint().getLocation().getDistance(start.getLocation()) < 50) start = info.getNearestNavPoint();
		
		end = null;
		
		pathLength = 0;
		path.clear();
		itemsOnPath.clear();
		itemsOnPathSequence.clear();
		itemsOnPathTaboo.clear();
		
		appendPoint(start);		
		
		end = start;
	}
	
	/**
	 * Start location of the path (at first index), bot's current location.
	 * @return
	 */
	public ILocated getStart() {
		return start;
	}
	
	/**
	 * Current path end.
	 * @return
	 */
	public ILocated getEnd() {
		return end;
	}
	
	/**
	 * Adds another 'point' into the path.
	 * @param point
	 */
	public void appendPoint(ILocated point) {
		if (point == null) {
			// IGNORE
			return;
		}
		
		// CHECK IF IT'S NOT THE SAME AS THE END
		if (end != null) {
			if (end.getLocation().getDistance(point.getLocation()) < 30) {
				// SAME AS PATH END ... ignore...
				return;
			}
		}
		
		// ADD THE PATH POINT
		path.add(point);
		end = point;
		
		// PROLONG THE PATH LENGTH
		if (path.size() > 1) {
			pathLength += path.get(path.size()-2).getLocation().getDistance(path.get(path.size()-1).getLocation());
		}
		
		// CHECK FOR ITEMS
		if (point instanceof NavPoint) {
			NavPoint np = (NavPoint)point;
			if (np.isInvSpot()) {
				Item item = np.getItemInstance();
				if (item != null) {
					
				}
			}
		} else
		if (point instanceof Item) {
			Item item = (Item)point;
			itemAddedIntoPath(item);
		}		
	}
	
	private void itemAddedIntoPath(Item item) {
		itemsOnPath.add(item);		
		itemsOnPathTaboo.add(item);
		
		ItemOnPath curr = new ItemOnPath(item, path.size()-1);
		
		if (itemsOnPathSequence.size() == 0) {
			itemsOnPathSequence.add(curr);
			curr.totalDistanceFromStart = getPathLength();
			return;
		}
		
		ItemOnPath previous = itemsOnPathSequence.get(itemsOnPathSequence.size()-1);
		itemsOnPathSequence.add(curr);
		
		curr.previous = previous;
		previous.next = curr;
		
		// COUNT PATH DISTANCE
		double distance = 0;
		ILocated from = path.get(previous.pathIndex);
		int i = previous.pathIndex + 1;
		while (i <= curr.pathIndex) {
			ILocated next = path.get(i);
			distance += from.getLocation().getDistance(next.getLocation());
			
			from = next;			
			++i;
		}
		
		curr.previousDistance = distance;
		previous.nextDistance = distance;		
		curr.totalDistanceFromStart = previous.totalDistanceFromStart + distance;
	}

	/**
	 * Returns list of points that represents current path.
	 * @return
	 */
	public List<ILocated> getPath() {
		return path;
	}
	
	/**
	 * Returns the path in the form that is suitable for {@link IUT2004Navigation#navigate(IPathFuture)}.
	 * @return
	 */
	public IPathFuture<ILocated> getPathFuture() {
		return new PrecomputedPathFuture<ILocated>(start, end, getPath());
	}
	
	/**
	 * Current path length (path-distance between {@link #getStart()} and {@link #getEnd()}.
	 * @return
	 */
	public double getPathLength() {
		return pathLength;
	}
	
	/**
	 * Current {@link Set} of {@link Item}s that lies directly on the path.
	 * Note that these {@link Item}s might not be spawned right now or at the point you reach it.
	 * @return
	 */
	public Set<Item> getItemsOnPath() {
		return itemsOnPath;
	}
	
	/**
	 * {@link Item}s that lies on the path in particular sequence as they are reached.
	 * @return
	 */
	public List<ItemOnPath> getItemsOnPathSequence() {
		return itemsOnPathSequence;
	}
	
	/**
	 * {@link TabooSet} of items that can be used as {@link IFilter}
	 * @return
	 */
	public TabooSet<Item> getItemsOnPathTaboo() {
		return itemsOnPathTaboo;
	}
	
	/**
	 * Simple check whether some {@link Item} already happens to be on the path.
	 * @param item
	 * @return
	 */
	public boolean isItemOnPath(Item item) {
		return itemsOnPath.contains(item);
	}
	
	/**
	 * Continue the path to from {@link #getEnd()} to 'target'.
	 * @param target
	 * @return
	 */
	public boolean planTo(ILocated target) {
		IPathFuture<ILocated> path = planner.computePath(end, target);
		if (path == null) {
			log.warning("NON-EXISTANT PATH: " + end + " -> " + target);
			return false;
		}
		return appendPath(path);
	}
	
	/**
	 * Append this segment to the path.
	 * @param pathFuture
	 * @return
	 */
	public boolean appendPath(IPathFuture pathFuture) {
		if (pathFuture == null) {
			log.warning("COULD NOT APPEND PATH ... pathFuture is NULL!");
			return false;
		}
		if (!pathFuture.isDone()) {
			log.warning("COULD NOT APPEND PATH ... pathFuture is not computed!");
			return false;
		}
		
		List path = pathFuture.get();		
		return appendPath(path);
	}
	
	/**
	 * Append this segment to the path.
	 * @param pathFuture
	 * @return
	 */
	public boolean appendPath(List path) {
		if (path == null) {
			log.warning("COULD NOT APPEND PATH ... path is NULL!");
			return false;
		} 
		for (Object o : path) {
			if (o == null) continue;
			if (o instanceof ILocated) {
				appendPoint((ILocated)o);
			} else
			if (o instanceof Location) {
				appendPoint((Location)o);
			} else {
				log.warning("COULD NOT APPEND PATH ... there is incompatible point on the path: " + o);
			}
		}
		
		return true;
	}
	

}
