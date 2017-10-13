package cz.cuni.amis.pogamut.ut2004.teamcomm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.mina.util.AvailablePortFinder;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendControlMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.StartPlayers;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.TCMinaServer;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.TCControlMessages;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.messages.TCControlServerAlive;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;
import cz.cuni.amis.utils.maps.LazyMap;

public class UT2004TCServer extends UT2004Server implements IUT2004Server {
	
	public static final UnrealId SERVER_UNREAL_ID = UnrealId.get("TC_CONTROL_SERVER");
	
	private static final Random random = new Random(System.currentTimeMillis());

	private static final double ALIVE_MESSAGE_INTERVAL_SECS = 30;
	
	 /**
     * BeginMessage listener - we get current server time here.
     */
    private IWorldEventListener<BeginMessage> myBeginMessageListener = new IWorldEventListener<BeginMessage>() {
        public void notify(BeginMessage event) {
            timeUpdate(event);
        }
    };
    
    /**
     * BeginMessage listener - we get current server time here.
     */
    private IWorldEventListener<EndMessage> myEndMessageListener = new IWorldEventListener<EndMessage>() {
        public void notify(EndMessage event) {
            batchEnd(event);
        }
    };
    
    /**
     * PlayerJoinsGame listener - we get informed that new player/bot has entered the game.
     */
    private IWorldEventListener<PlayerJoinsGame> myPlayerJoinsGameMessageListener = new IWorldEventListener<PlayerJoinsGame>() {
        public void notify(PlayerJoinsGame event) {
            playerJoinsGame(event);
        }
    };
    
    /**
     * PlayerLeft listener - we get informed that new player/bot has entered the game.
     */
    private IWorldEventListener<PlayerLeft> myPlayerLeftMessageListener = new IWorldEventListener<PlayerLeft>() {
        public void notify(PlayerLeft event) {
            playerLeft(event);
        }
    };

    /**
     * Player listener - we simply print out all player messages we receive.
     */
    private IWorldObjectEventListener<PlayerMessage, WorldObjectUpdatedEvent<PlayerMessage>> myPlayerListener = new IWorldObjectEventListener<PlayerMessage, WorldObjectUpdatedEvent<PlayerMessage>>() {
        public void notify(WorldObjectUpdatedEvent<PlayerMessage> event) {
            playerUpdate(event);
        }
    };
    
    private final UnrealId serverId;
    
    private Object mutex = new Object();
    
    private TCControlMessages messages = new TCControlMessages();
	
	private TCMinaServer minaServer;

	private long utSimTime = -1;
	
	private double utTimeLast = -1;

	private double utTimeCurrent = -1;

	private double utTimeDelta = -1;
	
	private Map<UnrealId, BotTCRecord<PlayerMessage>> records = new LazyMap<UnrealId, BotTCRecord<PlayerMessage>>() {

		@Override
		protected BotTCRecord<PlayerMessage> create(UnrealId key) {
			return new BotTCRecord<PlayerMessage>(key);
		}
		
	};
	
	private Set<UnrealId> leftPlayers = new HashSet<UnrealId>();	
	
	@Inject
	public UT2004TCServer(UT2004AgentParameters params, IAgentLogger agentLogger, IComponentBus bus, SocketConnection connection, UT2004WorldView worldView, IAct act) {
		super(params, agentLogger, bus, connection, worldView, act);
		
		minaServer = new TCMinaServer(this, new InetSocketAddress(getParams().getBindHost(), getPort()), getLogger().getCategory("TCMinaServer"));
		
		getWorldView().addEventListener(BeginMessage.class,    myBeginMessageListener);
        getWorldView().addEventListener(EndMessage.class,      myEndMessageListener);
        getWorldView().addEventListener(PlayerJoinsGame.class, myPlayerJoinsGameMessageListener);
        getWorldView().addEventListener(PlayerLeft.class,      myPlayerLeftMessageListener);
        getWorldView().addObjectListener(PlayerMessage.class,  WorldObjectUpdatedEvent.class, myPlayerListener);
        
        UUID uuid = UUID.randomUUID();
        serverId = UnrealId.get(uuid.toString());
	}
	
