package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.teamcomm.bot.UT2004TCClient;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestCreateChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestDestroyChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestGetStatus;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestJoinChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestLeaveChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestRegister;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoData;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRecipient;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRequestMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model.TCChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model.TCTeam;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.TCMinaServer;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoBotJoined;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoBotLeft;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoRequestFailed;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoRequestFailedException;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoStatus;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelBotJoined;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelBotLeft;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelCreated;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelDestroyed;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.future.FutureWithListeners;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.token.IToken;

public class TCMinaClient implements IoHandler {
	
	private static final int RETRY_REGISTER_PERIOD_SECS = 3;

	private Object mutex = new Object();
	
	private UT2004TCClient owner;
	
	private IWorldView teamWorldView;
	
	private UnrealId botId;
	
	private int botTeam;
	
	private InetSocketAddress address;
	
	private Logger log;
	
	private Flag<Boolean> connected = new Flag<Boolean>(false);
	
	private Flag<Boolean> connecting = new Flag<Boolean>(false);
	
	private IoFutureListener<ConnectFuture> connectionListener = new IoFutureListener<ConnectFuture>() {

		@Override
		public void operationComplete(ConnectFuture event) {
			connected(event);
		}
	};
	
	private Timer timer;
	
	private NioSocketConnector ioConnector;
	
	private ConnectFuture connectFuture;
	
	private IoSession session;
	
	private Set<UnrealId> allBots = new HashSet<UnrealId>();
	
	private TCTeam team;
	
	private int registerTries = 0;

	public TCMinaClient(UT2004TCClient owner, InetSocketAddress connectToAddress, IWorldView teamWorldView, Logger log) {
		this.owner = owner;
		this.teamWorldView = teamWorldView;
		this.botId = owner.getBotId();
		this.botTeam = owner.getBotTeam();
		this.address = connectToAddress;
		NullCheck.check(this.address, "connectToAddress");
		this.log = log;
		NullCheck.check(this.log, "log");
	}
	
	// =============================
	// PUBLIC INTERFACE - CONNECTION
	// =============================
	
	public String getHost() {
		return address.getHostName();
	}
	
	public int getPort() {
		return address.getPort();
	}
	
	/**
	 * WorldView used by this {@link TCMinaClient}. We're auto-propagating incoming messages to this.
	 * @return
	 */
	public IWorldView getWorldView() {
		return teamWorldView;
	}
		
	public ImmutableFlag<Boolean> getConnected() {
		return connected.getImmutable();
	}
	
	public ImmutableFlag<Boolean> getConnecting() {
		return connecting.getImmutable();
	}
	
	public void connect() {		
		synchronized(mutex) {
			if (connected.getFlag()) return;
			if (connecting.getFlag()) return;
			log.warning("Connecting to TC at " + getHost() + ":" + getPort() + " ...");
			connecting.setFlag(true);
		}
		
		try {
			ioConnector = new NioSocketConnector();
			
			ioConnector.setHandler(this);
			
			ioConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
			
			connectFuture = ioConnector.connect(address);
			
			connectFuture.addListener(connectionListener);
		} catch (Exception e1) {
			try {
				connecting.setFlag(false);
			} catch (Exception e2) {				
			}
		}
		
	}
	
	// =========================
	// PUBLIC INTERFACE - STATUS
	// =========================
	
	public boolean isConnected(UnrealId botId) {
		if (botId == null) return false;
		if (!getConnected().getFlag()) return false;
		return allBots.contains(botId);
	}
	
	public boolean isConnected(Player bot) {
		if (bot == null) return false;
		if (!getConnected().getFlag()) return false;
		return allBots.contains(bot.getId());
	}
	
	public boolean isConnectedToMyTeam(UnrealId botId) {
		if (botId == null) return false;
		if (!getConnected().getFlag()) return false;
		return team.getConnectedBots().contains(botId);
	}
	
	public boolean isConnectedToMyTeam(Player bot) {
		if (bot == null) return false;
		if (!getConnected().getFlag()) return false;
		return team.getConnectedBots().contains(bot.getId());
	}
	
	public boolean isConnectedToChannel(UnrealId botId, int channelId) {
		if (botId == null) return false;
		if (!getConnected().getFlag()) return false;		
		TCChannel channel = team.getChannels().get(channelId);
		if (channel == null) return false;
		return channel.getConnectedBots().contains(botId);
	}
	
