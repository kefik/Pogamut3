package math.bsp.algorithm.raycast.internal;

import math.bsp.node.IConstBspNode;
import math.bsp.node.IConstBspInternalNode;

public class BoundaryIntersection<TData, TBoundary> {
		public final double sideSignedDistanceSquare; 
		public final IConstBspInternalNode<TData, TBoundary> node;
		
		public BoundaryIntersection( IConstBspInternalNode<TData, TBoundary> node, double sideSignedDistanceSquare ) {
			this.sideSignedDistanceSquare = sideSignedDistanceSquare;
			this.node = node;
		}
		
		public IConstBspNode<TData, TBoundary> getNearSideNode() {
			if ( sideSignedDistanceSquare >= 0 ) {
				return node.getPositiveChild();
			} else {
				return node.getNegativeChild();
			}
		} 
		
		public IConstBspNode<TData, TBoundary> getFarSideNode() {
			if ( sideSignedDistanceSquare >= 0 ) {
				return node.getNegativeChild();
			} else {
				return node.getPositiveChild();
			}
		}
	}