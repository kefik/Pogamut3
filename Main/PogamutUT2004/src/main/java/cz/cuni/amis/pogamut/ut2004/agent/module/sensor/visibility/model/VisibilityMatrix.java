package cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.Visibility;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.IFilter;

//@XStreamAlias(value="visibilityMapPoints")
public class VisibilityMatrix implements Serializable {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -5542380797228986422L;

//	private transient static XStream xstream;
//	
//	private static XStream getXStream() {
//		if (xstream == null) {
//			xstream = new XStream(new DomDriver());
//			xstream.autodetectAnnotations(true);
//			xstream.alias(VisibilityMatrix.class.getAnnotation(XStreamAlias.class).value(), VisibilityMatrix.class);
//		}		
//		return xstream;
//	}
	
//	@XStreamAlias(value="locations")
	private Map<Integer, VisibilityLocation> locations = new HashMap<Integer, VisibilityLocation>();
	protected HashMap<NavPoint, Integer> navPointToLocationIndex = null;
	
	private BitMatrix matrix;

//	@XStreamAsAttribute
//	@XStreamAlias(value="mapName")
	private String mapName;
	
	protected VisibilityMatrix() {		
	}
	
	public VisibilityMatrix(String mapName, int size) {
		this.mapName = mapName;
		matrix = new BitMatrix(size, size);
	}
		
	public String getMapName() {
		return mapName;
	}
	
	public Map<Integer, VisibilityLocation> getLocations() {
		return locations;
	}
	
	public VisibilityLocation getLocation(int index) {
		return locations.get(index);
	}
	
	/**
	 * TRUE == visible
	 * FALSE == not-visible
	 * @return
	 */
	public BitMatrix getMatrix() {
		return matrix;
	}
	
//	public static String getFileName_Locations(String mapName) {
//		return "VisibilityMatrix-" + mapName + "-Locations.xml";
//	}
//	
//	public static String getFileName_Matrix(String mapName) {
//		return "VisibilityMatrix-" + mapName + "-Matrix.bin";
//	}
	
	public static String getFileName_All(String mapName) {
		return "VisibilityMatrix-" + mapName + "-All.bin";
	}
	
	public static File getFile_All(File directory, String mapName) {
		return new File(directory, getFileName_All(mapName));
	}

//	public void saveXStream(File directory) {
//		try {
//			File file1 = new File(directory, getFileName_Locations(mapName));
//			File file2 = new File(directory, getFileName_Matrix(mapName));
//			
//			FileWriter writer = new FileWriter(file1);			
//			XStream xstream = getXStream();
//			synchronized(xstream) {
//				xstream.toXML(this, writer);
//			}
//			writer.close();
//			
//			matrix.saveToFile(file2);			
//		} catch (Exception e) {
//			throw new RuntimeException("Failed to save VisibilityMatrix.", e);
//		}
//	}
//	
//	public static VisibilityMatrix loadXStream(File directory, String mapName) {
//		try {
//			File file1 = new File(directory, getFileName_Locations(mapName));
//			File file2 = new File(directory, getFileName_Matrix(mapName));
//			
//			VisibilityMatrix result = null;
//			
//			FileReader reader = new FileReader(file1);			
//			XStream xstream = getXStream();
//			synchronized(xstream) {
//				result = (VisibilityMatrix) xstream.fromXML(reader);
//			}
//			reader.close();
//			
//			result.matrix = BitMatrix.loadFromFile(file2);
//			
//			return result;
//		} catch (Exception e) {
//			throw new RuntimeException("Failed to load VisibilityMatrix.", e);
//		}
//	}
	
