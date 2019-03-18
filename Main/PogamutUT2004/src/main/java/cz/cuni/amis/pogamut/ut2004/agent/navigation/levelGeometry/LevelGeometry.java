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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric.BSPRayInfoContainer;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.RawLevelGeometryFile.RawTriangle;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AutoTraceRayMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import math.bsp.BspTree;
import math.bsp.node.BspInternalNode;
import math.bsp.node.BspLeafNode;
import math.bsp.node.IBspNode;
import math.bsp.node.IConstBspInternalNode;
import math.bsp.node.IConstBspNode;
import math.geom3d.Axis3D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.plane.AxisAlignedPlane3D;
import math.geom3d.transform.AffineTransform3D;

/** The geometry of the environment
 */
public class LevelGeometry implements Serializable {
	
    public static String pureLevelGeometryReadDir = "map"; 
    public static String processedLevelGeometryDir = "map";    

	private static final long serialVersionUID = 4L;
    
	protected transient Logger log; 
	
	protected boolean loaded = false;
	
    protected ArrayList<Triangle> triangles = new ArrayList<Triangle>();
    protected BspTree<ArrayList<Triangle>, AxisAlignedPlane3D> bspTree = BspTree.make( new RayCastBspStrategy( this, new Random(250760834l) ) );
    protected RayCaster rayCaster = new RayCaster( bspTree );
            
    /** Constructor
     */
    public LevelGeometry(Logger log) {
        this.log = log;
        if (this.log == null) {
        	this.log = new LogCategory("LevelGeometry");
        }
    }
    
    public boolean isLoaded() {
    	return loaded;
    }
    
    public void clear() {
    	loaded = false;
    	
        triangles = new ArrayList<Triangle>();
        bspTree.clear();
    }
    
    public void setLog( Logger value ) {
    	this.log = value;
    }
    
    public void load(String mapName) { 
    	if (loaded) {
    		clear();
    	}

		try {
	    	String coreFilename = pureLevelGeometryReadDir + "\\" + mapName;
			RawLevelGeometryFile rawLevelGeometry = new RawLevelGeometryFile(coreFilename, log);
			for ( RawTriangle rawTriangle : rawLevelGeometry.triangles ) {
	        	triangles.add( new Triangle( rawTriangle ) );
	        }
		} catch (IOException e) {
			log.warning("Could not load level geometry.");
			throw new RuntimeException( "Could not load raw level geometry", e);
		}   
        
        log.info("Creating BSP tree...");
        bspTree.setRoot( bspTree.makeLeafNode() );
        bspTree.getRoot().asLeaf().setData( triangles );
    	
        bspTree.optimize( bspTree.getRoot().asLeaf() );
        log.info("BSP tree is done building.");
        leafReport();
        
        loaded = true;
    }
    
    /** Print a report of leaves
     */
    public void leafReport() {
    	ArrayList<BspLeafNode<ArrayList<Triangle>, AxisAlignedPlane3D>> leaves = Lists.newArrayList();
    	ArrayList<IBspNode<ArrayList<Triangle>, AxisAlignedPlane3D>> unprocessedNodes = Lists.newArrayList();
  		unprocessedNodes.add( bspTree.getRoot() );
    	
    	while ( unprocessedNodes.size() > 0 ) {
    		IBspNode<ArrayList<Triangle>, AxisAlignedPlane3D> node = unprocessedNodes.remove( unprocessedNodes.size()-1 );
    		if ( node.isLeaf() ) {
    			leaves.add( node.asLeaf() );
    		} else {
    			unprocessedNodes.add( node.asInternal().getNegativeChild() );
    			unprocessedNodes.add( node.asInternal().getPositiveChild() );
    		}
    	}
    	
    	HashMap<Integer, Integer> sizeToCount = Maps.newHashMap();
    	
    	for ( BspLeafNode<ArrayList<Triangle>, AxisAlignedPlane3D> node : leaves ) {
    		int size = node.getData().size();
    		int count = 0;
    		if ( sizeToCount.containsKey(size) ) {
    			count = sizeToCount.get(size);
    		}
    		sizeToCount.put( size, count+1 );
    	}
    	
    	ArrayList<Integer> sizesAscending = Lists.newArrayList( sizeToCount.keySet() );
    	Collections.sort( sizesAscending );
    	
    	log.info( "BSP tree leaf counts by size:");
    	for ( int size : sizesAscending ) {
    		log.info( sizeToCount.get(size) + " leaves with " + size + " triangles" );
    	}
    }
    
