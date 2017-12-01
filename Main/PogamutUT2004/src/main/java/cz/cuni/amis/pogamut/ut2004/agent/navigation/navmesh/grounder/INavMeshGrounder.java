package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.grounder;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.INavMeshAtom;

/** Component that grounds a location to nav mesh
 */
public interface INavMeshGrounder {

	/** Try ground
	 *  
	 * @param located located object to ground
	 * @return nav mesh polygon or null
	 */
    public INavMeshAtom tryGround(ILocated located);
    
    /** Force ground
     * 
     * @param located located object to ground
     * @return nav mesh polygon
     */
    public INavMeshAtom forceGround(ILocated located);
}