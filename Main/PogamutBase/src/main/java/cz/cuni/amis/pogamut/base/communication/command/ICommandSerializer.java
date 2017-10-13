package cz.cuni.amis.pogamut.base.communication.command;

import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Command serializer that transforms the command message into data that can be sent to (and understood by) virtual world.
 * 
 * @author Jimmy
 */
public interface ICommandSerializer<DATA> {
	
	/**
	 * Serialize command into some form that may be sent to the world simulator.
	 * 
	 * @param command
	 */
	public DATA serialize(CommandMessage command);

}
