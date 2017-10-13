package cz.cuni.amis.pogamut.base.communication.connection;

import java.io.Reader;

import cz.cuni.amis.pogamut.base.communication.connection.exception.ConnectionException;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Interface that returns a reader that can be used to get messages from the world.
 * @author Jimmy
 */
public interface IWorldReaderProvider extends IComponent {
	
	/**
	 * To be used by (usually) IParser upon IParser.start() method call to obtain a world reader.
	 * <p><p>
	 * Use reader.close() to close the connection.
	 * 
	 * @return
	 * @throws ConnectionException
	 */
	public WorldReader getReader() throws CommunicationException;
	
}
