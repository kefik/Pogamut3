package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;

/**
 * Adapter that uses {@link ControlMessages} to read {@link ControlMessage} out of {@link IWorldView} translating them into {@link ICustomControlMessage}
 * that are {@link IWorldView#notifyImmediately(cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent)} re-entered into the same {@link IWorldView}.
 * <p><p>
 * This means that you can use {@link EventListener} annotation for your/custom {@link ICustomControlMessage}s as usual.
 * 
 * Default state: DISABLED, must be {@link ControlMessageTranslator#enable()} manually!
 * @author Jimmy
 */
public class ControlMessageTranslator {
	
	private IWorldView worldView;
	private ControlMessages controlMessagesParser;
	
	private IWorldEventListener<ControlMessage> controlMessageListener = new IWorldEventListener<ControlMessage>() {
		@Override
		public void notify(ControlMessage event) {
			controlMessage(event);
		}
	};
	
	private boolean exceptionMode;
	
	private boolean enabled = false;

	public ControlMessageTranslator(IWorldView worldView, ControlMessages controlMessagesParser, boolean exceptionOnUnreadableMessage) {
		this.worldView = worldView;
		this.controlMessagesParser = controlMessagesParser;
		this.exceptionMode = exceptionOnUnreadableMessage;
	}
	
	public ControlMessages getMessagesMapper() {
		return controlMessagesParser;
	}
	
	public void enable() {
		if (enabled) return;
		enabled = true;
		worldView.addEventListener(ControlMessage.class, controlMessageListener);
	}
	
	public void disable() {
		if (!enabled) return;
		enabled = false;
		worldView.removeEventListener(ControlMessage.class, controlMessageListener);
	}
	
	protected void controlMessage(ControlMessage event) {
		ICustomControlMessage msg;
		
		if (exceptionMode) {
			msg = controlMessagesParser.read(event);
		} else {
			try {
				msg = controlMessagesParser.read(event); 
			} catch (Exception e) {				
				// IGNORE...
				return;
			}
		}
		
		worldView.notifyImmediately(msg);
	}

}
