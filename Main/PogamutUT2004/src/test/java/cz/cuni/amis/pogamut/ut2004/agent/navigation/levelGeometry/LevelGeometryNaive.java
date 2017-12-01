/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.google.inject.internal.Lists;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.utils.ExceptionToString;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.plane.Plane3D;

/**
 * Class containing complete data with the geometry of the environment
 * It is useful for steering.
 * It is part of NavMesh class.
 * 
 * @author Jakub Tomek
 */
public class LevelGeometryNaive implements Serializable {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 1L;

	public static class RaycastResult {
		public Location from;
		public Location to;
		public Location rayVector;
		public Location hitLocation;
		public Vector3D hitNormal;
		public boolean hit;
		public double hitDistance;
		public List<Location> rayPoints = new ArrayList<Location>();
		/**
		 * Index into {@link LevelGeometryNaive#triangles}.
		 */
		public int hitTriangle;
		
		public RaycastResult(Location from, Location to) {
			super();
			this.from = from;
			this.to = to;
			this.hit = false;
			rayVector = to.sub(from);
		}		
	}
   
	private transient Logger log; 
	
	public static Random random = new Random();
	
    public ArrayList<double[]> verts = new ArrayList<double[]>();
    public ArrayList<int[]>  triangles = new ArrayList<int[]>();
    public int leafCount = 0;
    
    // boundries of the space
    public double minX = Double.POSITIVE_INFINITY;
    public double maxX = Double.NEGATIVE_INFINITY;
    public double minY = Double.POSITIVE_INFINITY;
    public double maxY = Double.NEGATIVE_INFINITY;
    public double minZ = Double.POSITIVE_INFINITY;
    public double maxZ = Double.NEGATIVE_INFINITY;
    
    /**
     * Constructor creates the object from a file defined by map name and path which is in constants
     * @param mapName 
     */
    public LevelGeometryNaive(Logger log) {
        this.log = log;
        if (this.log == null) {
        	this.log = new LogCategory("LevelGeometry");
        }
    }
    
    public boolean load(String mapName) throws FileNotFoundException, IOException, Exception { 
    	
        // vertices are indexed from 1. Put something to 0
        verts.add(new double[0]);
        
        double scale;
        double[] centre;
        String fileName, line;
        File file;
        BufferedReader br;
        
        // read scale
        fileName = LevelGeometry.pureLevelGeometryReadDir + "\\" + mapName + ".scale";
        file = new File(fileName);
        if (!file.exists()) {
        	log.warning("LevelGeometry .scale file does not exist at: " + file.getAbsolutePath());
        	return false;
        }
        br = new BufferedReader(new FileReader(file));
        line = br.readLine();
        scale = Double.parseDouble(line);
        br.close();
        
        // read centre
        fileName = LevelGeometry.pureLevelGeometryReadDir + "\\" + mapName + ".centre";
        file = new File(fileName);
        if (!file.exists()) {
        	log.warning("LevelGeometry .centre file does not exist at: " + file.getAbsolutePath());
        	return false;
        }
        br = new BufferedReader(new FileReader(file));
        line = br.readLine();
        String[] sc = line.split("[ \\t]");
        centre = new double[3];
        for(int i=0; i<3; i++) {
            centre[i] = Double.parseDouble(sc[i]);
        }
        br.close();
        
        // read all vertices and triangles from file
        fileName = LevelGeometry.pureLevelGeometryReadDir + "\\" + mapName + ".obj";
        file = new File(fileName);
        if (!file.exists()) {
        	log.warning("LevelGeometry .obj file does not exist at: " + file.getAbsolutePath());
        	return false;
        }
        
        br = new BufferedReader(new FileReader(file));     
        while((line = br.readLine()) != null) {
            String[] words = line.split("[ \\t]");
            if(words[0].equals("v")) {
                double[] v = new double[3];
                v[0] = Double.parseDouble(words[1])*scale + centre[0];
                v[1] = Double.parseDouble(words[3])*scale + centre[1];
                v[2] = Double.parseDouble(words[2])*scale + centre[2];
                verts.add(v);
                
                // check the boundries
                if(v[0] < minX) minX = v[0];
                if(v[0] > maxX) maxX = v[0];
                if(v[1] < minY) minY = v[1];
                if(v[1] > maxY) maxY = v[1];
                if(v[2] < minZ) minZ = v[2];
                if(v[2] > maxZ) maxZ = v[2];                
                
            }
            if(words[0].equals("f")) {
                int[] t = new int[3];
                t[0] = Integer.parseInt(words[1]);
                t[1] = Integer.parseInt(words[2]);
                t[2] = Integer.parseInt(words[3]);
                triangles.add(t);
            }
        }
        br.close();
        
        return true;
    }
    
