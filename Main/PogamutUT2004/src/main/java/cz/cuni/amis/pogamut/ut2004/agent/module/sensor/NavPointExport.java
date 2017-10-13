package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.vecmath.Vector3d;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

@XStreamAlias("NavPoint")
public class NavPointExport {

	
	private static final Comparator<NavPointLinkExport> EDGE_COMPARATOR = new Comparator<NavPointLinkExport>() {
		
		@Override
		public int compare(NavPointLinkExport o1, NavPointLinkExport o2) {
			if (o1.Id == null) {
				if (o2.Id == null) return 0;
				return -1;
			} else {
				if (o2.Id == null) return 1;
				return o1.Id.compareTo(o2.Id);
			}
		}
	};

	public NavPointExport() {
	}

	public NavPointExport(NavPoint navPoint) {

		if (navPoint.getId() != null) this.Id = navPoint.getId().getStringId();

		if (navPoint.getLocation() != null) this.Location = navPoint.getLocation().toString();

		if (navPoint.getVelocity() != null) this.Velocity = navPoint.getVelocity().toString();

		this.Visible = navPoint.isVisible();

		if (navPoint.getItem() != null) this.Item = navPoint.getItem().getStringId();

		if (navPoint.getItemClass() != null) this.ItemClass = navPoint.getItemClass().getName();

		this.ItemSpawned = navPoint.isItemSpawned();

		this.DoorOpened = navPoint.isDoorOpened();

		if (navPoint.getMover() != null) this.Mover = navPoint.getMover().getStringId();

		this.LiftOffset = navPoint.getLiftOffset();

		this.LiftJumpExit = navPoint.isLiftJumpExit();

		this.NoDoubleJump = navPoint.isNoDoubleJump();

		this.InvSpot = navPoint.isInvSpot();

		this.PlayerStart = navPoint.isPlayerStart();

		this.TeamNumber = navPoint.getTeamNumber();

		this.DomPoint = navPoint.isDomPoint();

		this.DomPointController = navPoint.getDomPointController();

		this.Door = navPoint.isDoor();

		this.LiftCenter = navPoint.isLiftCenter();

		this.LiftExit = navPoint.isLiftExit();

		this.AIMarker = navPoint.isAIMarker();

		this.JumpSpot = navPoint.isJumpSpot();

		this.JumpPad = navPoint.isJumpPad();

		this.JumpDest = navPoint.isJumpDest();

		this.Teleporter = navPoint.isTeleporter();

		this.Rotation = navPoint.getRotation();

		this.RoamingSpot = navPoint.isRoamingSpot();

		this.SnipingSpot = navPoint.isSnipingSpot();

		this.PreferedWeapon = navPoint.getPreferedWeapon();
		
		this.outgoingEdges = new ArrayList<NavPointLinkExport>(navPoint.getOutgoingEdges().size());
		for (NavPointNeighbourLink link : navPoint.getOutgoingEdges().values()) {
			outgoingEdges.add(new NavPointLinkExport(link));
		}
		Collections.sort(this.outgoingEdges, EDGE_COMPARATOR);

	}

	@XStreamAsAttribute
	public String Id;
	
	private transient UnrealId unrealId;
	
	public UnrealId getUnrealId() {
		if (Id == null) return null;
		if (unrealId == null) unrealId = UnrealId.get(Id);
		return unrealId;
	}

	@XStreamAsAttribute
	public String Location;

	@XStreamAsAttribute
	public String Velocity;

	@XStreamAsAttribute
	public Boolean Visible;

	@XStreamAsAttribute
	public String Item;
	
	@XStreamAsAttribute
	public String ItemClass;

	@XStreamAsAttribute
	public Boolean ItemSpawned;

	@XStreamAsAttribute
	public Boolean DoorOpened;

	@XStreamAsAttribute
	public String Mover = null;

	@XStreamAsAttribute
	public Vector3d LiftOffset;

	@XStreamAsAttribute
	public Boolean LiftJumpExit;

	@XStreamAsAttribute
	public Boolean NoDoubleJump;

	@XStreamAsAttribute
	public Boolean InvSpot;

	@XStreamAsAttribute
	public Boolean PlayerStart;

	@XStreamAsAttribute
	public int TeamNumber;

	@XStreamAsAttribute
	public Boolean DomPoint;

	@XStreamAsAttribute
	public int DomPointController;

	@XStreamAsAttribute
	public Boolean Door;

	@XStreamAsAttribute
	public Boolean LiftCenter;

	@XStreamAsAttribute
	public Boolean LiftExit;

	@XStreamAsAttribute
	public Boolean AIMarker;

	@XStreamAsAttribute
	public Boolean JumpSpot;

	@XStreamAsAttribute
	public Boolean JumpPad;

	@XStreamAsAttribute
	public Boolean JumpDest;

	@XStreamAsAttribute
	public Boolean Teleporter;

	@XStreamAsAttribute
	public Rotation Rotation;

	@XStreamAsAttribute
	public Boolean RoamingSpot;

	@XStreamAsAttribute
	public Boolean SnipingSpot;

	@XStreamAsAttribute
	public String PreferedWeapon;
	
	@XStreamImplicit(itemFieldName="link")
	public List<NavPointLinkExport> outgoingEdges;

}
