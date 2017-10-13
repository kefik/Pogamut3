package cz.cuni.amis.pogamut.ut2004.agent.navigation.navgraph.internal;

import java.util.HashMap;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

public class NavPointJsonParser extends JsonParser {
		
	public NavPointJsonParser(String navPointJson) {
		super(navPointJson.substring(navPointJson.indexOf("(")+1, navPointJson.length()-1));
	}		
	
	public NavPointMessage parse() {
		UnrealId id = nextId();
		nextPast(",");
		Location location = nextLocation();
		nextPast(",");
		Velocity velocity = nextVelocity();
		nextPast(",");
		boolean visible = nextBoolean();
		nextPast(",");
		UnrealId itemId = nextId();
		nextPast(",");
		nextId(); // itemClass
		nextPast(",");
		boolean itemSpawned = nextBoolean();
		nextPast(",");
		boolean doorOpened = nextBoolean();
		nextPast(",");
		UnrealId moverId = nextId();
		nextPast(",");
		Vector3d liftOffset = nextVector3d();
		nextPast(",");
		boolean liftJumpExit = nextBoolean();
		nextPast(",");
		boolean noDoubleJump = nextBoolean();
		nextPast(",");
		boolean invSpot = nextBoolean();
		nextPast(",");
		boolean playerStart = nextBoolean();
		nextPast(",");
		int teamNumber = nextInt();
		nextPast(",");
		boolean domPoint = nextBoolean();
		nextPast(",");
		int domPointController = nextInt();
		nextPast(",");
		boolean door = nextBoolean();
		nextPast(",");
		boolean liftCenter = nextBoolean();
		nextPast(",");
		boolean liftExit = nextBoolean();
		nextPast(",");
		boolean aiMarker = nextBoolean();
		nextPast(",");
		boolean jumpSpot = nextBoolean();
		nextPast(",");
		boolean jumpPad = nextBoolean();
		nextPast(",");
		boolean jumpDest = nextBoolean();
		nextPast(",");
		boolean teleporter = nextBoolean();
		nextPast(",");
		Rotation rotation = nextRotation();
		nextPast(",");
		boolean roamingSpot = nextBoolean();
		nextPast(",");
		boolean snipingSpot = nextBoolean();
		nextPast(",");
		String preferedWeapon = nextString();
		
		return new NavPointMessage(
				id, location, velocity, visible, itemId, null, itemSpawned, doorOpened, moverId, liftOffset,
				liftJumpExit, noDoubleJump, invSpot, playerStart, teamNumber, domPoint, domPointController,
				door, liftCenter, liftExit, aiMarker, jumpSpot, jumpPad, jumpDest, teleporter, rotation,
				roamingSpot, snipingSpot, null, new HashMap<UnrealId, NavPointNeighbourLink>(), 
				new HashMap<UnrealId, NavPointNeighbourLink>(), preferedWeapon
		);
	}
}