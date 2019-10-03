package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometry;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.NodeSpace;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.RayCastResult;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.Triangle;
import math.bsp.node.IConstBspInternalNode;
import math.bsp.node.IConstBspNode;
import math.geom3d.Point3D;
import math.geom3d.plane.AxisAlignedPlane3D;

public class LevelGeometryDraw extends UT2004Draw {
	
	private LevelGeometry levelGeometry;

	public LevelGeometryDraw(LevelGeometry levelGeometry, Logger log, IUT2004ServerProvider serverProvider) {
		super(log, serverProvider);
		this.levelGeometry = levelGeometry;
	}
	
	/**
     * Draws LevelGeometry within game.
     */   
    public boolean draw() {
    	if (levelGeometry == null || !levelGeometry.isLoaded()) return false;
    	
    	log.info("Drawing LevelGeomtry...");
    	
    	for ( Triangle triangle : levelGeometry.getTriangles() ) {
    		drawPolygon( triangle.verticesAsLoc[0], triangle.verticesAsLoc[1], triangle.verticesAsLoc[2] );
    	}
    	
    	log.info("LevelGeomtry drawn.");    	
    	
    	return true;
    }

	public void setLevelGeometry(LevelGeometry levelGeometry) {
		this.levelGeometry = levelGeometry;
	}

	public boolean drawBSP() {
		if (levelGeometry == null || !levelGeometry.isLoaded()) return false;
		
		log.info("Drawing LevelGeomtry BSP...");
		
		NodeSpace treeSpace = new NodeSpace(
   			 Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
   			 Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
   			 Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
   	    );
        for ( Triangle triangle : levelGeometry.getTriangles() ) {
    		for ( Point3D vertex : triangle.vertices ) {
    			treeSpace.expand( new Location( vertex ) );
    		}
    	}
		drawBSPRecursively( levelGeometry.getBspTreeRoot(), treeSpace );
		
		return true;
	}
	
	public boolean drawRaycast(RayCastResult ray) {
		if (levelGeometry == null || !levelGeometry.isLoaded()) return false;
				
		Color lineColor = ray.hitLocation != null ? Color.RED : Color.BLUE;
		Color cubeColor = ray.hitLocation != null ? Color.ORANGE : Color.CYAN;
		
		
		if (ray.hitLocation != null) {
			Triangle triangle = ray.hitTriangle;
			drawPolygon(cubeColor, triangle.verticesAsLoc[0], triangle.verticesAsLoc[1], triangle.verticesAsLoc[2]);
			drawLine(lineColor, new Location(ray.ray.getOrigin()), ray.hitLocation);			
		} else {
			drawLine(lineColor, new Location(ray.ray.getOrigin()), new Location(ray.ray.getExamplePoint2()));			
		}
		
		return true;
	}
	
	private void drawBSPRecursively(IConstBspNode<ArrayList<Triangle>, AxisAlignedPlane3D> node, NodeSpace nodeSpace) {
		if ( node.isLeaf() ) {
			return;
		}
		
		IConstBspInternalNode<ArrayList<Triangle>, AxisAlignedPlane3D> internalNode = node.asInternal();
		Location v1 = null;
		Location v2 = null;
		Location v3 = null;
		Location v4 = null;
		
		
		AxisAlignedPlane3D plane = internalNode.getBoundary();
		switch ( plane.axis ) {
		case X:
			v1 = new Location(plane.origin, nodeSpace.min.y, nodeSpace.min.z);
			v2 = new Location(plane.origin, nodeSpace.max.y, nodeSpace.min.z);
			v3 = new Location(plane.origin, nodeSpace.max.y, nodeSpace.max.z);
			v4 = new Location(plane.origin, nodeSpace.min.y, nodeSpace.max.z);
			break;
		case Y:
			v1 = new Location(nodeSpace.min.x, plane.origin, nodeSpace.min.z);
			v2 = new Location(nodeSpace.max.x, plane.origin, nodeSpace.min.z);
			v3 = new Location(nodeSpace.max.x, plane.origin, nodeSpace.max.z);
			v4 = new Location(nodeSpace.min.x, plane.origin, nodeSpace.max.z);
			break;
		case Z:
			v1 = new Location(nodeSpace.min.x, nodeSpace.min.y, plane.origin);
			v2 = new Location(nodeSpace.max.x, nodeSpace.min.y, plane.origin);
			v3 = new Location(nodeSpace.max.x, nodeSpace.max.y, plane.origin);
			v4 = new Location(nodeSpace.min.x, nodeSpace.max.y, plane.origin);
			break;
		}
		
		drawPolygon(Color.YELLOW, v1, v2, v3, v4);
		
		drawBSPRecursively( internalNode.getNegativeChild(), nodeSpace.splitOffNegative(plane) );
		drawBSPRecursively( internalNode.getPositiveChild(), nodeSpace.splitOffPositive(plane) );
	}
    
	
}
