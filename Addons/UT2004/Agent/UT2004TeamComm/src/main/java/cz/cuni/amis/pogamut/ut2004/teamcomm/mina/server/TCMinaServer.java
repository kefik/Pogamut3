package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestCreateChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestDestroyChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestGetStatus;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestJoinChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestLeaveChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.client.messages.TCRequestRegister;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCInfoMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRecipient;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRequestData;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.messages.TCRequestMessage;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model.TCChannel;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model.TCTeam;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoBotJoined;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoBotLeft;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoRequestFailed;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoRequestFailureType;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoStatus;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelBotJoined;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelBotLeft;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelCreated;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.messages.TCInfoTeamChannelDestroyed;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.UT2004TCServer;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.maps.LazyMap;

public class TCMinaServer implements IoHandler {
	
	private UT2004TCServer owner;
	
	private InetSocketAddress address;

	private Logger log;
	
	private Object mutex = new Object();
	
	private Flag<Boolean> running = new Flag<Boolean>(false);

	private IoAcceptor ioAcceptor;
	
	private Map<Integer, TCTeam> teams = new LazyMap<Integer, TCTeam>() {

		@Override
		protected TCTeam create(Integer key) {
			return new TCTeam(key);
		}
		
	};
	
	private Map<UnrealId, PlayerMessage> registeredBots = new HashMap<UnrealId, PlayerMessage>();
	
	private Map<UnrealId, IoSession> botSessions = new HashMap<UnrealId, IoSession>();
	
	public TCMinaServer(UT2004TCServer owner, InetSocketAddress bindAddress, Logger log) {
		this.owner = owner;
		this.address = bindAddress;
		NullCheck.check(this.address, "bindAddress");
		this.log = log;
		NullCheck.check(this.log, "log");
	}
	
	public UnrealId getMyId() {
		return owner.getServerId();
	}
	
	// ================
	// PUBLIC INTERFACE
	// ================
	
	/**
	 * 'owner' is reporting that some bot has left the UT2004 server. 
	 *
	 * @param botId
	 */
	public void botLeft(UnrealId botId) {		
		if (registeredBots.containsKey(botId)) {
			unregisterBot(botId);			
		}		
	}
	
	/**
	 * Whether the TCMinaServer is running. 
	 *
	 * @return
	 */
	public ImmutableFlag<Boolean> getRunning() {
		return running.getImmutable();
	}
	
	/**
	 * Starts this {@link TCMinaServer}. 
	 *
	 * @throws PogamutException
	 */
	public void start() throws PogamutException {
		try {
			synchronized(mutex) {
				log.warning("Starting TCMinaServer!");
				log.warning("Opening TC Socket: " + address.getHostName() + ":" + address.getPort());
				
				ioAcceptor = new NioSocketAcceptor();
				
				ioAcceptor.setHandler(this);
				
				ioAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
				
				try {
					ioAcceptor.bind(address);
				} catch (IOException e) {
					stop();
					throw new PogamutException("Failed to open server socket for TCMinaServer at " + address, e, log);				
				}
				
				log.warning("TCMinaServer running!");
				running.setFlag(true);
			}
		} catch (Exception e) {
			log.severe(ExceptionToString.process("Failed to start TCMinaServer!", e));
			stop();
		}
	}
	
	/**
	 * Stops this {@link TCMinaServer}. 
	 *
	 * @throws PogamutException
	 */
	public void stop() {
		synchronized(mutex) {
			log.warning("Stopping TCMinaServer!");
			if (ioAcceptor != null) {
				log.warning("Closing TCMinaServer Socket!");
				try {
					ioAcceptor.setCloseOnDeactivation(true);
					int i = 0;
					for (IoSession ss : ioAcceptor.getManagedSessions().values()) {
						log.warning("Closing managed sessions: " + "(++i)");
						ss.close(true);
					}
					log.warning("Unbinding ioAcceptor...");
					ioAcceptor.unbind();
				} catch (Exception e) {
					e.printStackTrace();
				}
				log.warning("Disposing ioAcceptor...");
				try {
					ioAcceptor.dispose();
				} catch (Exception e) {
					e.printStackTrace();
					ioAcceptor = null;
				}			
			}
			
			botSessions.clear();
			registeredBots.clear();
			teams.clear();
			
			try {
				running.setFlag(false);
			} catch (Exception e) {			
			}
			
			log.warning("TCMinaServer stopped!");
		}
	}
	
