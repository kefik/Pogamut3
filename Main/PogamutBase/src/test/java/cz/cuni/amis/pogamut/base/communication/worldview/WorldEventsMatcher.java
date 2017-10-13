package cz.cuni.amis.pogamut.base.communication.worldview;

import org.easymock.EasyMock;
import org.easymock.IArgumentMatcher;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;

/**
 * EasyMock matcher for the IWorldEvent and IWorldObjectEvent - it is a bridge containing two implementation
 * of the IArgumentMatcher.
 * <p><p>
 * First implementation - IWorldObjectEvent matcher is checking:
 * <ol>
 * <li>class of the event (must be equal)</li>
 * <li>id of the object inside the event (must be equal)</li>
 * <li>object inside the event (must be equal)</li>\
 * </ol>
 * <p>
 * Second implementation - IWorldEvent matcher is checking:
 * <ol>
 * <li>class of the event (must be equal)<li>
 * <li>event.equals()</li>
 * </ol>
 * Therefore we rely that the tester implements object.equals() and event.equals() correctly.
 *
 * @author Jimmy
 */
public class WorldEventsMatcher implements IArgumentMatcher {

	private static abstract class AbstractWorldEventMatcher<EVENT> implements IArgumentMatcher {

		EVENT event;

		public AbstractWorldEventMatcher(EVENT event) {
			this.event = event;
		}

		@Override
		public void appendTo(StringBuffer buffer) {
			buffer.append("eqEvent(");
			buffer.append(event);
			buffer.append(")");
		}

	}

	private static class WorldEventMatcher extends AbstractWorldEventMatcher<IWorldEvent> {

		public WorldEventMatcher(IWorldEvent event) {
			super(event);
		}

		@Override
		public boolean matches(Object argument) {
			if (!(event.getClass().equals(argument.getClass()))) return false;
			return event.equals(argument);
		}

	}

	private static class WorldObjectEventMatcher extends AbstractWorldEventMatcher<IWorldObjectEvent> {


		public WorldObjectEventMatcher(IWorldObjectEvent event) {
			super(event);
		}

		@Override
		public boolean matches(Object argument) {
			if (!(event.getClass().equals(argument.getClass()))) return false;
			IWorldObjectEvent matchedEvent = (IWorldObjectEvent) argument;
			if (!event.getId().equals(matchedEvent.getId())) return false;
			if (!event.getObject().equals(matchedEvent.getObject())) return false;
			return true;
		}

	}



	private IArgumentMatcher impl;

	public WorldEventsMatcher(IWorldEvent event) {
		if (event instanceof IWorldObjectEvent) {
			impl = new WorldObjectEventMatcher((IWorldObjectEvent) event);
		} else {
			impl = new WorldEventMatcher(event);
		}
	}

	@Override
	public void appendTo(StringBuffer buffer) {
		impl.appendTo(buffer);
	}

	@Override
	public boolean matches(Object argument) {
		return impl.matches(argument);
	}

	/**
	 * Creates matcher for pass event.
	 *
	 * @param <T>
	 * @param event
	 * @return
	 */
	public static <T extends IWorldEvent> T eqEvent(T event) {
		EasyMock.reportMatcher(new WorldEventsMatcher(event));
		return null;
	}

}