	public boolean isConnectedToChannel(Player bot, int channelId) {
		if (bot == null) return false;
		if (!getConnected().getFlag()) return false;		
		TCChannel channel = team.getChannels().get(channelId);
		if (channel == null) return false;
		return channel.getConnectedBots().contains(bot.getId());
	}
	
	public boolean isChannelExist(int channelId) {
		if (!getConnected().getFlag()) return false;		
		TCChannel channel = team.getChannels().get(channelId);
		if (channel == null) return false;
		return true;
	}
	
	/**
	 * Returns set of all bots connected to the same TC server.
	 * @return READ-ONLY!
	 */
	public Set<UnrealId> getConnectedAllBots() {
		if (!getConnected().getFlag()) return new HashSet<UnrealId>();
		return Collections.unmodifiableSet(allBots);
	}
	
	/**
	 * Returns set of bots of the same team connected to the same TC server.
	 * @return READ-ONLY!
	 */
	public Set<UnrealId> getConnectedTeamBots() {
		if (!getConnected().getFlag()) return new HashSet<UnrealId>();
		return Collections.unmodifiableSet(team.getConnectedBots());
	}
	
	/**
	 * Returns set of bots connected to the channel of 'channelId' of the same TC server.
	 * @return READ-ONLY!
	 */
	public Set<UnrealId> getConnectedChannelBots(int channelId) {
		if (!getConnected().getFlag()) return new HashSet<UnrealId>();
		TCChannel channel = team.getChannels().get(channelId);
		if (channel == null) return new HashSet<UnrealId>();
		return Collections.unmodifiableSet(channel.getConnectedBots());
	}
	
	/**
	 * Returns details about bots/channels connected to the team.
	 * 
	 * Returns NULL if not connected!
	 * 
	 * @return READ-ONLY!
	 */
	public TCTeam getTeam() {		
		synchronized(mutex) {
			if (!getConnected().getFlag()) return null;
			if (team == null) return null;
			return team.clone();			
		}
	}
	
	/**
	 * Returns details about bots/channels connected to the team.
	 * 
	 * Returns NULL if not connected or non-existing channel!
	 * 
	 * @return READ-ONLY!
	 */
	public TCChannel getChannel(int channelId) {		
		synchronized(mutex) {
			if (!getConnected().getFlag()) return null;
			if (team == null) return null;
			TCChannel channel = team.getChannels().get(channelId);
			if (channel == null) return null;
			return channel.clone();
		}
	}
	
	// ===========================
	// PUBLIC INTERFACE - REQUESTS
	// ===========================
	
	public static class RequestFuture<T> extends FutureWithListeners<T> {
		
		private TCRequestMessage message;
		
		public RequestFuture(TCRequestMessage message) {
			this.message = message;
		}
	}
	
	/**
	 * MessageType - request Id - request future.
	 */
	private HashMapMap<IToken, Long, RequestFuture<?>> requestFutures = new HashMapMap<IToken, Long, RequestFuture<?>>();
	
	
	/**
	 * Request to create a new channel, the channel will gets channelId assigned by {@link TCMinaServer}.
	 * 
	 * When created, you will be automatically added to it as its creator.
	 * 
	 * @return 
	 */
	public RequestFuture<TCInfoTeamChannelCreated> requestCreateChannel() {
		TCRequestCreateChannel data = new TCRequestCreateChannel(owner.getSimTime());
		TCRequestMessage message = new TCRequestMessage(botId, data);
		RequestFuture<TCInfoTeamChannelCreated> future = new RequestFuture<TCInfoTeamChannelCreated>(message);
		synchronized(mutex) {	
			if (!getConnected().getFlag()) return null;
			requestFutures.put(data.getMessageType(), data.getRequestId(), future);
			session.write(message);			
			
		}
		return future;
	}
	
