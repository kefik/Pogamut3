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
package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old;
//old navmesh

import java.util.ArrayList;
import java.util.Random;

import math.geom2d.Point2D;
import math.geom2d.line.StraightLine2D;

/**
 *
 * @author Jakub
 * Node of BSP tree structure
 * built for NavMesh
 */
@Deprecated
public class OldNavMeshBSPNode implements java.io.Serializable {
    
    /**
	 * Auto-generated
	 */
	private static final long serialVersionUID = 3893164811677500677L;
	
	public transient OldNavMesh navmesh;
    // list of polygon ids (actual polygons are stored in navmesh)
    public ArrayList polys;
    public OldNavMeshBSPNode parent;
    // vertices separating polygons in this node (actual vertices are in navmesh)
    public StraightLine2D sepLine;
    // node children
    public OldNavMeshBSPNode left;
    public OldNavMeshBSPNode right;
    
    private Random random;
    
    /**
     * 
     * @param m
     * @param par 
     * 
     * Cretaes a new node pointing to parent par with navmesh m.
     * Remember to fill polys propery.
     * Then build method should be called.
     */
    
    public OldNavMeshBSPNode(OldNavMesh m, OldNavMeshBSPNode par) {
        navmesh = m;
        parent = par;
        polys = new ArrayList();
        random = new Random();
        sepLine = null;
    }
    
    public boolean shouldSplit() {
        return (polys.size() > OldNavMeshConstants.stopSplittingNumberOfPolygons);
    }
    
    public StraightLine2D findSeparatingLine () throws Exception {
        //System.out.println("findSeparatingLine(): Trying to split " + polys.size() + " polygons...");
        // alg 1:
        // pick 3 polygons at random and try all treir edges. remember the best one
        // criterium is that the greater half of polgons should be as small as it can be - ideally 0.5
        double bestFoundSplitFactor = OldNavMeshConstants.maxAllowedSplitFactor;
        StraightLine2D bestFoundSeparatingLine = null;
        
        ArrayList candidatePolygons = (ArrayList) polys.clone();
        
        // several tries for polygons...
        for(int i = 0; i < OldNavMeshConstants.maxNumberOfPolygonsToTry; i++) {
            if(candidatePolygons.isEmpty()) break;
            
            int randomId = 0; //random.nextInt(candidatePolygons.size());
            Integer polygonId = (Integer) candidatePolygons.get(randomId);        
            int[] polygon = navmesh.getPolygon(polygonId);
            
            // pick all vertices... and try them
            for(int j = 0; j<polygon.length; j++) {
                int v1 = polygon[j];
                int v2 = polygon[(j+1) % polygon.length];
                double[] vertex1 = navmesh.getVertex(v1);
                double[] vertex2 = navmesh.getVertex(v2);
                
                // we create 2Dpoints ignoring the third dimension
                Point2D point2D1 = new Point2D(vertex1[0], vertex1[1]);
                Point2D point2D2 = new Point2D(vertex2[0], vertex2[1]);
                
                // horray, separating line
                StraightLine2D separatingLine = new StraightLine2D(point2D1, point2D2);
                
                // now lets divide all polygons to left and right part
                ArrayList[] splittedPolys = splitPolygonsByLine(polys, separatingLine);
                ArrayList left = splittedPolys[0];
                ArrayList right = splittedPolys[1];
                
                int intersectedPolygons = left.size() + right.size() - polys.size();
                
                double splitFactor = max(left.size() / polys.size(), right.size() / polys.size());
                
                // if this line is best found, we remember it
                if(splitFactor < bestFoundSplitFactor) {
                    bestFoundSeparatingLine = separatingLine;
                    bestFoundSplitFactor = splitFactor;
                }   
            }
            // dont search this polygon again
            candidatePolygons.remove(polygonId);
        }
        if(bestFoundSeparatingLine==null) throw new Exception("No good separating line have been found. Splitting " +polys.size()+ " polygons unsuccessfull." );
        //System.out.println("findSeparatingLine(): Returning a split line with splitFactor " + bestFoundSplitFactor);     
        return bestFoundSeparatingLine;
    }
   
     /*
     * Splits the input polygons by line into left and right part.
     * Polygons that this line intersects will occur in both sets
     */
    private ArrayList[] splitPolygonsByLine(ArrayList polysToSplit, StraightLine2D separatingLine) throws Exception {
        
        ArrayList left = new ArrayList();
        ArrayList right = new ArrayList();
        
        // walk through each polygon
        for(int i = 0; i < polysToSplit.size(); i++) {
            
            // switches where the polygon lie
            boolean isOnLeft = false;
            boolean isOnRight = false;
            
            Integer pId = (Integer) polysToSplit.get(i);
            int[] polygon = navmesh.getPolygon(pId);
            
            // walk through each vertex
            for(int j = 0; j < polygon.length; j++) {
                int vId = polygon[j];
                double[] vertex = navmesh.getVertex(vId);
                Point2D point2D = new Point2D(vertex[0], vertex[1]);     
                double dist = separatingLine.getSignedDistance(point2D);   
                if(dist<0) isOnLeft = true;
                if(dist>0) isOnRight = true;                
                if(isOnLeft && isOnRight) break;
            }
            if(isOnLeft) left.add(pId);
            if(isOnRight) right.add(pId);
            if(!isOnLeft && !isOnRight) throw new Exception("Something is wrong. The polygon number " +pId+ " seems to be on neither side of the separating line.");
            
        }  
        ArrayList[] ret = new ArrayList[2];
        ret[0] = left;
        ret[1] = right;
        return ret;
    }  
    
    /**
     * Recursive method building an antrige tree from this node as root
     */
    public void build() {
    
        // test end of recursion
        if (!shouldSplit()) {
            // set biggest leaf?
            if(polys.size() > navmesh.getNumberOfPolygonsInBiggestLeaf()) {
                //System.out.println("Setting biggest leaf sofar. Number of polygons in this node is " + polys.size());
                navmesh.setBiggestLeafInTree(this);
            }
            return;
        }
        
        // find separating line
        try {
            sepLine = findSeparatingLine();
        }
        catch(Exception e) {
            //System.out.println("No separating line found for this node. Ok, stop splitting. Number of polygons in this node is " + polys.size());
            // set biggest leaf?
            if(polys.size() > navmesh.getNumberOfPolygonsInBiggestLeaf()) {
                //System.out.println("Setting biggest leaf sofar. Number of polygons in this node is " + polys.size() + ". The polygons here are: " + polys.toString());
                navmesh.setBiggestLeafInTree(this);
            }
        }
        
        if(sepLine != null) {
            try {
                ArrayList[] splittedPolys = splitPolygonsByLine(polys, sepLine);
                //System.out.println("build(): Left has " + splittedPolys[0].size() + " polygons.");
                //System.out.println("build(): Right has " + splittedPolys[1].size() + " polygons.");
                //System.out.println("build(): Line is crossing " + (splittedPolys[0].size() + splittedPolys[1].size() - polys.size()) + " polygons.");
                left = new OldNavMeshBSPNode(navmesh, this);
                left.polys = splittedPolys[0];
                left.build();
                right = new OldNavMeshBSPNode(navmesh, this);
                right.polys = splittedPolys[1];
                right.build();
            } catch (Exception e) {
                //System.out.println("Could not split polys by the given line. This should not happen.");
                e.printStackTrace();
            }
        }    
    }

    private double max(double d1, double d2) {
        return d1 > d2 ? d1 : d2;
    }
    
    public boolean isLeaf() {
        return (left == null && right == null);
    }
    
    
}