    /**
     * Recursive function for getting ray collision
     * @param from
     * @param to
     * @param rayInfo
     * @return 
     */
    public RaycastResult raycast(Location from, Location to) {
    	RaycastResult result = new RaycastResult(from, to);
    	try {
    		raycastInner(from, to, result);
    	} catch (Exception e) {
    		log.severe(ExceptionToString.process("Failed to raycast: " + from + " -> " + to, e));
    	}
    	if (result.hit) {
    		result.hitDistance = result.hitLocation.getDistance(from.getLocation());
    	}
    	return result;
    }
    
    private void raycastInner(Location from, Location to, RaycastResult result) {
    	 // the actual 3D line
        StraightLine3D ray =  new StraightLine3D(from.asPoint3D(), to.asPoint3D());              
                
        // information about ray's collision with triangle
        boolean collisionFound = false;
        double collisionDistance = Double.POSITIVE_INFINITY;
        Location hitLocation = null;
        Vector3D normalVector = null;
        
        int triangleHit = -1;
        
        // now let's examine the ray's collisions with triangles
        for(Integer tId = 0; tId<triangles.size(); ++tId) {
            int[] triangle = this.triangles.get(tId);
            double[] v1 = this.verts.get(triangle[0]);
            double[] v2 = this.verts.get(triangle[1]);
            double[] v3 = this.verts.get(triangle[2]);
            Point3D p1 = new Point3D(v1[0],v1[1],v1[2]);
            Vector3D vector1 = new Vector3D(v2[0]-v1[0], v2[1]-v1[1], v2[2]-v1[2]);
            Vector3D vector2 = new Vector3D(v3[0]-v1[0], v3[1]-v1[1], v3[2]-v1[2]);            
            Plane3D plane = new Plane3D(p1,vector1,vector2);
            Point3D intersection = ray.getPlaneIntersection( plane );
            
            // is intersection inside triangle?
            boolean collision = isValidPoint(intersection) && this.isPointInTriangle(intersection, tId);
            if(!collision) continue;
            
            // is the point on the HalfLine FROM->TO?
            double zero = intersection.getDistance(from.asPoint3D()) + intersection.getDistance(to.asPoint3D()) - from.getDistance(to);
            collision = Math.abs(zero) < 0.000000000001;
            if(!collision) continue;

            // we remember the closest collision
            double distance = intersection.getDistance(from.asPoint3D());
            if(distance < collisionDistance) {
                collisionFound = true;
                collisionDistance = distance;
                hitLocation = new Location(intersection.getX(),intersection.getY(),intersection.getZ());
                normalVector = plane.getNormalVector();
                triangleHit = tId;
            }
        }
        
        // if we found a collision, we collect and return the information about it
        if(collisionFound) {
        	result.hitLocation = hitLocation;
        	result.hitNormal = normalVector;
        	result.hit = true;
        	result.hitTriangle = triangleHit;
            return;
        }
        
    }

    private boolean isValidPoint(Point3D point) {
    	return point!=null && isValidNumber(point.getX()) && isValidNumber(point.getY()) && isValidNumber(point.getZ());
    }
    
    private boolean isValidNumber(Double number) {
    	if (number == null) return false;
    	if (number == Double.POSITIVE_INFINITY) return false;
    	if (number == Double.NEGATIVE_INFINITY) return false;
    	if (number == Double.NaN) return false;
    	return true;
    }
    
    public boolean isPointInTriangle(Point3D point3D, Integer tId) {
    	int[] triangle = triangles.get(tId);
    	
    	Location p  = new Location(point3D); 
    	Location a = new Location(verts.get(triangle[0]));
    	Location b = new Location(verts.get(triangle[1]));
    	Location c = new Location(verts.get(triangle[2]));

    	Location v0 = b.sub(a);
    	Location v1 = c.sub(a);
    	Location v2 = p.sub(a);
    	
    	double d00 = v0.dot(v0);
    	double d01 = v0.dot(v1);
    	double d11 = v1.dot(v1);
    	double d20 = v2.dot(v0);
    	double d21 = v2.dot(v1);
    	double denom = d00 * d11 - d01 *d01;
    	
    	if (Math.abs(denom) < 0.0000001) return false; // stupid triangle, ignore...
    	
    	double v = (d11 * d20 - d01 * d21) / denom;
        double w = (d00 * d21 - d01 * d20) / denom;
        double u = 1 - v - w;
        
        boolean inside = 0 < u && u < 1 && 0 < v && v < 1 && 0 < w && w < 1;
    	
    	if (inside) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public void saveReferenceResults( List<RaycastRequest> requests, String filename ) {
    	ArrayList<PrecomputedRaycastResult> results = Lists.newArrayList();
		for ( RaycastRequest request : requests ) {
			RaycastResult result = raycast( request.from, request.to );
			results.add(
				new PrecomputedRaycastResult(
					request.from,
					request.to,
					(result.hit ? result.hitDistance : Double.NaN )
				)
			);
		}
		
		RaycastDataFileTools.save( results, filename );
    }
}

