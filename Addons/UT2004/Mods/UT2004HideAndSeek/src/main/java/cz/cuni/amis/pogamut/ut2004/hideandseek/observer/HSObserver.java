package cz.cuni.amis.pogamut.ut2004.hideandseek.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ConfigurationObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.InitializeObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.observer.impl.UT2004Observer;

public class HSObserver extends UT2004Observer {

	private boolean observing = false;
	
	private IWorldObjectEventListener<Player, WorldObjectUpdatedEvent<Player>> playerUpdatedListener = new IWorldObjectEventListener<Player, WorldObjectUpdatedEvent<Player>>() {

		@Override
		public void notify(WorldObjectUpdatedEvent<Player> event) {
			playerUpdated(event);
		}
	};
	
	private Map<Player, Long> visibleSinceMillis = new HashMap<Player, Long>();
	
	@Inject
	public HSObserver(UT2004AgentParameters params, IComponentBus bus, IAgentLogger agentLogger, UT2004WorldView worldView, IAct act) {
		super(params, bus, agentLogger, worldView, act);
		if (!(params instanceof HSObserverParams)) throw new RuntimeException("HSObserver must be instantiated with HSObserverParams not " + params.getClass().getSimpleName() + ".");
		getLogger().addDefaultConsoleHandler();
	}	
		
	public HSObserverParams getParams() {
		return (HSObserverParams)super.getParams();
	};
	
	protected void startAgent() {
		super.startAgent();
		
		getWorldView().addObjectListener(Player.class, WorldObjectUpdatedEvent.class, playerUpdatedListener);
		
		configureObserver();
	}
		
	public void configureObserver() {
		// TELL WE WANT TO OBSERVE THE CONCRETE BOT
		getAct().act(new InitializeObserver().setId(getParams().getBotIdToObserve()));
		// CONFIGURE WHICH MESSAGES WE WANT TO RECEIVE
		getAct().act(
				new ConfigurationObserver()
					.setAll(true)      // WE WANT TO RECEIVE UPDATES	
					.setUpdate(0.1)    // UPDATE TIME						
					.setSelf(false)    // WE WANT TO RECEIVE "Self"
					.setAsync(false)   // export Async events (BotDamaged, PlayerDamaged, BotKilled, PlayerKilled, HearNoise, etc.)
					.setGame(false)    // Export GAME message
					.setSee(true)	   // NavPoints, Items, Players
					.setSpecial(false) // SPECIFIC GAME TYPE MESSAGES (BOM, FLG, DOM)
		);
		getAct().act(new ConfigurationObserver().setUpdate(0.15));
		log.info("START OBSERVING: " + getParams().getBotIdToObserve());
	}
	
	protected void playerUpdated(WorldObjectUpdatedEvent<Player> event) {
		Player player = event.getObject();
				
		if (player.isVisible()) {
			// PLAYER IS/BECOMES VISIBLE
			synchronized(visibleSinceMillis) {
				if (visibleSinceMillis.containsKey(player)) {
					// PLAYER REMAINS VISIBLE, probably location/rotation/speed etc. change
					return;
				}
				// PLAYER BECOMES VISIBLE
				visibleSinceMillis.put(player, System.currentTimeMillis());
			}
		} else {
			synchronized(visibleSinceMillis) {
				// PLAYER NOT VISIBLE
				if (visibleSinceMillis.containsKey(player)) {
					// PLAYER VISIBILITY LOST
					visibleSinceMillis.remove(player);
					return;
				}
			}
		}
	}
	
	public List<Player> getPlayersVisibleMoreThanMillis(long millis) {		
		List<Player> result = new ArrayList<Player>(visibleSinceMillis.size());
		long currTime = System.currentTimeMillis();
		synchronized(visibleSinceMillis) {
			for (Entry<Player, Long> entry : visibleSinceMillis.entrySet()) {
				double visibleFor = currTime - entry.getValue(); 
				if (visibleFor > millis) {
					result.add(entry.getKey());
				}
			}
		}
		return result;
	}
		
}
