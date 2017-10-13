package cz.cuni.amis.pogamut.ut2004.teamcomm.mina.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;

public class TCChannel implements Serializable, Cloneable {

	/**
	 * Auto-generated
	 */
	private static final long serialVersionUID = -282321477236910161L;
	
	private int channelId = 0;
	
	private UnrealId creator = null;
	
	private Set<UnrealId> connectedBots = new HashSet<UnrealId>();
	
	public TCChannel() {		
	}
	
	public TCChannel clone() {
		TCChannel result = new TCChannel();
		result.creator = creator;
		result.channelId = channelId;
		synchronized(connectedBots) {
			result.getConnectedBots().addAll(connectedBots);
		}
		return result;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public UnrealId getCreator() {
		return creator;
	}

	public void setCreator(UnrealId creator) {
		this.creator = creator;
	}

	public Set<UnrealId> getConnectedBots() {
		return connectedBots;
	}

	public void setConnectedBots(Set<UnrealId> connectedBots) {
		this.connectedBots = connectedBots;
	}
	
}
