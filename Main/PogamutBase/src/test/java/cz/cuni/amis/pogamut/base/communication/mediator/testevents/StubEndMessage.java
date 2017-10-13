package cz.cuni.amis.pogamut.base.communication.mediator.testevents;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

/**
 * Synchronous message. This message signalizes end of synchronous batch.
 * 
 * Corresponding GameBots message is END.
 */

public class StubEndMessage extends InfoMessage

implements IWorldEvent, IWorldChangeEvent

{

	/**
	 * Creates new instance of command EndMessage.
	 * 
	 * Synchronous message. This message signalizes end of synchronous batch.
	 * Corresponding GameBots message for this command is .
	 * 
	 * @param Time
	 *            Time, when the message was sent - intern UT time.
	 */
	public StubEndMessage(long Time) {

		this.Time = Time;

	}

	/** Example how the message looks like - used during parser tests. */
	public static final String PROTOTYPE = "END {Time 0}";

	// ///// Properties BEGIN

	/**
	 * Time, when the message was sent - intern UT time.
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
	public StubEndMessage(StubEndMessage original) {

		this.Time = original.Time;

	}

	/**
	 * Used by Yylex to create empty message then to fill it's protected fields
	 * (Yylex is in the same package).
	 */
	public StubEndMessage() {
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
