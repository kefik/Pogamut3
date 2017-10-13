package cz.cuni.amis.pogamut.ut2004.teamcomm.bot;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.amis.pogamut.base.agent.module.SensomotoricModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.TCMinaClient;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.TCMinaClient.RequestFuture;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCMessageData;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model.TCChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model.TCTeam;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.TCMinaServer;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoStatus;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelBotJoined;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelBotLeft;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelCreated;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelDestroyed;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.UT2004TCServer;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.TCControlMessagesTranslator;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.messages.TCControlServerAlive;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.token.IToken;

public class UT2004TCClient extends SensomotoricModule<UT2004Bot> {
	
	/**
	 * ID of the server the TC client should connect to. 
	 */
	private UnrealId requiredServerId;	
	
	private UT2004Bot bot;
	
	private IWorldView botWorldView;
	
	private IWorldView teamWorldView;
	
	private IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> selfListener = new IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>() {
		
		@Override
		public void notify(WorldObjectUpdatedEvent<Self> event) {
			selfUpdate(event);
		}
	};
	
	private IWorldEventListener<BeginMessage> beginMessageListener = new IWorldEventListener<BeginMessage>() {
		
		@Override
		public void notify(BeginMessage event) {
			beginMessage(event);
		}
	};
	
	private IWorldEventListener<EndMessage> endMessageListener = new IWorldEventListener<EndMessage>() {
		
		@Override
		public void notify(EndMessage event) {
			endMessage(event);
		}
	};
	
	private IWorldEventListener<TCMessage> tcMessageListener = new IWorldEventListener<TCMessage>() {

		@Override
		public void notify(TCMessage event) {
			tcMessage(event);
		}
		
	};
	
	private List<TCMessage> current = new ArrayList<TCMessage>();
	
	private List<TCMessage> incoming = new ArrayList<TCMessage>();
	
	private TCControlMessagesTranslator tcTranslator;
	
	private TCEvents tcEvents;
	
	private Self self;
	
	private long simTime;
	
	private TCMinaClient minaClient = null;

	public UT2004TCClient(UT2004Bot bot, IWorldView teamWorldView) {
		super(bot);
		
		this.botWorldView = bot.getWorldView();
		this.teamWorldView = teamWorldView;
				
		tcTranslator = new TCControlMessagesTranslator(botWorldView, false);
		tcTranslator.enable();
		
		tcEvents = new TCEvents(agent.getWorldView()) {
			@Override
			public void tcControlServerAlive(TCControlServerAlive event) {
				UT2004TCClient.this.tcControlServerAlive(event);
			}
		};
		tcEvents.enableTCEvents();
		
		bot.getWorldView().addObjectListener(Self.class, WorldObjectUpdatedEvent.class, selfListener);		
		bot.getWorldView().addEventListener(BeginMessage.class, beginMessageListener);
		bot.getWorldView().addEventListener(EndMessage.class, endMessageListener);
		
		this.teamWorldView.addEventListener(TCMessage.class, tcMessageListener);
	}
	
	// ======================================
	// PUBLIC INTERFACE - CONNECTION ENFORCER
	// ======================================
	
	/**
	 * If ID is set (e.g. when running local TC server via {@link UT2004TCServer#startTCServer()} you can obtain id via {@link UT2004TCServer#getServerId()}), 
	 * the client will connect ONLY to server with this ID. Otherwise it will try to connect to the first server that will broadcast {@link TCControlServerAlive} message.
	 * @param tcServerId
	 */
	public void setServerId(UnrealId tcServerId) {
		this.requiredServerId = tcServerId;
	}
			
	// =========================
	// PUBLIC INTERFACE - STATUS
	// =========================
	
	/**
	 * Whether your bot is connected to the TC server.
	 * @return
	 */
	public boolean isConnected() {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return false;
		return mClient.getConnected().getFlag();
	}
	
	public Flag<Boolean> getConnectedFlag() {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return null;
		return mClient.getConnected();
	}
	
	public boolean isConnected(UnrealId botId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return false;
		return mClient.isConnected(botId);		
	}
	
