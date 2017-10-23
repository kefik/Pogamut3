package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing;

import java.util.List;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathFuture;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldINavMeshAtom;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldOffMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldOffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DrawStayingDebugLines;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import math.geom3d.Point3D;

public class NavMeshDraw extends UT2004Draw {

	private OldNavMesh navMesh;

	public NavMeshDraw(OldNavMesh navMesh, Logger log, IUT2004ServerProvider serverProvider) {
		super(log, serverProvider);
		this.navMesh = navMesh;
	}
	
	/**
     * Draws navmesh in game.
     * 
     * @param drawMesh whether to draw mesh polygons
     * @param drawOffMeshLinks whether to draw off-mesh-links
     */   
    public boolean draw(boolean drawMesh, boolean drawOffMeshLinks) {
    	if (navMesh == null || !navMesh.isLoaded()) return false;
    	
    	if (drawMesh) {
    	    // draw polygons
	        for(int i = 0; i < navMesh.polyCount(); i++) {
	            DrawStayingDebugLines d = new DrawStayingDebugLines();
	            String lines = this.polygonToDebugString(i);
	            d.setVectors(lines);
	            d.setColor(new Location(255, 255, 255));
	            d.setClearAll(i==0);                
	            getServer().getAct().act(d);                
	        }
    	}
        
        if (drawOffMeshLinks) {
	        // draw off-mesh connections
	        for(OldOffMeshPoint op : navMesh.getOffMeshPoints()) {
	            // draw all outgoing edges
	            for(OldOffMeshEdge oe : op.getOutgoingEdges()) {
	                DrawStayingDebugLines d = new DrawStayingDebugLines();
	                String lines = offMeshEdgeToDebugString(oe);
	                d.setVectors(lines);
	                d.setColor(getColorForOffMeshConnection(oe, getServer()));
	                d.setClearAll(false);                
	                getServer().getAct().act(d);
	            }
	        }
        }
        
        return true;
    }
    
    /**
     * Draws given lines
     * @param lines 
     * @param color 
     */
    public void draw(String lines, Location color) {
        DrawStayingDebugLines d = new DrawStayingDebugLines();
        d.setVectors(lines);
        d.setColor(color);
        d.setClearAll(false);                
        getServer().getAct().act(d);  
    }
    
    /**
     * Undraws all currently drawn lines
     */
    public void unDraw() {
        DrawStayingDebugLines d = new DrawStayingDebugLines();
        d.setClearAll(true);                
        getServer().getAct().act(d);        
    }
    
    /**
     * Draws one polygon
     * with the color set in color
     * @param i
     * @param color
     */   
    public void drawOnePolygon(int i, Location color) {
        DrawStayingDebugLines d = new DrawStayingDebugLines();
        String lines = this.polygonToDebugString(i);
        d.setVectors(lines);
        d.setColor(color);
        d.setClearAll(false);                
        getServer().getAct().act(d);                           
    } 
    
    /**
     * Draws one polygon
     * with default color (yellow)
     * @param i
     */   
    public void drawOnePolygon(int i) {
        drawOnePolygon(i, new Location(255,255,0));                         
    }    
    
    /**
     * Draws one atom (polygon or point)
     * @param atom
     * @param location 
     */
    private void drawAtom(OldINavMeshAtom atom, Location location) {
        if(atom.getClass()==OldNavMeshPolygon.class) {
            OldNavMeshPolygon p = (OldNavMeshPolygon) atom;
            drawOnePolygon(p.getPolygonId(), location);
        }
    }
    
    /**
     * Draws entire list of polygons
     * @param polygonPath 
     * @param location 
     */
    public void drawPolygonPath(List<OldINavMeshAtom> polygonPath, Location location) {
        for(OldINavMeshAtom atom : polygonPath) {
            drawAtom(atom, location);
        }
    }
    
