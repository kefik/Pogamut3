package cz.cuni.amis.pogamut.ut2004.server.impl;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.state.impl.AgentStateStarting;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.server.AbstractWorldServer;
import cz.cuni.amis.pogamut.base.utils.collections.adapters.WVObjectsSetAdapter;
import cz.cuni.amis.pogamut.base.utils.collections.adapters.WVVisibleObjectsSetAdapter;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.NativeUT2004BotAdapter;
import cz.cuni.amis.pogamut.ut2004.bot.jmx.BotJMXProxy;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.AddBot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.GetMaps;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.PasswordReply;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Ready;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapList;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Mutator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Password;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.PlayerListObtained;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.ReadyCommandRequest;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.map.UT2004Map;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.collections.TranslatedObservableCollection;
import cz.cuni.amis.utils.flag.Flag;

/**
 * Abstract class - ancestor of all UT2004 server controls.
 * <p>
 * <p>
 * It counts with GameBots2004 protocol therefore taking care of:
 * <ol>
 * <li>ReadyCommandRequest - sending automatically ready(), override
 * readyCommandRequested() if you're not comfortable with this</li>
 * <li>Password - when password is requested it calls method
 * createPasswordReply()</li>
 * </ol>
 * <p>
 * <p>
 * Also introducing user-method for setting up custom worldview listeners that
 * is called before Ready message is sent - prePrepareServer().
 * <p>
 * <p>
 * You may use setPassword() method to specify the password before starting the
 * agent.
 * 
 * @author Jimmy
 */
@AgentScoped
public abstract class AbstractUT2004Server<WORLD_VIEW extends IWorldView, ACT extends IAct> extends AbstractWorldServer<WORLD_VIEW, ACT, IUT2004Bot> implements IUT2004Server {

	ObservableCollection<Player> players = null;
	
	List<Mutator> mutators = null;
	
	Flag<Double> gameSpeed = new Flag<Double>();
	
	List<MapList> maps = null;
	
	Flag<String> mapName = new Flag<String>();
	
	private UT2004Map map;
	
	ObservableCollection<? extends NativeUT2004BotAdapter> nativeAgents = null;
	
	/**
	 * Collection of all connected Pogamut bots.
	 */
	ObservableCollection<IUT2004Bot> agents = null;
	
	/**
	 * If specified - used for the construction of the PasswordReply in
	 * createPasswordReply() method.
	 */
	private String desiredPassword = null;
	
	private IWorldEventListener<PlayerListObtained> playerListObtainedListener = new IWorldEventListener<PlayerListObtained>() {

		@Override
		public void notify(PlayerListObtained event) {
			players.addAll(event.getPlayers());
			// players list is received as the last in initial communication
		}
	};

	private IWorldEventListener<MapPointListObtained> mapPointListObtainedListener = new IWorldEventListener<MapPointListObtained>() {

		@Override
		public void notify(MapPointListObtained event) {
			// TODO process the navpoints
			// ask for maps on the server
			getAct().act(new GetMaps());
		}
	};
	private SocketConnection connection;

	@Inject
	public AbstractUT2004Server(IAgentId agentId, IAgentLogger agentLogger,
			IComponentBus bus, SocketConnection connection,
			WORLD_VIEW worldView, ACT act) {
		super(agentId, agentLogger, bus, worldView, act);

		this.connection = connection;

		getWorldView().addEventListener(ReadyCommandRequest.class, readyCommandRequestListener);
		getWorldView().addEventListener(Password.class, passwordRequestedListener);

		// listen for initial players list
		getWorldView().addEventListener(PlayerListObtained.class, playerListObtainedListener);

		getWorldView().addEventListener(MapPointListObtained.class, mapPointListObtainedListener);
		
		players = new WVVisibleObjectsSetAdapter<Player>(Player.class, getWorldView());
	}

	public void setAddress(String host, int port) { 
		if (log.isLoggable(Level.WARNING)) log.warning("Setting address to: " + host + ":" + port);
		this.connection.setAddress(new SocketConnectionAddress(host, port));
	}

	/**
	 * Specify the password that should be used if required by the world.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.desiredPassword = password;
	}

	// --------------
	// -=-=-=-=-=-=-=
	// READY LISTENER
	// -=-=-=-=-=-=-=
	// --------------
	/**
	 * This method is called whenever HelloBot message is parsed - the
	 * GameBots2004 is awaiting the bot to reply with Ready command to begin the
	 * handshake.
	 */
	protected void readyCommandRequested() {
		getAct().act(new Ready());
	}

	/**
	 * Listener that is hooked to WorldView awaiting event ReadyCommandRequest
	 * calling setupWorldViewListeners() and then readyCommandRequested() method
	 * upon receiving the event.
	 */
	private IWorldEventListener<ReadyCommandRequest> readyCommandRequestListener = new IWorldEventListener<ReadyCommandRequest>() {

		@Override
		public void notify(ReadyCommandRequest event) {
			setState(new AgentStateStarting("GameBots2004 greeted us, sending READY."));
			readyCommandRequested();
			setState(new AgentStateStarting("READY sent."));
		}
	};
	// -----------------
	// -=-=-=-=-=-=-=-=-
	// PASSWORD LISTENER
	// -=-=-=-=-=-=-=-=-
	// -----------------
	/**
	 * Instance of the password reply command that was sent upon receivieng
	 * request for the password (the world is locked).
	 * <p>
	 * <p>
	 * If null the password was not required by the time the bot connected to
	 * the world.
	 */
	private PasswordReply passwordReply = null;

	/**
	 * Instance of the password reply command that was sent upon receivieng
	 * request for the password (the world is locked).
	 * <p>
	 * <p>
	 * If null the password was not required by the time the bot connected to
	 * the world.
	 * 
	 * @return
	 */
	public PasswordReply getPasswordReply() {
		return passwordReply;
	}