	public boolean isConnected(Player bot) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return false;
		return mClient.isConnected(bot);
	}
	
	public boolean isConnectedToMyTeam(UnrealId botId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return false;
		return mClient.isConnectedToMyTeam(botId);
	}
	
	public boolean isConnectedToMyTeam(Player bot) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return false;
		return mClient.isConnectedToMyTeam(bot);
	}
	
	public boolean isConnectedToChannel(UnrealId botId, int channelId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return false;
		return mClient.isConnectedToChannel(botId, channelId);
	}
	
	public boolean isConnectedToChannel(Player bot, int channelId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return false;
		return mClient.isConnectedToChannel(bot, channelId);
	}
	
	public boolean isChannelExist(int channelId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return false;
		return mClient.isChannelExist(channelId);
	}
	
	/**
	 * Returns set of all bots connected to the same TC server.
	 * @return READ-ONLY!
	 */
	public Set<UnrealId> getConnectedAllBots() {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return new HashSet<UnrealId>();
		return mClient.getConnectedAllBots();
	}
	
	/**
	 * Returns set of bots of the same team connected to the same TC server.
	 * @return READ-ONLY!
	 */
	public Set<UnrealId> getConnectedTeamBots() {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return new HashSet<UnrealId>();
		return mClient.getConnectedTeamBots();
	}
	
	/**
	 * Returns set of bots connected to the channel of 'channelId' of the same TC server.
	 * @return READ-ONLY!
	 */
	public Set<UnrealId> getConnectedChannelBots(int channelId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return new HashSet<UnrealId>();
		return mClient.getConnectedChannelBots(channelId);
	}
	
	/**
	 * Returns details about bots/channels connected to the team.
	 * 
	 * Returns NULL if not connected!
	 * 
	 * @return READ-ONLY!
	 */
	public TCTeam getTeam() {		
		TCMinaClient mClient = minaClient;
		if (mClient == null) return null;
		return mClient.getTeam();
	}
	
	/**
	 * Returns details about bots/channels connected to the team.
	 * 
	 * Returns NULL if not connected or non-existing channel!
	 * 
	 * @return READ-ONLY!
	 */
	public TCChannel getChannel(int channelId) {		
		TCMinaClient mClient = minaClient;
		if (mClient == null) return null;
		return mClient.getChannel(channelId);
	}
	
	// ===========================
	// PUBLIC INTERFACE - REQUESTS
	// ===========================
		
	/**
	 * Request to create a new channel, the channel will gets channelId assigned by {@link TCMinaServer}.
	 * 
	 * When created, you will be automatically added to it as its creator.
	 * 
	 * @return 
	 */
	public RequestFuture<TCInfoTeamChannelCreated> requestCreateChannel() {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return null;
		return mClient.requestCreateChannel();		
	}
	
	/**
	 * Request to destroy an existing channel. Note that you must be channel's creator in able to do this!
	 * @param channelId
	 * @return 
	 */
	public RequestFuture<TCInfoTeamChannelDestroyed> requestDestroyChannel(int channelId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return null;
		return mClient.requestDestroyChannel(channelId);
	}
	
	/**
	 * Request {@link TCInfoStatus} update from {@link TCMinaServer}.
	 * @return request future or NULL if not connected
	 */
	public RequestFuture<TCInfoStatus> requestGetStatus() {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return null;
		return mClient.requestGetStatus();
	}
	
	/**
	 * Request to join an existing channel.
	 * @param channelId
	 * @return 
	 */
	public RequestFuture<TCInfoTeamChannelBotJoined> requestJoinChannel(int channelId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return null;
		return mClient.requestJoinChannel(channelId);
	}
	
	/**
	 * Request to leave an existing channel. Leaving a channel you have created will NOT destroy it.
	 * @param channelId
	 * @return
	 */
	public RequestFuture<TCInfoTeamChannelBotLeft> requestLeaveChannel(int channelId) {
		TCMinaClient mClient = minaClient;
		if (mClient == null) return null;
		return mClient.requestLeaveChannel(channelId);
	}
	
	// ================================
	// PUBLIC INTERFACE - COMMUNICATION
	// ================================	
	
	private boolean sendSanityCheck(TCMessageData data) {
		if (data == null) {
			log.warning("data is null, cannot send");
			return false;
		}
		if (data.getMessageType() == null) {
			log.warning("data.getMessageType() is null, cannot send");
			return false;
		}
		if (minaClient == null) {
			log.warning("minaClient is NULL, cannot send: " + data);
			return false;
		}
		if (!minaClient.getConnected().getFlag()) {
			log.warning("minaClient is NOT connected, cannot send: " + data);
			return false;
		}		
		return true;
	}
	
	private boolean sendSanityCheck(IToken messageType, Serializable data) {
		if (messageType == null) {
			log.warning("messageType is null, cannot send");
			return false;
		}
		if (data == null) {
			log.warning("data is null, cannot send");
			return false;
		}
		if (minaClient == null) {
			log.warning("minaClient is NULL, cannot send: " + data);
			return false;
		}
		if (!minaClient.getConnected().getFlag()) {
			log.warning("minaClient is NOT connected, cannot send: " + data);
			return false;
		}	
		return true;
	}
	
	public boolean sendToAll(IToken messageType, Serializable data) {
		if (!sendSanityCheck(messageType, data)) return false;				
		return minaClient.sendToAll(messageType, data);
	}
	
	public boolean sendToAll(TCMessageData data) {
		if (!sendSanityCheck(data)) return false;
		data.setSimTime(getSimTime());
		return sendToAll(data.getMessageType(), data);
	}
	
	public boolean sendToTeam(IToken messageType, Serializable data) {
		if (!sendSanityCheck(messageType, data)) return false;		
		return minaClient.sendToTeam(messageType, data);
	}
	
	public boolean sendToTeam(TCMessageData data) {
		if (!sendSanityCheck(data)) return false;
		data.setSimTime(getSimTime());
		return sendToTeam(data.getMessageType(), data);
	}
	
	public boolean sendToChannel(int channelId, IToken messageType, Serializable data) {
		if (!sendSanityCheck(messageType, data)) return false;	
		return minaClient.sendToChannel(channelId, messageType, data);
	}
	
	public boolean sendToChannel(int channelId, TCMessageData data) {
		if (!sendSanityCheck(data)) return false;	
		data.setSimTime(getSimTime());
		return sendToChannel(channelId, data.getMessageType(), data);
	}
	
	public boolean sendToBot(UnrealId bot, IToken messageType, Serializable data) {
		if (!sendSanityCheck(messageType, data)) return false;	
		return minaClient.sendPrivate(bot, messageType, data);
	}
	
	public boolean sendToBot(UnrealId bot, TCMessageData data) {
		if (!sendSanityCheck(data)) return false;	
		data.setSimTime(getSimTime());
		return sendToBot(bot, data.getMessageType(), data);
	}
	
	public boolean sendToAllOthers(IToken messageType, Serializable data) {
		if (!sendSanityCheck(messageType, data)) return false;	
		return minaClient.sendToAllOthers(messageType, data);
	}
	
	public boolean sendToAllOthers(TCMessageData data) {
		if (!sendSanityCheck(data)) return false;	
		data.setSimTime(getSimTime());
		return sendToAllOthers(data.getMessageType(), data);
	}
	
	public boolean sendToTeamOthers(IToken messageType, Serializable data) {
		if (!sendSanityCheck(messageType, data)) return false;	
		return minaClient.sendToTeamOthers(messageType, data);
	}
	
	public boolean sendToTeamOthers(TCMessageData data) {
		if (!sendSanityCheck(data)) return false;	
		data.setSimTime(getSimTime());
		return sendToTeamOthers(data.getMessageType(), data);
	}
	
	public boolean sendToChannelOthers(int channelId, IToken messageType, Serializable data) {
		if (!sendSanityCheck(messageType, data)) return false;
		return minaClient.sendToChannelOthers(channelId, messageType, data);
	}
	
	public boolean sendToChannelOthers(int channelId, TCMessageData data) {
		if (!sendSanityCheck(data)) return false;	
		data.setSimTime(getSimTime());
		return sendToChannelOthers(channelId, data.getMessageType(), data);
	}

	/**
	 * {@link TCMessage}s that came since the last {@link EndMessage}, i.e., since the last update, i.e., since the last logic() iteration.
	 * <p><p>
	 * This list is auto-emptied every {@link EndMessage}.
	 *  
	 * @return
	 */
	public List<TCMessage> getMessages() {
		return this.current;
	}
	
	// ========================
	// PUBLIC INTERFACE - UTILS
	// ========================
	
	public UnrealId getBotId() {
		if (self == null) {
			throw new PogamutException("Could not retrieve BotId, self is NULL.", log, this);
		}
		return self.getBotId();
	}
	
	public int getBotTeam() {
		if (self == null) {
			throw new PogamutException("Could not retrieve bot Team, self is NULL.", log, this);
		}
		return self.getTeam();
	}
	
	public long getSimTime() {
		return simTime;
	}

	// =====================
	// UT2004 EVENT HANDLERS
	// =====================
	
	protected void selfUpdate(WorldObjectUpdatedEvent<Self> event) {
		this.self = event.getObject();
		if (minaClient == null) return;
		if (minaClient.getConnected().getFlag()) return;
		if (minaClient.getConnecting().getFlag()) return;
		minaClient.connect();
	}
	
	protected void beginMessage(BeginMessage event) {
		this.simTime = event.getSimTime();
	}
	
	protected void endMessage(EndMessage event) {
		this.current.clear();
		List<TCMessage> temp = this.current;
		this.current = incoming;
		this.incoming = temp;
	}
	
	protected void tcControlServerAlive(TCControlServerAlive event) {
		if (minaClient == null) {
			if (this.self != null) {
				if (requiredServerId != null) {
					if (!event.getServerId().equals(requiredServerId)) {
						// IGNORE, not our server
						log.warning("Other TC server connected to UT2004 running at " + event.getHost() + ":" + event.getPort() + " but has different ID of " + event.getServerId().getStringId() + " != " + requiredServerId.getStringId() + " == forced server ID.");
						return;
					}
				}
				minaClient = new TCMinaClient(this, new InetSocketAddress(event.getHost(), event.getPort()), teamWorldView, log);
				if (minaClient.getConnected().getFlag()) return;
				if (minaClient.getConnecting().getFlag()) return;
				minaClient.connect();				
			}
		} else {
			if (requiredServerId != null && !event.getServerId().equals(requiredServerId)) {
				log.finer("There are multiple TC server connected to the UT2004.");
				return;
			}
		}
	}	
	
	protected void tcMessage(TCMessage event) {
		incoming.add(event);
	}
	
	// ================
	// MODULE LIFECYCLE
	// ================
	
	@Override
	protected void cleanUp() {
		if (minaClient != null) {
			minaClient.stop();
			minaClient = null;
			self = null;
			simTime = -1;
		}
		
		super.cleanUp();
		
	}

	

}
