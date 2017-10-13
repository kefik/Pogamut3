package cz.cuni.amis.pogamut.base.communication.mediator.testevents;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.utils.token.Token;

/**
 * Asynchronous message. You hear someone pick up an object from the ground.
 * 
 * Corresponding GameBots message is HRP.
 */

public class StubHearPickup extends InfoMessage

implements IWorldEvent, IWorldChangeEvent

{

	/**
	 * Creates new instance of command HearPickup.
	 * 
	 * Asynchronous message. You hear someone pick up an object from the ground.
	 * Corresponding GameBots message for this command is .
	 * 
	 * @param Source
	 *            Unique Id of an object picked up.
	 * @param Type
	 *            Class of the picked up actor.
	 * @param Rotation
	 *            How should bot rotate if it would like to be in the direction
	 *            of the pickuped actor
	 */
	public StubHearPickup(Token Source, String Type, Rotation Rotation) {

		this.Source = Source;

		this.Type = Type;

		this.Rotation = Rotation;

	}

	/** Example how the message looks like - used during parser tests. */
	public static final String PROTOTYPE = "HRP {Source unreal_id} {Type text} {Rotation 0,0,0}";

	// ///// Properties BEGIN

	/**
	 * Unique Id of an object picked up.
	 */
	protected Token Source = null;

	/**
	 * Unique Id of an object picked up.
	 */
	public Token getSource() {
		return Source;
	}

	/**
	 * Class of the picked up actor.
	 */
	protected String Type = null;

	/**
	 * Class of the picked up actor.
	 */
	public String getType() {
		return Type;
	}

	/**
	 * How should bot rotate if it would like to be in the direction of the
	 * pickuped actor
	 */
	protected Rotation Rotation = null;

	/**
	 * How should bot rotate if it would like to be in the direction of the
	 * pickuped actor
	 */
	public Rotation getRotation() {
		return Rotation;
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
	public StubHearPickup(StubHearPickup original) {

		this.Source = original.Source;

		this.Type = original.Type;

		this.Rotation = original.Rotation;

	}

	/**
	 * Used by Yylex to create empty message then to fill it's protected fields
	 * (Yylex is in the same package).
	 */
	public StubHearPickup() {
	}

	public String toString() {
		return

		super.toString() + " | " +

		"Source = " + String.valueOf(Source) + " | " +

		"Type = " + String.valueOf(Type) + " | " +

		"Rotation = " + String.valueOf(Rotation) + " | " + "";

	}

	public String toHtmlString() {
		return super.toString() +

		"<b>Source</b> : " + String.valueOf(Source) + " <br/> " +

		"<b>Type</b> : " + String.valueOf(Type) + " <br/> " +

		"<b>Rotation</b> : " + String.valueOf(Rotation) + " <br/> " + "";
	}

	@Override
	public long getSimTime() {
		return 0;
	}

}
