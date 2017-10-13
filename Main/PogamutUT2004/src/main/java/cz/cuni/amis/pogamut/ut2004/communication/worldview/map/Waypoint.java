/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.communication.worldview.map;

import java.util.HashSet;
import java.util.Set;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealWaypoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

/**
 * Something like NavPoint but without all ugly changes necessary for serialization
 * @author Honza
 */
public class Waypoint implements IUnrealWaypoint {
    private String id;
    private Location location;

    private Set<Waylink> outgoing = new HashSet<Waylink>();

    
    public String getID() {
    	return id;
    }
    
    public Waypoint(NavPoint nav) {
        this.id = nav.getId().getStringId();
        this.location = new Location(nav.getLocation());
        
        for (NavPointNeighbourLink edge : nav.getOutgoingEdges().values()) {
            outgoing.add(new Waylink(this, edge));
        }
    }

    @Override
    public Location getLocation() {
        return new Location(location);
    }

    @Override
    public Set<Waylink> getOutgoingEdges() {
        return outgoing;
    }
}
