package cz.cuni.amis.pogamut.base.communication.mediator.testevents;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 * Synchronous message. BombInfo contains all info about the bomb in the
 * BotBombingRun game mode. Is not sent in other game types.
 * 
 * Corresponding GameBots message is BOM.
 */

public class StubBombInfo extends InfoMessage

implements IWorldObject, ILocated, IWorldChangeEvent

{

	/**
	 * Creates new instance of command BombInfo.
	 * 
	 * Synchronous message. BombInfo contains all info about the bomb in the
	 * BotBombingRun game mode. Is not sent in other game types. Corresponding
	 * GameBots message for this command is .
	 * 
	 * @param Id
	 *            An unique Id for this bomb, assigned by the game.
	 * @param Location
	 *            An absolute location of the bomb (Sent if we can actually see
	 *            the flag).
	 * @param Holder
	 *            Id of player/bot holding the bomb. (Sent if we can actually
	 *            see the bomb and the bomb is being carried, or if the bomb is
	 *            being carried by us).
	 * @param HolderTeam
	 *            The team of the current holder (if any).
	 * @param Reachable
	 *            True if the bot can run here directly, false otherwise.
	 * @param Visible
	 *            True if the bot can see the bomb.
	 * @param State
	 *            Represents the state the bomb is in. Can be "Held", "Dropped"
	 *            or "Home".
	 */
	public StubBombInfo(WorldObjectId Id, Location Location, WorldObjectId Holder,
			int HolderTeam, boolean Reachable, boolean Visible, String State) {

		this.Id = Id;

		this.Location = Location;

		this.Holder = Holder;

		this.HolderTeam = HolderTeam;

		this.Reachable = Reachable;

		this.Visible = Visible;

		this.State = State;

	}

	/** Example how the message looks like - used during parser tests. */
	public static final String PROTOTYPE = "BOM {Id unreal_id} {Location 0,0,0} {Holder unreal_id} {HolderTeam 0} {Reachable False} {Visible False} {State text}";

	// ///// Properties BEGIN

	/**
	 * An unique Id for this bomb, assigned by the game.
	 */
	protected WorldObjectId Id = null;

	/**
	 * An unique Id for this bomb, assigned by the game.
	 */
	public WorldObjectId getId() {
		return Id;
	}

	/**
	 * An absolute location of the bomb (Sent if we can actually see the flag).
	 */
	protected Location Location = null;

	/**
	 * An absolute location of the bomb (Sent if we can actually see the flag).
	 */
	public Location getLocation() {
		return Location;
	}

	/**
	 * Id of player/bot holding the bomb. (Sent if we can actually see the bomb
	 * and the bomb is being carried, or if the bomb is being carried by us).
	 */
	protected WorldObjectId Holder = null;

	/**
	 * Id of player/bot holding the bomb. (Sent if we can actually see the bomb
	 * and the bomb is being carried, or if the bomb is being carried by us).
	 */
	public WorldObjectId getHolder() {
		return Holder;
	}

	/**
	 * The team of the current holder (if any).
	 */
	protected int HolderTeam = 0;

	/**
	 * The team of the current holder (if any).
	 */
	public int getHolderTeam() {
		return HolderTeam;
	}

	/**
	 * True if the bot can run here directly, false otherwise.
	 */
	protected boolean Reachable = false;

	/**
	 * True if the bot can run here directly, false otherwise.
	 */
	public boolean isReachable() {
		return Reachable;
	}

	/**
	 * True if the bot can see the bomb.
	 */
	protected boolean Visible = false;

	/**
	 * True if the bot can see the bomb.
	 */
	public boolean isVisible() {
		return Visible;
	}

	/**
	 * Represents the state the bomb is in. Can be "Held", "Dropped" or "Home".
	 */
	protected String State = null;

	/**
	 * Represents the state the bomb is in. Can be "Held", "Dropped" or "Home".
	 */
	public String getState() {
		return State;
	}

	// ///// Properties END

	// ///// Extra Java code BEGIN

	// ///// Additional code from xslt BEGIN

	protected long Time = 0;

	protected void setTime(long time) {
		this.Time = time;
	}

	@Override
	public long getSimTime() {
		return Time;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof StubBombInfo))
			return false;
		StubBombInfo cast = (StubBombInfo) obj;
		if (this.getId() != null)
			return this.getId().equals(cast.getId());
		else
			return cast.getId() == null;
	}

	public int hashCode() {
		if (getId() != null)
			return getId().hashCode();
		return 0;
	}

	// ///// Additional code from xslt END

	// ///// Extra Java from XML BEGIN

	// ///// Extra Java from XML END

	// ///// Extra Java code END

	/**
	 * Cloning constructor.
	 */
	public StubBombInfo(StubBombInfo original) {

		this.Id = original.Id;

		this.Location = original.Location;

		this.Holder = original.Holder;

		this.HolderTeam = original.HolderTeam;

		this.Reachable = original.Reachable;

		this.Visible = original.Visible;

		this.State = original.State;

	}

	/**
	 * Used by Yylex to create empty message then to fill it's protected fields
	 * (Yylex is in the same package).
	 */
	public StubBombInfo() {
	}


	/**
	 * Used to create event that drops the Visible flag of the item.
	 */
	public StubBombInfo(StubBombInfo Original, boolean Visible) {
		this(Original);
		this.Visible = Visible;
	}

	/**
	 * Here we save the original object for which this object is an update.
	 */
	private IWorldObject orig = null;

	public IWorldObject update(IWorldObject obj) {
		if (obj == null) {
			orig = this;
			return this;
		}
		orig = obj;
		// typecast
		StubBombInfo o = (StubBombInfo) obj;

		o.Holder = Holder;

		o.HolderTeam = HolderTeam;

		o.Reachable = Reachable;

		o.Visible = Visible;

		o.State = State;

		o.Time = Time;

		return o;
	}

	/**
	 * Returns original object (if method update() has already been called, for
	 * bot-programmer that is always true as the original object is updated and
	 * then the event is propagated).
	 */
	public IWorldObject getObject() {
		if (orig == null)
			return this;
		return orig;
	}

	public String toString() {
		return

		super.toString() + " | " +

		"Id = " + String.valueOf(Id) + " | " +

		"Location = " + String.valueOf(Location) + " | " +

		"Holder = " + String.valueOf(Holder) + " | " +

		"HolderTeam = " + String.valueOf(HolderTeam) + " | " +

		"Reachable = " + String.valueOf(Reachable) + " | " +

		"Visible = " + String.valueOf(Visible) + " | " +

		"State = " + String.valueOf(State) + " | " + "";

	}

	public String toHtmlString() {
		return super.toString() +

		"<b>Id</b> : " + String.valueOf(Id) + " <br/> " +

		"<b>Location</b> : " + String.valueOf(Location) + " <br/> " +

		"<b>Holder</b> : " + String.valueOf(Holder) + " <br/> " +

		"<b>HolderTeam</b> : " + String.valueOf(HolderTeam) + " <br/> " +

		"<b>Reachable</b> : " + String.valueOf(Reachable) + " <br/> " +

		"<b>Visible</b> : " + String.valueOf(Visible) + " <br/> " +

		"<b>State</b> : " + String.valueOf(State) + " <br/> " + "";
	}

}
