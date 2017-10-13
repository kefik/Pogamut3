package cz.cuni.amis.pogamut.base.communication.messages;

/**
 * Command is a wrapper for agent-effector which contains description of the command that the agent should carry out
 * inside virtual world.
 * 
 * @author Jimmy
 */
public abstract class CommandMessage {
	
	public String toString() {
		return "CommandMessage[" + getClass().getSimpleName() + "]";
	}

}
