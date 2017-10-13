
package cz.cuni.amis.pogamut.base.agent.module.comm;
import java.util.logging.Level;

import org.junit.Ignore;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;

@Ignore
public class CommTestAgent extends ObservingAgent {

	private AnnotationListenerRegistrator listenerRegistrator;

	private int testState = 0;

	private int channel;

	private int sendToChannel;
	
	public CommTestAgent(int channel, int sendToChannel) {
		this.channel = channel;
		this.sendToChannel = sendToChannel;
		getLogger().addDefaultConsoleHandler();
		getLog().setLevel(Level.INFO);
		listenerRegistrator = new AnnotationListenerRegistrator(this, getWorldView(), getLog());
		listenerRegistrator.addListeners();
	}
	
	@EventListener(eventClass=CommTestEvent.class)
	public void commTestEventListener(CommTestEvent event) {		
		if (event == null) {
			throw new RuntimeException("NULL EVENT!");
		}
		
		log.info("Event sensed: event.num = " + event.num);
		
		if (event.num != testState) {
			throw new RuntimeException("event.num == " + event.num + " != " + testState);
		}
		
		++testState;
		
		log.info("In test-state: " + testState);
		
		int toSend = event.num + (channel < sendToChannel ? 0 : 1);
		log.info("Sending event.num = " + toSend + " -> channel " + sendToChannel);
		PogamutJVMComm.getInstance().send(new CommTestEvent(toSend), sendToChannel);
		
		if (testState >= 10) {
			log.info("REACHED STATE: " + testState);
			stop();
			return;
		}
		
	}
	
	@Override
	protected void startAgent() {
		super.startAgent();
		PogamutJVMComm.getInstance().registerAgent(this, channel);
		log.info("Listening on CHANNEL " + channel);
	}
	
	@Override
	protected void stopAgent() {
		super.stopAgent();
		PogamutJVMComm.getInstance().unregisterAgent(this);
		log.info("STOPPED Listening");
	}
	
	@Override
	protected void killAgent() {
		super.killAgent();
		PogamutJVMComm.getInstance().unregisterAgent(this);
		log.info("STOPPED Listening");
	}

	public int getTestState() {
		return testState;
	}

}
