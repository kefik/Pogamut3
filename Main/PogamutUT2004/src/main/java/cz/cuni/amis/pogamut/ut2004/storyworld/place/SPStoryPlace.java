package cz.cuni.amis.pogamut.ut2004.storyworld.place;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.vecmath.Point3d;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.storyworld.perception.SPLocation;
import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Basic interface for all story places.
 * <p><p>
 * Contains two methods that provides a means to create hierarchical description of places.
 * <p>(Kitchen is in House that is in the City that is in the Czech Republic...)
 * <p><p>
 * We don't support changing the story-place-graph at runtime! 
 *  
 * @author Jimmy
 */
@XStreamAlias("place")
public class SPStoryPlace {
	
	@XStreamOmitField
	private static Random random = new Random(System.currentTimeMillis());
	
	@XStreamOmitField
	private SPStoryPlace insidePlace = null;
	
	@XStreamAlias("inside")
	@XStreamAsAttribute
	protected String insidePlaceName;
	
	@XStreamOmitField
	private Set<SPStoryPlace> higherPlaces = null;
	
	@XStreamOmitField
	private Set<SPStoryPlace> containsPlaces = new HashSet<SPStoryPlace>();
	
	@XStreamOmitField
	private Set<SPStoryPlace> containsAllPlaces = null;
	
	@XStreamOmitField
	private Set<NavPoint> virtualPlaces = null;
	
	@XStreamOmitField
	private SPLocation center = null;
	
	@XStreamOmitField
	private NavPoint centerNavPoint = null;
	
	@XStreamAlias("name")
	@XStreamAsAttribute
	private String nameString;
	
	@XStreamOmitField
	private Token name = null;

	@XStreamOmitField
	private List<NavPoint> navPointsList = null;
	
	public SPStoryPlace(String name, SPStoryPlace inside) {
		this.nameString = name;
		this.name = Tokens.get(name);
		setInsidePlace(inside);
		
	}
	
	public SPStoryPlace(String name) {
		this.nameString = name;
		this.name = Tokens.get(name);		
	}

	/**
	 * Called by XStream after deserialization.
	 */
	private SPStoryPlace readResolve() {
		insidePlace = null;
		higherPlaces = null;
		containsPlaces = new HashSet<SPStoryPlace>();
		containsAllPlaces = null;
		virtualPlaces = null;
		name = null;
		getName();
		return this;
	}
	
