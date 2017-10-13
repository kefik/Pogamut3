/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.communication.worldview.map;

import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealWaylink;
import java.io.Serializable;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

/**
 * Basically copy of NeighNavLink.
 * TODO: Fix whole structure, this is not proper way to construct it
 * @author Honza
 */
public class Waylink implements IUnrealWaylink {
    private String startId;
    private String endId;
    private int flags;

    private Waypoint start;
    private Waypoint end;

    public Waylink(Waypoint start, NavPointNeighbourLink edge) {
        this.startId = edge.getFromNavPoint().getId().getStringId();
        this.endId = edge.getToNavPoint().getId().getStringId();

        this.start = start;

        this.flags = edge.getFlags();
    }

    protected void setEnd(Waypoint end) {
        this.end = end;
    }

    public String getEndId() {
        return endId;
    }

    public int getFlags() {
        return flags;
    }

    public Waypoint getStart() {
        return start;
    }

    public Waypoint getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object b) {
        if (b == null)
            return false;
        if((b == null) || (b.getClass() != this.getClass()))
            return false;

        Waylink wayB = (Waylink) b;

        if (this.getFlags() != wayB.getFlags())
            return false;

        if (this.startId.equals(wayB.startId) && this.endId.equals(wayB.endId))
            return true;

        if (this.startId.equals(wayB.endId) && this.endId.equals(wayB.startId))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.startId != null ? this.startId.hashCode() : 0);
        hash = 83 * hash + (this.endId != null ? this.endId.hashCode() : 0);
        hash = 83 * hash + this.flags;
        return hash;
    }
}
