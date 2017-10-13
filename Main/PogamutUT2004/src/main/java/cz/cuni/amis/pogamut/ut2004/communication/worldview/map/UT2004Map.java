package cz.cuni.amis.pogamut.ut2004.communication.worldview.map;

import cz.cuni.amis.pogamut.unreal.communication.worldview.map.MapInfo;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.Box;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

//public class UT2004Map implements DirectedGraph<NavPoint, NavPointNeighbourLink>,
//                                  WeightedGraph<NavPoint, NavPointNeighbourLink> {
/**
 * Representation of map made from <tt>NavPOint</tt>s and <tt>NavPointNeighbourLink</tt>s.
 * Doesn't react on changes of worldview, data is copied in constructor.
 *
 * @author Honza
 */
public class UT2004Map implements IUnrealMap<MapInfo> {

    private String name = "";
    private MapInfo info;
    //private Map<IWorldObjectId, NavPoint> navs;
    // TODO: mit samostatnou tridu jako adapter pro JGraph
    //       tohleto reservovat pro veci jako: jmeno mapy ... :-) ... a spoustu dalsi zajimavych veci
    // Jakub: PROBOHA! String jako klic v mape je na zabiti! :-) ... pouzit cele UnrealId jako klic, pokud ty stringy jsou 
    //        nevyhnutelne pouzivat jako klic Token a stringy prevadet pomoci tridy Tokens
    private Map<String, Waypoint> waypoints = new HashMap<String, Waypoint>();

    public UT2004Map(IWorldView worldView) {
        Collection<NavPoint> navs = worldView.getAll(NavPoint.class).values();

        for (NavPoint nav : navs) {
            waypoints.put(nav.getId().getStringId(), new Waypoint(nav));
        }
        // update ending waypoints of edges
        for (Waypoint point : waypoints.values()) {
            for (Waylink link : point.getOutgoingEdges()) {
                Waypoint end = waypoints.get(link.getEndId());
                link.setEnd(end);
            }
        }

    }

    public void addInfo(MapInfo info) {
        this.info = info;
    }

    public MapInfo getInfo() {
        return info;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    /*
    public int inDegreeOf(NavPoint testedNav) {
    if (!navs.containsValue(testedNav)) {
    throw new RuntimeException("Nav is not in map");
    }
    return testedNav.getIncomingEdges().size();
    }
     */

    /*    public Set<NavPointNeighbourLink> incomingEdgesOf(NavPoint nav) {
    if (!navs.containsValue(nav)) {
    return null;
    }

    return new HashSet<NavPointNeighbourLink>(navs.get(nav).getIncomingEdges().values());
    }
     */
    public Collection<Waypoint> vertexSet() {
        return Collections.unmodifiableCollection(waypoints.values());
    }

    public Set<Waylink> edgeSet() {
        Set<Waylink> edges = new HashSet<Waylink>();

        for (Waypoint nav : waypoints.values()) {
            edges.addAll(nav.getOutgoingEdges());
        }

        // remove duplicates, from one side to other
        
        return edges;
    }

    public void printInfo() {
        System.out.println("Printing info about map, vers: " + waypoints.size() + ", links: " + this.edgeSet().size());
        for (Entry<String, Waypoint> entry : this.waypoints.entrySet()) {
            System.out.println(" * " + entry.getKey() + " " + entry.getValue().getLocation());
        }
    }



    /*
    public int outDegreeOf(NavPoint arg0) {
    // TODO Auto-generated method stub
    return 0;
    }

    public Set<NavPointNeighbourLink> outgoingEdgesOf(NavPoint arg0) {
    // TODO Auto-generated method stub
    return null;
    }

    public NavPointNeighbourLink addEdge(NavPoint arg0, NavPoint arg1) {
    // TODO Auto-generated method stub
    return null;
    }

    public boolean addEdge(NavPoint arg0, NavPoint arg1,
    NavPointNeighbourLink arg2) {
    // TODO Auto-generated method stub
    return false;
    }

    public boolean addVertex(NavPoint arg0) {
    // TODO Auto-generated method stub
    return false;
    }

    public boolean containsEdge(NavPoint arg0, NavPoint arg1) {
    // TODO Auto-generated method stub
    return false;
    }

    public boolean containsVertex(NavPoint arg0) {
    // TODO Auto-generated method stub
    return false;
    }
     */

    /*
    public Set<NavPointNeighbourLink> edgesOf(NavPoint arg0) {
    // TODO Auto-generated method stub
    return null;
    }

    public Set<NavPointNeighbourLink> getAllEdges(NavPoint arg0,
    NavPoint arg1) {
    // TODO Auto-generated method stub
    return null;
    }

    public NavPointNeighbourLink getEdge(NavPoint arg0, NavPoint arg1) {
    // TODO Auto-generated method stub
    return null;
    }

    public NavPoint getEdgeSource(NavPointNeighbourLink arg0) {
    // TODO Auto-generated method stub
    return null;
    }

    public NavPoint getEdgeTarget(NavPointNeighbourLink arg0) {
    // TODO Auto-generated method stub
    return null;
    }

    public double getEdgeWeight(NavPointNeighbourLink arg0) {
    // TODO Auto-generated method stub
    return 0;
    }
     */

    /*
    public boolean removeAllEdges(
    Collection<? extends NavPointNeighbourLink> arg0) {
    // TODO Auto-generated method stub
    return false;
    }

    public Set<NavPointNeighbourLink> removeAllEdges(NavPoint arg0,
    NavPoint arg1) {
    // TODO Auto-generated method stub
    return null;
    }

    public boolean removeAllVertices(Collection<? extends NavPoint> arg0) {
    // TODO Auto-generated method stub
    return false;
    }

    public boolean removeEdge(NavPointNeighbourLink arg0) {
    // TODO Auto-generated method stub
    return false;
    }

    public NavPointNeighbourLink removeEdge(NavPoint arg0, NavPoint arg1) {
    // TODO Auto-generated method stub
    return null;
    }

    public boolean removeVertex(NavPoint arg0) {
    // TODO Auto-generated method stub
    return false;
    }

    public boolean containsEdge(NavPointNeighbourLink arg0) {
    // TODO Auto-generated method stub
    return false;
    }
     */

    /**
     * Get smallest box that contains all waypoints of map.
     * @return 
     */
    public Box getBox() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        double minZ = Double.MAX_VALUE;
        double maxZ = Double.MIN_VALUE;

        Collection<Waypoint> navs = this.vertexSet();

        for (Waypoint nav : navs) {
            Location loc = nav.getLocation();

            if (loc.x < minX) {
                minX = loc.x;
            }
            if (loc.x > maxX) {
                maxX = loc.x;
            }

            if (loc.y < minY) {
                minY = loc.y;
            }
            if (loc.y > maxY) {
                maxY = loc.y;
            }

            if (loc.z < minZ) {
                minZ = loc.z;
            }
            if (loc.z > maxZ) {
                maxZ = loc.z;
            }
        }

        return new Box(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
