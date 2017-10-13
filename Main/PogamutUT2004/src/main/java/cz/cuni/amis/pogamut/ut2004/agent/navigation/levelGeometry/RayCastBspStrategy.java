/*
 * Copyright (C) 2016 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.inject.internal.Maps;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import math.bsp.BspOccupation;
import math.bsp.IBspStrategy;
import math.bsp.SplitData;
import math.bsp.node.IConstBspInternalNode;
import math.bsp.node.IConstBspLeafNode;
import math.bsp.node.IConstBspNode;
import math.bsp.strat.BspListDataStrategy;
import math.geom3d.Axis3D;
import math.geom3d.Point3D;
import math.geom3d.Shape3D;
import math.geom3d.plane.AxisAlignedPlane3D;

/** Level geometry binary space partitioning strategy optimized for ray casts
 * <p>
 * Uses axis aligned planes to divide the space.
 */
public class RayCastBspStrategy
	extends
		BspListDataStrategy<Triangle, AxisAlignedPlane3D>
	implements 
		IBspStrategy<ArrayList<Triangle>, AxisAlignedPlane3D>,
		Serializable {
	
	private static final long serialVersionUID = 3L;
	
	/** BSP accuracy
	 * <p> 
	 * Due to floating point math inaccuracies, objects within some distance of the boundary must be considered to occupy both sides
	 * this value should be safe, since there were no issues even with 0.000001.
	 */
	private static final double bspAccuracy = 0.0001;
	
	protected LevelGeometry geom;
	protected Random random;
	/** Node spaces mapping
	 * <p>
	 * Node spaces are used during BSP construction to compute SAH.
	 */
	protected transient HashMap<IConstBspNode<ArrayList<Triangle>, AxisAlignedPlane3D>, NodeSpace> nodeSpaces = Maps.newHashMap();
	
	/** Constructor
	 */
	public RayCastBspStrategy( LevelGeometry geom, Random random ) {
		this.geom = geom;
		this.random = random;
	}
	
	@Override
    public SplitData<ArrayList<Triangle>> splitData( AxisAlignedPlane3D boundary, ArrayList<Triangle> triangles ) {
    	ArrayList<Triangle> negativeTriangles = Lists.newArrayList();
    	ArrayList<Triangle> positiveTriangles = Lists.newArrayList();
    	
        for (Triangle triangle : triangles) {
        	BspOccupation occupation = determineElementOccupation( boundary, triangle );
            if (occupation.intersectsNegative()) {
            	negativeTriangles.add(triangle);
            }
            if (occupation.intersectsPositive()) {
            	positiveTriangles.add(triangle);
            }
        }
        
        return new SplitData<ArrayList<Triangle>>( negativeTriangles, positiveTriangles );
	}

    @Override
	public AxisAlignedPlane3D findBoundary( IConstBspLeafNode<ArrayList<Triangle>, AxisAlignedPlane3D> node ) {
    	NodeSpace nodeSpace = getNodeSpace(node);
    	
    	AxisAlignedPlane3D bestBoundary = null;
        double bestMetric = Double.POSITIVE_INFINITY;
        
        AxisAlignedPlane3D candidateBoundary;
        for (int i=0; (candidateBoundary = getBoundaryCandidate( node, i, random )) != null; ++i) {
        	double s = node.getData().size();
        	double sl = 0;
        	double sr = 0;
        	
	        for (Triangle triangle : node.getData()) {
	        	BspOccupation occupation = determineElementOccupation( candidateBoundary, triangle );
	            if (occupation.intersectsNegative()) {
	            	++sl;
	            }
	            if (occupation.intersectsPositive()) {
	            	++sr;
	            }
	        }
	        	        
	        NodeSpace leftNodeSpace = nodeSpace.splitOffNegative(candidateBoundary);
	        NodeSpace rightNodeSpace = nodeSpace.splitOffPositive(candidateBoundary);
	        double TRIANGLE_INTERSECTION_COST = 1.125;
	        double NODE_TRAVERSAL_COST = 1.0;
	        double splittingCost = (
	        	leftNodeSpace.getSurfaceArea()/nodeSpace.getSurfaceArea()*(TRIANGLE_INTERSECTION_COST*sl)
	        	+
	        	rightNodeSpace.getSurfaceArea()/nodeSpace.getSurfaceArea()*(TRIANGLE_INTERSECTION_COST*sr)
	        	+
	        	NODE_TRAVERSAL_COST
	        );
	        double leafCost = (
	        	TRIANGLE_INTERSECTION_COST*s
	        );
	        if (splittingCost < leafCost && splittingCost < bestMetric ) {
	        	bestMetric = splittingCost;
	        	bestBoundary = candidateBoundary;
	        }
        }
        
        return bestBoundary;
	}
    
    /** Suggest a boundary candidate
     * <p>
     * @param node node to split by the candidate
     * @param index index of candidate to suggest
     * @param random random number generator to be used
     * @return candidate or null if a reasonable number of candidates has already been suggested
     */
	public AxisAlignedPlane3D getBoundaryCandidate( IConstBspLeafNode<ArrayList<Triangle>, AxisAlignedPlane3D> node, int index, Random random ) {
		ArrayList<Triangle> triangles = node.getData();
		
		if ( triangles.size() <= 1000 ) {
			if ( index >= triangles.size()*6 ) {
				return null;
			}
			Triangle triangle = triangles.get(index/6);
			int subindex = index%6;
			
			Axis3D separationAxis = Axis3D.values()[index%3];
			double a = separationAxis.getCoord( triangle.vertices[0] );
			double b = separationAxis.getCoord( triangle.vertices[1] );
			double c = separationAxis.getCoord( triangle.vertices[2] );
			
			double separationCoord;
			if ( subindex < 3 ) {
				separationCoord = Math.max( a, Math.max( b, c) )+bspAccuracy;
			} else {
				separationCoord = Math.min( a, Math.min( b, c) )-bspAccuracy - Shape3D.ACCURACY;
			}
			
			return new AxisAlignedPlane3D( separationAxis, separationCoord );
		} else {
			if ( index > 1000 ) {
				return null;
			}
			
	    	NodeSpace nodeSpace = getNodeSpace(node);
	    	Axis3D separationAxis = Axis3D.values()[index%3]; 	
	        double separationCoord = (separationAxis.getCoord( nodeSpace.max.asPoint3D()  )+separationAxis.getCoord( nodeSpace.min.asPoint3D() ))/2;
	        
	        if (index > 3) {
	        	separationCoord += (
	        		(random.nextDouble()-0.5) 
	        		*
	        		((separationAxis.getCoord( nodeSpace.max.asPoint3D() )-separationAxis.getCoord( nodeSpace.min.asPoint3D() ))*0.5)
	        	);
	        }
	        return new AxisAlignedPlane3D( separationAxis, separationCoord );
		}
	}
	
	@Override
    public boolean shouldSplit( IConstBspLeafNode<ArrayList<Triangle>, AxisAlignedPlane3D> node ) {
		return node.getData().size() > 0;
    }
    
	@Override
    public BspOccupation determineElementOccupation( AxisAlignedPlane3D boundary, Triangle triangle) {
    	boolean intersectsPositive = false;
    	boolean intersectsNegative = false;
        for (int j = 0; j < 3; j++) {
            Point3D vertex = triangle.vertices[j];
            if (boundary.getAxisCoord(vertex) <= boundary.origin+bspAccuracy) {
                intersectsPositive = true;
            }
            if (boundary.getAxisCoord(vertex) > boundary.origin-bspAccuracy) {
                intersectsNegative = true;
            }
        }
        
        return BspOccupation.get( intersectsPositive, intersectsNegative );
    }

	@Override
	public ArrayList<Triangle> joinData(ArrayList<Triangle> data1, ArrayList<Triangle> data2) {
		return super.joinData(data1, data2);
	}

	@Override
	public ArrayList<Triangle> removeData(ArrayList<Triangle> data, ArrayList<Triangle> dataToRemove) {
		return super.removeData(data, dataToRemove);
	}
	
	/** Get node space of a node
	 */
	protected NodeSpace getNodeSpace( IConstBspNode<ArrayList<Triangle>, AxisAlignedPlane3D> node ) {
		if ( nodeSpaces == null ) {
			nodeSpaces = Maps.newHashMap();
		}
		
    	if ( !nodeSpaces.containsKey(node) ) {
    		if ( node.getParent() != null ) {
	    		IConstBspInternalNode<ArrayList<Triangle>, AxisAlignedPlane3D> parent = node.getParent();
	    		if ( parent.getNegativeChild() == node ) {
	    			nodeSpaces.put( node, getNodeSpace(parent).splitOffNegative(parent.getBoundary()) );
	    		} else {
	    			nodeSpaces.put( node, getNodeSpace(parent).splitOffPositive(parent.getBoundary()) );
	    		}
    		} else {
    			NodeSpace treeSpace = new NodeSpace(
		   			 Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
		   			 Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
		   			 Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
		   	    );
		        for ( Triangle triangle : node.getSubtreeData() ) {
		    		for ( Point3D vertex : triangle.vertices ) {
		    			treeSpace.expand( new Location( vertex ) );
		    		}
		    	}
    			nodeSpaces.put( node, treeSpace );
    		}
    	}
    	
    	return nodeSpaces.get(node);
    }
}
