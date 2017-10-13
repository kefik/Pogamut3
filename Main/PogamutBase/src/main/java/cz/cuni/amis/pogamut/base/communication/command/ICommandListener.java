package cz.cuni.amis.pogamut.base.communication.command;

import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.utils.listener.IListener;

/**
 * This is an interface you need to implement, if you want to listen for a certain type of the command message that is sent
 * by the agent. 
 * 
 * @author Jimmy
 */
public interface ICommandListener<CMD extends CommandMessage> extends IListener<CMD> {
	
}
