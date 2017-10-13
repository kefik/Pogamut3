package cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph.internal;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph.NavGraph;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

public class NavPointLinkJsonParser extends JsonParser {
	
	protected NavGraph navGraph;
	
	public NavPointLinkJsonParser(NavGraph navGraph, String navPointLinkJson) {
		super(navPointLinkJson.substring(navPointLinkJson.indexOf("(")+1, navPointLinkJson.length()-1));
		this.navGraph = navGraph;
	}
	
	public NavPointNeighbourLink parse(NavPoint from) {
		 UnrealId id = nextId();
		 nextPast(",");
		 int flags = nextInt();
		 nextPast(",");
		 int collisionR = nextInt();
		 nextPast(",");
		 int collisionH = nextInt();
		 nextPast(",");
		 double translocZOffset = nextDouble();
		 nextPast(",");
		 String translocId = nextString();
		 nextPast(",");
		 boolean translocatorOnly = nextBoolean();
		 nextPast(",");
		 boolean forceDoubleJump = nextBoolean();
		 nextPast(",");
		 Vector3d neededJump = nextVector3d();
		 nextPast(",");
		 boolean neverImpact = nextBoolean();
		 nextPast(",");
		 boolean noLowGrav = nextBoolean();
		 nextPast(",");
		 double calcGravityZ = nextDouble();
		 
		 return new NavPointNeighbourLink(
			 id, flags, collisionR, collisionH, translocZOffset, translocId, translocatorOnly, forceDoubleJump,
			 neededJump, neverImpact, noLowGrav, calcGravityZ, from, navGraph.navPointsById.get(id)
		);
	}
}