    /**
     * Draws the given path 
     * @param path 
     * @param color 
     */
    public void drawPath(IPathFuture<ILocated> path, Location color) {
        
        // the commented code sometimes doesnt work for soem reason. maybe there is a corrupted point along the path?
        //String lines = pathToDebugString(path);
        //System.out.println("path to be drawn: " + lines);
        //draw(lines,color);
        List<ILocated> pathList = path.get();
        for(int i = 0; i<pathList.size()-1; i++) {
            StringBuilder lines = new StringBuilder();
            lines.append(pathList.get(i).getLocation().x).append(",");
            lines.append(pathList.get(i).getLocation().y).append(",");
            lines.append(pathList.get(i).getLocation().z + 40).append(";");
            lines.append(pathList.get(i+1).getLocation().x).append(",");
            lines.append(pathList.get(i+1).getLocation().y).append(",");
            lines.append(pathList.get(i+1).getLocation().z + 40).append("");
            draw(lines.toString(), color);
        }       
    }
    
     /**
     * Debug method:  
     * Draws only the polygons in the biggest leaf
     * so that we can see why they could not have been split any further
     */   
    public void drawOnlyBiggestLeaf() {
        for(int i = 0; i < navMesh.polyCount(); i++) {          
            if(!navMesh.getBiggestLeafInTree().polys.contains(i)) continue;
            
            DrawStayingDebugLines d = new DrawStayingDebugLines();
            String lines = this.polygonToDebugString(i);
            //System.out.println("polygon["+i+"] lines: " + lines);
            d.setVectors(lines);
            d.setColor(new Location(255, 255, 0));
            d.setClearAll(false);                
            getServer().getAct().act(d);
        }    
    }
    
    // ===========
    // DEBUG STUFF
    // ===========
    
	/**
     * Returns all lines in one long string.
     */
    public String toDebugString() {
        StringBuilder result = new StringBuilder("");      
        // projdeme vsechny polygony a vykreslime caru vzdy z vrcholu n do n+1
        for(int i = 0; i < navMesh.getPolys().size(); i++) {
            int[] p = navMesh.getPolys().get(i);
            for(int j = 0; j<p.length; j++) {
                if(result.length()>0) result.append(";");
                // ziskame vrcholy v1 a v2 mezi kterymi vykreslime caru
                double[] v1,v2;
                v1 = navMesh.getVerts().get(p[j]);
                if(j==p.length-1) v2 = navMesh.getVerts().get(p[0]);
                else v2 = navMesh.getVerts().get(p[j+1]);
                // pridani cary
                result.append(v1[0]+","+v1[1]+","+v1[2]+";"+v2[0]+","+v2[1]+","+v2[2]);
            }
        }     
        return result.toString();
    }
    
    /**
     * Restricted alternative to toDebugString() - returns only one polygon as string
     */
    public String polygonToDebugString(int polygonNumber) {
        StringBuilder result = new StringBuilder("");      
        // projdeme vsechny polygony a vykreslime caru vzdy z vrcholu n do n+1
        int[] p = navMesh.getPolys().get(polygonNumber);
        for(int j = 0; j<p.length; j++) {
            if(result.length()>0) result.append(";");
            // ziskame vrcholy v1 a v2 mezi kterymi vykreslime caru
            double[] v1,v2;
            v1 = navMesh.getVerts().get(p[j]);
            if(j==p.length-1) v2 = (double[]) navMesh.getVerts().get(p[0]);
            else v2 = navMesh.getVerts().get(p[j+1]);
            // pridani cary
            result.append(v1[0]+","+v1[1]+","+(v1[2] + 30)+";"+v2[0]+","+v2[1]+","+(v2[2] + 30));
        }     
        return result.toString();    
    }
    