	/**
	 * This method is called whenever the Password event is caught telling us
	 * the world is locked and is requiring a password.
	 * <p>
	 * <p>
	 * May return null - in that case an empty password is sent to the server
	 * (which will probably result in closing the connection and termination of
	 * the agent).
	 * <p>
	 * <p>
	 * This message is then saved to private field passwordReply and is
	 * accessible via getPasswordReply() method if required to be probed during
	 * the bot's runtime.
	 * <p>
	 * <p>
	 * Note that if setPassword() method is called before this one it will use
	 * provided password via that method.
	 */
	protected PasswordReply createPasswordReply() {
		return desiredPassword != null ? new PasswordReply(desiredPassword)
				: null;
	}

	/**
	 * Listener that is hooked to WorldView awaiting event InitCommandRequest
	 * calling initCommandRequested() method upon receiving the event.
	 */
	private IWorldEventListener<Password> passwordRequestedListener = new IWorldEventListener<Password>() {

		@Override
		public void notify(Password event) {
			setState(new AgentStateStarting("Password requested by the world."));
			passwordReply = createPasswordReply();
			if (passwordReply == null) {
				passwordReply = new PasswordReply("");
			}
			if (log.isLoggable(Level.INFO)) log.info("Password required for the world, replying with '"
					+ passwordReply.getPassword() + "'.");
			getAct().act(passwordReply);
		}
	};
	// -------------------------
	// -=-=-=-=-=-=-=-=-=-=-=-=-
	// GAMEINFO MESSAGE LISTENER
	// -=-=-=-=-=-=-=-=-=-=-=-=-
	// -------------------------
	/**
	 * Contains information about the game.
	 */
	private GameInfo gameInfo = null;

	public GameInfo getGameInfo() {
		if (gameInfo == null) {
			gameInfo = getWorldView().getSingle(GameInfo.class);
		}
		return gameInfo;
	}

	@Override
	public WORLD_VIEW getWorldView() {
		return super.getWorldView();
	}

	@Override
	public Collection<MapList> getAvailableMaps() {
		return maps;
	}

	@Override
	public Flag<Double> getGameSpeedFlag() {
		return gameSpeed;
	}

	@Override
	public String getMapName() {
		if (getGameInfo() == null) return null;
		return getGameInfo().getLevel();
	}

	@Override
	public ObservableCollection<Player> getPlayers() {
		return players;
	}
	
	@Override
	public List<Mutator> getMutators() {
		return mutators;
	}

	@Override
	public ObservableCollection<IUT2004Bot> getAgents() {
		if (agents != null) {
			return agents;
		}

		if (getPlayers() == null) {
			// the info has not been initialized yet
			return null;
		} else {
			agents = new TranslatedObservableCollection<IUT2004Bot, Player>(
					getPlayers()) {

				@Override
				protected IUT2004Bot translate(Player obj) {
					if (obj.getJmx() != null) {
						try {
							// the player represents Pogamut agent
							return new BotJMXProxy(obj.getJmx());
						} catch (Exception ex) {
							// communication failed
							Logger.getLogger(
									AbstractUT2004Server.class.getName()).log(
									Level.SEVERE, "JMX error", ex);
							throw new RuntimeException(ex);
						}
					} else {
						return null;
					}
				}

				@Override
				protected Object getKeyForObj(Player elem) {
					return elem.getId();
				}
			};
		}
		return agents;
	}

	@Override
	public ObservableCollection<? extends NativeUT2004BotAdapter> getNativeAgents() {
		if (nativeAgents != null) {
			return nativeAgents;
		}

		if (getPlayers() == null) {
			// the info has not been initialized yet
			return null;
		} else {

			nativeAgents = new TranslatedObservableCollection<NativeUT2004BotAdapter, Player>(
					getPlayers()) {

				@Override
				protected NativeUT2004BotAdapter translate(Player obj) {
					if (obj.getJmx() == null) {
						try {
							// the player representing native bot
							return new NativeUT2004BotAdapter(obj,
									AbstractUT2004Server.this, getAct(),
									getWorldView());
						} catch (Exception ex) {
							// communication failed
							Logger.getLogger(
									AbstractUT2004Server.class.getName()).log(
									Level.SEVERE, "JMX error", ex);
							throw new RuntimeException(ex);
						}
					} else {
						return null;
					}
				}

				@Override
				protected Object getKeyForObj(Player elem) {
					return elem.getId();
				}
			};
		}
		return nativeAgents;

	}

	@Override
	public void connectNativeBot(String botName, String botType, int team) {
		getAct().act(new AddBot(botName, null, null, 3, team, botType));
	}

	@Override
	public UT2004Map getMap() {
		if (map == null) {
			map = new UT2004Map(getWorldView());
		}
		return map;
	}
	
	/////
	//
	// LIFECYCLE METHODS (starting/stopping server)
	//
	/////
	
	/**
	 * Called during stop/kill/reset events.
	 */
	protected void reset() {
		map = null;
		gameInfo = null;
		if (players != null) players.clear();
		if (mutators != null) mutators.clear();
		if (maps != null) maps.clear();
		if (nativeAgents != null) nativeAgents.clear();
		if (agents != null) agents.clear();
	}
	
	@Override
	protected void resetAgent() {
		super.resetAgent();
		reset();
	}
		
	@Override
	protected void stopAgent() {
		super.stopAgent();
		reset();
	}
	
	@Override
	protected void killAgent() {
		super.killAgent();
		reset();
	}
	
	@Override
	protected void startAgent() {
		super.startAgent();
	}	
	
	@Override
	protected void startPausedAgent() {
		super.startPausedAgent();
	}

}
