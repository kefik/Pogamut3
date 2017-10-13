package cz.cuni.amis.pogamut.base.utils.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.utils.IFilter;
import cz.cuni.amis.utils.Tuple2;

/**
 * DistanceUtils consists of usual routines for selecting "X" from some "collections of Xes" that are in some "distance" relation to the "target",
 * e.g., "get nearest weapon from collection of available weapons to my position".
 * <p><p>
 * Note that you may always use custom metric via {@link IGetDistance} interface.
 * <p><p>
 * Note that you may always use some custom filters via {@link IDistanceFilter} interface.
 * 
 * @author Jimmy
 * @author ik
 */
public class DistanceUtils {
	
	// --------------
	// =====================
	// RELATIONs
	// =====================
	// --------------
	
	/**
	 * Relation-ship estimator between two {@link ILocated} objects and their distance (or whatever required).
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static interface IBetterRelation<T> {
		
		public double getWorstValue();
		
		public boolean isBetterRelation(ILocated target, T examinedObject, double examinedObjectToTargetDistance, T currentBestCandidate, double currentBestObjectToTargetDistance);
		
	}
	
	/**
	 * Prefer "closer" objects to "target"
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static class RelationCloser<T> implements IBetterRelation<T> {
		
		public double getWorstValue() {
			return Double.MAX_VALUE;
		}
		
		@Override
		public boolean isBetterRelation(ILocated target, T examinedObject, double examinedObjetToTargetDistance, T currentBestCandidate, double currentBestObjectToTargetDistance) {
			return examinedObjetToTargetDistance < currentBestObjectToTargetDistance;
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public static final RelationCloser relationCloser = new RelationCloser();
	
	/**
	 * Prefer "further" objects to "target"
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static class RelationFurther<T> implements IBetterRelation<T> {
		
		public double getWorstValue() {
			return -1;
		}
		
		@Override
		public boolean isBetterRelation(ILocated target, T examinedObject, double examinedObjetToTargetDistance, T currentBestCandidate, double currentBestObjectToTargetDistance) {
			return examinedObjetToTargetDistance > currentBestObjectToTargetDistance;
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public static final RelationFurther relationFurther = new RelationFurther();
	
	// --------------
	// =====================
	// METRICS
	// =====================
	// --------------
	
	/**
	 * Distance estimator between object of types T and some {@link Location} target.
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static interface IGetDistance<T> {
		
		public double getDistance(T object, ILocated target);
		
	}
	
	/**
	 * Simple implementation of {@link IGetDistance} that uses {@link Location#getDistance(Location)} method.
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static class GetLocatedDistance3D<T extends ILocated> implements IGetDistance<T> {

		/**
		 * Uses {@link Location#getDistance(Location)} method.
		 */
		@Override
		public double getDistance(T object, ILocated target) {
			if (object.getLocation() == null) return Double.MAX_VALUE;
			return object.getLocation().getDistance(target.getLocation());
		}
		
	}
	
	/**
	 * See {@link GetLocatedDistance3D}.
	 */
	public static final GetLocatedDistance3D<ILocated> getLocatedDistance3D = new GetLocatedDistance3D<ILocated>();
	
	/**
	 * Simple implementation of {@link IGetDistance} that uses {@link Location#getDistance2D(Location)} method.
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static class GetLocatedDistance2D<T extends ILocated> implements IGetDistance<T> {

		/**
		 * Uses {@link Location#getDistance(Location)} method.
		 */
		@Override
		public double getDistance(T object, ILocated target) {
			if (object.getLocation() == null) return Double.MAX_VALUE;
			return object.getLocation().getDistance2D(target.getLocation());
		}
		
	}
	
	/**
	 * See {@link GetLocatedDistance2D}.
	 */
	public static final GetLocatedDistance2D<ILocated> getLocatedDistance2D = new GetLocatedDistance2D<ILocated>();
	
	// --------------
	// =====================
	// FILTERS
	// =====================
	// --------------
	
	/**
	 * Filter that allows to check whether "object" is accepted with respect to "distanceToTarget" for given "target".
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static interface IDistanceFilter<T> {
		
		/**
		 * @param object
		 * @param target
		 * @param distanceToTarget
		 * @return TRUE == can be result, FALSE == filter out
		 */
		public boolean isAccepted(T object, ILocated target, double distanceToTarget);
		
	}
	
	
	/**
	 * Filter that accepts all "objects" (does not filter anything out).
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static final class AcceptAllDistanceFilter<T> implements IDistanceFilter<T> {

		@Override
		public boolean isAccepted(T object, ILocated target, double distanceToTarget) {
			return true;
		}
		
	}
	
	/**
	 * See {@link AcceptAllDistanceFilter}.
	 */
	@SuppressWarnings("unchecked")
	public static final AcceptAllDistanceFilter acceptAllDistanceFilter = new AcceptAllDistanceFilter();
	
	/**
	 * Contains single filter, {@link #acceptAllDistanceFilter}, i.e., this will ACCEPTS-ALL-ITEMS.
	 */
	public static final IDistanceFilter[] NO_FILTER = new IDistanceFilter[]{ acceptAllDistanceFilter };
	
	/**
	 * Filter that accepts all "objects" that are within range of min/max distance (inclusive).
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static class RangeDistanceFilter<T> implements IDistanceFilter<T> {

		private double minDistance;
		private double maxDistance;

		public RangeDistanceFilter(double minDistance, double maxDistance) {
			this.minDistance = minDistance;
			this.maxDistance = maxDistance;
		}
		
		@Override
		public boolean isAccepted(T object, ILocated target, double distanceToTarget) {
			return minDistance <= distanceToTarget && distanceToTarget <= maxDistance;
		}
		
	}
	
	/**
	 * Accepts only VISIBLE ({@link IViewable#isVisible()} == TRUE) objects.
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static class VisibleFilter<T extends IViewable> implements IDistanceFilter<T> {

		@Override
		public boolean isAccepted(T object, ILocated target, double distanceToTarget) {
			return object.isVisible();
		}
		
	}
	
	/**
	 * See {@link VisibleFilter}.
	 */
	public static final VisibleFilter<IViewable> visibleFilter = new VisibleFilter<IViewable>();
	
	/**
	 * Adapter that wraps {@link IFilter} making it into {@link IDistanceFilter}.
	 * 
	 * @author Jimmy
	 *
	 * @param <T>
	 */
	public static class FilterAdapter<T> implements IDistanceFilter<T> {
		
		private IFilter<T> filter;

		public FilterAdapter(IFilter<T> filter) {
			this.filter = filter;
		}

		@Override
		public boolean isAccepted(T object, ILocated target, double distanceToTarget) {
			return filter.isAccepted(object);
		}
		
	}
	
	// --------------
	// =====================
	// getNearest()
	// =====================
	// --------------
	
	/**
     * Returns "in-best-distance-relation-to-'target'" object. 
     * <p><p>
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * Relation checking is provided via {@link IBetterRelation#isBetterRelation(Object, ILocated, double, double)}.
     * <p><p>
     * Only 'locations' that passes ALL 'filters' may get into the result.
     * 
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param getDistance distance-computer between 'locations' and 'target'.
     * @param betterRelation assessing 
     * @param filters if null or empty, is ignored
     * @return nearest object from collection of objects
     */
    public static <T> T getInBestRelation(Collection<T> locations, ILocated target, IGetDistance getDistance, IBetterRelation betterRelation, IDistanceFilter... filters) {
    	if (filters == null || (filters.length == 1 && filters[0] == acceptAllDistanceFilter)) {
    		return getInBestRelation(locations, target, getDistance, betterRelation);
    	}
    	if (locations == null) return null;
    	if (target == null) return null;
    	if (target.getLocation() == null) return null;
    	if (getDistance == null) return null;
    	if (betterRelation == null) return null;
        
        T bestCandidate = null;        
        double bestCandidateDistance = betterRelation.getWorstValue();
        double distance;
        
        for(T location : locations) {        	
        	distance = getDistance.getDistance(location, target);
        	
        	boolean accepted = true;
        	for (IDistanceFilter filter : filters) {
        		if (filter == null) continue;
        		if (filter.isAccepted(location, target, distance)) continue;
        		accepted = false;
        		break;
        	}
        	
        	if (accepted) {
	        	if (betterRelation.isBetterRelation(target, location, distance, bestCandidate, bestCandidateDistance)) {
	                bestCandidateDistance = distance;
	                bestCandidate = location;
	            }
        	}
        }
        
        return bestCandidate;
    }
    
    /**
     * Returns "in-best-distance-relation-to-'target'" object. 
     * <p><p>
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * Relation checking is provided via {@link IBetterRelation#isBetterRelation(Object, ILocated, double, double)}.
     * 
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @param betterRelation
     * @return nearest object from collection of objects
     */
    public static <T> T getInBestRelation(Collection<T> locations, ILocated target, IGetDistance getDistance, IBetterRelation betterRelation) {
    	if (locations == null) return null;
    	if (target == null) return null;
    	if (target.getLocation() == null) return null;
    	if (getDistance == null) return null;
    	if (betterRelation == null) return null;
        
        T best = null;        
        double bestDistance = betterRelation.getWorstValue();
        double distance;
        
        for(T location : locations) {        	
        	distance = getDistance.getDistance(location, target);
        	
        	if (betterRelation.isBetterRelation(target, location, distance, best, bestDistance)) {
        		bestDistance = distance;
	            best = location;
	        }
        }
        
        return best;
    }
    
    /**
     * Returns "in-nth-best-distance-relation-to-'target'" object from 'location'. This means is will not get the 1st best, 2nd best but n-th best, i.e.,
     * if we would sort all 'locations' according to 'betterRelation', this returns n-th element. 
     * <p><p>
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * Relation checking is provided via {@link IBetterRelation#isBetterRelation(Object, ILocated, double, double)}.
     * <p><p>
     * Only 'locations' that passes ALL 'filters' may get into the result.
     * 
     * WARNING: O(locations.size * N) ~ O(n^2) complexity!
     * 
     * @param <T>
     * @param nthBest if &lt;= 0, returns the 1-BEST
     * @param locations if locations.size() &lt; 'nthBest', returns the "worst" from 'locations'
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @param filters if insufficient (&lt; nthBest) locations gets to the result, the "worst" is returned, or null in case of all locations are filtered out
     * @return "in-nth-best-distance-relation-to-'target'" object from 'location'
     */
    public static <T> T getInNthBestRelation(int nthBest, Collection<T> locations, ILocated target, IGetDistance getDistance, IBetterRelation betterRelation, IDistanceFilter... filters) {
    	if (nthBest < 1) return getInBestRelation(locations, target, getDistance, betterRelation, filters);
    	if (filters == null) return null;
    	if (locations == null) return null;
    	if (target == null) return null;
    	if (target.getLocation() == null) return null;
    	if (getDistance == null) return null;
    	if (betterRelation == null) return null;
        
    	List<Tuple2<T, Double>> best = new ArrayList<Tuple2<T, Double>>();
    	
        double distance;
        
        for(T location : locations) {        	
        	distance = getDistance.getDistance(location, target);
        	
        	boolean accepted = true;
        	for (IDistanceFilter filter : filters) {
        		if (filter == null) continue;
        		if (filter.isAccepted(location, target, distance)) continue;
        		accepted = false;
        		break;
        	}
        	
        	if (accepted) {
	        	int i = 0;
	        	for (; i < best.size(); ++i) {
	        		Tuple2<T, Double> candidate = best.get(i);
	        		if (betterRelation.isBetterRelation(target, location, distance, candidate.getFirst(), candidate.getSecond())) {
	        			break;
	        		}
	        	}
	        	if (i < nthBest) {
	        		if (i < best.size()) {
	        			best.add(i, new Tuple2<T, Double>(location, distance));
	        		} else {
	        			best.add(new Tuple2<T, Double>(location, distance));
	        		}
	        	}        	
        	}
        }
        
        return best.size() == 0 ? null : best.get(best.size()-1).getFirst();
    }
    
    /**
     * Returns "in-nth-best-distance-relation-to-'target'" object from 'locations'. This means is will not get the 1st best, 2nd best but n-th best, i.e.,
     * if we would sort all 'locations' according to 'betterRelation', this returns n-th element. 
     * <p><p>
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * Relation checking is provided via {@link IBetterRelation#isBetterRelation(Object, ILocated, double, double)}.
     * 
     * WARNING: O(locations.size * N) ~ O(n^2) complexity!
     * 
     * @param <T>
     * @param nthBest if &lt;= 0, returns the 1-BEST
     * @param locations if locations.size() &lt; 'nthBest', returns the "worst" from 'locations'
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @return nth-nearest object from 'locations'
     */
    public static <T> T getInNthBestRelation(int nthBest, Collection<T> locations, ILocated target, IGetDistance getDistance, IBetterRelation betterRelation) {
    	if (nthBest < 1) return getInBestRelation(locations, target, getDistance, betterRelation);
    	if (locations == null) return null;
    	if (target == null) return null;
    	if (target.getLocation() == null) return null;
    	if (getDistance == null) return null;
    	if (betterRelation == null) return null;
        
    	List<Tuple2<T, Double>> best = new ArrayList<Tuple2<T, Double>>();
    	
        double distance;
        
        for(T location : locations) {        	
        	distance = getDistance.getDistance(location, target);
        	
        	int i = 0;
        	for (; i < best.size(); ++i) {
        		Tuple2<T, Double> candidate = best.get(i);
        		if (betterRelation.isBetterRelation(target, location, distance, candidate.getFirst(), candidate.getSecond())) {
        			break;
        		}
        	}
        	if (i < nthBest) {
        		if (i < best.size()) {
        			best.add(i, new Tuple2<T, Double>(location, distance));
        		} else {
        			best.add(new Tuple2<T, Double>(location, distance));
        		}
        	}        	
        }
        
        return best.size() == 0 ? null : best.get(best.size()-1).getFirst();
    }
    
    /**
     * Returns the nearest object from 'location' to 'target' that is accepted by all 'filters'.
     * <p><p> 
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @param filters filters to be used (that can filter out unsuitable results)
     * @return nearest object from 'locations'
     */
    public static <T> T getNearest(Collection<T> locations, ILocated target, IGetDistance getDistance, IDistanceFilter... filters) {
    	return 
    		getInBestRelation(
    				locations, 
    				target, 
    				getDistance, 
    				relationCloser,
    				filters
    		);
    }
    
    /**
     * Returns the n-th nearest object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p> 
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @param filters filters to be used (that can filter out unsuitable results)
     * @return nth-nearest object from 'locations'
     */
    public static <T> T getNthNearest(int nthNearest, Collection<T> locations, ILocated target, IGetDistance getDistance, IDistanceFilter... filters) {
    	return 
    		getInNthBestRelation(
    				nthNearest,
    				locations, 
    				target, 
    				getDistance, 
    				relationCloser,
    				filters
    		);
    }
    
    /**
     * Returns the farthest object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p> 
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @param filters filters to be used (that can filter out unsuitable results)
     * @return nearest object from 'location'
     */
    public static <T> T getFarthest(Collection<T> locations, ILocated target, IGetDistance getDistance, IDistanceFilter... filters) {
    	return 
    		getInBestRelation(
    				locations, 
    				target, 
    				getDistance, 
    				relationFurther,
    				filters
    		);
    }
    
    /**
     * Returns the n-th farthest object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p> 
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @param filters filters to be used (that can filter out unsuitable results)
     * @return nearest object from 'locations'
     */
    public static <T> T getNthFarthest(int nthFarthest, Collection<T> locations, ILocated target, IGetDistance getDistance, IDistanceFilter... filters) {
    	return 
    		getInNthBestRelation(
    				nthFarthest,
    				locations, 
    				target, 
    				getDistance, 
    				relationFurther,
    				filters
    		);
    }
	
	/**
     * Returns the nearest object from 'locations' to 'target'. 
     * <p><p>
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @return nearest object from 'locations'
     */
    public static <T> T getNearest(Collection<T> locations, ILocated target, IGetDistance getDistance) {
    	return 
    		getInBestRelation(
    			locations, 
    			target,
    			getDistance,
    			relationCloser
    		);    	
    }
    
    /**
     * Returns the n-th nearest object from 'locations' to 'target'. 
     * <p><p>
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @return nth-nearest object from 'locations'
     */
    public static <T> T getNthNearest(int nthNearest, Collection<T> locations, ILocated target, IGetDistance getDistance) {
    	return 
    		getInNthBestRelation(
    			nthNearest,
    			locations, 
    			target,
    			getDistance,
    			relationCloser
    		);    	
    } 
    
    /**
     * Returns the farthest object to 'target'. 
     * <p><p>
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @return farthest object from 'locations'
     */
    public static <T> T getFarthest(Collection<T> locations, ILocated target, IGetDistance getDistance) {
    	return 
    		getInBestRelation(
    			locations, 
    			target,
    			getDistance,
    			relationFurther
    		);    	
    }
    
    /**
     * Returns the n-th farthest object from 'locations' to 'target'. 
     * <p><p>
     * Distance is obtained via provided {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @param getDistance distance computer between 'locations' and 'target'.
     * @return nth-farthest object from 'locations'
     */
    public static <T> T getNthFarthest(int nthFarthest, Collection<T> locations, ILocated target, IGetDistance getDistance) {
    	return 
    		getInNthBestRelation(
    			nthFarthest,
    			locations, 
    			target,
    			getDistance,
    			relationFurther
    		);    	
    }
    
    /**
     * Returns the nearest object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filters
     * @return nearest object from 'locations'
     */
    public static <T extends ILocated> T getNearest(Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getNearest(locations, target, getLocatedDistance3D, filters);
    }
    
    /**
     * Returns the nearest object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @param filters
     * @return nearest object from 'locations'
     */
    public static <T extends ILocated> T getNthNearest(int nthNearest, Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getNthNearest(nthNearest, locations, target, getLocatedDistance3D, filters);
    }
    
    /**
     * Returns the farthest object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filters
     * @return farthest object from 'locations'
     */
    public static <T extends ILocated> T getFarthest(Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getFarthest(locations, target, getLocatedDistance3D, filters);
    }
    
    /**
     * Returns the nth-farthest object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @param filters
     * @return nth-farthest object from 'locations'
     */
    public static <T extends ILocated> T getNthFarthest(int nthFarthest, Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getNthFarthest(nthFarthest, locations, target, getLocatedDistance3D, filters);
    }
	
    /**
     * Returns the nearest object from 'locations' to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from 'locations'
     */
    public static <T extends ILocated> T getNearest(Collection<T> locations, ILocated target) {
    	return getNearest(locations, target, getLocatedDistance3D);
    }
    
    /**
     * Returns the nth-nearest object from 'locations' to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @return nth-nearest object from 'locations'
     */
    public static <T extends ILocated> T getNthNearest(int nthNearest, Collection<T> locations, ILocated target) {
    	return getNthNearest(nthNearest, locations, target, getLocatedDistance3D);
    }
    
    /**
     * Returns the farthest object from 'locations' to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return farthest object from 'locations'
     */
    public static <T extends ILocated> T getFarthest(Collection<T> locations, ILocated target) {
    	return getFarthest(locations, target, getLocatedDistance3D);
    }
    
    /**
     * Returns the nth-farthest object from 'locations' to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @return nth-farthest object from 'locations'
     */
    public static <T extends ILocated> T getNthFarthest(int nthFarthest, Collection<T> locations, ILocated target) {
    	return getNthFarthest(nthFarthest, locations, target, getLocatedDistance3D);
    }
    
    /**
     * Returns the nearest object from 'locations' to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return nearest object from 'locations' that is not further than 'maxDistance'
     */
    public static <T extends ILocated> T getNearest(Collection<T> locations, ILocated target, double maxDistance) {
    	return getNearest(locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the nth-nearest object from 'locations' to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @param maxDistance
     * @return nth-nearest object from 'locations' that is not further than 'maxDistance'
     */
    public static <T extends ILocated> T getNthNearest(int nthNearest, Collection<T> locations, ILocated target, double maxDistance) {
    	return getNthNearest(nthNearest, locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the farthest object from 'locations' to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return farthest object from 'locations' that is not further than 'maxDistance'
     */
    public static <T extends ILocated> T getFarthest(Collection<T> locations, ILocated target, double maxDistance) {
    	return getFarthest(locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the nth-farthest object from 'locations' to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @param maxDistance
     * @return nth-farthest object from 'locations' that is not further than 'maxDistance'
     */
    public static <T extends ILocated> T getNthFarthest(int nthFarthest, Collection<T> locations, ILocated target, double maxDistance) {
    	return getNthFarthest(nthFarthest, locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the nearest object from 'location' to 'target' that is accepted by 'filter'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return nearest object from 'locations' accepted by 'filter'
     */
    public static <T extends ILocated> T getNearestFiltered(Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getNearest(locations, target);
    	}
    	return getNearest(locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the nth-nearest object from 'locations' to 'target' that is accepted by 'filter'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return nth-nearest object from 'locations' accepted by 'filter'
     */
    public static <T extends ILocated> T getNthNearestFiltered(int nthNearest, Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getNthNearest(nthNearest, locations, target);
    	}
    	return getNthNearest(nthNearest, locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the farthest object from 'locations' to 'target' that is accepted by 'filter'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return farthest object from 'locations' accepted by 'filter'
     */
    public static <T extends ILocated> T getFarthestFiltered(Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getFarthest(locations, target);
    	}
    	return getFarthest(locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the nth-farthest object from 'locations' to 'target' that is accepted by 'filter'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return nth-farthest object from 'locations' accepted by 'filter'
     */
    public static <T extends ILocated> T getNthFarthestFiltered(int nthFarthest, Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getNthFarthest(nthFarthest, locations, target);
    	}
    	return getNthFarthest(nthFarthest, locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the nearest object from 'locations' to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return nearest visible object from 'locations'
     */
    public static <T extends IViewable> T getNearestVisible(Collection<T> locations, ILocated target) {
    	return getNearest(locations, target, getLocatedDistance3D, visibleFilter);
    }
    
    /**
     * Returns the nth-nearest object from 'locations' to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param ntNearest if &lt;= 0, returns the nearest
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return nth-nearest visible object from 'locations'
     */
    public static <T extends IViewable> T getNthNearestVisible(int nthNearest, Collection<T> locations, ILocated target) {
    	return getNthNearest(nthNearest, locations, target, getLocatedDistance3D, visibleFilter);
    }
    
    /**
     * Returns the farthest object from 'locations' to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return farthest visible object from 'locations'
     */
    public static <T extends IViewable> T getFarthestVisible(Collection<T> locations, ILocated target) {
    	return getFarthest(locations, target, getLocatedDistance3D, visibleFilter);
    }
    
    /**
     * Returns the nth-farthest object from 'locations' to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param ntFarthest if &lt;= 0, returns the farthest
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return nth-farthest visible object from 'locations'
     */
    public static <T extends IViewable> T getNthFarthestVisible(int nthFarthest, Collection<T> locations, ILocated target) {
    	return getNthFarthest(nthFarthest, locations, target, getLocatedDistance3D, visibleFilter);
    }
    
	// --------------
	// =====================
	// getDistanceSorted()
	// =====================
	// --------------

    private static class DistancesComparator implements Comparator<Tuple2<Object, Double>> {

		@Override
		public int compare(Tuple2<Object, Double> o1, Tuple2<Object, Double> o2) {
			double result = o1.getSecond() - o2.getSecond();
			if (result < 0) return -1;
			if (result > 0) return 1;
			return 0;
		}
    	
    }
    
    private static final DistancesComparator distancesComparator = new DistancesComparator();

    /**
     * Returns "locations" sorted according to the distance to "target". Sorted from the nearest to the farthest.
     * <p><p>
     * Distance is provided by {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: 2*O(n) + O(n*log n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param getDistance
     * @return
     */
    public static <T> List<T> getDistanceSorted(Collection<T> locations, ILocated target, IGetDistance getDistance) {
    	if (locations == null) return null;
    	if (target == null) return null;
    	if (target.getLocation() == null) return null;
    	if (getDistance == null) return null;
    	
    	List<Tuple2<T, Double>> distances = new ArrayList<Tuple2<T, Double>>(locations.size());
    	
    	for (T location : locations) {
    		double distance = getDistance.getDistance(location, target);
    		distances.add(new Tuple2<T, Double>(location, distance));
    	}
    	
    	Collections.sort((List)distances, (Comparator)distancesComparator);
    	
    	List<T> result = new ArrayList<T>(distances.size());
    	
    	for (Tuple2<T, Double> location : distances) {
    		result.add(location.getFirst());
    	}
    	
    	return result;
    }
    
    /**
     * Returns "locations" accepted by all "filters" sorted according to the distance to "target". Sorted from the nearest to the farthest.
     * <p><p>
     * Distance is provided by {@link IGetDistance#getDistance(Object, Location)}.
     * <p><p>
     * WARNING: 2*O(n) + O(n*log n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param getDistance
     * @return
     */
    public static <T> List<T> getDistanceSorted(Collection<T> locations, ILocated target, IGetDistance getDistance, IDistanceFilter... filters) {
    	if (filters == null || filters.length == 0 || (filters.length == 1 && filters[0] instanceof AcceptAllDistanceFilter)) {
    		return getDistanceSorted(locations, target, getDistance);
    	}
    	if (locations == null) return null;
    	if (target == null) return null;
    	if (getDistance == null) return null;
    	Location targetLoc = target.getLocation();
    	if (targetLoc == null) return null;
    	
    	List<Tuple2<T, Double>> distances = new ArrayList<Tuple2<T, Double>>(locations.size());
    	
    	for (T location : locations) {
    		boolean accepted = true;
    		double distance = getDistance.getDistance(location, targetLoc);
    		for (IDistanceFilter filter : filters) {
    			if (!filter.isAccepted(location, targetLoc, distance)) {
    				accepted = false;
    				break;
    			}
    		}
    		if (!accepted) continue;
    		
    		distances.add(new Tuple2<T, Double>(location, distance));
    	}
    	
    	Collections.sort((List)distances, (Comparator)distancesComparator);
    	
    	List<T> result = new ArrayList<T>(distances.size());
    	
    	for (Tuple2<T, Double> location : distances) {
    		result.add(location.getFirst());
    	}
    	
    	return result;
    }
    
    /**
     * Returns "locations" accepted by all "filters" sorted according to the distance to "target". Sorted from the nearest to the farthest.
     * <p><p>
     * WARNING: 2*O(n) + O(n*log n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filters
     * @return nearest object from collection of objects
     */
    public static <T extends ILocated> List<T> getDistanceSorted(Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getDistanceSorted(locations, target, getLocatedDistance3D, filters);
    }
	
    /**
     * Returns "locations" sorted according to the distance to "target". Sorted from the nearest to the farthest.
     * <p><p>
     * WARNING: 2*O(n) + O(n*log n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from collection of objects
     */
    public static <T extends ILocated> List<T> getDistanceSorted(Collection<T> locations, ILocated target) {
    	return getDistanceSorted(locations, target, getLocatedDistance3D);
    }
    
    /**
     * Returns "locations" sorted according to the distance to "target". Sorted from the nearest to the farthest.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: 2*O(n) + O(n*log n) complexity!
     * 
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return nearest object from collection of objects that is not further than 'maxDistance'.
     */
    public static <T extends ILocated> List<T> getDistanceSorted(Collection<T> locations, ILocated target, double maxDistance) {
    	return getDistanceSorted(locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns "locations" sorted according to the distance to "target". Sorted from the nearest to the farthest.
     * <p><p>
     * WARNING: 2*O(n) + O(n*log n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return nearest object from collection of objects
     */
    public static <T extends ILocated> List<T> getDistanceSortedFiltered(Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getDistanceSorted(locations, target);
    	}
    	return getDistanceSorted(locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns visible "locations" sorted according to the distance to "target". Sorted from the nearest to the farthest.
     * <p><p>
     * WARNING: 2*O(n) + O(n*log n) complexity!
     * 
     * @param <T>
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return nearest visible object from collection of objects
     */
    public static <T extends IViewable> List<T> getDistanceSortedVisible(Collection<T> locations, ILocated target) {
    	return (List<T>) getDistanceSorted(locations, target, (IGetDistance)getLocatedDistance3D, (IDistanceFilter)visibleFilter);
    }
    
    // --------------
	// =====================
	// getSecondNearest()
	// =====================
	// --------------
    
    /**
     * Returns the second nearest object to 'target'.
     * <p><p>
     * Distance is provided by {@link IGetDistance#getDistance(Object, ILocated)}.
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearest(int, Collection, ILocated, IGetDistance)} instead!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from collection of objects
     */
    @Deprecated
    public static <T> T getSecondNearest(Collection<T> locations, ILocated target, IGetDistance getDistance) {
    	return getNthNearest(2, locations, target, getDistance);
    }
    
    /**
     * Returns the second nearest object to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearest(int, Collection, ILocated, IGetDistance, IDistanceFilter...)} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return nearest object from collection of objects
     */
    @Deprecated
    public static <T> T getSecondNearest(Collection<T> locations, ILocated target, IGetDistance getDistance, IDistanceFilter... filters) {
    	return getNthNearest(2, locations, target, getDistance, filters);
    }
    
    /**
     * Returns the second nearest object to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearest(int, Collection, ILocated, IDistanceFilter...))} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filters
     * @return nearest object from collection of objects
     */
    @Deprecated
    public static <T extends ILocated> T getSecondNearest(Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getNthNearest(2, locations, target, filters);
    }
	
    /**
     * Returns the second nearest object to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearest(int, Collection, ILocated)} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return second nearest object from collection of objects
     */
    @Deprecated
    public static <T extends ILocated> T getSecondNearest(Collection<T> locations, ILocated target) {
    	return getNthNearest(2, locations, target);
    }
    
    /**
     * Returns the second nearest object to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearest(int, Collection, ILocated, double)} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return second nearest object from collection of objects that is not further than 'maxDistance'.
     */
    @Deprecated
    public static <T extends ILocated> T getSecondNearest(Collection<T> locations, ILocated target, double maxDistance) {
    	return getNthNearest(2, locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the second nearest object to 'target' that is accepted by filter.
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearestFiltered(int, Collection, ILocated, IFilter)} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return second nearest object from collection of objects
     */
    @Deprecated
    public static <T extends ILocated> T getSecondNearestFiltered(Collection<T> locations, ILocated target, IFilter filter) {
    	return getNthNearestFiltered(2, locations, target, filter);
    }
    
    /**
     * Returns the second nearest object to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearestVisible(int, Collection, ILocated)
     * 
     * @param <T>
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return second nearest visible object from collection of objects
     */
    @Deprecated
    public static <T extends IViewable> T getSecondNearestVisible(Collection<T> locations, ILocated target) {
    	return getNthNearestVisible(2, locations, target);
    }
    
    // --------------
	// =====================
	// getNearest2D()
	// =====================
	// --------------
    
    /**
     * Returns the nearest (in 2D ~ [x,y]) object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filters
     * @return nearest (in 2D ~ [x,y]) object from 'locations'
     */
    public static <T extends ILocated> T getNearest2D(Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getNearest(locations, target, getLocatedDistance2D, filters);
    }
    
    /**
     * Returns the nearest (in 2D ~ [x,y]) object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @param filters
     * @return nearest (in 2D ~ [x,y]) object from 'locations'
     */
    public static <T extends ILocated> T getNthNearest2D(int nthNearest, Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getNthNearest(nthNearest, locations, target, getLocatedDistance2D, filters);
    }
    
    /**
     * Returns the farthest (in 2D ~ [x,y]) object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filters
     * @return farthest (in 2D ~ [x,y]) object from 'locations'
     */
    public static <T extends ILocated> T getFarthest2D(Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getFarthest(locations, target, getLocatedDistance2D, filters);
    }
    
    /**
     * Returns the nth-farthest (in 2D ~ [x,y]) object from 'locations' to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @param filters
     * @return nth-farthest (in 2D ~ [x,y]) object from 'locations'
     */
    public static <T extends ILocated> T getNthFarthest2D(int nthFarthest, Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getNthFarthest(nthFarthest, locations, target, getLocatedDistance2D, filters);
    }
	
    /**
     * Returns the nearest (in 2D ~ [x,y]) object from 'locations' to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return nearest (in 2D ~ [x,y]) object from 'locations'
     */
    public static <T extends ILocated> T getNearest2D(Collection<T> locations, ILocated target) {
    	return getNearest(locations, target, getLocatedDistance2D);
    }
    
    /**
     * Returns the nth-nearest (in 2D ~ [x,y]) object from 'locations' to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @return nth-nearest (in 2D ~ [x,y]) object from 'locations'
     */
    public static <T extends ILocated> T getNthNearest2D(int nthNearest, Collection<T> locations, ILocated target) {
    	return getNthNearest(nthNearest, locations, target, getLocatedDistance2D);
    }
    
    /**
     * Returns the farthest (in 2D ~ [x,y]) object from 'locations' to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return farthest (in 2D ~ [x,y]) object from 'locations'
     */
    public static <T extends ILocated> T getFarthest2D(Collection<T> locations, ILocated target) {
    	return getFarthest(locations, target, getLocatedDistance2D);
    }
    
    /**
     * Returns the nth-farthest object from 'locations' to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @return nth-farthest (in 2D ~ [x,y]) object from 'locations'
     */
    public static <T extends ILocated> T getNthFarthest2D(int nthFarthest, Collection<T> locations, ILocated target) {
    	return getNthFarthest(nthFarthest, locations, target, getLocatedDistance2D);
    }
    
    /**
     * Returns the nearest (in 2D ~ [x,y]) object from 'locations' to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return nearest (in 2D ~ [x,y]) object from 'locations' that is not further than 'maxDistance'
     */
    public static <T extends ILocated> T getNearest2D(Collection<T> locations, ILocated target, double maxDistance) {
    	return getNearest2D(locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the nth-nearest (in 2D ~ [x,y]) object from 'locations' to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @param maxDistance
     * @return nth-nearest (in 2D ~ [x,y]) object from 'locations' that is not further than 'maxDistance'
     */
    public static <T extends ILocated> T getNthNearest2D(int nthNearest, Collection<T> locations, ILocated target, double maxDistance) {
    	return getNthNearest2D(nthNearest, locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the farthest (in 2D ~ [x,y]) object from 'locations' to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return farthest (in 2D ~ [x,y]) object from 'locations' that is not further than 'maxDistance'
     */
    public static <T extends ILocated> T getFarthest2D(Collection<T> locations, ILocated target, double maxDistance) {
    	return getFarthest2D(locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the nth-farthest (in 2D ~ [x,y]) object from 'locations' to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @param maxDistance
     * @return nth-farthest (in 2D ~ [x,y]) object from 'locations' that is not further than 'maxDistance'
     */
    public static <T extends ILocated> T getNthFarthest2D(int nthFarthest, Collection<T> locations, ILocated target, double maxDistance) {
    	return getNthFarthest2D(nthFarthest, locations, target, new RangeDistanceFilter<T>(0, maxDistance));
    }
    
    /**
     * Returns the nearest (in 2D ~ [x,y]) object from 'location' to 'target' that is accepted by 'filter'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return nearest (in 2D ~ [x,y]) object from 'locations' accepted by 'filter'
     */
    public static <T extends ILocated> T getNearestFiltered2D(Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getNearest2D(locations, target);
    	}
    	return getNearest2D(locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the nth-nearest (in 2D ~ [x,y]) object from 'locations' to 'target' that is accepted by 'filter'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthNearest if &lt;= 0, returns the nearest
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return nth-nearest (in 2D ~ [x,y]) object from 'locations' accepted by 'filter'
     */
    public static <T extends ILocated> T getNthNearestFiltered2D(int nthNearest, Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getNthNearest2D(nthNearest, locations, target);
    	}
    	return getNthNearest2D(nthNearest, locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the farthest (in 2D ~ [x,y]) object from 'locations' to 'target' that is accepted by 'filter'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return farthest (in 2D ~ [x,y]) object from 'locations' accepted by 'filter'
     */
    public static <T extends ILocated> T getFarthestFiltered2D(Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getFarthest2D(locations, target);
    	}
    	return getFarthest2D(locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the nth-farthest (in 2D ~ [x,y]) object from 'locations' to 'target' that is accepted by 'filter'.
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param nthFarthest if &lt;= 0, returns the farthest
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return nth-farthest (in 2D ~ [x,y]) object from 'locations' accepted by 'filter'
     */
    public static <T extends ILocated> T getNthFarthestFiltered2D(int nthFarthest, Collection<T> locations, ILocated target, IFilter filter) {
    	if (filter == null) {
    		return getNthFarthest2D(nthFarthest, locations, target);
    	}
    	return getNthFarthest2D(nthFarthest, locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the nearest (in 2D ~ [x,y]) object from 'locations' to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return nearest (in 2D ~ [x,y]) visible object from 'locations'
     */
    public static <T extends IViewable> T getNearestVisible2D(Collection<T> locations, ILocated target) {
    	return getNearest(locations, target, getLocatedDistance2D, visibleFilter);
    }
    
    /**
     * Returns the nth-nearest (in 2D ~ [x,y]) object from 'locations' to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param ntNearest if &lt;= 0, returns the nearest
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return nth-nearest (in 2D ~ [x,y]) visible object from 'locations'
     */
    public static <T extends IViewable> T getNthNearestVisible2D(int nthNearest, Collection<T> locations, ILocated target) {
    	return getNthNearest(nthNearest, locations, target, getLocatedDistance2D, visibleFilter);
    }
    
    /**
     * Returns the farthest (in 2D ~ [x,y]) object from 'locations' to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return farthest (in 2D ~ [x,y]) visible object from 'locations'
     */
    public static <T extends IViewable> T getFarthestVisible2D(Collection<T> locations, ILocated target) {
    	return getFarthest(locations, target, getLocatedDistance2D, visibleFilter);
    }
    
    /**
     * Returns the nth-farthest (in 2D ~ [x,y]) object from 'locations' to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * 
     * @param <T>
     * @param ntFarthest if &lt;= 0, returns the farthest
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return nth-farthest (in 2D ~ [x,y]) visible object from 'locations'
     */
    public static <T extends IViewable> T getNthFarthestVisible2D(int nthFarthest, Collection<T> locations, ILocated target) {
    	return getNthFarthest(nthFarthest, locations, target, getLocatedDistance2D, visibleFilter);
    }
    
    // --------------
	// =====================
	// getSecondNearest2D()
	// =====================
	// --------------

    /**
     * Returns the second nearest (in 2D ~ [x,y]) object to 'target' that is accepted by all 'filters'.
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearest2D(int, Collection, ILocated, filters)} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filters
     * @return second nearest (in 2D ~ [x,y]) object from collection of objects
     */
    @Deprecated
    public static <T extends ILocated> T getSecondNearest2D(Collection<T> locations, ILocated target, IDistanceFilter... filters) {
    	return getNthNearest2D(2, locations, target, filters);
    }
	
    /**
     * Returns the second nearest (in 2D ~ [x,y]) object to 'target'.
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATAED: use {@link #getNthNearest2D(int, Collection, ILocated)} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @return second nearest (in 2D ~ [x,y]) object from collection of objects
     */
    @Deprecated
    public static <T extends ILocated> T getSecondNearest2D(Collection<T> locations, ILocated target) {
    	return getNthNearest2D(2, locations, target);
    }
    
    /**
     * Returns the second nearest (in 2D ~ [x,y]) object to 'target' that is not further than 'maxDistance'.
     * <p><p>
     * Using {@link RangeDistanceFilter} (minDistance = 0, maxDistance is provided).
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearest2D(int, Collection, ILocated, double)} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param maxDistance
     * @return second nearest (in 2D ~ [x,y]) object from collection of objects that is not further than 'maxDistance'.
     */
    @Deprecated
    public static <T extends ILocated> T getSecondNearest2D(Collection<T> locations, ILocated target, double maxDistance) {
    	return getNthNearest2D(2, locations, target, maxDistance);
    }
    
    /**
     * Returns the second nearest (in 2D ~ [x,y]) object to 'target' that is accepted by filter.
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearestFiltered2D(int, Collection, ILocated, IFilter)} instead
     * 
     * @param <T>
     * @param locations
     * @param target
     * @param filter if null behave as if ALL locations are accepted
     * @return second nearest (in 2D ~ [x,y]) object from collection of objects
     */
    @Deprecated
    public static <T extends ILocated> T getSecondNearest2DFiltered(Collection<T> locations, ILocated target, IFilter<T> filter) {
    	if (filter == null) {
    		return getNthNearest2D(2, locations, target);
    	}
    	return getNthNearest2D(2, locations, target, new FilterAdapter<T>(filter));
    }
    
    /**
     * Returns the second nearest (in 2D ~ [x,y]) object to 'target' that is visible (using {@link VisibleFilter}).
     * <p><p>
     * WARNING: O(n) complexity!
     * <p><p>
     * DEPRECATED: use {@link #getNthNearestVisible2D(int, Collection, ILocated)} instead
     * 
     * @param <T>
     * @param locations must be objects implementing {@link IViewable} as well as {@link ILocated} (so {@link Item} or {@link Player} is usable)
     * @param target
     * @return second nearest (in 2D ~ [x,y]) visible object from collection of objects
     */
    @Deprecated
    public static <T extends IViewable> T getSecondNearest2DVisible(Collection<T> locations, ILocated target) {
    	return getNthNearestVisible2D(2, locations, target);
    }

}
