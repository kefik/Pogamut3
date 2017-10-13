package cz.cuni.amis.pogamut.base.communication.connection;

import com.google.inject.ImplementedBy;

import cz.cuni.amis.pogamut.base.communication.connection.exception.AlreadyConnectedException;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.component.IControllable;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Interface for the connection to a remote world.
 * <p><p>
 * Simple methods that are needed to make connection read/write messages.
 * <p><p>
 * Implementor is required to implement the interface as reusable. Meaning that one
 * instance of object may serve first as the connection to one server and then to another.
 * <p><p>
 * Control the object through {@link IControllable} interface.<p>
 * Example scenario 1:
 * <p><p>
 * <ol>
 * <li>instantiate</li>
 * <li>set address through setAddress()</li>
 * <li>start()</li>
 * <li>do some work with reader/writer</li>
 * <li>stop()</li>
 * </ol><p>
 * Or more advance scenario 2 (reconnection):
 * <ol>
 * <li>instantiate</li>
 * <li>set address through setAddress()</li>
 * <li>start()</li>
 * <li>do some work with reader/writer</li>
 * <li>stop()</li>
 * <li>set address again</li>
 * <li>start() ... this connects again</li>
 * <li>do some work on other server</li>
 * <li>stop()</li>
 * </ol>
 * There is a nice abstract implementation AbstractConnection where all you have to do 
 * is implement inner methods for connect / close / getting raw readers and writers.
 * 
 * @author Jimmy
 */
public interface IWorldConnection<ADDRESS extends IWorldConnectionAddress> extends IWorldReaderProvider, IWorldWriterProvider {
	
	/**
	 * Sets whether to log the messages that are sent/received through writer/reader. Note
	 * that logging those messages is costly operation and may slow things a lot. That's
	 * why this is <b>OFF as DEFAULT</b>. 
	 * 
	 * @param logMessages
	 */
	public void setLogMessages(boolean logMessages);
	
	/**
	 * Get the descriptor of the connection's remote side.
	 * @return
	 */
	public ADDRESS getAddress();
	
	/**
	 * Sets the connection address to the object.
	 * <p><p>
	 * If the object is connected - it throws exception {@link AlreadyConnectedException}
	 * 
	 * @param address
	 * @throws CommunicationException
	 */
	public void setAddress(ADDRESS address) throws CommunicationException;
		
}