	/**
	 * Used to inject the insidePlace after construction with "name" only (needed because the xml definition may
	 * be written the way it needs to be processed twiced, 1) create SPStoryPlaces, 2) inject inside)
	 * <p><p>
	 * Can't be called if insidePlace is already bound (RuntimeException).
	 * 
	 * @param place
	 */
	protected void setInsidePlace(SPStoryPlace place) {
		if (insidePlace != null) throw new RuntimeException("insidePlace already set for the " + this);
		this.insidePlace = place;
		if (this.insidePlace != null) {
			this.insidePlaceName = place.getName().getToken();
			this.insidePlace.getContainsPlaces().add(this);
		}
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof SPStoryPlace)) return false;
		SPStoryPlace place = (SPStoryPlace)obj;
		return name.equals(place.getName());
	}
			
	public Token getName() {
		if (name == null) name = Tokens.get(nameString);		
		return name;
	}

	/**
	 * Returns the place that this one is a part.<p> 
	 * If null - that means it's the highest
	 * @return
	 */
	public SPStoryPlace getInsidePlace() {
		return insidePlace;
	}
	
	/**
	 * Returns name of the place this one is inside.
	 * @return
	 */
	public String getInsidePlaceName() {
		return insidePlaceName;
	}
	
	/**
	 * Returns set of all places this one is a part of.<p>
	 * (You may ask whether this Kitchen in Czech Republic
	 * <p><p>
	 * Can't be called before the definition of all places 
	 * is completed otherwise it won't contain all higher places!
	 * (Lazy initialization.)
	 * 
	 * @return
	 */
	public Set<SPStoryPlace> getHigherPlaces() {
		if (higherPlaces == null) {
			higherPlaces = new HashSet<SPStoryPlace>();
			SPStoryPlace current = insidePlace;
			while (current != null) {
				higherPlaces.add(current);
				current = current.getInsidePlace();
			}
		}
		return higherPlaces;
	}

	/**
	 * Returns set with places this one contains (not recursive!).
	 * @return
	 */
	public Set<SPStoryPlace> getContainsPlaces() {
		return containsPlaces;
	}
	
	/**
	 * Returns all places that are inside this one.
	 * <p><p>
	 * Can't be called before the definition of all story places are defined,
	 * otherwise it won't contains all places. (Lazy initialization.)
	 * 
	 * @return
	 */
	public Set<SPStoryPlace> getContainsAllPlaces() {
		if (containsAllPlaces == null) {
			containsAllPlaces = new HashSet<SPStoryPlace>();
			for (SPStoryPlace place : containsPlaces) {
				containsAllPlaces.addAll(place.getContainsAllPlaces());
			}
		}
		return containsAllPlaces;
	}
	
	/**
	 * Returns places inside the virtual world that belongs to this place. Basically
	 * this is binding to the chosen 3D world simulator. It should contains objects upon
	 * whose the real path-finding can run.
	 * <p><p>
	 * Can't be called before the definition of all story places are defined,
	 * otherwise it won't contains all places. (Lazy initialization.)
	 * 
	 * @return
	 */
	public Set<NavPoint> getNavPoints() {
		if (virtualPlaces == null) {
			virtualPlaces = new HashSet<NavPoint>();
			for (SPStoryPlace place : getContainsPlaces()) {
				virtualPlaces.addAll(place.getNavPoints());
			}
		}
		return virtualPlaces; 
	}
	
	/**
	 * Returns places inside the virtual world that belongs to this place. Basically
	 * this is binding to the chosen 3D world simulator. It should contains objects upon
	 * whose the real path-finding can run.
	 * <p><p>
	 * Can't be called before the definition of all story places are defined,
	 * otherwise it won't contains all places. (Lazy initialization.)
	 * @return
	 */
	public List<NavPoint> getNavPointsList() {
		if (navPointsList == null) {
			navPointsList = MyCollections.asList(getNavPoints());
		}
		return navPointsList;
	}
	
	public SPLocation getCenter() {
		if (center == null) {
			double x = 0, y = 0, z = 0;
			for (NavPoint navPoint : getNavPoints()) {
				x += navPoint.getLocation().x;
				y += navPoint.getLocation().y;
				z += navPoint.getLocation().z;
			}
			center = new SPLocation(x / getNavPoints().size(), y / getNavPoints().size(), z / getNavPoints().size());
		}
		return center;
	}
	
	public NavPoint getCenterNavPoint() {
		if (centerNavPoint == null) {
			centerNavPoint = getNearestNavPoint(getCenter());
		}
		return centerNavPoint;
	}
	
	public NavPoint getRandomNavPoint() {
		if (getNavPointsList().size() == 0) return null;
		return getNavPointsList().get(random.nextInt(getNavPointsList().size()));
	}
	
	public  NavPoint getNearestNavPoint(SPLocation location) {
		// TODO: implement using oct-trees
		Point3d loc = location.asPoint3d();
		double nearestDistance = Double.MAX_VALUE;
		NavPoint nearest = null;
		for (NavPoint navPoint : getNavPoints()) {
			double distance = loc.distance(navPoint.getLocation().getPoint3d());
			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearest = navPoint;
			}
		}		
		return nearest;
	}
	
	public  NavPoint getFurthestNavPoint(SPLocation location) {
		// TODO: implement using oct-trees
		Point3d loc = location.asPoint3d();
		double furthestDistance = Double.MAX_VALUE;
		NavPoint furthest = null;
		for (NavPoint navPoint : getNavPoints()) {
			double distance = loc.distance(navPoint.getLocation().getPoint3d());
			if (distance > furthestDistance) {
				furthestDistance = distance;
				furthest = navPoint;
			}
		}		
		return furthest;
	}
	
	public Map<NavPoint, Double> getNavPointDistances(SPLocation location) {
		Map<NavPoint, Double> distances = new HashMap<NavPoint, Double>();
		for (NavPoint navPoint : getNavPoints()) { 
			distances.put(navPoint, navPoint.getLocation().getPoint3d().distance(location.asPoint3d()));
		}
		return distances;
	}
	
	public Map<Double, NavPoint> getNavPointDistancesSwapped(SPLocation location) {
		Map<Double, NavPoint> distances = new HashMap<Double, NavPoint>();
		for (NavPoint navPoint : getNavPoints()) { 
			distances.put(navPoint.getLocation().getPoint3d().distance(location.asPoint3d()), navPoint);
		}
		return distances;
	}
	
	/**
	 * @param distance must be &lt;0,1> ... 0 ~ pick from all possible navpoint, 1 ~ pick the furthest navpoint
	 * @return
	 */
	public NavPoint getRandomNavPoint(SPLocation location, double distance) {
		if (distance >= 1) {
			return getFurthestNavPoint(location);
		}
		Map<Double, NavPoint> distances = getNavPointDistancesSwapped(location);
		
		if (distance <= 0) {
			Collection<NavPoint> navPoints = distances.values();
			int num = random.nextInt(navPoints.size());
			Iterator<NavPoint> np = navPoints.iterator();
			for (int i = 0; i < num-1; ++i) {
				np.next();
			}
			return np.next();				
		}
		
		Double[] keys = distances.keySet().toArray(new Double[distances.keySet().size()]);
		Arrays.sort(keys, new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				return Double.compare(o1, o2);
			}
			
		});
		int randomInt = Double.valueOf(Math.round((1-distance)*keys.length)).intValue();
		int index = 0;
		if (randomInt > 1) {
			index = keys.length - 1 - random.nextInt(randomInt);
		} else { 
			index = keys.length - 1;
		}
		if (index < 0) {
			System.out.println("huh");
		}
		NavPoint navPoint = distances.get(keys[index]); 
		return navPoint;						
	}
	
	/**
	 * DO NOT ALTER!
	 * <p><p>
	 * Used during translation into prolog!
	 */
	@Override
	public String toString() {
		return name.getToken(); 
	}

	public boolean contains(SPStoryPlace place) {
		if (place == this) return true;
		return getContainsAllPlaces().contains(place);		 
	}

}