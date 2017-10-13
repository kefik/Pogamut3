package cz.cuni.amis.pogamut.base.communication.mediator.testevents;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.utils.token.Token;

/**
 * Asynchronous message. Bot bumped into another actor.
 * 
 * Corresponding GameBots message is BMP.
 */

public class StubBumped extends InfoMessage

implements IWorldEvent, IWorldChangeEvent

{

	/**
	 * Creates new instance of command Bumped.
	 * 
	 * Asynchronous message. Bot bumped into another actor. Corresponding
	 * GameBots message for this command is .
	 * 
	 * @param Id
	 *            Unique Id of the actor we have bumped to (actors include other
	 *            players or bots and other physical objects that can block your
	 *            path).
	 * @param Location
	 *            Location of thing you've rammed into.
	 */
	public StubBumped(Token Id, Location Location) {

		this.Id = Id;

		this.Location = Location;

	}

	/** Example how the message looks like - used during parser tests. */
	public static final String PROTOTYPE = "BMP {Id unreal_id} {Location 0,0,0}";

	// ///// Properties BEGIN

	/**
	 * Unique Id of the actor we have bumped to (actors include other players or
	 * bots and other physical objects that can block your path).
	 */
	protected Token Id = null;

	/**
	 * Unique Id of the actor we have bumped to (actors include other players or
	 * bots and other physical objects that can block your path).
	 */
	public Token getId() {
		return Id;
	}

	/**
	 * Location of thing you've rammed into.
	 */
	protected Location Location = null;

	/**
	 * Location of thing you've rammed into.
	 */
	public Location getLocation() {
		return Location;
	}

	// ///// Properties END

	// ///// Extra Java code BEGIN

	// ///// Additional code from xslt BEGIN

	// ///// Additional code from xslt END

	// ///// Extra Java from XML BEGIN

	// ///// Extra Java from XML END

	// ///// Extra Java code END

	/**
	 * Cloning constructor.
	 */
	public StubBumped(StubBumped original) {

		this.Id = original.Id;

		this.Location = original.Location;

	}

	/**
	 * Used by Yylex to create empty message then to fill it's protected fields
	 * (Yylex is in the same package).
	 */
	public StubBumped() {
	}

	public String toString() {
		return

		super.toString() + " | " +

		"Id = " + String.valueOf(Id) + " | " +

		"Location = " + String.valueOf(Location) + " | " + "";

	}

	public String toHtmlString() {
		return super.toString() +

		"<b>Id</b> : " + String.valueOf(Id) + " <br/> " +

		"<b>Location</b> : " + String.valueOf(Location) + " <br/> " + "";
	}

	@Override
	public long getSimTime() {
		return 0;
	}

}
