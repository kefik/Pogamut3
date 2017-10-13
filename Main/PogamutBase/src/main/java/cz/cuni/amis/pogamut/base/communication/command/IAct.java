package cz.cuni.amis.pogamut.base.communication.command;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldWriterProvider;
import cz.cuni.amis.pogamut.base.communication.messages.CommandMessage;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;

/**
 * IAct provides a way to send command messages to the world allowing you to attach
 * listeners to outgoing commands.
 * 
 * @author Jimmy
 */
public interface IAct extends IComponent {
	
	/**
	 * Execute an action in the world.
	 * <p><p>
	 * May block.
	 * <p><p>
	 * Should serialize the command object and send it through the writer that is usually provided by {@link IWorldWriterProvider}.
	 * 
	 * @return parsed message
	 */
	public void act(CommandMessage command) throws ComponentNotRunningException, ComponentPausedException;
	
	/**
	 * Attach listener to outgoing commands from body.
	 * <p><p>
	 * After the command is sent to the world, listener will be notified.
	 * 
	 * @param commandClass which command you want to listen to
	 * @param listener
	 */
	public void addCommandListener(Class commandClass, ICommandListener listener);
	
	/**
	 * Whether the listener is listening for commands of commandClass.
	 * 
	 * @param commandClass
	 * @param listener
	 * @return
	 */
	public boolean isCommandListening(Class commandClass, ICommandListener listener);
	
	/**
	 * Remove the listener to outgoing commands.
	 * 
	 * @param commandClass which command you want to listen to
	 * @param listener
	 */
	public void removeCommandListener(Class commandClass, ICommandListener listener);

}
