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

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.agent.navigation.impl.PrecomputedPathFuture;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004EdgeChecker;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMeshConstants;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DrawStayingDebugLines;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.vecmath.Vector2d;
import math.geom2d.Point2D;
import math.geom2d.Shape2D;
import math.geom2d.line.Line2D;
import math.geom2d.line.StraightLine2D;
import math.geom3d.Point3D;
import math.geom3d.Vector3D;
import math.geom3d.line.StraightLine3D;
import math.geom3d.plane.Plane3D;

/**
 * Class storing NavMesh data structures.
 *
 * Controlled from {@link OldNavMeshModule}.
 *
 * @author Jakub Tomek
 * @author Jakub Gemrot aka Jimmy
 */
@Deprecated
public class OldNavMesh implements IPathPlanner<ILocated> {

	public static interface INavPointWorldView {
		Map<UnrealId, NavPoint> get();
	}
    private INavPointWorldView worldView;
    private Logger log;

    private Random random;

    //
    // STATE
    //
    private boolean loaded = false;
    private GameInfo loadedForMap = null;

    //
    // NAVMESH DATA STRUCTURES
    //
    private ArrayList<double[]> verts = new ArrayList<double[]>();
    private ArrayList<int[]> polys = new ArrayList<int[]>();
    private ArrayList<ArrayList<Integer>> vertsToPolys;
    private ArrayList<Boolean> safeVertex;
    private OldNavMeshBSPNode bspTree;
    private OldNavMeshBSPNode biggestLeafInTree;
    private ArrayList<OldOffMeshPoint> offMeshPoints;
    private HashMap<UnrealId,OldOffMeshPoint> navPointToOffMeshPoint;
    private HashMap<UnrealId,OldNavMeshPolygon> navPointToNearestPolygon;
    private ArrayList<ArrayList<OldOffMeshPoint>> polysToOffMeshPoints;
    private Map<UnrealId, NavPoint> nps = null;

    //
    // FW map as metric for teleport paths.
    //
    private FloydWarshallMap fwMap;
    private boolean isFwMapAvailable = false;
    private boolean hasTeleports = false;

    public OldNavMesh( INavPointWorldView worldView, Logger log) {
        this.log = log;
        if (this.log == null) {
            this.log = new LogCategory("NavMesh");
        }
        random = new Random();

        this.worldView = worldView;
        NullCheck.check(worldView, "worldView");
        navPointToNearestPolygon = new HashMap<UnrealId,OldNavMeshPolygon>();
    }
    
    // ================    
    // ================
    // PUBLIC INTERFACE
    // ================
    // ================    

    /**
     * Tells whether the NavMesh has been successfully initialized and thus can be used. Use {@link #load(GameInfo, boolean)} to initialize NavMesh. 
     * @return
     */
    public boolean isLoaded() {
        return loaded;
    }
    
    /**
     * Returns the number of polygons in navmesh
     */
    public int polyCount() {
        return polys.size();
    }

    /**
     * Returns the number of vertices in navmesh
     */
    public int vertCount() {
        return verts.size();
    }
    
    /**
     * Returns list of all NavMesh polygons. A polygon is formed by triple [vertexId, vertexId, vertexId] pointing into {@link #getVerts()} array. 
     *
     * @return
     */
    public ArrayList<int[]> getPolys() {
        return polys;
    }

    /**
     * Returns list of all NavMesh points. A point is formed by triple [double,double,double] ~ [x,y,z]. 
     * @return
     */
    public ArrayList<double[]> getVerts() {
        return verts;
    }

    /**
     * Gets a clone of polygon by its order
     */
    public int[] getPolygon(int polygonId) {
        int[] p = ((int[]) polys.get(polygonId)).clone();
        return p;
    }

    /**
     * Gets a clone of vertex by its order
     */
    public double[] getVertex(int vertexId) {
        double[] v = ((double[]) verts.get(vertexId)).clone();
        return v;
    }

    /**
     * Gets a list of polygons containing this vertex.
     */
    @SuppressWarnings("unchecked")
	public ArrayList<Integer> getPolygonsByVertex(int vertexId) {
        return (ArrayList<Integer>) (this.vertsToPolys.get(vertexId)).clone();
    }

    /**
     * Gets an array of polygon ids by an polygon id
     */
    public ArrayList<Integer> getNeighbourIdsToPolygon(int polygonId) {

        ArrayList<Integer> neighbours = new ArrayList<Integer>();
        int[] p = this.getPolygon(polygonId);

        for (int j = 0; j < p.length; j++) {
            ArrayList<Integer> p2 = getPolygonsByVertex(p[j]);
            for (int k = 0; k < p2.size(); k++) {
                int candidateId = p2.get(k);
                //this polygon shares one vertex with the input polygon, but that is not enough. neighbour must share two.
                // p[j] is one vertex
                int secondVertex = p[((j == p.length - 1) ? 0 : j + 1)];
                int[] candidatePolygon = this.getPolygon(candidateId);
                for (int l = 0; l < candidatePolygon.length; l++) {
                    if (candidatePolygon[l] == secondVertex) {
                        // its him! ok! shares 2 tertices
                        if (!neighbours.contains(candidateId) && candidateId != polygonId) {
                            neighbours.add(candidateId);
                        }
                        break;
                    }
                }

            }
        }
        return neighbours;
    }
    
    /**
     * Returns list of {@link OldOffMeshPoint}s within the NavMesh.
     * @return
     */
    public ArrayList<OldOffMeshPoint> getOffMeshPoints() {
        return offMeshPoints;
    }

    /**
     * Returns a List of offmeshpoints that are located on target polygon
     *
     * @param polygonId
     * @return
     */
    public List<OldOffMeshPoint> getOffMeshPointsOnPolygon(int polygonId) {
        return polysToOffMeshPoints.get(polygonId);
    }

    /**
     * Debug method: helps to describe BSP tree by telling the number of
     * polygons in the biggest leaf (this should not bee too big. 5 is a good
     * number.)
     */
    public int getNumberOfPolygonsInBiggestLeaf() {
        if (biggestLeafInTree != null) {
            return biggestLeafInTree.polys.size();
        } else {
            return -1;
        }
    }

    /**
     * DEBUG ONLY: sets the biggest leaf so it can be easily found.
     */
    public void setBiggestLeafInTree(OldNavMeshBSPNode node) {
        biggestLeafInTree = node;
    }

