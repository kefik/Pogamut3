package cz.cuni.amis.pogamut.base.communication.worldview.stubs;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.utils.SafeEquals;

public class EventChatMessageStub extends AbstractEventStub {

	private String message;
	private String from;

	public EventChatMessageStub(String from, String message) {
		this.message = message;
		this.from = from;
	}

	@Override
	public EventChatMessageStub clone() {
		return (EventChatMessageStub) super.clone();
	}

	public String getMessage() {
		return message;
	}

	public String getFrom() {
		return from;
	}

}
