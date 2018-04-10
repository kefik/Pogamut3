package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing;

import java.util.List;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.NavMesh;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshEdge;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.OffMeshPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DrawStayingDebugLines;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.LinkFlag;
import math.geom3d.Point3D;

public class NavMeshDraw extends UT2004Draw implements INavMeshDraw {

    private NavMesh navMesh;
    
    private final static int zOffset = 30;
    
    public NavMeshDraw(NavMesh navMesh, Logger log, IUT2004ServerProvider serverProvider) {
        super(log, serverProvider);
        this.navMesh = navMesh;
    }
    
    public boolean draw(boolean drawMesh, boolean drawOffMeshLinks) {
        if (navMesh == null || !navMesh.isLoaded()) {
            return false;
        }
        
        if (drawMesh) {
            boolean isFirst = true;
            for (NavMeshPolygon polygon : navMesh.getPolygons()) {                
                DrawStayingDebugLines d = new DrawStayingDebugLines();
                String lines = polygonToDebugString(polygon);
                d.setVectors(lines);
                d.setColor(new Location(255, 255, 255));
                d.setClearAll(isFirst);
                isFirst = false;
                getServer().getAct().act(d);
            }
        }
        
        if (drawOffMeshLinks) {
            for (OffMeshPoint op : navMesh.getOffMeshPoints()) {
                for (OffMeshEdge edge : op.getOutgoingEdges()) {
                    DrawStayingDebugLines d = new DrawStayingDebugLines();
                    String lines = offMeshEdgeToDebugString(edge);
                    d.setVectors(lines);
                    d.setColor(getColorForOffMeshConnection(edge, getServer()));
                    d.setClearAll(false);                
                    getServer().getAct().act(d);
                }
            }
        }
        
        return true;
    }
    
    private Location getColorForOffMeshConnection(OffMeshEdge oe, UT2004Server server) {
        NavPoint from = server.getWorldView().get(oe.getFrom().getNavPoint().getId(), NavPoint.class);
        NavPoint to = server.getWorldView().get(oe.getTo().getNavPoint().getId(), NavPoint.class);
        
        if (from == null || to == null) {
            return new Location(255,255,100);
        }

        if(from.isLiftCenter() || to.isLiftCenter()) {
            return new Location(0, 0, 255); // Blue
        }
        if(from.isTeleporter() && to.isTeleporter()) {
            return new Location(150, 0, 255); // Violet
        }
        
        NavPointNeighbourLink oeLink = oe.getLink();
        if (oeLink == null) {
            return new Location(255,255,100);
        }
        
        NavPointNeighbourLink link = from.getOutgoingEdges().get(oeLink.getId());
        if (link != null)  {
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
            if ((linkFlags & LinkFlag.JUMP.get()) > 0) {return new Location(100, 255, 255);}
        }
        
        return new Location(255,255,100);
    }

    private String offMeshEdgeToDebugString(OffMeshEdge edge) {
        StringBuilder result = new StringBuilder("");
        
        Location l1 = edge.getFrom().getLocation();
        Location l2 = edge.getTo().getLocation();
        result.append(l1.x + "," + l1.y + "," + l1.z + ";" + l2.x + "," + l2.y + "," + l2.z);

        // Add arrow at the end
        double[] vector = new double[3];
        vector[0] = l1.x - l2.x;
        vector[1] = l1.y - l2.y;
        vector[2] = l1.z - l2.z;
        
        double length = Math.sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]);
        vector[0] *= 1/length * 20;
        vector[1] *= 1/length * 20;
        vector[2] *= 1/length * 20;
        Point3D cross = new Point3D(l2.x+vector[0], l2.y+vector[1], l2.z+vector[2]);
        
        double[] vector2 = new double[2];
        vector2[0] = vector[1]/2;
        vector2[1] = -vector[0]/2;
        Point3D arrowPoint1 = new Point3D(cross.getX()+vector2[0], cross.getY()+vector2[1], cross.getZ());
        Point3D arrowPoint2 = new Point3D(cross.getX()-vector2[0], cross.getY()-vector2[1], cross.getZ());
        
        result.append(";");
        result.append(arrowPoint1.getX()+","+arrowPoint1.getY()+","+arrowPoint1.getZ()+";"+l2.x+","+l2.y+","+l2.z);
        result.append(";");
        result.append(arrowPoint2.getX()+","+arrowPoint2.getY()+","+arrowPoint2.getZ()+";"+l2.x+","+l2.y+","+l2.z);
        result.append(";");
        result.append(arrowPoint1.getX()+","+arrowPoint1.getY()+","+arrowPoint1.getZ()+";"+arrowPoint2.getX()+","+arrowPoint2.getY()+","+arrowPoint2.getZ());
        
        return result.toString();
    }

    public String polygonToDebugString(NavMeshPolygon polygon) {
        StringBuilder result = new StringBuilder("");
        List<NavMeshEdge> edges = polygon.getEdges();
        for(NavMeshEdge e : edges) {
            Location s = e.getSource().getLocation();
            Location d = e.getDestination().getLocation();
            result.append(s.x + "," + s.y + "," + (s.z + zOffset) + ";" + d.x + "," + d.y + "," + (d.z + zOffset) + ";");
        }     
        return result.toString(); 
    }
    
}
