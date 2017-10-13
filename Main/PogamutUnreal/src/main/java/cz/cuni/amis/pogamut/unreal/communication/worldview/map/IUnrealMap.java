
package cz.cuni.amis.pogamut.unreal.communication.worldview.map;

import java.util.Collection;
import java.util.Set;

/**
 *
 * @author ik
 */
public interface IUnrealMap<MAP_INFO extends IUnrealMapInfo> {

    public void addInfo(MAP_INFO info);

    public MAP_INFO getInfo();

    public void setName(String name);

    public String getName();

    public Collection<? extends IUnrealWaypoint> vertexSet();

    public Set<? extends IUnrealWaylink> edgeSet();

    public void printInfo();

    public Box getBox();
}