	/**
	 * Request to destroy an existing channel. Note that you must be channel's creator in able to do this!
	 * @param channelId
	 * @return 
	 */
	public RequestFuture<TCInfoTeamChannelDestroyed> requestDestroyChannel(int channelId) {
		TCRequestDestroyChannel data = new TCRequestDestroyChannel(owner.getSimTime());
		data.setChannelId(channelId);
		TCRequestMessage message = new TCRequestMessage(botId, data);
		RequestFuture<TCInfoTeamChannelDestroyed> future = new RequestFuture<TCInfoTeamChannelDestroyed>(message);
		synchronized(mutex) {	
			if (!getConnected().getFlag()) return null;
			TCChannel channel = team.getChannels().get(channelId);
			if (channel == null) return null;
			if (!channel.getCreator().equals(botId)) return null;
			requestFutures.put(data.getMessageType(), data.getRequestId(), future);
			session.write(message);						
		}
		return future;
	}
	
	/**
	 * Request {@link TCInfoStatus} update from {@link TCMinaServer}.
	 * @return request future or NULL if not connected
	 */
	public RequestFuture<TCInfoStatus> requestGetStatus() {
		TCRequestGetStatus data = new TCRequestGetStatus(owner.getSimTime());
		TCRequestMessage message = new TCRequestMessage(botId, data);
		RequestFuture<TCInfoStatus> future = new RequestFuture<TCInfoStatus>(message);
		synchronized(mutex) {	
			if (!getConnected().getFlag()) return null;
			requestFutures.put(data.getMessageType(), data.getRequestId(), future);
			session.write(message);			
			
		}
		return future;
	}
	
	/**
	 * Request to join an existing channel.
	 * @param channelId
	 * @return 
	 */
	public RequestFuture<TCInfoTeamChannelBotJoined> requestJoinChannel(int channelId) {
		TCRequestJoinChannel data = new TCRequestJoinChannel(owner.getSimTime());
		data.setChannelId(channelId);
		TCRequestMessage message = new TCRequestMessage(botId, data);
		RequestFuture<TCInfoTeamChannelBotJoined> future = new RequestFuture<TCInfoTeamChannelBotJoined>(message);
		synchronized(mutex) {	
			if (!getConnected().getFlag()) return null;
			TCChannel channel = team.getChannels().get(channelId);
			if (channel == null) return null;
			requestFutures.put(data.getMessageType(), data.getRequestId(), future);
			session.write(message);						
		}
		return future;
	}
	
	/**
	 * Request to leave an existing channel. Leaving a channel you have created will NOT destroy it.
	 * @param channelId
	 * @return
	 */
	public RequestFuture<TCInfoTeamChannelBotLeft> requestLeaveChannel(int channelId) {
		TCRequestLeaveChannel data = new TCRequestLeaveChannel(owner.getSimTime());
		data.setChannelId(channelId);
		TCRequestMessage message = new TCRequestMessage(botId, data);
		RequestFuture<TCInfoTeamChannelBotLeft> future = new RequestFuture<TCInfoTeamChannelBotLeft>(message);
		synchronized(mutex) {	
			if (!getConnected().getFlag()) return null;
			TCChannel channel = team.getChannels().get(channelId);
			if (channel == null) return null;
			requestFutures.put(data.getMessageType(), data.getRequestId(), future);
			session.write(message);						
		}
		return future;
	}
	
	// ================================
	// PUBLIC INTERFACE - COMMUNICATION
	// ================================
	
	/**
	 * Sends message to ALL connected bots (not just to your team) EXCLUDING you (you will NOT receive the message).
	 * @param messageType CANNOT BE NULL
	 * @param data may be null
	 * @return
	 */
	public boolean sendToAllOthers(IToken messageType, Serializable data) {
		return sendToAll(messageType, data, false);
	}
	
	/**
	 * Sends message to ALL connected bots (not just to your team) INCLUDING you (you WILL receive the message AS WELL).
	 * @param messageType CANNOT BE NULL
	 * @param data may be null
	 * @return
	 */
	public boolean sendToAll(IToken messageType, Serializable data) {
		return sendToAll(messageType, data, true);
	}
	
	/**
	 * Sends message to ALL connected bots (not just to your team)
	 * @param messageType CANNOT BE NULL
	 * @param data may be null
	 * @param includeMe whether the message should be send to YOU (your client) as well
	 * @return
	 */
	public boolean sendToAll(IToken messageType, Serializable data, boolean includeMe) {
		if (messageType == null) return false;
		
		TCMessage message = new TCMessage(botId, TCRecipient.GLOBAL, !includeMe, messageType, data, owner.getSimTime());
		
		synchronized(mutex) {
			if (!connected.getFlag() || session == null) return false;
			try {
				session.write(message);
			} catch (Exception e) {
				log.warning(ExceptionToString.process("Failed to sendToAll: " + data, e));
				return false;
			}			
			return true;
		}
	}
	
