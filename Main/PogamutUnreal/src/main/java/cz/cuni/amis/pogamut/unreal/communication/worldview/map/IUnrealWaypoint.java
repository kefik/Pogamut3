/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.communication.worldview.map;

import java.io.Serializable;
import java.util.Set;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 *
 * @author ik
 */
public interface IUnrealWaypoint extends Serializable {

	public String getID();
	
    public Location getLocation();

    public Set<? extends IUnrealWaylink> getOutgoingEdges();

}
