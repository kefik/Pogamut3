package cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.node.internal;

import java.io.Serializable;

import com.google.common.base.Function;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;

/** Nav graph glue object
 * 
 * Since we do not serialize nav graph objects, only their IDs, we have to provide a lookup method somewhere.
 * The nav graph context can and must be set after deserialization via {@link #setNavGraph(Function)}. 
 */
public class NavMeshNavGraphGlue implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected transient Function<UnrealId, NavPoint> navGraphView;

	public NavMeshNavGraphGlue( Function<UnrealId, NavPoint> navGraphView ) {
		this.navGraphView = navGraphView;
	}
	
    public void setNavGraph( Function<UnrealId, NavPoint> navGraphView ) {
    	this.navGraphView = navGraphView;
    }
    
	public NavPoint getNavPoint(UnrealId navPointId) {
		return navGraphView.apply( navPointId );
	}

}