	public void save(File directory) {
		try {
			File file1 = getFile_All(directory, mapName);
			
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file1));
			try {
				output.writeObject(this);
			} finally {
				output.close();
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to save VisibilityMatrix.", e);
		}
	}
	
	public static VisibilityMatrix load(File directory, String mapName) {				
		try {
			File file1 = getFile_All(directory, mapName);
			
    		ObjectInputStream input = new ObjectInputStream(new FileInputStream(file1));
    		VisibilityMatrix result = null;
    		try {
    			result = (VisibilityMatrix)input.readObject();
    		} finally {
    			input.close();
    		}
    		
    		return result;
    	} catch (Exception e) {
    		throw new RuntimeException("Failed to load VisibilityMatrix.", e);
    	}
	}
	
	/**
	 * Return total number of locations-pairs (not including X-X).
	 * @return
	 */
	public int getPairCount() {
		return locations.size() * (locations.size() - 1) / 2;
	}
	
	/**
	 * Return total number of visible pairs.
	 * 
	 * WARNING: O(n^2) time complexity.
	 * 
	 * @return
	 */
	public int getVisiblePairCount() {
		int count = 0;
		for (int i = 0; i < locations.size(); ++i) {
			for (int j = i+1; j < locations.size(); ++j) {
				if (matrix.get(i, j)) {
					++count;
				}
			}
		}
		return count;
	}
	
	// ============================
	// READ RESOLVE
	// ============================
	
	private Object readResolve() throws ObjectStreamException {
		mutex = new Object();
		coverPoints = new HashMap<Integer, Set<VisibilityLocation>>();
		visiblePoints = new HashMap<Integer, Set<VisibilityLocation>>();
		coverNavPoints = new HashMap<Integer, Set<NavPoint>>();
		visibleNavPoints = new HashMap<Integer, Set<NavPoint>>();
		
		return this;
	}
	
	// ============================
	// SENSORS / QUERIES
	// ============================
	
	private transient Object mutex = new Object();
	
	private transient Map<Integer, Set<VisibilityLocation>> coverPoints = new HashMap<Integer, Set<VisibilityLocation>>();
		
	private transient Map<Integer, Set<VisibilityLocation>> visiblePoints = new HashMap<Integer, Set<VisibilityLocation>>();
	
	private transient Map<Integer, Set<NavPoint>> coverNavPoints = new HashMap<Integer, Set<NavPoint>>();
	
	private transient Map<Integer, Set<NavPoint>> visibleNavPoints = new HashMap<Integer, Set<NavPoint>>();

	/**
	 * Returns all {@link VisibilityLocation} that are visible according to 'column' (column[key] == TRUE, locations.get(key) included).
	 * @param column
	 * @return
	 */
	public Set<VisibilityLocation> getVisiblePoints(BitSet column) {
		if (column == null) return null;
		Set<VisibilityLocation> result = new HashSet<VisibilityLocation>();
		for (int i = 0; i < column.length() && i < matrix.rows(); ++i) {
			if (column.get(i)) {
				result.add(locations.get(i));
			}
		}
		return result;
	}
	
	/**
	 * Returns all {@link NavPoint} that are visible according to 'column' (column[key] == TRUE, locations.get(key) included if NavPoint).
	 * @param column
	 * @return
	 */
	public Set<NavPoint> getVisibleNavPoints(BitSet column) {
		if (column == null) return null;
		Set<NavPoint> result = new HashSet<NavPoint>();
		for (int i = 0; i < column.length() && i < matrix.rows(); ++i) {
			if (column.get(i)) {
				VisibilityLocation vLoc = locations.get(i);
				if (vLoc.navPoint != null) result.add(vLoc.navPoint);
			}
		}
		return result;
	}
	
	/**
	 * Returns nav points from 'visibilityLocations'.
	 * @param visibilityLocations
	 * @return
	 */
	public Set<NavPoint> getNavPoints(Collection<VisibilityLocation> visibilityLocations) {
		Set<NavPoint> result = new HashSet<NavPoint>();
		for (VisibilityLocation vLoc : visibilityLocations) {
			if (vLoc.navPoint != null) {
				result.add(vLoc.navPoint);
			}
		}
		return result;
	}
	
	/**
	 * Returns all {@link VisibilityLocation} that are NOT visible according to 'column' (column[key] == FALSE, locations.get(key) included).
	 * @param column
	 * @return
	 */
	public Set<VisibilityLocation> getCoverPoints(BitSet column) {
		Set<VisibilityLocation> result = new HashSet<VisibilityLocation>();
		for (int i = 0; i < column.length() && i < matrix.rows(); ++i) {
			if (!column.get(i)) {
				result.add(locations.get(i));
			}
		}
		return result;
	}
	
	/**
	 * Returns all {@link NavPoint} that are NOT visible according to 'column' (column[key] == FALSE, locations.get(key) included if NavPoint).
	 * @param column
	 * @return
	 */
	public Set<NavPoint> getCoverNavPoints(BitSet column) {
		Set<NavPoint> result = new HashSet<NavPoint>();
		for (int i = 0; i < column.length() && i < matrix.rows(); ++i) {
			if (!column.get(i)) {
				VisibilityLocation vLoc = locations.get(i);
				if (vLoc.navPoint != null) result.add(vLoc.navPoint);
			}
		}
		return result;
	}

	
	/**
	 * Nearest key-{@link VisibilityLocation} entry to 'located'.
	 */
	public Entry<Integer, VisibilityLocation> getNearestEntry(ILocated located) {
		if (located == null) return null;
		Location location = located.getLocation();
		double minDistance = Double.MAX_VALUE;
		Entry<Integer, VisibilityLocation> nearest = null;
		for (Entry<Integer, VisibilityLocation> entry : locations.entrySet()) { // SHIT HITS THE FAN
			double distance = location.getDistance(entry.getValue().getLocation());
			if (distance < minDistance) {
				minDistance = distance;
				nearest = entry;
			}
		}
		return nearest;
	}
	
	/**
	 * Nearest {@link VisibilityLocation} to 'located'.
	 * @param located
	 * @return
	 */
	public VisibilityLocation getNearest(ILocated located) {
		if (located == null) return null;
		Entry<Integer, VisibilityLocation> nearest = getNearestEntry(located);
		if (nearest == null) return null;
		return nearest.getValue();
	}
	
	/**
	 * Nearest {@link VisibilityLocation} to 'located'.
	 * @param located
	 * @return
	 */
	public NavPoint getNearestNavPoint(ILocated located) {
		if (located == null) return null;
		VisibilityLocation nearest = 
			DistanceUtils.getNearestFiltered(
				locations.values(), 
				located, 
				new IFilter<VisibilityLocation>() {
					@Override
					public boolean isAccepted(VisibilityLocation object) {
						return object.navPoint != null;
					}
				}
			);		
		if (nearest == null) return null;
		return nearest.navPoint;
	}
	
	/**
	 * Nearest {@link VisibilityLocation} index to 'located'.
	 * @param located
	 * @return
	 */
	public Integer getNearestIndex(ILocated located) {
		if (located == null) return null;
		
		Entry<Integer, VisibilityLocation> nearest = null;
		if (located instanceof NavPoint) {
			NavPoint navPoint = (NavPoint) located;
			return getIndexByNavPoint(navPoint);
		} else {
			nearest = getNearestEntry(located);
		}
		
		if (nearest == null) return null;
		return nearest.getKey();
	}
	
	/**
	 * Returns whether loc1 is visible from loc2 (and vice versa == symmetrix info).
	 * 
	 * Note that the information is only aproximated by obtaining nearest known {@link VisibilityLocation}.
	 * The information is accurate for navpoints and very accurate for points on links between navpoints. 
	 * 
	 * If module is not {@link Visibility#isInitialized()}, returns false.
	 * 
	 * @return
	 */
	public boolean isVisible(ILocated loc1, ILocated loc2) {
		if (loc1 == null) return false;
		if (loc2 == null) return false;
		Integer index1 = getNearestIndex(loc1);
		if (index1 == null) return false;
		Integer index2 = getNearestIndex(loc2);
		if (index2 == null) return false;
		return matrix.get(index1, index2);
	}
	
	/**
	 * Returns set of {@link VisibilityLocation} that are not visible from "loc".
	 * @param loc
	 * @return
	 */
	public Set<VisibilityLocation> getCoverPoints(ILocated loc) {
		if (loc == null) return new HashSet<VisibilityLocation>();
		
		Entry<Integer, VisibilityLocation> vLocEntry = getNearestEntry(loc);
		if (vLocEntry == null) return null;
		
		int key = vLocEntry.getKey();
		VisibilityLocation vLoc = vLocEntry.getValue();
				
		Set<VisibilityLocation> result = coverPoints.get(key);
		if (result != null) return result;

		synchronized(mutex) {
			result = coverPoints.get(key);
			if (result != null) return result;
			
			BitSet visibility = matrix.getColumn(key);
			result = getCoverPoints(visibility);			
			coverPoints.put(key, result);
			
			return result;
		}
	}
	
	/**
	 * Returns nearest cover point to 'loc'.
	 * @param loc
	 * @return
	 */
	public VisibilityLocation getNearestCoverPoint(ILocated loc) {
		return DistanceUtils.getNearest(getCoverPoints(loc), loc);
	}
	
	/**
	 * Returns nearest cover point agains 'from' for 'target'.
	 * @param from
	 * @return
	 */
	public VisibilityLocation getNearestCoverPoint(ILocated target, ILocated from) {
		return DistanceUtils.getNearest(getCoverPoints(from), target);
	}
	
	/**
	 * Returns set of {@link VisibilityLocation} that are visible from "loc".
	 * 
	 * @param loc
	 * @return
	 */
	public Set<VisibilityLocation> getVisiblePoints(ILocated loc) {
		if (loc == null) return new HashSet<VisibilityLocation>();
		
		Entry<Integer, VisibilityLocation> vLocEntry = getNearestEntry(loc);
		if (vLocEntry == null) return null;
		
		int key = vLocEntry.getKey();
		VisibilityLocation vLoc = vLocEntry.getValue();
				
		Set<VisibilityLocation> result = visiblePoints.get(key);
		if (result != null) return result;

		synchronized(mutex) {
			result = coverPoints.get(key);
			if (result != null) return result;
			
			BitSet visibility = matrix.getColumn(key);
			result = getVisiblePoints(visibility);
			visiblePoints.put(key, result);
			
			return result;
		}
	}
	
	/**
	 * Returns set of {@link NavPoint} that are not visible from "loc".
	 * @param loc
	 * @return
	 */
	public Set<NavPoint> getCoverNavPoints(ILocated loc) {
		if (loc == null) return new HashSet<NavPoint>();
		
		Entry<Integer, VisibilityLocation> vLocEntry = getNearestEntry(loc);
		if (vLocEntry == null) return null;
		
		int key = vLocEntry.getKey();
		VisibilityLocation vLoc = vLocEntry.getValue();
				
		Set<NavPoint> result = coverNavPoints.get(key);
		if (result != null) return result;

		synchronized(mutex) {
			result = coverNavPoints.get(key);
			if (result != null) return result;
			
			BitSet visibility = matrix.getColumn(key);
			result = getCoverNavPoints(visibility);
			coverNavPoints.put(key, result);
			
			return result;
		}
	}
	
	/**
	 * Returns nearest cover {@link NavPoint} to 'loc'.
	 * @param loc
	 * @return
	 */
	public NavPoint getNearestCoverNavPoint(ILocated loc) {
		return DistanceUtils.getNearest(getCoverNavPoints(loc), loc);
	}
	
	/**
	 * Returns nearest cover {@link NavPoint} against 'from' for 'target'.
	 * @param loc
	 * @return
	 */
	public NavPoint getNearestCoverNavPoint(ILocated target, ILocated loc) {
		return DistanceUtils.getNearest(getCoverNavPoints(loc), loc);
	}
	
	/**
	 * Returns set of {@link NavPoint} that are visible from "loc".
	 * 
	 * @param loc
	 * @return
	 */
	public Set<NavPoint> getVisibleNavPoints(ILocated loc) {
		if (loc == null) return new HashSet<NavPoint>();
		
		Entry<Integer, VisibilityLocation> vLocEntry = getNearestEntry(loc);
		if (vLocEntry == null) return null;
		
		int key = vLocEntry.getKey();
		VisibilityLocation vLoc = vLocEntry.getValue();
				
		Set<NavPoint> result = visibleNavPoints.get(key);
		if (result != null) return result;

		synchronized(mutex) {
			result = visibleNavPoints.get(key);
			if (result != null) return result;
			
			BitSet visibility = matrix.getColumn(key);
			result = getVisibleNavPoints(visibility);
			visibleNavPoints.put(key, result);
			
			return result;
		}
	}
		
	/**
	 * Returns indices of nearest {@link VisibilityLocation} for 'locs'.
	 * @param locs
	 * @return
	 */
	public Set<Integer> getNearestIndices(ILocated... locs) {
		if (locs == null) return null;
		Set<Integer> keys = new HashSet<Integer>();
		for (ILocated loc : locs) {
			if (loc == null) continue;
			Integer key = getNearestIndex(loc);
			keys.add(key);
		}
		return keys;
	}
	
	/**
	 * Returns set of {@link VisibilityLocation} that are not visible from any 'locs'.
	 * @param locs
	 * @return
	 */
	public Set<VisibilityLocation> getCoverPointsN(ILocated... locs) {
		if (locs == null) return null;
		Set<Integer> keys = getNearestIndices(locs);

		BitSet covers = matrix.or(keys); // now we have "LOCATIONS THAT ARE VISIBLE FROM ANY OF 'locs'"

		return getCoverPoints(covers);		
	}
	
	/**
	 * Returns nearest cover point for 'target' that is covered from all 'coveredFrom'.
	 * @param target
	 * @param coveredFrom
	 * @return
	 */
	public VisibilityLocation getNearestCoverPointN(ILocated target, ILocated... coveredFrom) {
		if (target == null) return null;
		if (coveredFrom == null) return getNearest(target);
		return DistanceUtils.getNearest(getCoverPointsN(coveredFrom), target);
	}
	
	/**
	 * Returns set of {@link NavPoint} that are not visible from any 'locs'.
	 * @param locs
	 * @return
	 */
	public Set<NavPoint> getCoverNavPointsN(ILocated... locs) {
		if (locs == null) return null;
		return getNavPoints(getCoverPointsN(locs));
	}
	
	/**
	 * Returns nearest cover nav point for 'target' that is covered from all 'coveredFrom'.
	 * @param target
	 * @param coveredFrom
	 * @return
	 */
	public NavPoint getNearestCoverNavPointN(ILocated target, ILocated... coveredFrom) {
		if (target == null) return null;
		if (coveredFrom == null) return getNearestNavPoint(target);
		return DistanceUtils.getNearest(getCoverNavPointsN(coveredFrom), target);
	}
	
	protected Integer getIndexByNavPoint(NavPoint navPoint) {
		if ( navPointToLocationIndex == null ) {
			navPointToLocationIndex = new HashMap<NavPoint, Integer>();
		}
		
		if ( !navPointToLocationIndex.containsKey(navPoint) ) {
			for ( Entry<Integer, VisibilityLocation> entry : locations.entrySet() ) {
				if ( entry.getValue().navPoint == null ) {
					continue;
				}
				
				if ( navPoint.equals(entry.getValue().navPoint) ) {
					navPointToLocationIndex.put(navPoint, entry.getKey());
					break;
				}
			}
			
			if (!navPointToLocationIndex.containsKey(navPoint)) {
				Entry<Integer,VisibilityLocation> entry = getNearestEntry(navPoint);
				navPointToLocationIndex.put(navPoint, entry.getKey());
			}
		}
		
		return navPointToLocationIndex.get(navPoint);
	}
	
}