	/** Draw all triangles
	 */
	public void drawTriangles( UT2004Server server, Location color ) {
		for( Triangle triangle : triangles ) {
			triangle.draw(server, color);
		}
	}
    
    /** Ray cast emulating the GB2004 API.
     */
    public AutoTraceRay getAutoTraceRayMessage(Self self, BSPRayInfoContainer rayInfo) {

        Vector3D relativeDirection = new Vector3D( rayInfo.direction ).getNormalizedVector();
        AffineTransform3D agentViewTranformation = AffineTransform3D.createRotationOz( UnrealUtils.unrealDegreeToRad(self.getRotation().getYaw()) );
        Vector3D absoluteDirection = agentViewTranformation.transformVector( relativeDirection );
        Vector3D rayVector = absoluteDirection.times( rayInfo.length );
        
        // now get the from and to locations
        
        Location from = self.getLocation(); 
        Location to = from.add( new Velocity(rayVector) ); 

        // call the recursive function
        RayCastResult raycastResult = rayCast(from, to);
        return ( 
			new AutoTraceRayMessage(
				rayInfo.unrealId,
				from,
				to,
				false,
				rayInfo.floorCorrection,
				raycastResult.isHit(),
				( raycastResult.isHit() ? raycastResult.hitNormal.asVector3d() : null ),
				( raycastResult.isHit() ? raycastResult.hitLocation : null ),
				false,
				null
			)
        );
    }
        
    /** Ray cast, find the first collision between to points
     * 
     * @param from from point
     * @param to to point
     * @return result containing info about the first collision, if there is no collision, result containing nulls and NaNs
     */
    public RayCastResult rayCast(Location from, Location to) {
    	if ( dumpRayCastRequest ) {
    		try {
	    		rayCastRequestDumper.writeObject( from );
				rayCastRequestDumper.writeObject( to );
			} catch (IOException e) {
				throw new RuntimeException( "Raycast request dump failed.", e );
			}
    	}
    	
    	StraightLine3D line = new StraightLine3D( from.asPoint3D(), to.asPoint3D() );
    	RayCastResult retval = rayCaster.getCollision( line );
    	
    	if ( retval == null ) {
    		retval = new RayCastResult( line, null, null, Double.NaN, null );
    	}
    	return retval;
    } 
    
	public Triangle getTriangle(int index) {
		return triangles.get(index);
	}
	
	public List<Triangle> getTriangles() {
		return Collections.unmodifiableList(triangles);
	}
	
	public IConstBspNode<ArrayList<Triangle>, AxisAlignedPlane3D> getBspTreeRoot() {
		return bspTree.getRoot().asConst();
	}
    
	static final boolean dumpRayCastRequest = false;
    static final ObjectOutputStream rayCastRequestDumper = makeRaycastRequestDumper();
    static ObjectOutputStream makeRaycastRequestDumper() {
    	if ( dumpRayCastRequest ) {
	   		try {
				return new ObjectOutputStream( new FileOutputStream("raycastRequestDump.bin") );
			} catch (FileNotFoundException e) {
				throw new RuntimeException( "Failed to create raycast dump.", e );
			} catch (IOException e) {
				throw new RuntimeException( "Failed to create raycast dump.", e );
			}
    	} else {
    		return null;
    	}
    }
    
