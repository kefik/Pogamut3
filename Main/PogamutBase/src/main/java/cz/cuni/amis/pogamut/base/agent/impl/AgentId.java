package cz.cuni.amis.pogamut.base.agent.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.server.UID;
import java.util.Random;
import java.util.UUID;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Default AgentId providing unique id based on {@link UID} and IP of the host.
 * @author Jimmy
 */
@AgentScoped
public class AgentId implements IAgentId {

	public static final String AGENT_NAME_DEPENDENCY = "AgentName";
	
	/**
	 * Provides unique number of the agent instance in the scope of JVM.
	 */
	private static int agentCounter = 0;
	
	protected static Random random = new Random(System.currentTimeMillis());
	
	public static final String NO_NAME = "no_name";

	private Flag<String> name = new Flag<String>(NO_NAME);

	private Token token;
	
	public AgentId() {
		UUID uuid = null;
		try {
			synchronized(random) {
				uuid = new UUID(random.nextLong(), random.nextLong());
			}
			this.token = Tokens.get(getClass().getSimpleName() + "-" + (++agentCounter) + "@"
					+ InetAddress.getLocalHost().getCanonicalHostName() + "/" + uuid.toString());
		} catch (UnknownHostException ex) {
			if (uuid == null) throw new IllegalStateException("Can't initialize AgentId, instantiation of UUID failed.");
			this.token = Tokens.get(getClass().getSimpleName() + "-" + (++agentCounter) + "@unknownHost/" + uuid.toString());
		}
	}
	
	@Inject
	public AgentId(@Named(AGENT_NAME_DEPENDENCY) String agentName) {
		NullCheck.check(agentName, "name");
		UUID uuid = null;
		try {
			synchronized(random) {
				uuid = new UUID(random.nextLong(), random.nextLong());
			}
			this.token = Tokens.get(agentName + "-" + (++agentCounter) + "@"
					+ InetAddress.getLocalHost().getCanonicalHostName() + "/" + uuid.toString());
		} catch (UnknownHostException ex) {
			this.token = Tokens.get(getClass().getSimpleName() + "-" + (++agentCounter) + "@unknownHost/" + uuid.toString());
		}
		getName().setFlag(agentName);
	}
	
	@Override
	public int hashCode() {
		return this.token.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof AgentId)) return false;
		return token.equals(((AgentId)obj).token);
	}
	
	@Override
	public Flag<String> getName() {
		return name;
	}

	@Override
	public long[] getIds() {
		return token.getIds();
	}

	@Override
	public String getToken() {
		return token.getToken();
	}
	
	@Override
	public String toString() {
		return "AgentId[" + getToken() + "]";
	}
	
}