	/**
	 * Sends message to connected bots of YOUR TEAM (not to all bots connected to the TC server) EXCLUDING you (you will NOT receive the message).
	 * @param messageType CANNOT BE NULL
	 * @param data may be null.
	 * @return
	 */
	public boolean sendToTeamOthers(IToken messageType, Serializable data) {
		return sendToTeam(messageType, data, false);
	}
	
	/**
	 * Sends message to connected bots of YOUR TEAM (not to all bots connected to the TC server) INCLUDING you (you WILL receive the message AS WELL).
	 * @param messageType CANNOT BE NULL
	 * @param data may be null.
	 * @return
	 */
	public boolean sendToTeam(IToken messageType, Serializable data) {
		return sendToTeam(messageType, data, true);
	}
	
	/**
	 * Sends message to connected bots of YOUR TEAM (not to all bots connected to the TC server).
	 * @param messageType CANNOT BE NULL
	 * @param data may be null.
	 * @param includeMe whether the message should be send to YOU (your client) as well
	 * @return
	 */
	public boolean sendToTeam(IToken messageType, Serializable data, boolean includeMe) {
		if (messageType == null) return false;
		
		TCMessage message = new TCMessage(botId, TCRecipient.TEAM, !includeMe, messageType, data, owner.getSimTime());
		
		synchronized(mutex) {
			if (!connected.getFlag() || session == null) return false;
			try {
				session.write(message);
			} catch (Exception e) {
				log.warning(ExceptionToString.process("Failed to sendToTeam: " + data, e));
				return false;
			}			
			return true;			
		}
	}
	
	/**
	 * Sends message to a CONCRETE CHANNEL you are connected to EXCLUDING you (you will NOT receive the message). 
	 * Note that you cannot send messages to channels you are not connected to.
	 * 
	 * @param channelId
	 * @param messageType
	 * @param data
	 * @return
	 */
	public boolean sendToChannelOthers(int channelId, IToken messageType, Serializable data) {
		return sendToChannel(channelId, messageType, data, false);
	}
	
	/**
	 * Sends message to a CONCRETE CHANNEL you are connected to INCLUDING you (you WILL receive the message AS WELL). 
	 * Note that you cannot send messages to channels you are not connected to.
	 * 
	 * @param channelId
	 * @param messageType
	 * @param data
	 * @return
	 */
	public boolean sendToChannel(int channelId, IToken messageType, Serializable data) {
		return sendToChannel(channelId, messageType, data, true);
	}
	
	/**
	 * Sends message to a CONCRETE CHANNEL you are connected to. Note that you cannot send messages to channels
	 * you are not connected to.
	 * 
	 * @param channelId
	 * @param messageType
	 * @param data
	 * @param includeMe whether the message should be send to YOU (your client) as well
	 * @return
	 */
	public boolean sendToChannel(int channelId, IToken messageType, Serializable data, boolean includeMe) {
		if (messageType == null) return false;
		
		TCMessage message = new TCMessage(botId, TCRecipient.CHANNEL, includeMe, messageType, data, owner.getSimTime());
		message.setChannelId(channelId);
		
		synchronized(mutex) {
			if (!connected.getFlag() || session == null) return false;
			try {
				session.write(message);
			} catch (Exception e) {
				log.warning(ExceptionToString.process("Failed to sendToChannel(" + channelId + "): " + data, e));
				return false;
			}			
			return true;
		}
	}
	
	/**
	 * Sends private message to a concrete bot. Note that you can use this to send messages to yourself as well.
	 * @param targetBotId
	 * @param messageType
	 * @param data
	 * @return
	 */
	public boolean sendPrivate(UnrealId targetBotId, IToken messageType, Serializable data) {
		if (messageType == null) return false;
		
		TCMessage message = new TCMessage(botId, TCRecipient.PRIVATE, false, messageType, data, owner.getSimTime());
		message.setTargetId(targetBotId);
		
		synchronized(mutex) {
			if (!connected.getFlag() || session == null) return false;
			try {
				session.write(message);
			} catch (Exception e) {
				log.warning(ExceptionToString.process("Failed to sendPrivate(" + targetBotId.getStringId() + "): " + data, e));
				return false;
			}			
			return true;
		}
	}
	
