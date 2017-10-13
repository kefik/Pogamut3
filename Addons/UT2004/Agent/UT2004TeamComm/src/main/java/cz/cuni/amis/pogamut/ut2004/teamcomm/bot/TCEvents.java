package cz.cuni.amis.pogamut.ut2004.teamcomm.bot;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.messages.TCControlMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.messages.TCControlServerAlive;

/**
 * Listens to {@link IWorldView} for {@link TCControlMessage} descendant events
 * 
 * Intended to be subclassed and appropriate method(s) {@link #tcControlServerAlive(TagGameStart)} overridden.
 * <p><p>  
 * 
 * Default state: DISABLED, must be {@link #enableTCEvents()}ed manually in order to receive callbacks.
 * 
 * @author Jimmy
 */
public class TCEvents {
	
	protected boolean enabled = false;

	protected IWorldView worldView;
	
	protected IWorldEventListener<TCControlServerAlive> tcControlServerAliveListener = new IWorldEventListener<TCControlServerAlive>() {

		@Override
		public void notify(TCControlServerAlive event) {
			tcControlServerAlive(event);
		}
		
	};
	
	public TCEvents(IWorldView worldView) {
		this.worldView = worldView;
	}
	
	public void enableTCEvents() {
		if (enabled) return;
		enabled = true;
		
		worldView.addEventListener(TCControlServerAlive.class, tcControlServerAliveListener);
	}
	
	public void disableTCEvents() {
		if (!enabled) return;
		enabled = false;
		
		worldView.removeEventListener(TCControlServerAlive.class, tcControlServerAliveListener);
	}
	
	// ================
	// EVENTS TO HANDLE
	// ================
	
	public void tcControlServerAlive(TCControlServerAlive event) {
	}
	
}