    protected void writeObject(ObjectOutputStream out) throws IOException {
    	// custom write to avoid class name overhead for triangles, vertices, nodes etc.
    	out.writeBoolean( loaded );

    	HashMap<Point3D, Integer> vertexToIndexMap = Maps.newHashMap();
    	ArrayList<Point3D> vertices = Lists.newArrayList();
    	for ( Triangle triangle : triangles ) {
    		for ( Point3D vertex : triangle.vertices ) {
    			if ( !vertexToIndexMap.containsKey(vertex) ) {
    				vertexToIndexMap.put( vertex, vertices.size() );
    				vertices.add( vertex );
    			}
    		}
    	}
    	
    	out.writeInt( vertices.size() );
    	for ( Point3D vertex : vertices ) {
			out.writeDouble( vertex.getX() );
			out.writeDouble( vertex.getY() );
			out.writeDouble( vertex.getZ() );
    	}
    	
    	HashMap<Triangle, Integer> triangleToIndexMap = Maps.newHashMap();
    	out.writeInt( triangles.size() );
    	for ( int triangleIndex=0; triangleIndex<triangles.size(); ++triangleIndex ) {
    		Triangle triangle = triangles.get(triangleIndex);
    		triangleToIndexMap.put( triangle, triangleIndex );
    		for ( Point3D vertex : triangle.vertices ) {
    			out.writeInt( vertexToIndexMap.get(vertex) );
    		}
    	}
    	
    	writeNode( out, bspTree.getRoot(), triangleToIndexMap );
    }
        
       
	protected void readObject(ObjectInputStream in) throws IOException {
    	loaded = in.readBoolean();
    	
    	ArrayList<Point3D> allVertices = Lists.newArrayList();
    	int vertexCount = in.readInt();
    	while ( allVertices.size() < vertexCount ) {
    		double x = in.readDouble();
    		double y = in.readDouble();
    		double z = in.readDouble(); 
    		allVertices.add( new Point3D( x, y, z ) );
    	}
    	
    	triangles = Lists.newArrayList();
    	int triangleCount = in.readInt();
    	while ( triangles.size() < triangleCount ) {
    		Point3D triangleVertices[] = new Point3D[3];
    		for ( int i=0; i<3; ++i ) {
        		triangleVertices[i] = allVertices.get( in.readInt() );
    		}
    		triangles.add( new Triangle( triangleVertices[0], triangleVertices[1], triangleVertices[2] ) );
    	}
    	
        bspTree = BspTree.make( new RayCastBspStrategy( this, new Random(250760834l) ) );
        rayCaster = new RayCaster( bspTree );
        
        bspTree.setRoot( readNode( in ) );
    }
	
	protected void writeNode(ObjectOutputStream out, IConstBspNode<ArrayList<Triangle>, AxisAlignedPlane3D> node, HashMap<Triangle, Integer> triangleToIndexMap ) throws IOException {
    	out.writeBoolean( node.isInternal() );
		if ( node.isInternal() ) {
			IConstBspInternalNode<ArrayList<Triangle>, AxisAlignedPlane3D> internalNode = node.asInternal();
			out.writeInt( internalNode.getBoundary().axis.ordinal() );
			out.writeDouble( internalNode.getBoundary().origin );
			writeNode( out, internalNode.getNegativeChild(), triangleToIndexMap );
			writeNode( out, internalNode.getPositiveChild(), triangleToIndexMap );
		} else {
			ArrayList<Triangle> triangles = node.asLeaf().getData();
			out.writeInt( triangles.size() );
			for ( Triangle triangle : triangles ) {
				out.writeInt( triangleToIndexMap.get(triangle) );
			}
		}
	}
	
	protected IBspNode<ArrayList<Triangle>, AxisAlignedPlane3D> readNode( ObjectInputStream in ) throws IOException {
		boolean isInternal = in.readBoolean();
		if ( isInternal ) {
			BspInternalNode<ArrayList<Triangle>, AxisAlignedPlane3D> internalNode = bspTree.makeInternalNode();
			
			Axis3D axis = Axis3D.values()[in.readInt()];
			double origin = in.readDouble();
			internalNode.setBoundary( new AxisAlignedPlane3D( axis, origin) );
			
			internalNode.setNegativeChild( readNode(in) );
			internalNode.setPositiveChild( readNode(in) );
			
			return internalNode;
		} else {
			BspLeafNode<ArrayList<Triangle>, AxisAlignedPlane3D> leafNode = bspTree.makeLeafNode();
			
			int triangleCount = in.readInt();
			ArrayList<Triangle> data = Lists.newArrayList();
			while ( data.size() < triangleCount ) {
				data.add( triangles.get( in.readInt() ) );
			}
			leafNode.setData( data );
			
			return leafNode;
		}
	}
}