	// ======
	// EVENTS
	// ======
	
	protected void connected(ConnectFuture event) {
		log.info("Connected to TC at " + getHost() + ":" + getPort());		
		
		this.session = event.getSession();
		
		connectFuture.removeListener(connectionListener);
		connectFuture = null;
		
		log.info("Sending REGISTER request, expecting TCInfoStatus reply...");
		
		registerTries = 1;
		sendRegisterRequest();
	}

	
	private void sendRegisterRequest() {
		TCRequestRegister request = new TCRequestRegister(owner.getSimTime());
		TCRequestMessage message = new TCRequestMessage(botId, request);
		synchronized(mutex) {
			session.write(message);
		}
	}

	// ================================
	// IMPLEMENTATING {@link IOHandler}
	// ================================
	
	@Override
	public void exceptionCaught(IoSession session, Throwable exception) throws Exception {
		log.warning(ExceptionToString.process("TCMinaClient Exception", exception));
		stop();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {		
		if (message == null) {
			log.warning("Invalid message: " + String.valueOf(message));
			return;
		}
		if (!(message instanceof TCMessage)) {
			log.warning("Invalid message: " + String.valueOf(message));
			return;
		}
		
		TCMessage tcMessage = (TCMessage)message;
		
		if (tcMessage.getSource() == null) {
			log.warning("TCMessage.getSource() is NULL, cannot process: " + String.valueOf(message));
			return;
		}
		if (tcMessage.getMessageType() == null) {
			log.warning("TCMessage.getMessageType() is NULL, cannot process: " + String.valueOf(message));
			return;
		}
		if (tcMessage.getTarget() == null) {
			log.warning("TCMessage.getTarget() is NULL, cannot process the message: " + String.valueOf(message));
			return;
		}
		
		switch(tcMessage.getTarget()) {
		case GLOBAL:
		case TEAM:
		case CHANNEL:
		case PRIVATE:
			Serializable data;
			try {
				data = tcMessage.getMessage();
			} catch (Exception e) {
				log.warning(ExceptionToString.process("Invalid request data, failed to deserialize TCMessage.getMessage(): " + String.valueOf(tcMessage), e));
				return;
			}
			botMessage(tcMessage, data);
			return;
		case TC_INFO:
			if (!(tcMessage instanceof TCInfoMessage)) {
				log.warning("TCMessage recipient is " + tcMessage.getTarget() + ", but the message is not TCInfoMessage: " + String.valueOf(tcMessage));
				return;
			}
			TCInfoData infoData = null;
			try {
				infoData = (TCInfoData)tcMessage.getMessage();
			} catch (Exception e) {
				log.warning("Invalid request data, failed to deserialize TCMessage.getMessage() as TCInfoMessageData: " + String.valueOf(tcMessage));
				return;
			}
			infoMessage((TCInfoMessage)tcMessage, infoData);				
			return;
		case TC_REQUEST:
			log.warning("Received TC_REQUEST message, cannot process: " + String.valueOf(message));
			return;
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		synchronized(mutex) {
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			
			session = null;
			
			log.warning("TC Server connection closed.");
			
			connected.setFlag(false);
			connecting.setFlag(false);			
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
	}
	
	// ==================
	// TCMESSAGE HANDLERS
	// ==================
	
	private void botMessage(TCMessage message, Serializable data) {
		notify(message, data);
	}

	private void infoMessage(TCInfoMessage message, TCInfoData data) {
		if (data.getMessageType() == TCInfoStatus.MESSAGE_TYPE) {
			status(message, (TCInfoStatus)data);
		} else 
		if (data.getMessageType() == TCInfoBotJoined.MESSAGE_TYPE) {
			botJoined(message, (TCInfoBotJoined)data);
		} else
		if (data.getMessageType() == TCInfoBotLeft.MESSAGE_TYPE) {
			botLeft(message, (TCInfoBotLeft)data);
		} else 
		if (data.getMessageType() == TCInfoRequestFailed.MESSAGE_TYPE) {
			requestFailed(message, (TCInfoRequestFailed)data);
		} else
		if (data.getMessageType() == TCInfoRequestFailed.MESSAGE_TYPE) {
			status(message, (TCInfoStatus)data);
		} else
		if (data.getMessageType() == TCInfoTeamChannelBotJoined.MESSAGE_TYPE) {
			channelBotJoined(message, (TCInfoTeamChannelBotJoined)data);
		} else	
		if (data.getMessageType() == TCInfoTeamChannelBotLeft.MESSAGE_TYPE) {
			channelBotLeft(message, (TCInfoTeamChannelBotLeft)data);
		} else	
		if (data.getMessageType() == TCInfoTeamChannelCreated.MESSAGE_TYPE) {
			channelCreated(message, (TCInfoTeamChannelCreated)data);
		} else
		if (data.getMessageType() == TCInfoTeamChannelDestroyed.MESSAGE_TYPE) {
			channelDestroyed(message, (TCInfoTeamChannelDestroyed)data);
		} else {
			log.warning("Unhandled INFO message type: " + data.getMessageType().getToken());
		}
	}
	
	// =======================
	// TCMESSAGE DATA HANDLERS
	// =======================

	private void botJoined(TCInfoMessage message, TCInfoBotJoined data) {
		if (data.getBotId() == null) {
			log.warning("TCInfoBotJoined.getBotId() is NULL!");
			return;
		}
		
		log.info("Bot " + data.getBotId().getStringId() + " has joined TC.");
		
		synchronized(mutex) {
			allBots.add(data.getBotId());
			
			if (team == null) return;
			
			if (data.getTeam() != team.getTeam()) {
				return;
			}
			
			team.getConnectedBots().add(data.getBotId());
		}
		
		notify(message, data);
	}

	private void botLeft(TCInfoMessage message, TCInfoBotLeft data) {
		if (data.getBotId() == null) {
			log.warning("TCInfoBotLeft.getBotId() is NULL!");
			return;
		}
		
		log.warning("Bot " + data.getBotId().getStringId() + " has left TC.");
		
		synchronized(mutex) {
			allBots.add(data.getBotId());
			
			if (team == null) return;
			
			if (data.getTeam() != team.getTeam()) {
				return;
			}
			
			team.getConnectedBots().remove(data.getBotId());
			for (TCChannel channel : team.getChannels().values()) {
				channel.getConnectedBots().remove(data.getBotId());
			}
		}	
		
		notify(message, data);
	}

	private void requestFailed(TCInfoMessage message, TCInfoRequestFailed data) {
		if (connecting.getFlag()) {
			log.warning("Failed to register, will retry in " + RETRY_REGISTER_PERIOD_SECS + " seconds...");
			if (timer == null) timer = new Timer(); 
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					++registerTries;
					log.warning("Trying to register again (" + registerTries + ")...");					
					sendRegisterRequest();
				}				
			}, RETRY_REGISTER_PERIOD_SECS * 1000);
			return;
		} 
		
		// CONNECTED TO THE SERVER
		// => process as user request
		
		failRequestFuture(null, data);			
		
		notify(message, data);		
	}

