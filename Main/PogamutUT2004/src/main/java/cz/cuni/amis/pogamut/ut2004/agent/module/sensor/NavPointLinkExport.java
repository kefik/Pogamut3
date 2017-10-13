package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;

@XStreamAlias("Link")
public class NavPointLinkExport {

	public NavPointLinkExport() {
	}

	public NavPointLinkExport(NavPointNeighbourLink link) {

		if (link.getId() != null) this.Id = link.getId().getStringId();

		this.Flags = link.getFlags();

		this.CollisionR = link.getCollisionR();

		this.CollisionH = link.getCollisionH();

		this.TranslocZOffset = link.getTranslocZOffset();

		this.TranslocTargetTag = link.getTranslocTargetTag();

		this.OnlyTranslocator = link.isOnlyTranslocator();

		this.ForceDoubleJump = link.isForceDoubleJump();

		if (link.getNeededJump() != null) this.NeededJump = new Location(link.getNeededJump()).toString();

		this.NeverImpactJump = link.isNeverImpactJump();

		this.NoLowGrav = link.isNoLowGrav();

		this.CalculatedGravityZ = link.getCalculatedGravityZ();

		if (link.getFromNavPoint() != null && link.getFromNavPoint().getId() != null) this.FromNavPoint = link.getFromNavPoint().getId().getStringId();

		if (link.getToNavPoint() != null && link.getToNavPoint().getId() != null) this.ToNavPoint = link.getToNavPoint().getId().getStringId();

	}

	@XStreamAsAttribute
	public String Id;

	@XStreamAsAttribute
	public Integer Flags;

	@XStreamAsAttribute
	public Integer CollisionR;

	@XStreamAsAttribute
	public Integer CollisionH;

	@XStreamAsAttribute
	public Double TranslocZOffset;

	@XStreamAsAttribute
	public String TranslocTargetTag;

	@XStreamAsAttribute
	public Boolean OnlyTranslocator;

	@XStreamAsAttribute
	public Boolean ForceDoubleJump;

	@XStreamAsAttribute
	public String NeededJump;

	@XStreamAsAttribute
	public Boolean NeverImpactJump;

	@XStreamAsAttribute
	public Boolean NoLowGrav;

	@XStreamAsAttribute
	public Double CalculatedGravityZ;

	@XStreamAsAttribute
	public String FromNavPoint;

	@XStreamAsAttribute
	public String ToNavPoint;
	
	private transient UnrealId unrealId;
	
	public UnrealId getUnrealId() {
		if (Id == null) return null;
		if (unrealId == null) unrealId = UnrealId.get(Id);
		return unrealId;
	}

}