	public UnrealId getServerId() {
		return serverId;
	}

	@Override
	public UT2004TCServerParams getParams() {
		UT2004AgentParameters params = super.getParams();
		if (!(params instanceof UT2004TCServerParams)) throw new RuntimeException("Invalid parameters passed, expecting UT2004TCServerParams, got " + params);
		return (UT2004TCServerParams) params;
	}
	
	public int getPort() {
		if (getParams().getBindPort() <= 0) {		
			int port = getAvailablePorts(3)[random.nextInt(3)];
			//int port = getAvailablePorts(3)[0];
			getParams().setBindPort(port);			
		}
		return getParams().getBindPort();
	}
	
	private int[] getAvailablePorts(int count) {
		int[] ports = new int[count];
		
		for (int i = 0; i < ports.length; ++i) {
			ports[i] = getAvailablePort(10000 + 10000*i, 20000 + 10000*i);
		}
		
		return ports;
	}
	
	private int getAvailablePort(int fromPort, int toPort) {
		if (toPort <= fromPort) return fromPort;
		int count = 50;
		int step = (toPort - fromPort) / count;
		while (count > 0) {
			int port = fromPort + (50-count) * step + random.nextInt(step);
			try {
			    ServerSocket s = new ServerSocket();
			    s.bind(new InetSocketAddress("localhost", port));
			    s.close();			    
			} catch (IOException ex) {
				--count;
			    continue;
			}
			return port;
		}
		return random.nextInt((toPort-fromPort)) + fromPort;
		
	}

	@Override
    protected void init() {
    	super.init();
    	
    	//getLogger().getCategory(YylexParser.COMPONENT_ID.getToken()).setLevel(Level.ALL);
    	//getLogger().getCategory(getWorldView()).setLevel(Level.ALL);
    	
    	log.setLevel(Level.INFO);
    	
    	synchronized(mutex) {
	        getAct().act(new StartPlayers(true, true, false));
    	}
    }
	
	@Override
	protected void stopAgent() {
		synchronized(mutex) {
			if (minaServer != null) {
				minaServer.stop();
				minaServer = null;
			}
		}
		super.stopAgent();
	}
	
	// ================
	// PUBLIC INTERFACE
	// ================
	
	public long getSimTime() {
		return utSimTime;
	}
	
	public PlayerMessage getPlayer(UnrealId source) {
		 
		if (!records.containsKey(source)) return null;
		
		BotTCRecord<PlayerMessage> record = records.get(source);
		
		if (record == null) return null;
		
		return record.getPlayer();			
	}

	
	// ===========
	// BOT RECORDS
	// ===========
	
	private void deleteRecord(UnrealId id) {
		BotTCRecord<PlayerMessage> record = records.remove(id);
		
		if (record != null) {
			minaServer.botLeft(id);
		}
		
		leftPlayers.add(id);
	}
	
	private void ensureRecord(PlayerMessage object) {
		records.put(object.getId(), new BotTCRecord<PlayerMessage>(object.getId(), object));
	}
	
	// =====================
	// UT2004 EVENT HANDLERS
	// =====================
	
	protected void playerUpdate(IWorldObjectEvent<PlayerMessage> event) {
		if (!records.containsKey(event.getId())) {
			if (leftPlayers.contains(event.getId())) {
				// EATING EXTRA UPDATE FOR NON-EXISTING PLAYER...
				leftPlayers.remove(event.getId());
				return;
			}
			log.info(event.getObject().getId().getStringId() + ": First PlayerMessage received.");
			sendAlive();
		}
		ensureRecord(event.getObject());
	}

