package math.bsp.algorithm.raycast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import math.bsp.IConstBspTree;
import math.bsp.algorithm.raycast.internal.CollisionIterator;

/** BSP ray caster
 * <p>
 * Performs ray casting in a BSP tree.
 * <p>
 * Collisions are ordered by distance if the following conditions hold:
 * <ul>
 * <li> Iterators provided {@link #getCollisions(Object, Object)} return collision ordered by distance. 
 * <li> BSP tree boundaries separate possible collision to their respective sides.
 * </ul>
 * 
 * @param <TData> Type of data used by the BSP tree
 * @param <TBoundary> Type of boundary used by the BSP tree
 * @param <TRay> Ray data type
 * @param <TCollision> Data type describing the collisions between ray and data in the BSP tree
 */
public abstract class BspRayCaster<TData, TBoundary, TRay, TCollision>
	implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final IConstBspTree<TData, TBoundary> tree;
	public final boolean doDuplicateCollisionsOccur;
	
	/** Constructor
	 * 
	 * @param tree BSP tree to perform ray casting on.
	 * @param doDuplicateCollisionsOccur Whether collisions are unique.
	 * Some BSP tree implementations can detect the same collision in multiple leaf data.
	 * If this is the case, TCollision should implement suitable {@link #hashCode()} and {@link #equals(Object)},
	 * so that duplicates can be removed efficiently.   
	 */
	public BspRayCaster( IConstBspTree<TData, TBoundary> tree, boolean doDuplicateCollisionsOccur ) {
		this.tree = tree;
		this.doDuplicateCollisionsOccur = doDuplicateCollisionsOccur;
	}
	
	/** Get a collision with the ray
	 * 
	 * @param ray ray to cast
	 * @return collision or null if the ray doesn't collide with anything.
	 * If collisions are ordered by distance, the nearest collision is returned. 
	 */
	public TCollision getCollision(TRay ray) {
		Iterator<TCollision> iterator = new CollisionIterator<TData, TBoundary, TRay, TCollision>( this, ray );
		if ( iterator.hasNext() ) {
			return iterator.next();
		} else {
			return null;
		}
	}
	
	/** Get all collision with the ray
	 * 
	 * @param ray ray to cast
	 * @return List of collision.
	 */
	public List<TCollision> getCollisions(TRay ray) {
		ArrayList<TCollision> retval = Lists.newArrayList();
		for (	Iterator<TCollision> iterator = new CollisionIterator<TData, TBoundary, TRay, TCollision>( this, ray );
				iterator.hasNext();
		) {
			retval.add( iterator.next() );
		}
		return retval;
	}
	
	/** Get iterator over all collision with the ray
	 * 
	 * @param ray ray to cast
	 * @return Iterator over collision. Collisions are computed on demand to save processing time.
	 * Modifying the tree or data will result in undefined behavior of the iterator.
	 */
	public Iterator<TCollision> getCollisionIterator( TRay ray ) {
		return new CollisionIterator<TData, TBoundary, TRay, TCollision>( this, ray );
	}
	
	/** Compute signed square of distance of intersection from ray origin
	 * 
	 * @param boundary boundary to intersect
	 * @param ray ray to intersect
	 * @return signed squared distance of intersection or signed infinity (no intersection), positive for origin on positive side, negative for origin on negative side
	 */
	public abstract double computeSideSignedDistanceSquare( TBoundary boundary, TRay ray );
	
	/** Get collision list 
	 * 
	 * @param ray ray to collide with
	 * @param data data to search for collisions
	 * @param minDistanceSquare square of minimum distance for eligible collision
	 * @param maxDistanceSquare square of maximum distance for eligible collision
	 * @return collisions with the data
	 */
	public abstract List<TCollision> getCollisions( TRay ray, double minDistanceSquare, double maxDistanceSquare, TData data );
}
