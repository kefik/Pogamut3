package cz.cuni.amis.pogamut.ut2004.server.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateDown;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateGoingUp;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.event.WorldEventFuture;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ChangeMap;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SetGameSpeed;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapListObtained;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MutatorListObtained;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.unreal.server.exception.MapChangeException;
import cz.cuni.amis.utils.Job;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagListener;

public class UT2004Server extends AbstractUT2004Server<UT2004WorldView, IAct> implements IUT2004Server {
	
	/**
	 * How many times we're going to try to connect to the GB2004
	 * before declaring that the change-map has failed.
	 */
	public static final int MAX_CHANGING_MAP_ATTEMPTS = 20;
	public static final int MAP_CHANGE_CONNECT_INTERVAL_MILLIS = 1000;
	

	private volatile BusAwareCountDownLatch mapLatch = null;
	
	protected IWorldEventListener<PlayerJoinsGame> playerJoinsListener = null;
    protected IWorldEventListener<MapListObtained> mapListListener = null;
    
    /**
	 * Parameters passed into the constructor/factory/runner (by whatever means the agent has been started).
	 */
	private UT2004AgentParameters params;
	
    @Inject
    public UT2004Server(UT2004AgentParameters params, IAgentLogger agentLogger, IComponentBus bus, SocketConnection connection, UT2004WorldView worldView, IAct act) {
        super(params.getAgentId(), agentLogger, bus, connection, worldView, act);
        this.params = params;
        mapLatch = new BusAwareCountDownLatch(1, getEventBus(), worldView);
        
        // place where to hook listeners!
        

        // TODO change the players list on the fly
       
        /*getWorldView().addListener(PlayerJoinsGame.class, playerJoinsListener = new WorldEventListener<PlayerJoinsGame>() {

        public void notify(PlayerJoinsGame event) {
        // TODO
        players.add(null);
        }
        });
         */
        // TODO player left

        // mutators list
        getWorldView().addEventListener(MutatorListObtained.class, new IWorldEventListener<MutatorListObtained>() {

            public void notify(MutatorListObtained event) {
                mutators = event.getMutators();
            }
        });

        // TODO where to get gamespeed?
        // gamespeed
        gameSpeed.addListener(new FlagListener<Double>() {

            public void flagChanged(Double changedValue) {
                getAct().act(new SetGameSpeed(changedValue));
            }
        });

        // maps
        getWorldView().addEventListener(MapListObtained.class, mapListListener = new IWorldEventListener<MapListObtained>() {

            public void notify(MapListObtained event) {
            	maps = event.getMaps();
                // first send command
                getAct().act(new StartPlayers(true, true, true));
                // than rise the latch to continue with server starting
                mapLatch.countDown();
            }
            
        });
        
    }
    
    /**
     * Returns parameters that were passed into the agent during the construction. 
     * <p><p>
     * This is a great place to parametrize your agent. Note that you may pass arbitrary subclass of {@link UT2004AgentParameters}
     * to the constructor/factory/runner and pick them up here.
     * 
     * @return parameters
     */
    public UT2004AgentParameters getParams() {
		return params;
	}

	////////  
    //
    // SERVER CONTROL METHODS
    //
    ////////
    @Override
    protected void startAgent() {
        super.startAgent();
        boolean succeded;
        if (log.isLoggable(Level.INFO)) log.info("Waiting for the map list to arrive...");
        succeded = mapLatch.await(60000, TimeUnit.MILLISECONDS);
        if (!succeded) {
            throw new ComponentCantStartException("The server did not received maps in 60 seconds.", this);
        } else {
        	if (log.isLoggable(Level.INFO)) log.info("Maps received.");
        }      
        init();
    }
    
    @Override
    protected void startPausedAgent() {
    	super.startPausedAgent();
    	boolean succeded;
        if (log.isLoggable(Level.INFO)) log.info("Waiting for the map list to arrive...");
        succeded = mapLatch.await(60000, TimeUnit.MILLISECONDS);
        if (!succeded) {
            throw new ComponentCantStartException("The server did not received maps in 60 seconds.", this);
        } else {
        	if (log.isLoggable(Level.INFO)) log.info("Maps received.");
        }    
    }
    
    /**
     * Hook for users (descendants of this class) to fill-in initialization code, any listeners hooked here should be removed inside {@link UT2004Server#reset()}.
     */
    protected void init() {    	
    }
     
	protected void reset() {
    	super.reset();
    	mapLatch = new BusAwareCountDownLatch(1, getEventBus(), getWorldView());
    }

    //////
	////////
	// MAP CHANGING
	////////
	//////
	
	// WARNING: very fragile feature - it depends on the UT2004 behavior + exact handling of start/stop that is happening outside UT2004Server object
	
	protected Object changingMapMutex = new Object();
	protected boolean changingMap = false;
	protected int changingMapAttempt = 0;
	protected String targetMap = null;
	protected MapChangeFuture mapChangeFuture = null;
	
