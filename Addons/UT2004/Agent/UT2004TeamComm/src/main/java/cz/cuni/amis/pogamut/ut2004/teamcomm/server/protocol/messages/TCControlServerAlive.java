package cz.cuni.amis.pogamut.ut2004.teamcomm.server.protocol.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageField;
import cz.cuni.amis.pogamut.ut2004.communication.messages.custom.ControlMessageType;
import cz.cuni.amis.pogamut.ut2004.teamcomm.server.UT2004TCServer;

/**
 * {@link UT2004TCServer} hearbeat... 
 * @author Jimmy
 */
@ControlMessageType(type="TCControlServerAlive")
public class TCControlServerAlive extends TCControlMessage {

	@ControlMessageField(index=1)
	private String host;
	
	@ControlMessageField(index=1)
	private Integer port;
	
	@ControlMessageField(index=2)
	private UnrealId serverId;
	
	public TCControlServerAlive() {
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public UnrealId getServerId() {
		return serverId;
	}

	public void setServerId(UnrealId serverId) {
		this.serverId = serverId;
	}
	
}
