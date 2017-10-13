package math.bsp.algorithm.raycast.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import math.bsp.algorithm.raycast.BspRayCaster;
import math.bsp.node.IConstBspNode;
import math.bsp.node.IConstBspInternalNode;

public class CollisionIterator<TData, TBoundary, TRay, TCollision>
	implements Iterator<TCollision> {

	protected BspRayCaster<TData, TBoundary, TRay, TCollision> rayCaster;
	protected TRay ray;
	protected Iterator<TCollision> currentDataCollisionIterator = null;
	protected List<BoundaryIntersection<TData, TBoundary>> intersections = Lists.newArrayList();
	protected Set<TCollision> previousCollision = Sets.newHashSet();
	protected TCollision next;
	protected boolean hasNext;
	protected boolean nextLoaded = false;
	protected double resumeDistanceSquare = 0.0;
	protected double nextIntersectionDistanceSquare = Double.POSITIVE_INFINITY;
	
	public CollisionIterator(BspRayCaster<TData, TBoundary, TRay, TCollision> rayCaster, TRay ray) {
		this.rayCaster = rayCaster; 
		this.ray = ray;
		descent( rayCaster.tree.getRoot() );
	}

	@Override
	public boolean hasNext() {
		loadNext();
		return hasNext;
	}

	@Override
	public TCollision next() {
		loadNext();
		if ( hasNext ) {
			return next;
		} else {
			throw new NoSuchElementException();
		}
	}

	protected void loadNext() {
		while ( !nextLoaded ) {
			if ( currentDataCollisionIterator != null && currentDataCollisionIterator.hasNext() ) {
				next = currentDataCollisionIterator.next();

				if ( rayCaster.doDuplicateCollisionsOccur ) {
					if ( previousCollision.contains( next ) ) {
						continue;
					} else {
						previousCollision.add( next );
					}
				}

				hasNext = true;
				nextLoaded = true;
			} else if ( intersections.isEmpty() ) {
				hasNext = false;
				nextLoaded = true;
			} else {
				IConstBspNode<TData, TBoundary> node = backtrack();
				descent( node );
			}
		}
	}

	protected void descent( IConstBspNode<TData, TBoundary> node ) {
		while( node.isInternal() ) {
			IConstBspInternalNode<TData, TBoundary> internalNode = node.asInternal();
			
			double signedDistanceSquare = rayCaster.computeSideSignedDistanceSquare( internalNode.getBoundary(), ray );
			
			if ( !Double.isInfinite( signedDistanceSquare ) && Math.abs(signedDistanceSquare) > resumeDistanceSquare ) {
				intersections.add( new BoundaryIntersection<TData, TBoundary>(internalNode, signedDistanceSquare) );
				nextIntersectionDistanceSquare = Math.min( nextIntersectionDistanceSquare, Math.abs(signedDistanceSquare) );
			}

			if ( (0 <= signedDistanceSquare) == (resumeDistanceSquare < Math.abs(signedDistanceSquare)) ) {
				node = internalNode.getPositiveChild();
			} else {
				node = internalNode.getNegativeChild();
			}
		}
		
		currentDataCollisionIterator = rayCaster.getCollisions( ray, resumeDistanceSquare, nextIntersectionDistanceSquare, node.asLeaf().getData() ).iterator();
	}

	protected IConstBspNode<TData, TBoundary> backtrack() {
		nextIntersectionDistanceSquare = Double.POSITIVE_INFINITY;

		BoundaryIntersection<TData, TBoundary> closestIntersection = Collections.min( intersections, intersectionDistanceComparator );

		resumeDistanceSquare = Math.abs( closestIntersection.sideSignedDistanceSquare );

		for ( 	Iterator<BoundaryIntersection<TData, TBoundary>> iterator = intersections.iterator();
				iterator.hasNext();
				) {
			BoundaryIntersection<TData, TBoundary> intersection = iterator.next();
			if ( intersection.node.getDepth() >= closestIntersection.node.getDepth() ) {
				iterator.remove();
			} else {
				nextIntersectionDistanceSquare = Math.min( nextIntersectionDistanceSquare, Math.abs( intersection.sideSignedDistanceSquare ) );
			}
		}
		
		return closestIntersection.getFarSideNode(); 
	}
	
	protected Comparator<BoundaryIntersection<TData, TBoundary>> intersectionDistanceComparator = new Comparator<BoundaryIntersection<TData, TBoundary>>() {
		@Override
		public int compare(BoundaryIntersection<TData, TBoundary> lhs, BoundaryIntersection<TData, TBoundary> rhs) {
			return Double.compare( Math.abs(lhs.sideSignedDistanceSquare), Math.abs(rhs.sideSignedDistanceSquare) );
		}
	};

	@Override
	public void remove() {
		throw new RuntimeException("Unsupported operation: remove()");
	}
}