	@Override
	public Future<Boolean> setGameMap(String map) throws MapChangeException {
		try {
			synchronized(changingMapMutex) {
				if (!inState(IAgentStateUp.class)) {
					throw new MapChangeException("Can't change map as we're not connected to GB2004 server.", this);
				}
				
				if (log.isLoggable(Level.WARNING)) log.warning("Changing map to '" + map + "'");
				
				WorldEventFuture<MapChange> mapChangeLatch = new WorldEventFuture<MapChange>(getWorldView(), MapChange.class);
				changingMap = true;
				changingMapAttempt = 0;
				targetMap = map;				
				mapChangeFuture = new MapChangeFuture();
				
				getAct().act(new ChangeMap().setMapName(map));
				
				if (mapChangeLatch.get(20000, TimeUnit.MILLISECONDS) == null) {
					throw new MapChangeException("ChangeMap sent but GB2004 failed to response with MapChange message in 20sec.", this);
				}				
				
				return this.mapChangeFuture; 
			}			
		} catch (Exception e) {
			throw new MapChangeException("Can't change map to " + map + ".", e);
		}
		
	}
		
	public class MapChangeFuture implements Future<Boolean> {

		boolean canceled = false;
		Boolean success = null;
		CountDownLatch doneLatch = new CountDownLatch(1);		
		
		IAgentState lastState = null;
		
		FlagListener<IAgentState> listener = new FlagListener<IAgentState>() {
			
			@Override
			public void flagChanged(IAgentState changedValue) {
				if (lastState != null && lastState.getClass().isAssignableFrom(changedValue.getClass())) {
					return;
				}
				lastState = changedValue;
				if (changedValue instanceof IAgentStateGoingUp) {
					++changingMapAttempt;
					if (log.isLoggable(Level.WARNING)) log.warning("Map change attempt: " + changingMapAttempt + " / " + MAX_CHANGING_MAP_ATTEMPTS);
				} else
				if (changedValue instanceof IAgentStateDown) {
					if (changingMapAttempt >= MAX_CHANGING_MAP_ATTEMPTS) {
						synchronized(changingMapMutex) {
							changingMap = false;
							changingMapAttempt = 0;
							targetMap = null;
							mapChangeFuture = null;
							
							success = false;
							doneLatch.countDown();
							getState().removeListener(this);
						}
					} else {
						Job<Boolean> restartServer = new MapChangeRestartServerJob();
						restartServer.startJob();
					}
				} else 
				if (changedValue instanceof IAgentStateUp) {
					if (getMapName() == null || !getMapName().equalsIgnoreCase(targetMap)) {
						if (log.isLoggable(Level.WARNING)) log.warning("Reconnected to GB2004 but the map was not changed to '" + targetMap + "' yet.");
						Job<Boolean> restartServer = new MapChangeRestartServerJob();
						restartServer.startJob();
					} else {
						success = true;
						doneLatch.countDown();
						getState().removeListener(this);
					}
				}
				
			}
			
		};
		
		protected MapChangeFuture() {
			getState().addListener(listener);
		}
		
		public void restartServer() {
			new MapChangeRestartServerJob().startJob();
		}

		@Override
		public boolean cancel(boolean arg0) {
			synchronized(changingMapMutex) {
				changingMap = false;
				changingMapAttempt = 0;
				targetMap = null;
				mapChangeFuture = null;
				
				success = false;
				canceled = true;
				doneLatch.countDown();
			}
			return false;
		}

		@Override
		public Boolean get() {
			try {
				doneLatch.await();
			} catch (InterruptedException e) {
				new PogamutInterruptedException("Interrupted while waiting for the map change to finish.", e);
			}
			return success;
		}

		@Override
		public Boolean get(long arg0, TimeUnit arg1) {
			try {
				doneLatch.await(arg0, arg1);
			} catch (InterruptedException e) {
				new PogamutInterruptedException("Interrupted while waiting for the map change to finish.", e);
			}			
			return success;
		}

		@Override
		public boolean isCancelled() {
			return canceled;
		}

		@Override
		public boolean isDone() {
			return doneLatch.getCount() <= 0;
		}
		
	}

	private class MapChangeRestartServerJob extends Job<Boolean> {
		
		@Override
		protected void job() throws Exception {
			try {
				UT2004Server.this.stop();
			} catch (Exception e) {
				UT2004Server.this.kill();
			}
			try {
				Thread.sleep(MAP_CHANGE_CONNECT_INTERVAL_MILLIS);
			} catch (Exception e) {									
			}
			UT2004Server.this.start();
			setResult(true);
		}
			
	}

	@Override
	/**
	 * <b>WARNING</b> use this only when no team needs to be specified
	 */
	public void connectNativeBot(String botName, String botType) {
		super.connectNativeBot(botName, botType, 0);		
	};
}