	// ================================
	// IMPLEMENTATING {@link IOHandler}
	// ================================

	@Override
	public void exceptionCaught(IoSession session, Throwable exception) throws Exception {
		sessionError(session, ExceptionToString.process("TCMinaServer uncaught exception!", exception));
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message == null) {
			sessionError(session, "NULL message received.");
			return;
		}
		if (!(message instanceof TCMessage)) {
			sessionError(session, "Received message that was not TCMessage.");
			return;
		}
		
		TCMessage tcMessage = (TCMessage)message;
		
		UnrealId source = tcMessage.getSource();
		if (source == null) {
			sessionError(session, "TCMessage.getSource() is NULL.");
			return;
		}
						
		if (!ensureSession(source, session)) {
			// FAILED TO BIND 'session' WITH ITS 'source'
			sessionError(session, "Failed to bind the session with the source.");
			return;
		}
		
		if (tcMessage.getMessageType() == null) {
			invalidRequest(session, TCInfoRequestFailureType.INVALID_MESSAGE_TYPE, "TCMessage.getMessageType() is NULL, cannot process: " + String.valueOf(message), -1, source);
			return;
		}
		if (tcMessage.getTarget() == null) {
			invalidRequest(session, TCInfoRequestFailureType.INVALID_RECIPIENT, "TCMessage.getTarget() is is NULL, cannot process the message: " + String.valueOf(message), -1, source);
			return;
		}
		
		PlayerMessage player = owner.getPlayer(source);
		
		if (registeredBots.containsKey(source)) {
		
			if (player == null) {
				log.warning(source.getStringId() + ": Cannot process the message as the bot has already left the UT2004 server!");
				unregisterBot(source);
				return;
			}
			
			// BOT HAS ITS SESSION BOUND AT THIS POINT
			
		} else {
		
			if (player == null) {
				invalidRequest(session, TCInfoRequestFailureType.UNKNOWN_BOT_UNREALID, "TCServer does not have info that this bot is connected to UT2004. If you are, please try again later (in about 5 secs).", -1, source);
				return;
			}
			
			if (tcMessage.getTarget() == TCRecipient.TC_REQUEST && tcMessage.getMessageType() == TCRequestRegister.MESSAGE_TYPE && tcMessage instanceof TCRequestMessage) {
				TCRequestRegister data;
				try {
					data = (TCRequestRegister)tcMessage.getMessage();					
				} catch (Exception e) {
					invalidRequest(session, TCInfoRequestFailureType.FAILED_TO_DESERIALIZE_REQUEST_DATA, "Invalid request data, failed to deserialize TCMessage.getMessage() as TCRequestData.", -1, source);
					return;
				}
				// REGISTER THE BOT!
				if (!registerBot(session, source, player, (TCRequestMessage)tcMessage, data)) {
					invalidRequest(session, TCInfoRequestFailureType.FAILED_TO_REGISTER_THE_BOT, "TCServer could not register the bot bot with UnrealId '" + source.getStringId() + "'.", -1, source);
					return;
				}
				// BOT HAS BEEN REGISTERED
				return;
			}	
			
		}
		