	private void status(TCInfoMessage message, TCInfoStatus data) {
		log.info("Received TCInfoStatus message...");
		
		synchronized(mutex) {
			allBots = new HashSet<UnrealId>(data.getAllBots());
			team = data.getTeam();
		
			if (connecting.getFlag()) {
				log.info("Connected to TC Server at " + address.getHostName() + ":" + address.getPort());
				
				timer = null;
				
				connected.setFlag(true);
				connecting.setFlag(false);
			} else {
				requestFinishedUnsync(TCRequestGetStatus.MESSAGE_TYPE, data.getRequestId(), data);
			}
		}
		
		notify(message, data);
	}

	private void channelBotJoined(TCInfoMessage message, TCInfoTeamChannelBotJoined data) {
		if (data.getBotId() == null) {
			log.warning("TCInfoTeamChannelBotJoined.getBotId() is NULL!");
		}
		synchronized(mutex) {
			TCChannel channel = team.getChannels().get(data.getChannelId());
			if (channel == null) {
				log.warning("Bot " + data.getBotId().getStringId() + " has joined unknown channel " + data.getChannelId() + "! Requesting STATUS...");
				requestGetStatus();
				return;
			}
			channel.getConnectedBots().add(data.getBotId());
			
			requestFinishedUnsync(TCRequestJoinChannel.MESSAGE_TYPE, data.getRequestId(), data);
		}
		notify(message, data);
	}

