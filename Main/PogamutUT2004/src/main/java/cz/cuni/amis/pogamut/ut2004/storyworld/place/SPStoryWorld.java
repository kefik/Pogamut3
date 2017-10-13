package cz.cuni.amis.pogamut.ut2004.storyworld.place;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.vecmath.Point3d;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointMessage;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.pogamut.ut2004.storyworld.perception.SPLocation;
import cz.cuni.amis.utils.Job;
import cz.cuni.amis.utils.maps.HashMapSet;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * TODO: CURRENTLY IT DOES NOT WORK!
 * @author Jimmy
 *
 */
public class SPStoryWorld {

	private static final double NEAR = 1000;

	private static final Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());

	private Map<Token, SPStoryPlace> places;

	private Map<Token, SPStoryPlaceBase> bases;

	/**
	 * Lazy initialization.
	 */
	private Map<Token, SPStoryPlace> allPlaces = null;

	/**
	 * Lazy initialization.
	 */
	private Set<NavPoint> navPoints = null;

	private Map<String, NavPoint> navPointsMap = null;

	/**
	 * Lazy initialization
	 */

	/**
	 * Lazy initialization.
	 */
	private HashMapSet<UnrealId, SPStoryPlaceBase> navPointIdToBasePlace = null;

	private IWorldEventListener<MapPointListObtained> mapPointsListener =
		new IWorldEventListener<MapPointListObtained>() {

			@Override
			public void notify(MapPointListObtained event) {
				mapPointsList(event);
				new Job<Boolean>() {

					@Override
					protected void job() throws Exception {
						//bot.stop();
						setResult(true);
					}

				}.startJob();
			}

	};

	private IWorldView worldView;

	private FloydWarshallMap navigation;

	private Logger log;

	private SPStoryWorldData data;

	public SPStoryWorld(String worldXMLDefinitionFile, IWorldView ww, Logger log) throws FileNotFoundException {
		this(SPStoryWorldData.loadXML(new File(worldXMLDefinitionFile)), ww, log);
	}

	public SPStoryWorld(File worldXMLDefinition, IWorldView ww, Logger log) throws FileNotFoundException {
		this(SPStoryWorldData.loadXML(worldXMLDefinition), ww, log);
	}

	public SPStoryWorld(SPStoryWorldData data, IWorldView ww, Logger log) {
		this.log = log;

		this.data = data;

		this.places = data.getPlaces();

		this.bases = data.getBases();

		this.worldView = ww;
		this.worldView.addEventListener(MapPointListObtained.class, mapPointsListener);


	}

	private void mapPointsList(MapPointListObtained map) {
		for (SPStoryPlaceBase placeBase : bases.values()) {
			placeBase.bountNavPoints(map);
		}
		getNavPoints();
		getNavPointsToPlaceMap();
		// TODO: [Jakub] redesign the whole class! Merge together with Radim's work.
		//this.navigation = new FloydWarshallMap(map, log);
	}

	public SPStoryPlace getPlace(Token name) {
		return getAllPlaces().get(name);
	}

	public SPStoryPlace getPlace(String name) {
		return getAllPlaces().get(Tokens.get(name));
	}

	public SPStoryPlaceBase getBase(Token name) {
		return bases.get(name);
	}

	public NavPoint getNavPoint(String id) {
		return navPointsMap.get(id);
	}

	public SPStoryPlaceBase getBase(String name) {
		return bases.get(Tokens.get(name));
	}

	public Set<SPStoryPlaceBase> getBase(UnrealId navPointId) {
		Set<SPStoryPlaceBase> bases = getNavPointsToPlaceMap().get(navPointId);
		if (bases == null || bases.size() == 0) {
			throw new RuntimeException("story base place hasn't been found for nav point " + navPointId);
		}
		return bases;
	}

	/**
	 * Nearest navpoint must be max "NEAR" far.
	 * @param location
	 * @return
	 */
	public Set<SPStoryPlaceBase> at(SPLocation location) {
		NavPoint nearest = getNearestNavPoint(location);
		if (location.asPoint3d().distance(nearest.getLocation().getPoint3d()) > NEAR) {
			return EMPTY_SET;
		}
		return getBase(nearest.getId());
	}

	public  NavPoint getNearestNavPoint(SPLocation location) {
		// TODO: implement using oct-trees
		Point3d loc = location.asPoint3d();
		double nearestDistance = Double.MAX_VALUE;
		NavPoint nearest = null;
		for (NavPoint navPoint : getNavPoints()) {
			try{
				double distance = loc.distance(navPoint.getLocation().getPoint3d());
				if (distance < nearestDistance) {
					nearestDistance = distance;
					nearest = navPoint;
				}
			}catch(NullPointerException npe){

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

	/**
	 * Returns whether 'location' belongs to the 'place'. Nearest navpoint must be at least 1000 close not to return false
	 * automatically.
	 * @param location
	 * @param place
	 * @return
	 */
	public boolean isInside(SPLocation location, SPStoryPlace place) {
		Set<SPStoryPlaceBase> bases = at(location);
		if (bases == null || bases.size() == 0) return false;
		for (SPStoryPlaceBase base : bases) {
			if (base.contains(place)) return true;
		}
		return false;
	}

	/**
	 * Returns places inside the virtual world.
	 * <p><p>
	 * Can't be called before the definition of all story places are defined,
	 * otherwise it won't contains all places. (Lazy initialization.)
	 *
	 * @return
	 */
	public Set<NavPoint> getNavPoints() {
		if (navPoints == null) {
			navPoints = new HashSet<NavPoint>();
			navPointsMap = new HashMap<String, NavPoint>();
			for (SPStoryPlaceBase base : bases.values()) {
				for (NavPoint np : base.getNavPoints()) {
					navPointsMap.put(np.getId().getStringId(), np);
				}
				navPoints.addAll(base.getNavPoints());
			}
		}
		return navPoints;
	}

	protected HashMapSet<UnrealId, SPStoryPlaceBase> getNavPointsToPlaceMap() {
		if (navPointIdToBasePlace == null) {
			navPointIdToBasePlace = new HashMapSet<UnrealId, SPStoryPlaceBase>();
			for (SPStoryPlaceBase base : bases.values()) {
				for (NavPoint navPoint : base.getNavPoints()) {
					navPointIdToBasePlace.add(navPoint.getId(), base);
				}
			}
		}
		return navPointIdToBasePlace;
	}

	protected Map<Token, SPStoryPlace> getAllPlaces() {
		if (allPlaces == null) {
			allPlaces = new HashMap<Token, SPStoryPlace>();
			for (SPStoryPlace place : places.values()) {
				allPlaces.put(place.getName(), place);
			}
			for (SPStoryPlaceBase base : bases.values()) {
				allPlaces.put(base.getName(), base);
			}
		}
		return allPlaces;
	}

	public FloydWarshallMap getNavigation() {
		return navigation;
	}

	/**
	 * Finds path between navpoints that are the nearest to "from" / "to" location.
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public List<NavPoint> getPath(SPLocation from, SPLocation to) {
		return getPath(from, getNearestNavPoint(to));
	}

	/**
	 * Finds path between navpoint that is the nearest to "from" and navpoint "to".
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	public List<NavPoint> getPath(SPLocation from, NavPoint to) {
		return getNavigation().getPath(getNearestNavPoint(from), to);
	}

	/**
	 * Finds shortest path between the nearest navpoint to "from" and the "place",
	 * searching all the navpoints that is contained inside the place.
	 *
	 * @param from
	 * @param place
	 * @return
	 */
	public List<NavPoint> getPath(SPLocation from, SPStoryPlace place) {
		NavPoint start = getNearestNavPoint(from);
		double shortestPath = Float.POSITIVE_INFINITY;
		List<NavPoint> path = null;
		for (NavPoint np : place.getNavPoints()) {
			double distance = getNavigation().getDistance(start, np);
			if (shortestPath > distance) {
				shortestPath = distance;
				path = getNavigation().getPath(start, np);
			}

		}
		return path;
	}

	public SPStoryWorldData getStoryWorldData() {
		return data;
	}

	@Override
	public String toString() {
		return "SPStoryWorld";
	}

}