		switch(tcMessage.getTarget()) {
		case CHANNEL:			
			channelMessage(tcMessage, session, source, player);
			return;
		case GLOBAL:
			globalMessage(tcMessage, session, source, player);
			return;
		case PRIVATE:
			privateMessage(tcMessage, session, source, player);
			return;
		case TEAM:
			teamMessage(tcMessage, session, source, player);
			return;
		case TC_INFO:
			invalidRequest(session, TCInfoRequestFailureType.INVALID_RECIPIENT, "TCServer does not process TC_INFO messages, they are not meant to be sent to TCServer EVER!", -1, source);
			return;
		case TC_REQUEST:
			TCRequestData requestData = null;
			try {
				if (tcMessage instanceof TCRequestMessage) {
					requestData = (TCRequestData)tcMessage.getMessage();					
				} else {
					invalidRequest(session, TCInfoRequestFailureType.FAILED_TO_DESERIALIZE_REQUEST_DATA, "TCMessage recipient is TC_REQUEST, but the message is not TCRequestMessage.", -1, source);
					return;
				}
			} catch (Exception e) {
				invalidRequest(session, TCInfoRequestFailureType.FAILED_TO_DESERIALIZE_REQUEST_DATA, "Invalid request data, failed to deserialize TCMessage.getMessage() as TCRequestData.", -1, source);
				return;
			}
			requestMessage((TCRequestMessage)tcMessage, requestData, session, source, player);			
			return;
		}
	}
	
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
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
	
	private void globalMessage(TCMessage message, IoSession session, UnrealId source, PlayerMessage player) {
		if (message.isExcludeMyselfIfApplicable()) {
			sendGlobalExcept(message, source);
		} else {
			sendGlobalExcept(message, null);
		}
	}

	private void teamMessage(TCMessage message, IoSession session, UnrealId source, PlayerMessage player) {
		if (message.isExcludeMyselfIfApplicable()) {
			sendTeamExcept(message, player.getTeam(), source);
		} else {
			sendTeamExcept(message, player.getTeam(), null);
		}
	}
	
	private void channelMessage(TCMessage message, IoSession session, UnrealId source, PlayerMessage player) {
		if (message.isExcludeMyselfIfApplicable()) {
			sendChannelExcept(message, player.getTeam(), message.getChannelId(), source);
		} else {
			sendChannelExcept(message, player.getTeam(), message.getChannelId(), null);
		}
	}
	
	private void privateMessage(TCMessage message, IoSession session, UnrealId source, PlayerMessage player) {
		sendPrivate(message, message.getTargetId());
	}

	private void requestMessage(TCRequestMessage message, TCRequestData data, IoSession session, UnrealId source, PlayerMessage player) {
		if (data == null) {
			invalidRequest(session, TCInfoRequestFailureType.FAILED_TO_DESERIALIZE_REQUEST_DATA, "Request data are NULL", -1, source);
			return;
		}
		
		if (data instanceof TCRequestCreateChannel) {
			requestCreateChannel(message, (TCRequestCreateChannel)data, session, source, player);
		} else
		if (data instanceof TCRequestDestroyChannel) {
			requestDestroyChannel(message, (TCRequestDestroyChannel)data, session, source, player);
		} else
		if (data instanceof TCRequestGetStatus) {
			requestGetStatus(message, (TCRequestGetStatus)data, session, source, player);
		} else
		if (data instanceof TCRequestJoinChannel) {
			requestJoinChannel(message, (TCRequestJoinChannel)data, session, source, player);
		} else
		if (data instanceof TCRequestLeaveChannel) {
			requestLeaveChannel(message, (TCRequestLeaveChannel)data, session, source, player);
		} else
		if (data instanceof TCRequestRegister) {
			throw new RuntimeException("Should not reach here!");
		} else {
			invalidRequest(session, TCInfoRequestFailureType.FAILED_TO_PROCESS_REQUEST, "Unknown request.", data.getRequestId(), source);
		}
	}
	
	// ================
	// REQUEST HANDLERS
	// ================

	private void requestCreateChannel(TCRequestMessage message, TCRequestCreateChannel data, IoSession session, UnrealId source, PlayerMessage player) {		
		synchronized(mutex) {
			if (!running.getFlag()) return;
			TCTeam team = teams.get(player.getTeam());
			synchronized(team) {
				Map<Integer, TCChannel> channels = team.getChannels();
				synchronized(channels) {
					int newChannelId = team.getNextChannelId();
					TCChannel newChannel = new TCChannel();
					newChannel.setChannelId(newChannelId);
					newChannel.setCreator(source);
					newChannel.getConnectedBots().add(source);
					channels.put(newChannelId, newChannel);
									
					TCInfoTeamChannelCreated infoDataTeam = new TCInfoTeamChannelCreated(-1, owner.getSimTime());
					infoDataTeam.setChannel(newChannel.clone());
					TCInfoMessage infoMessageTeam = new TCInfoMessage(getMyId(), infoDataTeam);
					sendTeamExcept(infoMessageTeam, player.getTeam(), source);
					
					TCInfoTeamChannelCreated infoDataRequestee = new TCInfoTeamChannelCreated(data.getRequestId(), owner.getSimTime());
					infoDataRequestee.setChannel(newChannel.clone());
					TCInfoMessage infoMessageRequestee = new TCInfoMessage(getMyId(), infoDataRequestee);
					sendPrivate(infoMessageRequestee, source);				
				}
			}
		}
	}
	
	private void requestDestroyChannel(TCRequestMessage message, TCRequestDestroyChannel data, IoSession session, UnrealId source, PlayerMessage player) {
		synchronized(mutex) {
			if (!running.getFlag()) return;
			TCTeam team = teams.get(player.getTeam());
			synchronized(team) {
				Map<Integer, TCChannel> channels = team.getChannels();
				synchronized(channels) {
					TCChannel channel = channels.get(data.getChannelId());
					if (channel == null) {
						invalidRequest(session, TCInfoRequestFailureType.CHANNEL_DOES_NOT_EXIST, "Cannot destroy team " + player.getTeam() + " channel " + data.getChannelId() + " as it does not exist.", data.getRequestId(), source);
						return;
					}
					if (channel.getCreator() != source) {
						invalidRequest(session, TCInfoRequestFailureType.CHANNEL_NOT_OWNED_BY_YOU, "Cannot destroy team " + player.getTeam() + " channel " + data.getChannelId() + " as it is not OWNED by you!", data.getRequestId(), source);
						return;
					}					
					
					channels.remove(data.getChannelId());
					
					TCInfoTeamChannelDestroyed infoDataTeam = new TCInfoTeamChannelDestroyed(-1, owner.getSimTime());
					infoDataTeam.setChannelId(data.getChannelId());
					infoDataTeam.setDestroyer(source);
					TCInfoMessage infoMessageTeam = new TCInfoMessage(getMyId(), infoDataTeam);
					sendTeamExcept(infoMessageTeam, player.getTeam(), source);
					
					TCInfoTeamChannelDestroyed infoDataRequestee = new TCInfoTeamChannelDestroyed(data.getRequestId(), owner.getSimTime());
					infoDataRequestee.setChannelId(data.getChannelId());
					infoDataRequestee.setDestroyer(source);
					TCInfoMessage infoMessageRequestee = new TCInfoMessage(getMyId(), infoDataRequestee);
					sendPrivate(infoMessageRequestee, source);
				}
			}
		}
	}
	
	private void requestGetStatus(TCRequestMessage message, TCRequestGetStatus data, IoSession session, UnrealId source, PlayerMessage player) {
		TCInfoStatus infoData = new TCInfoStatus(data.getRequestId(), owner.getSimTime());		
		
		synchronized(registeredBots) {
			infoData.setAllBots(new ArrayList<UnrealId>(registeredBots.keySet()));
		}
		
		TCTeam team = teams.get(player.getTeam());
		synchronized(team) {
			infoData.setTeam(team.clone());
		}
		
		TCInfoMessage infoMessage = new TCInfoMessage(getMyId(), infoData);
		
		sendPrivate(infoMessage, source);
	}
	
	private void requestJoinChannel(TCRequestMessage message, TCRequestJoinChannel data, IoSession session, UnrealId source, PlayerMessage player) {
		synchronized(mutex) {
			if (!running.getFlag()) return;
			TCTeam team = teams.get(player.getTeam());
			
			TCChannel channel = team.getChannels().get(data.getChannelId());
			
			if (channel == null) {
				invalidRequest(session, TCInfoRequestFailureType.CHANNEL_DOES_NOT_EXIST, "You cannot join team " + player.getTeam() + " channel " + data.getChannelId() + " as it does not exist.", data.getRequestId(), source);
				return;
			}
			
			if (channel.getConnectedBots().contains(source)) {
				invalidRequest(session, TCInfoRequestFailureType.CHANNEL_DOES_NOT_EXIST, "You are already coonnected to team " + player.getTeam() + " channel " + data.getChannelId() + ".", data.getRequestId(), source);
				return;
			}
			
			synchronized(channel) {
				channel.getConnectedBots().add(source);
				
				TCInfoTeamChannelBotJoined infoDataTeam = new TCInfoTeamChannelBotJoined(-1, owner.getSimTime());
				infoDataTeam.setChannelId(data.getChannelId());
				infoDataTeam.setBotId(source);
				TCInfoMessage infoMessageTeam = new TCInfoMessage(getMyId(), infoDataTeam);
				sendTeamExcept(infoMessageTeam, player.getTeam(), source);
				
				TCInfoTeamChannelBotJoined infoDataRequestee = new TCInfoTeamChannelBotJoined(data.getRequestId(), owner.getSimTime());
				infoDataRequestee.setChannelId(data.getChannelId());
				infoDataRequestee.setBotId(source);
				TCInfoMessage infoMessageRequestee = new TCInfoMessage(getMyId(), infoDataRequestee);
				sendPrivate(infoMessageRequestee, source);			
			}
		}
	}
	
	private void requestLeaveChannel(TCRequestMessage message, TCRequestLeaveChannel data, IoSession session, UnrealId source, PlayerMessage player) {
		synchronized(mutex) {
			if (!running.getFlag()) return;
			TCTeam team = teams.get(player.getTeam());
			
			TCChannel channel = team.getChannels().get(data.getChannelId());
			
			if (channel == null) {
				invalidRequest(session, TCInfoRequestFailureType.CHANNEL_DOES_NOT_EXIST, "You cannot leave team " + player.getTeam() + " channel " + data.getChannelId() + " as it does not exist.", data.getRequestId(), source);
				return;
			}
			
			if (!channel.getConnectedBots().contains(source)) {
				invalidRequest(session, TCInfoRequestFailureType.NOT_CONNECTED_TO_CHANNEL, "You are not coonnected to team " + player.getTeam() + " channel " + data.getChannelId() + ".", data.getRequestId(), source);
				return;
			}
			
			synchronized(channel) {
				channel.getConnectedBots().remove(source);
				
				TCInfoTeamChannelBotLeft infoDataTeam = new TCInfoTeamChannelBotLeft(-1, owner.getSimTime());
				infoDataTeam.setChannelId(data.getChannelId());
				infoDataTeam.setBotId(source);
				TCInfoMessage infoMessageTeam = new TCInfoMessage(getMyId(), infoDataTeam);
				sendTeamExcept(infoMessageTeam, player.getTeam(), source);
				
				TCInfoTeamChannelBotLeft infoDataRequestee = new TCInfoTeamChannelBotLeft(data.getRequestId(), owner.getSimTime());
				infoDataRequestee.setChannelId(data.getChannelId());
				infoDataRequestee.setBotId(source);
				TCInfoMessage infoMessageRequestee = new TCInfoMessage(getMyId(), infoDataRequestee);
				sendPrivate(infoMessageRequestee, source);			
			}
		}
	}
	
	// ============
	// BROADCASTING
	// ============

	private void sendGlobalExcept(TCMessage message, UnrealId except) {
		synchronized(botSessions) {
			for (Entry<UnrealId, IoSession> botSessionEntry : botSessions.entrySet()) {
				if (botSessionEntry.getKey() == except) continue;
				synchronized(botSessionEntry.getValue()) {
					botSessionEntry.getValue().write(message);
				}
			}
		}
	}
	
	private void sendTeamExcept(TCMessage message, int teamNum, UnrealId except) {
		TCTeam team = teams.get(teamNum);
		if (team == null) return;
		synchronized(team) {
			synchronized(team.getConnectedBots()) {
				for (UnrealId target : team.getConnectedBots()) {
					if (target == except) continue;
					IoSession targetSession = botSessions.get(target);
					synchronized(targetSession) {
						targetSession.write(message);
					}
				}
			}
		}
	}
	
	private void sendChannelExcept(TCMessage message, int teamNum, int channelId, UnrealId except) {
		TCTeam team = teams.get(teamNum);
		if (team == null) return;
		TCChannel channel = team.getChannels().get(channelId);
		if (channel == null) return;
		synchronized(channel) {
			synchronized(channel.getConnectedBots()) {
				for (UnrealId target : channel.getConnectedBots()) {
					if (target == except) continue;
					IoSession targetSession = botSessions.get(target);
					synchronized(targetSession) {
						targetSession.write(message);
					}
				}
			}
		}
	}
	
	private void sendPrivate(TCMessage message, UnrealId target) {
		IoSession targetSession = botSessions.get(target);
		if (targetSession != null) {
			synchronized(targetSession) {
				targetSession.write(message);
			}
		}
	}
	
	private void sendInfo(TCInfoMessage message, IoSession session) {
		synchronized(session) {
			session.write(message);
		}
	}
	
	// ======================================
	// ENSURE SESSION / UN/REGISTER BOT UTILS
	// ======================================
	
	/**
	 * Returns whether the session is OK.
	 *
	 * @param source
	 * @param session
	 * @return
	 */
	private boolean ensureSession(UnrealId source, IoSession session) {
		if (source == null) {
			return false;
		}
		if (session == null) {
			return false;
		}
		IoSession existing = botSessions.get(source);
		if (existing == null) {
			// BIND NEW SESSION
			synchronized(mutex) {
				if (!running.getFlag()) return false;
				existing = botSessions.get(source);
				if (existing == null) {
					log.warning(source.getStringId() + ": Binding new session for this bot.");
					synchronized(botSessions) {
						botSessions.put(source, session);
					}
					return true;
				}
			}
		}
		// SESSION ALREADY EXIST FOR THE SOURCE!
		if (existing != session) {
			log.warning(source.getStringId() + ": Multiple sessions per 1 bot detected, invalid! (Have you started more than one TCMinaClient per bot instance?)");
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Registers the bot into all internal data structures.
	 *  
	 * @param session
	 * @param source
	 * @param player
	 * @param tcMessage
	 * @param registerData
	 * @return whether the bot has been successfully registered
	 */
	private boolean registerBot(IoSession session, UnrealId source, PlayerMessage player, TCRequestMessage tcMessage, TCRequestRegister registerData) {
		TCTeam team = null;
		TCInfoStatus data = null;
		
		synchronized(mutex) {			
			if (!running.getFlag()) return false;
			
			log.warning(source.getStringId() + ": Registering this bot for team " + player.getTeam());
			
			team = teams.get(player.getTeam());
		
			synchronized(team) {
				synchronized(team.getConnectedBots()) {
					team.getConnectedBots().add(source);
				}
			}
			
			synchronized(registeredBots) {
				registeredBots.put(source, player);
			}
			
			// BOT JOINED
			{
				log.info(source.getStringId() + ": Bot joined the TC, notifying connected bots.");
				
				TCInfoBotJoined dataJoined = new TCInfoBotJoined(-1, owner.getSimTime());
				dataJoined.setBotId(source);
				dataJoined.setTeam(player.getTeam());
				
				TCInfoMessage messageJoined = new TCInfoMessage(getMyId(), dataJoined);
				sendGlobalExcept(messageJoined, source);
			}
			
			// SEND INITIAL STATUS OF TC SERVER
			log.info(source.getStringId() + ": Sending initial TCInfoStatus to this newly joined bot.");
			data = new TCInfoStatus(-1, owner.getSimTime());
			synchronized(registeredBots) {
				data.setAllBots(new ArrayList<UnrealId>(registeredBots.keySet()));
			}
			synchronized(team) {
				data.setTeam(team.clone());
			}
			
			TCInfoMessage message = new TCInfoMessage(getMyId(), data);
			
			sendInfo(message, session);
			
			return true;
		}
	}

	private void unregisterBot(UnrealId botId) {
		if (botId == null) return;
		
		synchronized(mutex) {
			
			if (!running.getFlag()) return;
			if (!registeredBots.containsKey(botId)) return;
			
			log.warning(botId.getStringId() + ": Unregistering bot.");
			
			PlayerMessage player;
			synchronized(registeredBots) {
				player = registeredBots.remove(botId);
			}
		
			if (player == null) {
				log.severe(botId.getStringId() + ": Could not FULLY unregister bot as we do not have PlayerMessage for it! Data structures corrupted.");
			} else {
				TCTeam team = teams.get(player.getTeam());
				if (team == null) {
					log.severe(botId.getStringId() + ": Could not FULLY unregister bot as we do not have TCTeam[" + player.getTeam() + "] for it!");
				} else {
					synchronized(team) {					
						team.getConnectedBots().remove(botId);
						Set<TCChannel> toRemove = new HashSet<TCChannel>();
						synchronized(team.getChannels()) {
							for (TCChannel channel : team.getChannels().values()) {
								if (channel.getCreator().equals(botId)) {
									toRemove.add(channel);
								} else {
									synchronized(channel) {
										channel.getConnectedBots().remove(botId);
									}
								}
							}	
							// DESTROY CHANNELS OWNED BY THE BOT
							for (TCChannel channel : toRemove) {
								team.getChannels().remove(channel.getChannelId());
								
								// INFORM THE REST OF THE TEAM
								TCInfoTeamChannelDestroyed infoDataTeam = new TCInfoTeamChannelDestroyed(-1, owner.getSimTime());
								infoDataTeam.setChannelId(channel.getChannelId());
								infoDataTeam.setDestroyer(botId);
								TCInfoMessage infoMessageTeam = new TCInfoMessage(getMyId(), infoDataTeam);
								sendTeamExcept(infoMessageTeam, player.getTeam(), botId);
							}
						}
					}
				}
			}
			
			synchronized(botSessions) {
				IoSession session = botSessions.remove(botId);				
				if (session == null) {
					log.severe(botId.getStringId() + ": Could not FULLY unregister bot as we do not have IoSession for it! Data structures corrupted?");
				} else {
					session.close(true);
				}
			}	
			
			TCInfoBotLeft infoData = new TCInfoBotLeft(-1, owner.getSimTime());
			infoData.setBotId(botId);
			infoData.setTeam(player == null ? -1 : player.getTeam());
			TCInfoMessage infoMessage = new TCInfoMessage(getMyId(), infoData);
			sendGlobalExcept(infoMessage, botId);			
		}		
	}

	// =================================
	// INVALID REQUEST & ERROR REPORTING
	// =================================
	
	/**
	 * @param session
	 * @param failureType may be null
	 * @param reason may be null
	 * @param requestId
	 * @param source may be null
	 */
	private void invalidRequest(IoSession session, TCInfoRequestFailureType failureType, String reason, long requestId, UnrealId source) {
		if (session == null) return;
		
		if (failureType == null) failureType = TCInfoRequestFailureType.GENERIC_FAILURE;
		if (reason == null || reason.isEmpty()) reason = "No info.";
				
		log.warning((source == null ? "" : source.getStringId()) + ": InvalidRequest[" + failureType + "] " + reason);
		
		TCInfoRequestFailed data = new TCInfoRequestFailed(requestId, owner.getSimTime());
		data.setFailureType(failureType);
		
		TCInfoMessage message = new TCInfoMessage(getMyId(), data);
		
		try {
			synchronized(session) {
				session.write(message);
			}
		} catch (Exception e) { // we might have been stopped mean-while...
			if (running.getFlag()) {
				log.warning(ExceptionToString.process(source.getStringId() + ": Failed to send message to the bot: " + message, e));
			}
		}
	}
	
	private void sessionError(IoSession session, String reason) {
		if (session == null) return;		
		
		synchronized(mutex) {
			
			if (!running.getFlag()) return;
			
			log.warning("SessionError: " + reason);		
			
			UnrealId source = null;
			
			synchronized(botSessions) {
				for (Entry<UnrealId, IoSession> entry : botSessions.entrySet()) {
					if (entry.getValue() == session) {
						source = entry.getKey();
						break;
					}
				}
			}	
			
			if (source != null) {
				botLeft(source); // will close the session as well!
			} else {			
				synchronized(session) {
					try {
						session.close(true);
					} catch (Exception e) {
						log.warning(ExceptionToString.process("Could not close the session... ???", e));
					}
				}
			}
			
		}
		
	}
	
}
