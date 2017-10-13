package cz.cuni.amis.pogamut.base.communication.mediator.testevents;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

/**
 * Synchronous message. Begin message signalizes start of synchronous batch. In
 * the batch are send information about visible navpoints, game status, items
 * and so on.
 * 
 * Corresponding GameBots message is BEG.
 */

public class StubBeginMessage extends InfoMessage

implements IWorldEvent, IWorldChangeEvent

{

	/**
	 * Creates new instance of command BeginMessage.
	 * 
	 * Synchronous message. Begin message signalizes start of synchronous batch.
	 * In the batch are send information about visible navpoints, game status,
	 * items and so on. Corresponding GameBots message for this command is .
	 * 
	 * @param Time
	 *            Timestamp form the GameBots.
	 */
	public StubBeginMessage(long Time) {

		this.Time = Time;

	}

	/** Example how the message looks like - used during parser tests. */
	public static final String PROTOTYPE = "BEG {Time 0}";

	// ///// Properties BEGIN

	/**
	 * Timestamp form the GameBots.
	 */
	protected long Time = 0;

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
	public StubBeginMessage(StubBeginMessage original) {

		this.Time = original.Time;

	}

	/**
	 * Used by Yylex to create empty message then to fill it's protected fields
	 * (Yylex is in the same package).
	 */
	public StubBeginMessage() {
	}

	public String toString() {
		return

		super.toString() + " | " +

		"Time = " + String.valueOf(Time) + " | " + "";

	}

	public String toHtmlString() {
		return super.toString() +

		"<b>Time</b> : " + String.valueOf(Time) + " <br/> " + "";
	}

	@Override
	public long getSimTime() {
		return 0;
	}

}