	private void channelBotLeft(TCInfoMessage message, TCInfoTeamChannelBotLeft data) {
		if (data.getBotId() == null) {
			log.warning("TCInfoTeamChannelBotLeft.getBotId() is NULL!");
		}
		synchronized(mutex) {
			TCChannel channel = team.getChannels().get(data.getChannelId());
			if (channel == null) {
				log.warning("Bot " + data.getBotId().getStringId() + " has left unknown channel " + data.getChannelId() + "! Requesting STATUS...");
				requestGetStatus();
				return;
			}
			channel.getConnectedBots().remove(data.getBotId());
			
			requestFinishedUnsync(TCRequestLeaveChannel.MESSAGE_TYPE, data.getRequestId(), data);
		}
		notify(message, data);
	}

	private void channelCreated(TCInfoMessage message, TCInfoTeamChannelCreated data) {
		if (team == null) return;
		if (data.getChannel() == null) {
			log.warning("TCInfoTeamChannelCreated.getChannel() is NULL!");
			return;
		}
		synchronized(mutex) {
			team.getChannels().put(data.getChannel().getChannelId(), data.getChannel().clone());
			requestFinishedUnsync(TCRequestCreateChannel.MESSAGE_TYPE, data.getRequestId(), data);
		}
		notify(message, data);
	}

	private void channelDestroyed(TCInfoMessage message, TCInfoTeamChannelDestroyed data) {
		synchronized(mutex) {
			if (team == null) return;
			team.getChannels().remove(data.getChannelId());
			requestFinishedUnsync(TCRequestDestroyChannel.MESSAGE_TYPE, data.getRequestId(), data);
		}
		notify(message, data);
	}
	
	// ========================
	// NOTIFICATION TO THE USER
	// ========================
	
	@SuppressWarnings("rawtypes")
	private void requestFinishedUnsync(IToken requestMessageType, long requestId, Object result) {
		Map<Long, RequestFuture<?>> futures = requestFutures.get(requestMessageType);
		if (futures == null) return;
		RequestFuture requestFuture = futures.remove(requestId);
		if (requestFuture == null) return;
		requestFuture.setResult(result);
	}

	/**
	 * @param requestMessageType can be null (all request message types are going to be checked...)
	 * @param data
	 */
	private void failRequestFuture(IToken requestMessageType, TCInfoRequestFailed data) {
		synchronized(mutex) {
			if (requestMessageType == null) {
				for (IToken messageType : requestFutures.keySet()) {
					Map<Long, RequestFuture<?>> futures = requestFutures.get(messageType);
					RequestFuture<?> requestFuture = futures.remove(data.getRequestId());
					if (requestFuture != null) {
						requestFuture.computationException(new TCInfoRequestFailedException(requestFuture.message, data, log, this));
					}
				}
			} else {
				Map<Long, RequestFuture<?>> futures = requestFutures.get(requestMessageType);
				if (futures == null) return;
				RequestFuture<?> requestFuture = futures.remove(data.getRequestId());
				if (requestFuture == null) return;
				requestFuture.computationException(new TCInfoRequestFailedException(requestFuture.message, data, log, this));				
			}
		}
	}
	
	private void notify(TCMessage message, Serializable data) {
		if (data instanceof IWorldChangeEvent && data instanceof IWorldEvent) {
			teamWorldView.notify((IWorldChangeEvent)data);
		} 
		if (message instanceof IWorldChangeEvent && message instanceof IWorldEvent) {
			teamWorldView.notify(message);
		}
	}
	
	// =========
	// LIFECYCLE
	// =========

	public void stop() {
		synchronized(mutex) {
			log.info("Stopping TCMinaClient!");
			
			allBots.clear();
			team = null;
			
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			
			try {
				if (session != null) {
					session.close(true);
				}
			} catch (Exception e) {				
			}
			session = null;
			
			try {
				connected.setFlag(false);
			} catch (Exception e) {
			}
			try {
				connecting.setFlag(false);
			} catch (Exception e) {			
			}
			
			for (IToken messageType : requestFutures.keySet()) {
				Map<Long, RequestFuture<?>> futures = requestFutures.get(messageType);
				for (RequestFuture<?> future : futures.values()) {
					future.cancel(true);					
				}				
			}
			requestFutures.clear();
			
			log.info("TCMinaClient stopped!");
		}
	}

}
