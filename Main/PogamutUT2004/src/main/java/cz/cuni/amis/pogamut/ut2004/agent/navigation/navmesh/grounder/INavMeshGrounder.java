package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.NavMeshPolygon;

/** Component that grounds a location to nav mesh
 */
public interface INavMeshGrounder {

	/** Try ground
	 *  
	 * @param located located object to ground
	 * @return nav mesh polygon or null
	 */
    public NavMeshPolygon tryGround(ILocated located);
    
    /** Force ground
     * 
     * @param located located object to ground
     * @return nav mesh polygon
     */
    public NavMeshPolygon forceGround(ILocated located);
}