    /**
     * computes debug string of one off-mesh edge to be drawn.
     * @param oe
     * @return 
     */
    private String offMeshEdgeToDebugString(OldOffMeshEdge oe) {
        StringBuilder result = new StringBuilder("");      
        Location l1 = oe.getFrom().getLocation();
        Location l2 = oe.getTo().getLocation();
        result.append(l1.x+","+l1.y+","+l1.z+";"+l2.x+","+l2.y+","+l2.z);
        
        // add arrow at the end
       double[] vector = new double[3];
       vector[0] = l1.x - l2.x;
       vector[1] = l1.y - l2.y;
       vector[2] = l1.z - l2.z;
       // normalize the vector to small lenght
       double length = Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]);
       vector[0] *= 1/length * 20;
       vector[1] *= 1/length * 20;
       vector[2] *= 1/length * 20;
       Point3D cross = new Point3D(l2.x+vector[0], l2.y+vector[1], l2.z+vector[2]);
       // to find edges of the arrow, we take 2D normal vector. And half size
       double[] vector2 = new double[2];
       vector2[0] = vector[1]/2;
       vector2[1] = -vector[0]/2;
       Point3D arrowPoint1 = new Point3D(cross.getX()+vector2[0], cross.getY()+vector2[1], cross.getZ());
       Point3D arrowPoint2 = new Point3D(cross.getX()-vector2[0], cross.getY()-vector2[1], cross.getZ()); 
       // we have all the points, now just connect them 
       result.append(";");
       result.append(arrowPoint1.getX()+","+arrowPoint1.getY()+","+arrowPoint1.getZ()+";"+l2.x+","+l2.y+","+l2.z);
       result.append(";");
       result.append(arrowPoint2.getX()+","+arrowPoint2.getY()+","+arrowPoint2.getZ()+";"+l2.x+","+l2.y+","+l2.z);
       result.append(";");
       result.append(arrowPoint1.getX()+","+arrowPoint1.getY()+","+arrowPoint1.getZ()+";"+arrowPoint2.getX()+","+arrowPoint2.getY()+","+arrowPoint2.getZ());
                 
       
       return result.toString();   
    }

    /**
     * Creates a string of vector to be drawn from given path
     * @param path
     * @return 
     */
    private String pathToDebugString(IPathFuture<ILocated> path) {
        StringBuilder result = new StringBuilder("");      
        // projdeme vsechny body a vykreslime caru vzdy z vrcholu n do n+1
        ILocated p0 = null;
        List<ILocated> pathList = path.get();
        for(ILocated p1 : pathList) {
            if(result.length()>0) result.append(";");
            if(p0 != null) {
                result.append(Math.round(p0.getLocation().x)+","+Math.round(p0.getLocation().y)+","+Math.round(p0.getLocation().z)+";"+Math.round(p1.getLocation().x)+","+Math.round(p1.getLocation().y)+","+Math.round(p1.getLocation().z));
            }
            p0 = p1;
        }     
        return result.toString(); 
    }
	
    public static Location getColorForOffMeshConnection(OldOffMeshEdge oe, UT2004Server server) {
        
        NavPoint from = server.getWorldView().get(oe.getFrom().getNavPointId(), NavPoint.class);
        NavPoint to = server.getWorldView().get(oe.getTo().getNavPointId(), NavPoint.class);
        
        if (from == null) return new Location(255,255,100);
        if (to == null) return new Location(255,255,100);

        //lift is blue
        if(from.isLiftCenter() || to.isLiftCenter()) return new Location(0, 0, 255);
        // teleporter is violet
        if(from.isTeleporter() && to.isTeleporter()) return new Location(150, 0, 255);
        // return new Location(0, 180, 64);
        
        NavPointNeighbourLink link = from.getOutgoingEdges().get(oe.getLinkId());
        
        if (link == null) return new Location(255,255,100);
        
        int linkFlags = link.getFlags();        
        if ((linkFlags & LinkFlag.DOOR.get()) > 0) {}
        if ((linkFlags & LinkFlag.FLY.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.FORCED.get()) > 0) {return new Location(255, 170, 255);}      
        if ((linkFlags & LinkFlag.LADDER.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.PLAYERONLY.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.PROSCRIBED.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.SPECIAL.get()) > 0) {return new Location(255, 0, 255);}
        if ((linkFlags & LinkFlag.SWIM.get()) > 0) {return new Location(255, 0, 0);}
        if ((linkFlags & LinkFlag.WALK.get()) > 0) {}
        // JUMP is light green
        if ((linkFlags & LinkFlag.JUMP.get()) > 0) {return new Location(100, 255, 255);}
        // default
        return new Location(255,255,100);
    }
}