    /**
     * Returns PolygonId for a given point3D.
     * @param point3D
     * @return id of polygon on which is this point
     */
    public int getPolygonId(Point3D point3D) {
        //System.out.println("trying to fing polygon for location [" + point3D.getX() + ", " + point3D.getY() + ", " + point3D.getZ() + "]");
        // 2D projection of point
        Point2D point2D = new Point2D(point3D.getX(), point3D.getY());
        // walk through BSP tree
        OldNavMeshBSPNode node = bspTree;
        while (!node.isLeaf()) {
            //System.out.println("Searching an inner node. There are " +node.polys.size()+ " polygons and their numbers are: " + node.polys.toString());            
            StraightLine2D sepLine = node.sepLine;
            double dist = sepLine.getSignedDistance(point2D);
            if (dist < 0) {
                //System.out.println("go to left child");
                node = node.left;
            }
            if (dist > 0) {
                //System.out.println("go to right child");
                node = node.right;
            }
            if (dist == 0) {
                //System.out.println("Wow, the location is exactly on the border. Let's move a little");
                point2D = new Point2D(point3D.getX() + random.nextDouble() - 0.5, point3D.getY() + random.nextDouble() - 0.5);
            }
        }
        // now we are in leaf so, we should see the list of possible polygons
        //System.out.println("The leaf is found. There are " +node.polys.size()+ " polygons and their numbers are: " + node.polys.toString());

        // now we must choose which polygon is really the one we are staying at, if any
        ArrayList<Integer> candidatePolygons = new ArrayList<Integer>();

        // are we staying inside in 2D projection? if not, reject this polygon
        for (int i = 0; i < node.polys.size(); i++) {
            Integer pId = (Integer) node.polys.get(i);

            if (polygonContainsPoint(pId, point2D)) {
                //System.out.println("Polygon " +pId+ " contains this location.");
                candidatePolygons.add(pId);
            } else {
                //System.out.println("Polygon " +pId+ " does not contain this location.");
            }
        }

        // now we have the candidate polygons. the agent is inside each of them in a 2D projection
        // now we select the one that is closest on z coordinate and return it   
        //System.out.println("candidatePolygons: " + candidatePolygons.toString());
        if (candidatePolygons.isEmpty()) {
            return -1;
        }

        // spocitame vzdalenost od vrcholu 0 na z souradnici. nejmensi vzdalenost = vitez
        //TODO: Fixed - checking for rounding errors before accepting the polygon
        double minDist = NavMeshConstants.maxDistanceBotPolygon;
        int retPId = -2;
        for (int i = 0; i < candidatePolygons.size(); i++) {
            Integer pId = (Integer) candidatePolygons.get(i);
            int[] polygon = getPolygon(pId);

            // we take first three points, create plane, calculate distance
            double[] v1 = getVertex(polygon[0]);
            double[] v2 = getVertex(polygon[1]);
            double[] v3 = getVertex(polygon[2]);
            Point3D p1 = new Point3D(v1[0], v1[1], v1[2]);
            Vector3D vector1 = new Vector3D(v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]);
            Vector3D vector2 = new Vector3D(v3[0] - v1[0], v3[1] - v1[1], v3[2] - v1[2]);
            Plane3D plane = new Plane3D(p1, vector1, vector2);
            double dist = plane.getDistance(point3D);
            if (dist < minDist) {
                //It looks good, inspecting for rounding errors...

                // watch out for rounding errors!
                // if those three points are almost in line rounding error could become huge
                // better try more points
                if (polygon.length > 3) {
                    v1 = getVertex(polygon[0]);
                    v2 = getVertex(polygon[1]);
                    v3 = getVertex(polygon[3]);
                    p1 = new Point3D(v1[0], v1[1], v1[2]);
                    vector1 = new Vector3D(v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]);
                    vector2 = new Vector3D(v3[0] - v1[0], v3[1] - v1[1], v3[2] - v1[2]);
                    plane = new Plane3D(p1, vector1, vector2);
                    dist = plane.getDistance(point3D);
                    if (dist < minDist) {
                        //Still looks good...

                        // or even more
                        if (polygon.length > 4) {
                            v1 = getVertex(polygon[0]);
                            v2 = getVertex(polygon[2]);
                            v3 = getVertex(polygon[4]);
                            p1 = new Point3D(v1[0], v1[1], v1[2]);
                            vector1 = new Vector3D(v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]);
                            vector2 = new Vector3D(v3[0] - v1[0], v3[1] - v1[1], v3[2] - v1[2]);
                            plane = new Plane3D(p1, vector1, vector2);
                            dist = plane.getDistance(point3D);
                            if (dist < minDist) {
                                retPId = pId;
                                minDist = dist;
                            } else {
                                //We are too far...
                                continue;
                            }
                        } else {
                            //We have enough precise results..
                            retPId = pId;
                            minDist = dist;
                        }
                    } else {
                        //We are too far
                        continue;
                    }
                } else {
                    //We have enough precise results...
                    retPId = pId;
                    minDist = dist;
                }

            }
        }
        log.fine("Distance of a point from polygon " + retPId + " is " + minDist);
        return retPId;
    }

    /**
     * Gets the id of a polygon that contains this location
     *
     * @param location
     * @return id of polygon or value < 0 if the is no such polygon
     */
    public int getPolygonId(Location location) {
        return getPolygonId(new Point3D(location.x, location.y, location.z));
    }
    
    // ===============
    // ===============
    // NAVMESH LOADING
    // ===============
    // ===============    

    /**
     * Load NavMesh according to current map (obtained through {@link GameInfo#getLevel()}) from directories specified by {@link NavMeshConstants}.
     *  
     * @param info
     * @param shouldReloadNavMesh whether we should ignore ".navmesh.processed" file and recreate NavMesh again, comes in handy when you are working with {@link NavigationGraphBuilder}
     * @return
     */
    public boolean load(GameInfo info, boolean shouldReloadNavMesh) {
        if (info == null) {
            log.severe("Could not load for 'null' GameInfo!");
            return false;
        }
        if (loaded) {
            if (loadedForMap == null) {
                // WTF?
                clear();
            } else {
                if (loadedForMap.getLevel().equals(info.getLevel())) {
                    // ALREADY INITIALIZED FOR THE SAME LEVEL
                    return true;
                }
            }
        }

        // LOAD THE NAVMESH ACCORDING TO 'info'
        String mapName = info.getLevel();
        log.warning("Loading NavMesh for: " + mapName);
        
        String processedNavMeshFileName = NavMeshConstants.processedMeshDir + "/" + mapName + ".navmesh.processed";
        File processedNavMeshFile = new File(processedNavMeshFileName);
        
        String pureMeshFileName = NavMeshConstants.pureMeshReadDir + "/" + mapName + ".navmesh";
        File pureMeshFile = new File(pureMeshFileName);
        
        if (shouldReloadNavMesh) {
        	// 0. WE HAVE TO RELOAD NAVMESH
	        if (processedNavMeshFile.exists() && processedNavMeshFile.isFile()) {
	        	if (pureMeshFile.exists() && pureMeshFile.isFile()) {
	        		log.warning("Going to RELOAD / REPROCESS navmesh.");
	        		log.warning("Deleting old processed navmesh file: " + processedNavMeshFile.getAbsolutePath());
	        		processedNavMeshFile.delete();
	        	} else {
	        		log.warning("NavMesh RELOAD flag true, but .navmesh file for a given map not found (at " + pureMeshFile.getAbsolutePath() + "), going to load .navmesh.processed file.");
	        	}
	        }
        } 
        
        // 1. TRY TO READ ALL THE DATA FROM PREVIOUSLY PROCESSED NAVMESH	
        try {
            if (!processedNavMeshFile.exists()) {
            	if (!shouldReloadNavMesh) {
            		log.warning("Processed NavMesh does not exist at: " + processedNavMeshFile.getAbsolutePath());
            	}
            } else {
                // LOAD IT!      
                if (loadNavMeshFromCoreFile(mapName)) {
                	log.warning("NavMesh LOADED SUCCESSFULLY.");
                    loaded = true;
                    loadedForMap = info;
                    return true;
                }
            }
        } catch (Exception e) {
            log.warning(ExceptionToString.process("NavMesh could not be loaded from previously stored binary file: " + processedNavMeshFile.getAbsolutePath(), e));
        }

        // 2. TRY TO CONSTRUCT THE NAVMESH FROM THE .navmesh FILE
        try {
            if (!pureMeshFile.exists()) {
                log.warning("NavMesh source text file does not exist at: " + pureMeshFile.getAbsolutePath());
                log.severe("NavMesh COULD NOT INITIALIZE FOR MAP: " + mapName);
                return false;

            } else {
        		// LOAD IT!

                // 3. load .navmesh file
                loadSourceFile(pureMeshFile);
            }
        } catch (Exception e) {
            log.warning(ExceptionToString.process("NavMesh could not be loaded from source text file: " + pureMeshFile.getAbsolutePath(), e));
            log.severe("NavMesh COULD NOT HAVE BEEN INITIALIZED FOR MAP: " + mapName);
            return false;
        }

        // 4. when vertices and polygons are done creating, we create an array mapping vertices to polygons
        resetVertsToPolys();

        // 5. mark safe and unsafe vertices
        resetSafeVerts();

        // 6. create a BSP tree structure
        resetBSPTree();

        // 7. get rid of unreachable polygons
        if (!eliminateUnreachablePolygons()) {
            return false;
        }

        // 8. create off-mesh connections
        resetOffMeshConnections();

        // 9. save data core for next time
        saveNavMeshCore(mapName);

        loaded = true;
        loadedForMap = info;
        return true;
    }

    protected boolean loadNavMeshFromCoreFile(String mapName) throws FileNotFoundException, IOException, ClassNotFoundException {        
        OldNavMeshCore core = OldNavMeshCoreCache.getNavMeshCore(mapName);
        if (core == null) return false;
        this.biggestLeafInTree = core.biggestLeafInTree;
        this.bspTree = core.bspTree;
        this.polys = core.polys;
        this.verts = core.verts;
        this.vertsToPolys = core.vertsToPolys;
        this.offMeshPoints = core.offMeshPoints;
        regenerateNavPointToOffMeshPointMap();
        this.polysToOffMeshPoints = core.polysToOffMeshPoints;
        this.safeVertex = core.safeVertex;
        return true;
    }

    protected void loadSourceFile(File pureMeshFile) throws NumberFormatException, IOException {
        log.warning("Loading NavMesh from text file: " + pureMeshFile.getAbsolutePath());

        BufferedReader br = new BufferedReader(new FileReader(pureMeshFile));
        String line;

        while ((line = br.readLine()) != null) {
            String[] toks = line.split("[ \\t]");

            if (toks[0].equals("v")) {
                double[] v = new double[3];
                v[0] = Double.parseDouble(toks[1]);
                v[1] = Double.parseDouble(toks[2]);
                v[2] = Double.parseDouble(toks[3]);
                verts.add(v);
            }
            if (toks[0].equals("p")) {
                int[] p = new int[toks.length - 1];
                for (int i = 0; i < toks.length - 1; i++) {
                    p[i] = Integer.parseInt(toks[i + 1]);
                }
                polys.add(p);
            }
        }
    }

    /**
     * Builds the resetVertsToPolys mapping array
     */
    protected void resetVertsToPolys() {
        log.info("Setting vertsToPolys mapping array...");
        vertsToPolys = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < verts.size(); i++) {
            vertsToPolys.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < polys.size(); i++) {
            int[] p = (int[]) polys.get(i);
            for (int j = 0; j < p.length; j++) {
                ArrayList<Integer> p2 = (ArrayList<Integer>) vertsToPolys.get(p[j]);
                if (!p2.contains(i)) {
                    p2.add(i);
                }
            }
        }
    }

    /**
     * Resets the array of boolean values saying whether a vertex is at the edge
     * of navmesh
     */
    protected void resetSafeVerts() {
        log.info("Setting safe vertices...");
        safeVertex = new ArrayList<Boolean>();
        int safeCount = 0;

        // check vertices one by one
        for (int v1 = 0; v1 < verts.size(); v1++) {
            log.fine("Looking at vertex " + v1 + "...");

            // angles of polygons around must give 2 pi in sum
            double sum = 0;

            ArrayList<Integer> polys = vertsToPolys.get(v1);

            for (Integer pId : polys) {
                //System.out.println("Looking at polygon "+pId+"...");
                //this.drawOnePolygon(pId);
                int[] polygon = getPolygon(pId);
                // we must fint the three vertices creating angle around vertex i
                int v0 = -1, v2 = -1;
                for (int i = 0; i < polygon.length; i++) {
                    if (polygon[i] == v1) {
                        v0 = (i == 0 ? polygon[polygon.length - 1] : polygon[i - 1]);
                        v2 = (i == polygon.length - 1 ? polygon[0] : polygon[i + 1]);
                        break;
                    }
                }
                // three vertices found, now get the angle
                double[] vv0 = getVertex(v0);
                double[] vv1 = getVertex(v1);
                double[] vv2 = getVertex(v2);

                double a = Math.sqrt((vv1[0] - vv0[0]) * (vv1[0] - vv0[0]) + (vv1[1] - vv0[1]) * (vv1[1] - vv0[1]) + (vv1[2] - vv0[2]) * (vv1[2] - vv0[2]));
                double b = Math.sqrt((vv2[0] - vv1[0]) * (vv2[0] - vv1[0]) + (vv2[1] - vv1[1]) * (vv2[1] - vv1[1]) + (vv2[2] - vv1[2]) * (vv2[2] - vv1[2]));
                double c = Math.sqrt((vv2[0] - vv0[0]) * (vv2[0] - vv0[0]) + (vv2[1] - vv0[1]) * (vv2[1] - vv0[1]) + (vv2[2] - vv0[2]) * (vv2[2] - vv0[2]));

                double gama = Math.acos((a * a + b * b - c * c) / (2 * a * b));
                log.fine("Angle gama is " + gama);
                //this.drawOnePolygon(pId, new Location(255,255,255));
                sum += gama;
            }
            log.fine("Sum angle is " + sum);
            // we give it some tolerance for rounding errors
            if (sum >= 2 * 3.14) {
                safeVertex.add(v1, true);
                safeCount++;
            } else {
                safeVertex.add(v1, false);
            }
        }
        log.info("There are " + safeCount + " safe and " + (verts.size() + safeCount) + " unsafe vertices.");
    }

    /**
     * Gets a new BSPTree for this mesh
     */
    protected void resetBSPTree() {
        log.info("Creating BSP tree...");
        bspTree = new OldNavMeshBSPNode(this, null);
        for (int i = 0; i < polys.size(); i++) {
            bspTree.polys.add(i);
        }
        biggestLeafInTree = null;
        bspTree.build();
        log.info("BSP tree for NavMesh polygons has been built. Biggest leaf has " + biggestLeafInTree.polys.size() + " polygons.");
    }

    /**
     * Some polygons cannot be reached we find them with the help of navigation
     * graph Definition: 1. Any polygon with navigation point is reachable 2.
     * Any polygon sharing edge with a reachable polygon is also reachable.
     */
    protected boolean eliminateUnreachablePolygons() {
        log.info("eliminateUnreachablePolygons() starts...");

        // 1. get list of all navpoints
        Map<UnrealId, NavPoint> navPoints = nps;
        if (navPoints == null) {
            navPoints = worldView.get();
        }

        if (navPoints == null || navPoints.size() == 0) {
            // WE DO NOT HAVE ANY NAVPOINTS
            // => ignore the request
            log.warning("There are no navpoints present within the worldview, could not eliminateUnreachablePolygons() ...");
            return false;
        }

        // which polygons are reachbale and which are not?
        boolean[] reachable = new boolean[polys.size()];
        // 2. walk through all navpoints and mark all reachable polygons
        log.info("Marking reachable polygons...");
        for (NavPoint navPoint : navPoints.values()) {
            Point3D point3D = navPoint.getLocation().asPoint3D();
            int pId = getPolygonId(point3D);
            if (pId < 0) {
                continue;
            }
            markAsReachableRecursive(pId, reachable);
        }

        // debugging control: how many polygons are unreachable?
        int reachableCount = 0;
        int polyDelCount = 0;
        int vertDelCount = 0;
        for (int i = 0; i < polys.size(); i++) {
            if (reachable[i]) {
                reachableCount++;
            }
        }

        if (polys.size() == reachableCount) {
            log.warning("Marking complete. All " + reachableCount + " polygons are reachable, no need to delete anything.");
            return true;
        }

        log.warning("Marking complete. There are " + reachableCount + " reachable polygons and " + (polys.size() - reachableCount) + " unreachable polygons.");

        log.warning("Deleting unreachable polygons...");
        for (int i = polys.size() - 1; i >= 0; i--) {
            if (!reachable[i]) {
                polys.remove(i);
                polyDelCount++;
            }
        }

        resetVertsToPolys();

        log.warning("Deleting unused vertices...");
        for (int i = vertsToPolys.size() - 1; i >= 0; i--) {
            ArrayList<Integer> polygons = (ArrayList<Integer>) vertsToPolys.get(i);
            if (polygons.isEmpty()) {
                verts.remove(i);
                vertDelCount++;
                // after removing a vertex (and moving all following vertices by -1) we must recalculate the vertices in polygons
                for (int j = 0; j < polys.size(); j++) {
                    int[] polygon = (int[]) polys.get(j);
                    for (int k = 0; k < polygon.length; k++) {
                        if (polygon[k] > i) {
                            polygon[k]--;
                        }
                    }
                }
            }
        }

        log.warning("Deleting done. " + polyDelCount + " polygons and " + vertDelCount + " vertices were deleted.");

        // we have changed the polygon a and vertex numbers, therefore we must reset BSPTree and vertsToPolys mapping array
        resetVertsToPolys();
        resetSafeVerts();
        resetBSPTree();

        return true;
    }

    /**
     * Helping function used only in method eliminateUnreachablePolygons
     *
     * @param pId
     * @param reachable
     */
    private void markAsReachableRecursive(int pId, boolean[] reachable) {
        if (reachable[pId]) {
            return;
        }
        reachable[pId] = true;
        ArrayList neighbours = getNeighbourIdsToPolygon(pId);
        for (int i = 0; i < neighbours.size(); i++) {
            markAsReachableRecursive((Integer) neighbours.get(i), reachable);
        }
    }

    /**
     * Creates off-mesh connections between polygons that does not share an
     * edge, but there is a connection from one to the other in navigation
     * graph. The endpoints are not necessarily polygons. Thay also may be
     * off-mesh navpoints. This method also creates list of off-mesh points and
     * also creates mappings between polygons and these points
     */
    protected void resetOffMeshConnections() {
        log.info("Creating off-mesh connections...");

        Map<UnrealId, OldOffMeshPoint> offPoints = new HashMap<UnrealId, OldOffMeshPoint>();

        // 1. get list of all navpoints
        Map<UnrealId, NavPoint> navPoints = nps;
        if (navPoints == null) {
            navPoints = worldView.get();
        }

        // offPoint definition: it has more than 0 offMeshEdges (outgoing or incoming)
        // 1. we act like if every navpoint was an offpoint and calculate all outgoing & incoming offedges
        // 2. any point with any (outgoing or incoming) out edges will be added to list of navmesh
        // for each navpoint create offpoint
        for (Map.Entry<UnrealId, NavPoint> entry : navPoints.entrySet()) {
            NavPoint np = entry.getValue();
            UnrealId uId = entry.getKey();
            int pId = this.getPolygonId(np.getLocation().asPoint3D());
            //LiftCenter is moving, and we want the bot to use the LiftExit NavPoint, when boarding lift, 
            //so we force the LiftCenter NavPoints from the navMesh by setting polygonId to -1
            if (np.isLiftCenter()) {
                pId = -1;
            }
            OldOffMeshPoint op = new OldOffMeshPoint(np, pId);
            offPoints.put(uId, op);
        }

        // now again - for each navpoint check his outgoing edges
        // if we find an offedge, we add it to both start and target
        for (Map.Entry<UnrealId, NavPoint> entry : navPoints.entrySet()) {
            NavPoint np = entry.getValue();
            UnrealId uId = entry.getKey();

            // check all outgoing edges
            for (Map.Entry<UnrealId, NavPointNeighbourLink> entry2 : np.getOutgoingEdges().entrySet()) {
                NavPointNeighbourLink link = entry2.getValue();
                UnrealId uId2 = entry2.getKey();

                NavPoint target = link.getToNavPoint();

                log.fine("Checking edge from navpoint " + uId + " to navpoint " + target.getId() + "...");

                // Flags. Important thing. Why?
                // Some edges will be considered as off-mesh immidietely, without checking mesh (lift)
                // Some edges will be ignored immidietely (ladder, swimming, etc.)
                // maybe put this code block into separate method/class/NavMeshConsts?
                boolean forceIgnore = false;
                boolean forceAdd = false;
                boolean addThisEdge = false;
                // point flags
                if (np.isLiftCenter()) {
                    forceAdd = true;
                }
                if (target.isLiftCenter()) {
                    forceAdd = true;
                }
                // edge flags
                //whether the edge is suitable for navigation
                forceIgnore = !UT2004EdgeChecker.checkLink(link);

                if (!forceAdd && !forceIgnore) {

                    // 2D projection of link
                    Line2D linkAsLine2D = new Line2D(link.getFromNavPoint().getLocation().x, link.getFromNavPoint().getLocation().y, link.getToNavPoint().getLocation().x, link.getToNavPoint().getLocation().y);

                    // how to decide if edge is offmesh?
                    // 1. start on the polygon of starting navpoinpoint (no polygon = offmesh)
                    // 2. while the current polygon (no polygon = offmesh) is not polygon of target repeat:
                    // 3. go to the neighbour polygon that is behind the edge that is crossed by our line                 
                    int currentPolygonId = getPolygonId(link.getFromNavPoint().getLocation().asPoint3D());
                    int targetPolygonId = this.getPolygonId(link.getToNavPoint().getLocation().asPoint3D());
                    int tabooPolygon = -1; // we are not searching backwards
                    while (currentPolygonId >= 0 && currentPolygonId != targetPolygonId) {
                        int newPolygon = -1;

                        // try all neighbours (except last one)
                        List<Integer> neighbours = this.getNeighbourIdsToPolygon(currentPolygonId);
                        for (Integer neighbour : neighbours) {
                            if (neighbour.intValue() == tabooPolygon) {
                                continue;
                            }

                            // find the shared edge
                            Line2D sharedEdge = null;
                            int[] polygon1 = getPolygon(currentPolygonId);
                            int[] polygon2 = getPolygon(neighbour);
                            for (int i = 0; i < polygon1.length; i++) {
                                int v1 = polygon1[i];
                                int v2 = polygon1[((i == polygon1.length - 1) ? 0 : i + 1)];
                                // polygon2 must contain both vertices
                                boolean containsV1 = false, containsV2 = false;
                                for (int j = 0; j < polygon2.length; j++) {
                                    if (polygon2[j] == v1) {
                                        containsV1 = true;
                                    }
                                    if (polygon2[j] == v2) {
                                        containsV2 = true;
                                    }
                                }
                                if (containsV1 && containsV2) {
                                    double[] vertex1 = this.getVertex(v1);
                                    double[] vertex2 = this.getVertex(v2);
                                    sharedEdge = new Line2D(vertex1[0], vertex1[1], vertex2[0], vertex2[1]);
                                }
                            }

                            // now we should have the shared edge or there is an error
                            if (sharedEdge == null) {
                                log.severe("Shared edge between polygon " + currentPolygonId + " and " + neighbour + " was not found!");
                            }

                            // does our examined edge cross the shared edge?
                            if (linkAsLine2D.getIntersection(sharedEdge) != null) {
                                log.fine("Crossed a line into polygon " + neighbour);
                                tabooPolygon = currentPolygonId;
                                newPolygon = neighbour;
                                break;
                            }
                        }
                        currentPolygonId = newPolygon;
                    }
                    // so now we either reached the target over the polygons, or we are off the mesh
                    // which one is it?
                    if (currentPolygonId >= 0) {
                        // path is inside navmesh
                        addThisEdge = false;
                    } else {
                        // path is off mesh
                        addThisEdge = true;
                    }
                } // end of checking path
                // else: we were forced to add/reject this edge
                else {
                    // ignoring has higher priority
                    if (forceAdd) {
                        addThisEdge = true;
                    }
                    if (forceIgnore) {
                        addThisEdge = false;
                    }
                }

                // will we add this edge?
                if (addThisEdge) {
                    log.fine("This edge is off-mesh: " + uId.getStringId() + " -> " + target.getId().getStringId());
                    OldOffMeshPoint op1 = offPoints.get(uId);
                    OldOffMeshPoint op2 = offPoints.get(target.getId());
                    OldOffMeshEdge oe = new OldOffMeshEdge(op1, op2, link);
                    op1.getOutgoingEdges().add(oe);
                    op2.getIncomingEdges().add(oe);
                } else {
                    log.finer("This edge is not off-mesh.");
                }
            }
        }

        // all edges from all navpoints are checked. now lets see how many off-mesh points do we have
        offMeshPoints = new ArrayList<OldOffMeshPoint>();
        int offCount = 0;
        for (OldOffMeshPoint op : offPoints.values()) {
            if (op.getOutgoingEdges().isEmpty() && op.getIncomingEdges().isEmpty()) {
                // nothing
            } else {
                offMeshPoints.add(op);
                offCount++;
            }
        }
        regenerateNavPointToOffMeshPointMap();
        log.warning("We found " + offCount + " offMeshPoints from total of " + offPoints.size() + " NavPoints.");

        // create mapping from polygons to offmesh points
        polysToOffMeshPoints = new ArrayList<ArrayList<OldOffMeshPoint>>();
        for (int i = 0; i < polys.size(); i++) {
            polysToOffMeshPoints.add(new ArrayList<OldOffMeshPoint>());
        }
        for (OldOffMeshPoint op : offMeshPoints) {
            int pId = op.getPId();
            if (pId >= 0) {
                polysToOffMeshPoints.get(pId).add(op);
            }
        }

        log.info("Off-mesh connections done.");
    }

    protected void saveNavMeshCore(String mapName) {
        String coreFileName = NavMeshConstants.processedMeshDir + File.separator + mapName + ".navmesh.processed";
        File coreFile = new File(coreFileName);

        log.info("Writing NavMesh core to a file: " + coreFile.getAbsolutePath());

        if (coreFile.exists()) {
            log.warning("NavMesh core file exist, rewriting: " + coreFile.getAbsolutePath());
            coreFile.delete();
        }

        coreFile.getParentFile().mkdirs();

        try {
            OldNavMeshCore core = new OldNavMeshCore();
            core.biggestLeafInTree = this.biggestLeafInTree;
            core.bspTree = this.bspTree;
            core.polys = this.polys;
            core.verts = this.verts;
            core.vertsToPolys = this.vertsToPolys;
            core.offMeshPoints = this.offMeshPoints;
            core.polysToOffMeshPoints = this.polysToOffMeshPoints;
            core.safeVertex = this.safeVertex;
            
            // STORE WITHIN CACHE FOR FUTURE REUSE
            OldNavMeshCoreCache.setNavMeshCore(mapName, core);
            
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(coreFile));
            out.writeObject(core);
            log.warning("NavMesh core binary file saved at: " + coreFile.getAbsolutePath());
        } catch (Exception e) {
            log.severe(ExceptionToString.process("Failed to write/serialize NavMesh core file to disk.", e));
        }
    }

    // =========
    // =========
    // UTILITIES
    // =========
    // =========

    /**
     * Decides whether the input point is inside of the polygon of navmesh
     * identified by its id.
     *
     * @param pId
     * @param point2D
     * @return
     */
    private boolean polygonContainsPoint(Integer pId, Point2D point2D) {
        boolean result = true;
        double rightSide = 0.0;

        int[] polygon = getPolygon(pId);
        double[] v1, v2;
        for (int i = 0; i < polygon.length; i++) {
            v1 = getVertex(polygon[i]);
            if (i < polygon.length - 1) {
                v2 = getVertex(polygon[i + 1]);
            } else {
                v2 = getVertex(polygon[0]);
            }
            Point2D p1 = new Point2D(v1[0], v1[1]);
            Point2D p2 = new Point2D(v2[0], v2[1]);
            StraightLine2D line = new StraightLine2D(p1, p2);
            double dist = line.getSignedDistance(point2D);

            if (rightSide == 0.0) {
                rightSide = Math.signum(dist);
            } else {
                if (rightSide * dist < 0) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    // =======================
    // =======================
    // IPathExecutor INTERFACE
    // =======================
    // =======================
    
    /**
     * Computes and returns a path between two points anywhere on the map. If no
     * such path is found, returns path of zero length;
     *
     * @param from
     * @param to
     * @return
     */
    @Override
    public IPathFuture<ILocated> computePath(ILocated from, ILocated to) {
        return new PrecomputedPathFuture<ILocated>(from, to, getPath(from, to));
    }
    
    @Override
    public double getDistance(ILocated from, ILocated to) {
    	IPathFuture<ILocated> path = computePath(from, to);
    	if (path.isDone()) {
    		List<ILocated> list = path.get();
    		if (list.size() == 0) return Double.POSITIVE_INFINITY;
    		ILocated location = list.get(0);
    		double result = 0;
    		for (int i = 1; i < list.size(); ++i) {
    			ILocated next = list.get(i);
    			result += location.getLocation().getDistance(next.getLocation());
    			location = next;
    		}
    		return result;
    	} else {
    		return Double.POSITIVE_INFINITY;
    	}
    }
    
    
    /**
     * Gets a List of polygons on which the path should go.
     *
     * @param fromAtom
     * @param toAtom
     * @return
     */
    public List<OldINavMeshAtom> getPolygonPath(OldINavMeshAtom fromAtom, OldINavMeshAtom toAtom) {
        // List of atoms from which we will always pick the one with shortest distance and expand ir
        List<OldAStarNode> pickable = new ArrayList<OldAStarNode>();
        // List of atoms, that are no longer pickable, because they have no more neighbours
        List<OldAStarNode> expanded = new ArrayList<OldAStarNode>();
        OldAStarNode firstNode = new OldAStarNode(null, fromAtom, this, fromAtom, toAtom);
        pickable.add(firstNode);

        // Let's search for toAtom!
        OldAStarNode targetNode = null;

        // target reach test = start and end atom are the same atom
        if (fromAtom.equals(toAtom)) {
            targetNode = firstNode;
        }

        while (targetNode == null) {

            // 1. if pickable is empty, there is no way
            if (pickable.isEmpty()) {
                return null;
            }

            // 2. find the most perspective node in pickable
            // that means that it has the shortest estimated total path length;
            OldAStarNode best = pickable.get(0);
            for (OldAStarNode node : pickable) {
                if (node.getEstimatedTotalDistance() < best.getEstimatedTotalDistance()) {
                    best = node;
                }
            }

            // 3. we expand the best node
            List<OldINavMeshAtom> neighbours = best.getAtom().getNeighbours(this);
            for (OldINavMeshAtom atom : neighbours) {
                boolean add = true;
                // if this atom is already in our expanded tree, we reject it?
                // TODO some optimalization for teleports
                for (OldAStarNode expNode : expanded) {
                    if (expNode.getAtom().equals(atom)) {
                        add = false;
                    }
                }
                // we add new neighbour
                if (add) {
                    OldAStarNode newNode = new OldAStarNode(best, atom, this, fromAtom, toAtom);
                    best.getFollowers().add(newNode);
                    pickable.add(newNode);
                    // target reach test
                    if (atom.equals(toAtom)) {
                        targetNode = newNode;
                    }
                }
            }
            // put expadned node into expanded
            pickable.remove(best);
            expanded.add(best);
        }

        // now we just return the path of atoms from start to end. We must build it from the end
        List<OldINavMeshAtom> path = new ArrayList<OldINavMeshAtom>();
        OldAStarNode node = targetNode;
        while (node != null) {
            path.add(node.getAtom());
            node = node.getFrom();
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Calls the method with the same name but polygons as arguments and returns
     * result
     *
     * @param from
     * @param to
     * @return
     */
    public List<OldINavMeshAtom> getPolygonPath(Location from, Location to) {
        OldINavMeshAtom fromAtom = getNearestAtom(from);
        OldINavMeshAtom toAtom = getNearestAtom(to);
        return getPolygonPath(fromAtom, toAtom);
    }

    /**
     * Counts a simple path from polygonPath. Only for testing use. rules for
     * adding points: for each new atom in path:
     *
     * 1. point -> point : just add the new point! :-) 2. point -> polygon : is
     * that point inside that polygon? a) yes : add nothing b) no : this should
     * not happen. offmesh points are connected to points inside. Add the
     * center... 3. polygon -> polygon : add point that is in middle of shared
     * line 4. polygon -> point : is that point inside that polygon? a) yes :
     * add the new point b) no : this should not happen. offmesh points are
     * connected to points inside. Add the new point anyway
     *
     * @param from
     * @param to
     * @param polygonPath
     * @return
     */
    private List<ILocated> getPolygonCentrePath(ILocated from, ILocated to, List<OldINavMeshAtom> polygonPath) {
        List path = new ArrayList<ILocated>();
        path.add(from);
        OldINavMeshAtom lastAtom = null;
        for (OldINavMeshAtom atom : polygonPath) {
            // * -> point
            if (atom.getClass() == OldOffMeshPoint.class) {
                NavPoint np = worldView.get().get( ((OldOffMeshPoint) atom).getNavPointId() );
                path.add(np);
            } else {
                // point -> polygon
                OldNavMeshPolygon polygon = (OldNavMeshPolygon) atom;

                if (lastAtom == null || lastAtom.getClass() == OldOffMeshPoint.class) {
                    OldOffMeshPoint op = (OldOffMeshPoint) lastAtom;

                    // was lastAtom inside this polygon?
                    boolean inside;
                    if (lastAtom == null) {
                        inside = polygonContainsPoint(polygon.getPolygonId(), new Point2D(from.getLocation().x, from.getLocation().y));
                    } else {
                        inside = false;
                        List<OldOffMeshPoint> offPs = polysToOffMeshPoints.get(polygon.getPolygonId());
                        for (OldOffMeshPoint op2 : offPs) {
                            if (op2.equals(op)) {
                                inside = true;
                            }
                        }
                    }

                    if (inside) {
                        // nothing
                    } else {
                        // add center
                        path.add(getLocation(atom));
                    }
                } // polygon -> polygon
                else {
                    // we must find the two shared points
                    OldNavMeshPolygon polygon2 = (OldNavMeshPolygon) lastAtom;
                    int[] p1 = this.getPolygon(polygon.getPolygonId());
                    int[] p2 = this.getPolygon(polygon2.getPolygonId());
                    int v1 = -1;
                    int v2 = -1;
                    outer:
                    for (int i = 0; i < p1.length; i++) {
                        for (int j = 0; j < p2.length; j++) {
                            if (p1[i] == p2[j]) {
                                if (v1 == -1) {
                                    v1 = p1[i];
                                } else {
                                    if (p1[i] != v1) {
                                        v2 = p1[i];
                                    }
                                    break outer;
                                }
                            }
                        }
                    }
                    double[] vv1 = getVertex(v1);
                    double[] vv2 = getVertex(v2);
                    path.add(new Location((vv1[0] + vv2[0]) / 2, (vv1[1] + vv2[1]) / 2, (vv1[2] + vv2[2]) / 2 + NavMeshConstants.liftPolygonLocation));
                }
            }
            lastAtom = atom;
        }
        path.add(to);
        return path;
    }

    /**
     * Computes shortest funneled path between given points from and to
     *
     * @param from
     * @param to
     * @param polygonPath
     * @return
     */
    private List<ILocated> getFunneledPath(ILocated from, ILocated to, List<OldINavMeshAtom> polygonPath) {
        List<ILocated> path = new ArrayList<ILocated>();
        path.add(from);
        OldINavMeshAtom lastAtom = null;
        OldINavMeshAtom atom = null;
        int index = -1;
        while (index < polygonPath.size() - 1) {
            index++;
            if (index > 0) {
                lastAtom = polygonPath.get(index - 1);
            } else {
                lastAtom = null;
            }
            atom = polygonPath.get(index);

            // * -> point
            if (atom.getClass() == OldOffMeshPoint.class) {
                NavPoint np = worldView.get().get( ((OldOffMeshPoint) atom).getNavPointId() );
                path.add(np);
            } // * -> polygon
            else {

                OldNavMeshPolygon polygon = (OldNavMeshPolygon) atom;
                // point -> polygon
                if (lastAtom == null || lastAtom.getClass() == OldOffMeshPoint.class) {
                    OldOffMeshPoint op = (OldOffMeshPoint) lastAtom;

                    // was lastAtom inside this polygon?
                    boolean inside;
                    if (lastAtom == null) {
                        inside = polygonContainsPoint(polygon.getPolygonId(), new Point2D(from.getLocation().x, from.getLocation().y));
                    } else {
                        inside = false;
                        List<OldOffMeshPoint> offPs = polysToOffMeshPoints.get(polygon.getPolygonId());
                        for (OldOffMeshPoint op2 : offPs) {
                            if (op2.equals(op)) {
                                inside = true;
                            }
                        }
                    }

                    if (inside) {
                        // nothing
                    } else {
                        // add center
                        path.add(getLocation(atom));
                    }
                } // polygon -> polygon
                else {
                    // here comes the funneling

                    // point from which we are starting = last point in path
                    ILocated start = path.get(path.size() - 1);

                    // we must find the 'gateway'
                    OldNavMeshPolygon polygon2 = (OldNavMeshPolygon) lastAtom;
                    int[] p1 = this.getPolygon(polygon.getPolygonId());
                    int[] p2 = this.getPolygon(polygon2.getPolygonId());
                    int v1 = -1;
                    int v2 = -1;
                    outer:
                    for (int i = 0; i < p1.length; i++) {
                        for (int j = 0; j < p2.length; j++) {
                            if (p1[i] == p2[j]) {
                                if (v1 == -1) {
                                    v1 = p1[i];
                                } else {
                                    if (p1[i] != v1) {
                                        v2 = p1[i];
                                    }
                                    break outer;
                                }
                            }
                        }
                    }
                    double[] vv1 = getVertex(v1);
                    double[] vv2 = getVertex(v2);
                    Line2D gateway = new Line2D(vv1[0], vv1[1], vv2[0], vv2[1]);
                    // gateway found

                    if ((start.getLocation().x == vv2[0] && start.getLocation().y == vv2[1])
                            || (start.getLocation().x == vv1[0] && start.getLocation().y == vv1[1])) {
                        log.fine("We are already in the next polygon. No comparation, let's just continue.");
                        continue;
                    }

                    // !!! recgonize left and right correctly !!!
                    double dist = gateway.getSignedDistance(start.getLocation().x, start.getLocation().y);
                    // create left and right mantinel

                    Line2D leftMantinel = new Line2D(start.getLocation().x, start.getLocation().y, vv2[0], vv2[1]);
                    Line2D rightMantinel = new Line2D(start.getLocation().x, start.getLocation().y, vv1[0], vv1[1]);
                    if (dist < 0) {
                        Line2D swap = leftMantinel;
                        leftMantinel = rightMantinel;
                        rightMantinel = swap;
                        vv1 = getVertex(v2);
                        vv2 = getVertex(v1);
                        gateway = new Line2D(vv1[0], vv1[1], vv2[0], vv2[1]);
                    }
                    // now left and right mantinel are set correctly                                        

                    int leftMantinelIndex = index;
                    double leftMantinelZ = vv2[2];
                    Location leftMantinelTarget;
                    if (safeVertex.get(v2)) {
                        leftMantinelTarget = new Location(vv2[0], vv2[1], vv2[2] + NavMeshConstants.liftPolygonLocation);
                    } else {
                        if (gateway.getLength() <= 2 * NavMeshConstants.agentRadius) {
                            leftMantinelTarget = new Location((vv2[0] + vv1[0]) / 2, (vv2[1] + vv1[1]) / 2, (vv2[2] + vv1[2]) / 2 + NavMeshConstants.liftPolygonLocation);
                        } else {
                            leftMantinelTarget = new Location(vv2[0] + (vv1[0] - vv2[0]) / gateway.getLength() * NavMeshConstants.agentRadius,
                                    vv2[1] + (vv1[1] - vv2[1]) / gateway.getLength() * NavMeshConstants.agentRadius,
                                    vv2[2] + (vv1[2] - vv2[2]) / gateway.getLength() * NavMeshConstants.agentRadius + NavMeshConstants.liftPolygonLocation);
                        }
                    }

                    int rightMantinelIndex = index;
                    double rightMantinelZ = vv1[2];
                    Location rightMantinelTarget;
                    if (safeVertex.get(v1)) {
                        rightMantinelTarget = new Location(vv1[0], vv1[1], vv1[2] + NavMeshConstants.liftPolygonLocation);
                    } else {
                        if (gateway.getLength() <= 2 * NavMeshConstants.agentRadius) {
                            rightMantinelTarget = new Location((vv2[0] + vv1[0]) / 2, (vv2[1] + vv1[1]) / 2, (vv2[2] + vv1[2]) / 2 + NavMeshConstants.liftPolygonLocation);
                        } else {
                            rightMantinelTarget = new Location(vv1[0] + (vv2[0] - vv1[0]) / gateway.getLength() * NavMeshConstants.agentRadius,
                                    vv1[1] + (vv2[1] - vv1[1]) / gateway.getLength() * NavMeshConstants.agentRadius,
                                    vv1[2] + (vv2[2] - vv1[2]) / gateway.getLength() * NavMeshConstants.agentRadius + NavMeshConstants.liftPolygonLocation);
                        }
                    }

                    // now we will go further over the polygons until the mantinels cross or until we find target point inside funnel
                    boolean targetAdded = false;
                    boolean outOfMantinels = false;
                    boolean endOfPolygonPathReached = false;
                    while (!targetAdded && !outOfMantinels) {
                        index++;
                        lastAtom = polygonPath.get(index - 1);
                        if (index < polygonPath.size()) {
                            atom = polygonPath.get(index);
                        } // if we are at the end, we dont need an atom
                        else {
                            endOfPolygonPathReached = true;
                        }
                        // last atom surely was polygon because we are in polygon->polygon branch
                        polygon2 = (OldNavMeshPolygon) lastAtom;

                        // new atom is point - potential end of algorithm
                        // also go this way if we reached the last polygon
                        // every command has an alternative for this special option
                        if (endOfPolygonPathReached || atom.getClass() == OldOffMeshPoint.class) {
                            NavPoint np = null;
                            if (!endOfPolygonPathReached) {
                                np = worldView.get().get( ((OldOffMeshPoint) atom).getNavPointId() );
                            }

                            ILocated target;
                            if (endOfPolygonPathReached) {
                                target = to;
                            } else {
                                target = np;
                            }

                            // is np inside funnel?
                            // compare with left mantinel:
                            Line2D virtualGateway1 = new Line2D(target.getLocation().x, target.getLocation().y, leftMantinel.p2.x, leftMantinel.p2.y);
                            dist = virtualGateway1.getSignedDistance(start.getLocation().x, start.getLocation().y);
                            if (dist < 0) {
                                // point is out from left mantinel. we must go to corner of left mantinel and continue from there
                                //path.add(new Location(leftMantinel.p2.x, leftMantinel.p2.y, leftMantinelZ+NavMeshConstants.liftPolygonLocation));
                                path.add(leftMantinelTarget);
                                // we will now 'restart' funneling algorithm - continue from this point and its polygon 
                                outOfMantinels = true;
                                index = leftMantinelIndex;
                            } else {
                                // point is inside left mantinel - Ok
                                // check the right mantinel
                                Line2D virtualGateway2 = new Line2D(rightMantinel.p2.x, rightMantinel.p2.y, target.getLocation().x, target.getLocation().y);
                                dist = virtualGateway2.getSignedDistance(start.getLocation().x, start.getLocation().y);
                                if (dist < 0) {
                                    // point is out from right mantinel. we must go to corner of right mantinel and continue from there
                                    //path.add(new Location(rightMantinel.p2.x, rightMantinel.p2.y, rightMantinelZ+NavMeshConstants.liftPolygonLocation));
                                    path.add(rightMantinelTarget);
                                    // we will now 'restart' funneling algorithm - continue from this point and its polygon 
                                    outOfMantinels = true;
                                    //Possible fix: adding rightMantinelTarget and RIGHT mantinelIndex 
                                    index = rightMantinelIndex;
                                } else {
                                    // point is inside the maninels. that is great - we successfully finnished this funnelling
                                    if (!endOfPolygonPathReached) {
                                        path.add(np);
                                    }
                                    targetAdded = true;
                                }
                            }
                        } // new atom is polygon again
                        else {
                            polygon = (OldNavMeshPolygon) atom;
                            Point2D middleOfOldGateway = new Point2D((gateway.p1.x + gateway.p2.x) / 2, (gateway.p1.y + gateway.p2.y) / 2);
                            // find new gateway
                            p1 = this.getPolygon(polygon.getPolygonId());
                            p2 = this.getPolygon(polygon2.getPolygonId());
                            v1 = -1;
                            v2 = -1;
                            outer:
                            for (int i = 0; i < p1.length; i++) {
                                for (int j = 0; j < p2.length; j++) {
                                    if (p1[i] == p2[j]) {
                                        if (v1 == -1) {
                                            v1 = p1[i];
                                        } else {
                                            if (p1[i] != v1) {
                                                v2 = p1[i];
                                            }
                                            break outer;
                                        }
                                    }
                                }
                            }
                            vv1 = getVertex(v1);
                            vv2 = getVertex(v2);
                            gateway = new Line2D(vv1[0], vv1[1], vv2[0], vv2[1]);
                            // decide which endpoint of gateway is on the left side and which is on the right side
                            // if the gateway is directed correctly, than the middle of old gateway should be on the left side of it
                            dist = gateway.getSignedDistance(middleOfOldGateway);
                            if (dist < 0) {
                                vv1 = getVertex(v2);
                                vv2 = getVertex(v1);
                                gateway = new Line2D(vv1[0], vv1[1], vv2[0], vv2[1]);
                            }
                            // gateway found

                            // try to update mantinels
                            // left mantinel
                            dist = leftMantinel.getSignedDistance(gateway.p2);
                            // if the point is inside, it should be right from left mantinel
                            if (dist < 0) {
                                // ok, it is right from left mantinel
                                // now check if the new mantinel would cross right mantinel
                                dist = rightMantinel.getSignedDistance(gateway.p2);
                                // if the point is inside, it should be on the left
                                if (dist > 0) {
                                    // ok, left point is inside funnel. we can narrow the funnel
                                    leftMantinel = new Line2D(leftMantinel.p1, gateway.p2);
                                    leftMantinelIndex = index;
                                    leftMantinelZ = vv2[2];
                                    if (safeVertex.get(v2)) {
                                        leftMantinelTarget = new Location(vv2[0], vv2[1], vv2[2] + NavMeshConstants.liftPolygonLocation);
                                    } else {
                                        if (gateway.getLength() <= 2 * NavMeshConstants.agentRadius) {
                                            leftMantinelTarget = new Location((vv2[0] + vv1[0]) / 2, (vv2[1] + vv1[1]) / 2, (vv2[2] + vv1[2]) / 2 + NavMeshConstants.liftPolygonLocation);
                                        } else {
                                            leftMantinelTarget = new Location(vv2[0] + (vv1[0] - vv2[0]) / gateway.getLength() * NavMeshConstants.agentRadius,
                                                    vv2[1] + (vv1[1] - vv2[1]) / gateway.getLength() * NavMeshConstants.agentRadius,
                                                    vv2[2] + (vv1[2] - vv2[2]) / gateway.getLength() * NavMeshConstants.agentRadius + NavMeshConstants.liftPolygonLocation);
                                        }
                                    }
                                } else {
                                    // there is a cross! left mantinel would cross the right one!
                                    // we will restart funneling and continue from corner of right mantinel
                                    //path.add(new Location(rightMantinel.p2.x, rightMantinel.p2.y, rightMantinelZ+NavMeshConstants.liftPolygonLocation));
                                    path.add(rightMantinelTarget);
                                    outOfMantinels = true;
                                    index = rightMantinelIndex;
                                }
                            } else {
                                // point is left from left mantinel.
                                // we cannot do anything.
                                // the mantinel stays where it is
                            }

                            // now the right mantinel
                            dist = rightMantinel.getSignedDistance(gateway.p1);
                            // if the point is inside, it should be left from right mantinel
                            if (dist > 0) {
                                // ok, it is left from right mantinel

                                // btw this is impossible if the left mantinel is already crossing the right.
                                // now check if the new mantinel would cross left mantinel
                                dist = leftMantinel.getSignedDistance(gateway.p1);
                                // if the point is inside, it should be on the right
                                if (dist < 0) {
                                    // ok, right point is inside funnel. we can narrow the funnel
                                    rightMantinel = new Line2D(rightMantinel.p1, gateway.p1);
                                    rightMantinelIndex = index;
                                    rightMantinelZ = vv1[2];
                                    if (safeVertex.get(v1)) {
                                        rightMantinelTarget = new Location(vv1[0], vv1[1], vv1[2] + NavMeshConstants.liftPolygonLocation);
                                    } else {
                                        if (gateway.getLength() <= 2 * NavMeshConstants.agentRadius) {
                                            rightMantinelTarget = new Location((vv2[0] + vv1[0]) / 2, (vv2[1] + vv1[1]) / 2, (vv2[2] + vv1[2]) / 2 + NavMeshConstants.liftPolygonLocation);
                                        } else {
                                            rightMantinelTarget = new Location(vv1[0] + (vv2[0] - vv1[0]) / gateway.getLength() * NavMeshConstants.agentRadius,
                                                    vv1[1] + (vv2[1] - vv1[1]) / gateway.getLength() * NavMeshConstants.agentRadius,
                                                    vv1[2] + (vv2[2] - vv1[2]) / gateway.getLength() * NavMeshConstants.agentRadius + NavMeshConstants.liftPolygonLocation);
                                        }
                                    }
                                } else {
                                    // there is a cross! right mantinel would cross the left one!
                                    // we will restart funneling and continue from corner of left mantinel
                                    //path.add(new Location(leftMantinel.p2.x, leftMantinel.p2.y, leftMantinelZ+NavMeshConstants.liftPolygonLocation));
                                    path.add(leftMantinelTarget);
                                    outOfMantinels = true;
                                    index = leftMantinelIndex;
                                }
                            } else {
                                // point is right from right mantinel.
                                // we cannot do anything.
                                // the mantinel stays where it is
                            }
                        }
                    }
                }
            }
        }
        path.add(to);
        return path;
    }

    /**
     * Computes and returns a path between two points anywhere on the map. If no
     * such path is found, returns null;
     *
     * @param from
     * @param to
     * @return
     */
    public List<ILocated> getPath(ILocated from, ILocated to) {

        List<OldINavMeshAtom> polygonPath = null;

        //Look for possible teleport paths...
        if (nps == null) {
            initNavPoints();
        }
        if (isFwMapAvailable && nps != null) {

            NavPoint fromNp = DistanceUtils.getNearest(nps.values(), from);
            NavPoint toNp = DistanceUtils.getNearest(nps.values(), to);

            if (hasTeleports) {
                List<NavPoint> path = fwMap.getPath(fromNp, toNp);
                if (path != null) {
                    OldINavMeshAtom atomFrom = getNearestAtom(from.getLocation());
                    boolean skip = false;

                    for (NavPoint np : path) {
                        if (skip) {

                            atomFrom = getNearestOffmeshPoint(np.getLocation());
                            skip = false;

                        } else if (np.isTeleporter()) {

                            OldINavMeshAtom atomTo = getNearestOffmeshPoint(np.getLocation());
                            List<OldINavMeshAtom> pathPart = getPolygonPath(atomFrom, atomTo);
                            if (pathPart == null) {
                                polygonPath = null;
                                break;
                            }
                            if (polygonPath == null) {
                                polygonPath = pathPart;
                            } else {
                                polygonPath.addAll(pathPart);
                            }
                            skip = true;
                        }
                    }

                    if (polygonPath != null) {
                        OldINavMeshAtom atomTo = getNearestAtom(to.getLocation());
                        List<OldINavMeshAtom> pathPart = getPolygonPath(atomFrom, atomTo);
                        if (pathPart != null) {
                            polygonPath.addAll(pathPart);
                        } else {
                            polygonPath = null;
                        }
                    }
                }
            }
        }

        // first we found a list of polygons and off-mesh connections on the path
        // using A* algorithm
        if (polygonPath == null) {
            polygonPath = getPolygonPath(from.getLocation(), to.getLocation());
        }
        if (polygonPath == null) {
            return null;
        }

        //this.drawPolygonPath(polygonPath, new Location(255,255,0));
        List<ILocated> path;

        // now we transform path made of polygons to path made of Locations        
        // path = getPolygonCentrePath(from, to, polygonPath);      
        path = getFunneledPath(from, to, polygonPath);

        return path;
    }

    private OldINavMeshAtom getNearestAtom(Location location) {
        return getNearestAtom(location, true);
    }

    public OldNavMeshPolygon getNearestPolygon(Location location) {
        return (OldNavMeshPolygon) getNearestAtom(location, false);
    }

    /**
     * Returns the nearest NavMeshAtom to given location
     *
     * @param location
     * @return
     */
    private OldINavMeshAtom getNearestAtom(Location location, boolean includeOffMeshPoints) {

        // if this point is on a polygon we return this polygon
        int pId = getPolygonId(location);
        if (pId >= 0) {
            return new OldNavMeshPolygon(pId);
        } else {
            // we return nearest offmeshPoint
            // TODO: be smarter! count in polygons too!

            //TODO: Possibly save polygon centres?
            OldINavMeshAtom nearest = null;
            if (includeOffMeshPoints) {
                nearest = getNearestOffmeshPoint(location);
            }
            double minDist = Double.MAX_VALUE;
            // if there are no offmeshpoints, return nearest polygon
            // this is slow and it should not happen often
            if (nearest == null) {
                for (int i = 0; i < polys.size(); i++) {
                    OldNavMeshPolygon p = new OldNavMeshPolygon(i);
                    Location pl = getLocation(p);
                    double dist = location.getDistance(pl);
                    if (dist < minDist) {
                        nearest = p;
                        minDist = dist;
                    }
                }
            }

            return nearest;
        }
    }

    /**
     * Returns distance between two atoms (euclidean distance) If the atom is a
     * polygon, this method takes its middle
     *
     * @param atom1
     * @param atom2
     * @return
     */
    public double getDistance(OldINavMeshAtom atom1, OldINavMeshAtom atom2) {
        if (atom1.equals(atom2)) {
            return 0.0;
        }
        Location l1, l2;
        l1 = getLocation(atom1);
        l2 = getLocation(atom2);
        return l1.getDistance(l2);
    }

    /**
     * Empty method it is nver really called for interface atom. always for
     * polygon or point
     *
     * @param atom1
     * @return
     */
    private Location getLocation(OldINavMeshAtom atom1) {
        if (atom1.getClass() == OldOffMeshPoint.class) {
            return getLocation((OldOffMeshPoint) atom1);
        }
        if (atom1.getClass() == OldNavMeshPolygon.class) {
            return getLocation((OldNavMeshPolygon) atom1);
        }
        throw new UnsupportedOperationException("Not implemented. Not now. Not ever.");
    }

    /**
     * Returns location of the contained navpoint
     *
     * @param op
     * @return
     */
    private Location getLocation(OldOffMeshPoint op) {
        NavPoint np = worldView.get().get(op.getNavPointId());
        return np.getLocation();
    }

    /**
     * Returns the middle point of the polygon
     *
     * @param p
     * @return
     */
    public Location getLocation(OldNavMeshPolygon p) {
        int[] polygon = this.getPolygon(p.getPolygonId());
        double sumX = 0.0;
        double sumY = 0.0;
        double sumZ = 0.0;
        for (int i = 0; i < polygon.length; i++) {
            double[] v = getVertex(polygon[i]);
            sumX += v[0];
            sumY += v[1];
            sumZ += v[2];
        }
        return new Location(sumX / polygon.length, sumY / polygon.length, (sumZ / polygon.length) + NavMeshConstants.liftPolygonLocation);
    }

    /**
     * A simple implementation of NavMesh's 2D raycasting. Returns distance from
     * the edge of navmesh in a direction from a location if the entrire ray is
     * inside navmesh of there is no navmesh it returns 0;
     *
     * @param location
     * @param vector
     * @return
     */
    public double getDistanceFromEdge(Location location, Vector2d vector, double rayLength) {
        if (rayLength <= 0) {
            return 0;
        }

        // get the end location (in 2D)
        vector.normalize();
        vector.x *= rayLength;
        vector.y *= rayLength;
        Location end = new Location(location.x + vector.x, location.y + vector.y);

        // get a 2D projection of ray
        Line2D ray = new Line2D(location.x, location.y, end.x, end.y);
        // get the current polygon
        int pId = this.getPolygonId(location);
        if (pId < 0) {
            return 0;
        }

        // how to find end of navmesh?
        // 1. start on the polygon of starting location
        // 2. find where the line crosses its border
        // 3. while there is another polyogn behind, repeat
        // 4. return the last cross (its distance from location)
        int currentPolygonId = pId;
        int lastPolygonId = -1;
        int nextPolygonId = -1;

        // find the first cross
        Point2D cross = null;
        int v1 = -1, v2 = -1;
        int[] polygon = getPolygon(currentPolygonId);
        for (int i = 0; i < polygon.length; i++) {
            v1 = polygon[i];
            v2 = polygon[((i == polygon.length - 1) ? 0 : i + 1)];
            double[] vertex1 = getVertex(v1);
            double[] vertex2 = getVertex(v2);
            Line2D edge = new Line2D(vertex1[0], vertex1[1], vertex2[0], vertex2[1]);
            cross = ray.getIntersection(edge);
            if (cross != null) {
                if (cross.x <= Math.max(edge.p1.x, edge.p2.x) && cross.x >= Math.min(edge.p1.x, edge.p2.x)
                        && cross.x <= Math.max(ray.p1.x, ray.p2.x) && cross.x >= Math.min(ray.p1.x, ray.p2.x)) {
                    // int's a cross!
                    break;
                } else {
                    // its not a cross
                    cross = null;
                }
            }
        }
        // there is no polygon. we return distance of cross
        double distanceToPolygonEdge = ray.p1.getDistance(cross);
        // now we have the cross.
        // if it too far from location, we return 0;
        if (cross == null || distanceToPolygonEdge >= rayLength) {
            return 0;
        }

        // is there another polygon behind?
        nextPolygonId = getNeighbourPolygon(currentPolygonId, v1, v2);
        if (nextPolygonId == -1) {
            // there is no polygon. we return distance of cross
            return distanceToPolygonEdge;
        } else {
            // if there is another polygon, we return recursively distance from egde in that direction
            // move a little so it is in the neighbour polygon
            vector = ((Vector2d) vector.clone());
            vector.normalize();
            Location crossLocation = new Location(cross.x + vector.x, cross.y + vector.y, location.z);
            //TODO: Fixed ERROR? Should be DISTANCE_TO_CROSS + getDistanceFromEdge() ???
            return distanceToPolygonEdge + getDistanceFromEdge(crossLocation, vector, rayLength - distanceToPolygonEdge);

        }
    }

    /**
     * Returns distance of the location from the navmesh's edge in the given
     * direction. If location is not not on navmesh, 0 is returned
     *
     * @param location
     * @param vector
     * @return
     */
    public double getDistanceFromEdge(Location location, Vector2d vector) {
        //CHANGED: Originally Double.MAX_VALUE -> overflow in called method.
        return getDistanceFromEdge(location, vector, 10000);
    }

    /**
     * Finds neighbour behind given vertexes. Returns polygon id or -1 there is
     * none
     *
     * @param currentPolygonId
     * @param v1
     * @param v2
     * @return
     */
    public int getNeighbourPolygon(int currentPolygonId, int v1, int v2) {
        // try all neighbours (except last one)
        List<Integer> neighbours = this.getNeighbourIdsToPolygon(currentPolygonId);
        for (Integer neighbour : neighbours) {
            // find the shared edge
            int[] polygon2 = getPolygon(neighbour);
            // polygon2 must contain both vertices
            boolean containsV1 = false, containsV2 = false;
            for (int j = 0; j < polygon2.length; j++) {
                if (polygon2[j] == v1) {
                    containsV1 = true;
                }
                if (polygon2[j] == v2) {
                    containsV2 = true;
                }
            }

            if (containsV1 && containsV2) {
                return neighbour;
            }
        }
        // no good neighbour was found
        return -1;
    }

    public OldNavMeshBSPNode getBiggestLeafInTree() {
        return biggestLeafInTree;
    }

   
//    public void load(GameInfo single, Map<UnrealId, NavPoint> navPoints, boolean shouldRel) {
//        this.nps = navPoints;
//
//        load(single);
//    }

    public void setFwMap(FloydWarshallMap fwMap) {
        this.fwMap = fwMap;
        isFwMapAvailable = fwMap != null;
    }

    private void initNavPoints() {
        nps = worldView.get();
        if (nps != null) {
            for (NavPoint np : nps.values()) {
                if (np.isTeleporter()) {
                    hasTeleports = true;
                    break;
                }
            }
        }
    }

    private OldINavMeshAtom getNearestOffmeshPoint(Location location) {
        OldINavMeshAtom nearest = null;
        double minDist = Double.MAX_VALUE;

        for (OldOffMeshPoint op : offMeshPoints) {
            double dist = location.getDistance(op.getLocation());
            if (dist < minDist) {
                nearest = op;
                minDist = dist;
            }
        }
        return nearest;
    }
    
    /**
     * Reseting {@link OldNavMesh} instance, completely "unloading" the NavMesh. 
     */
    public void clear() {
        log.warning("NavMesh has been cleared...");

        verts = new ArrayList<double[]>();
        polys = new ArrayList<int[]>();
        vertsToPolys = null;
        safeVertex = null;
        bspTree = null;
        biggestLeafInTree = null;
        offMeshPoints = null;
        navPointToOffMeshPoint = null;
        polysToOffMeshPoints = null;

        loaded = false;
        loadedForMap = null;
    }

	public ArrayList<OldNavMeshPolygon> getAllPolygons() {
		ArrayList<OldNavMeshPolygon> retval = new ArrayList<OldNavMeshPolygon>(polys.size());
		for (int i=0; i<polys.size(); ++i) {
			retval.add(new OldNavMeshPolygon(i));
		}
		return retval;
	}

	public OldOffMeshPoint getOffMeshPointsByNavPoint(NavPoint navPoint) {
		return navPointToOffMeshPoint.get(navPoint.getId());
	}
	
	protected void regenerateNavPointToOffMeshPointMap() {
		navPointToOffMeshPoint = new HashMap<UnrealId,OldOffMeshPoint>();
		for ( OldOffMeshPoint offMeshPoint : offMeshPoints ) {
			navPointToOffMeshPoint.put( offMeshPoint.getNavPointId(), offMeshPoint);
		}
	}
	
	public OldNavMeshPolygon getNearestPolygon(NavPoint navPoint) {
		if ( !navPointToNearestPolygon.containsKey(navPoint.getId()) ) {
			navPointToNearestPolygon.put(navPoint.getId(), getNearestPolygon(navPoint.getLocation()));
		}
		
		return navPointToNearestPolygon.get(navPoint.getId());
	}
}
