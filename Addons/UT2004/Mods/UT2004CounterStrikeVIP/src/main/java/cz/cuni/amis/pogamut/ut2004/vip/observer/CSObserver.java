package cz.cuni.amis.pogamut.ut2004.vip.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ConfigurationObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.InitializeObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.observer.impl.UT2004Observer;

public class CSObserver extends UT2004Observer {

	private boolean observing = false;
	
	private boolean botAlive = false;
	
	private IWorldEventListener<BotKilled> botKilled = new IWorldEventListener<BotKilled>() {

		@Override
		public void notify(BotKilled event) {
			botKilled(event);
		}
		
	};

	private IWorldEventListener<Spawn> botSpawned = new IWorldEventListener<Spawn>() {
		
		@Override
		public void notify(Spawn event) {
			botSpwaned(event);
		}
		
	};

	private IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> selfListener = new IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>() {

		@Override
		public void notify(WorldObjectUpdatedEvent<Self> event) {
			selfUpdated(event.getObject());
		}
		
	};
		
	@Inject
	public CSObserver(UT2004AgentParameters params, IComponentBus bus, IAgentLogger agentLogger, UT2004WorldView worldView, IAct act) {
		super(params, bus, agentLogger, worldView, act);
		if (!(params instanceof CSObserverParams)) throw new RuntimeException("VIPObserver must be instantiated with VIPObserverParams not " + params.getClass().getSimpleName() + ".");
		getLogger().addDefaultConsoleHandler();
	}	
		
	public CSObserverParams getParams() {
		return (CSObserverParams)super.getParams();
	};
	
	protected void startAgent() {
		super.startAgent();
		
		getWorldView().addEventListener(Spawn.class, botSpawned);	
		getWorldView().addEventListener(BotKilled.class, botKilled);
		getWorldView().addObjectListener(Self.class, WorldObjectUpdatedEvent.class, selfListener);
		
		configureObserver();
		
		observing = true;
	}
	
	@Override
	protected void killAgent() {
		observing = false;
		super.killAgent();
	}
		
	public void configureObserver() {
		// TELL WE WANT TO OBSERVE THE CONCRETE BOT
		getAct().act(new InitializeObserver().setId(getParams().getBotIdToObserve()));
		// CONFIGURE WHICH MESSAGES WE WANT TO RECEIVE
		getAct().act(
				new ConfigurationObserver()
					.setAll(true)      // WE WANT TO RECEIVE UPDATES	
					.setUpdate(0.5)    // UPDATE TIME						
					.setSelf(true)     // WE DO WANT TO RECEIVE "Self"
					.setAsync(true)    // export Async events (BotDamaged, PlayerDamaged, BotKilled, PlayerKilled, HearNoise, etc.)
					.setGame(false)    // Export GAME message
					.setSee(false)	   // NavPoints, Items, Players
					.setSpecial(false) // SPECIFIC GAME TYPE MESSAGES (BOM, FLG, DOM)
		);
		getAct().act(new ConfigurationObserver().setUpdate(0.5));
		log.info("START OBSERVING: " + getParams().getBotIdToObserve());
	}
	
	protected void selfUpdated(Self self) {
		botAlive = true;
	}

	protected void botSpwaned(Spawn event) {
		botAlive = true;
	}
	
	protected void botKilled(BotKilled event) {
		botAlive = false;
	}

	public boolean isBotAlive() {
		return botAlive;
	}

	public boolean isObserving() {
		return observing;
	}
	
}
