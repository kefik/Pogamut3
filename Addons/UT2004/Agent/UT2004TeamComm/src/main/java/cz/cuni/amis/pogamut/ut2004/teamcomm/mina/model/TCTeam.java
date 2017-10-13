package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.teamcomm.mina.server.TCMinaServer;

public class TCTeam implements Serializable, Cloneable {

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -6503924718003317176L;

	private int team = 0;
	
	private Set<UnrealId> connectedBots = new HashSet<UnrealId>();
	
	private Map<Integer, TCChannel> channels = new HashMap<Integer, TCChannel>();
	
	/**
	 * TO BE USED BY {@link TCMinaServer} ONLY! 
	 */
	private transient int lastChannelId = 0;
	
	public TCTeam() {		
	}
	
	public TCTeam clone() {
		TCTeam result = new TCTeam();
		synchronized(connectedBots) {
			result.getConnectedBots().addAll(connectedBots);
		}
		synchronized(channels){
			for (TCChannel channel : channels.values()) {
				synchronized(channel) {
					result.getChannels().put(channel.getChannelId(), channel.clone());
				}
			}
		}
		return result;
	}

	public TCTeam(Integer team) {
		this.team = team;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public Set<UnrealId> getConnectedBots() {
		return connectedBots;
	}

	public void setConnectedBots(Set<UnrealId> connectedBots) {
		this.connectedBots = connectedBots;
	}

	public Map<Integer, TCChannel> getChannels() {
		return channels;
	}

	public void setChannels(Map<Integer, TCChannel> channels) {
		this.channels = channels;
	}

	public int getNextChannelId() {
		return ++lastChannelId;
	}

}