	protected void playerLeft(PlayerLeft event) {
		log.info(event.getId().getStringId() + ": Player has LEFT the game.");
		deleteRecord(event.getId());
		minaServer.botLeft(event.getId());
	}

	protected void playerJoinsGame(PlayerJoinsGame event) {
		log.info(event.getId().getStringId() + ": Player has JOINED the game.");
	}

	protected void batchEnd(EndMessage event) {
	}

	protected void timeUpdate(BeginMessage event) {
		utSimTime = event.getSimTime();
		
		utTimeLast = utTimeCurrent;
    	utTimeCurrent = event.getTime();
    	utTimeDelta = utTimeCurrent - utTimeLast;
    	
    	if (utSimTime > 0 && utTimeCurrent > 0 && (lastAlive < 0 || utTimeCurrent - lastAlive > ALIVE_MESSAGE_INTERVAL_SECS)) {
    		
    		if (!minaServer.getRunning().getFlag()) {
    			log.info("Starting MINA SERVER!");
    			minaServer.start();
    		}
    		
    		sendAlive();
    	}
	}
	
	// ====================
	// CONTROL SERVER UTILS
	// ====================
	
	private double lastAlive = -1;
	
	private void sendAlive() {
		log.fine("Sending ALIVE message");
		
		lastAlive = utTimeCurrent;
		
		TCControlServerAlive message = new TCControlServerAlive();
		
		message.setServerId(getServerId());
		message.setHost(getParams().getBindHost());
		message.setPort(getParams().getBindPort());
		
    	SendControlMessage command = messages.write(message);
    	command.setSendAll(true);
    	getAct().act(command);    	
    }
	
	/**
	 * Starts TC server on localhost:random-port(>10000) connecting to UT2004 at localhost:3001 (may be overriden via PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST, PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT and properties) 
	 * @return
	 */
	public static UT2004TCServer startTCServer() {
		String defaultHost = Pogamut.getPlatform().getProperty(
								PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST.getKey()) == null ? 
													"localhost" 
												:	Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_HOST.getKey()
							 );
		int defaultPort = Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT.getKey()) == 0 ?
													  	3001
													: 	Pogamut.getPlatform().getIntProperty(PogamutUT2004Property.POGAMUT_UT2004_SERVER_PORT.getKey()
						  );
		
		return startTCServer(defaultHost, defaultPort);
	}
	
	/**
	 * Starts TC server on localhost:random-port(>10000) connecting to UT2004 at ut2004Host:ut2004ControlServerPort
	 * @param ut2004Host
	 * @param ut2004ControlServerPort
	 * @return
	 */
	public static UT2004TCServer startTCServer(String ut2004Host, int ut2004ControlServerPort) {
		return startTCServer(ut2004Host, ut2004ControlServerPort, "localhost", -1);
	}
	
	/**
	 * Starts TC server using specified parameters.
	 * @param ut2004Host
	 * @param ut2004ControlServerPort
	 * @param bindHost
	 * @param bindPort
	 * @return
	 */
	public static UT2004TCServer startTCServer(String ut2004Host, int ut2004ControlServerPort, String bindHost, int bindPort) {
		// START UT2004TagServer
        UT2004TCServerModule module = new UT2004TCServerModule();
        UT2004ServerFactory factory = new UT2004ServerFactory(module);
        UT2004ServerRunner<UT2004Server, UT2004TCServerParams> serverRunner = new UT2004ServerRunner(factory);
        
        UT2004TCServerParams params = new UT2004TCServerParams();
        params.setWorldAddress(new SocketConnectionAddress(ut2004Host, ut2004ControlServerPort));
        params.setBindHost(bindHost);
        params.setBindPort(bindPort);        
         
        return (UT2004TCServer) serverRunner.setLogLevel(Level.INFO).setMain(false).startAgents(params).get(0);
	}
	
	public static void main(String[] args) {
		startTCServer();
	}
	
}
