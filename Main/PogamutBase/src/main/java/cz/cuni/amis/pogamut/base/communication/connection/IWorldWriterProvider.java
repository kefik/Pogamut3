package cz.cuni.amis.pogamut.base.communication.connection;

import java.io.Writer;

import com.google.inject.ImplementedBy;

import cz.cuni.amis.pogamut.base.communication.connection.exception.ConnectionException;
import cz.cuni.amis.pogamut.base.communication.exception.CommunicationException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;

/**
 * Interface that provides a writer that can be used to send commands to the world.
 * @author Jimmy
 */
public interface IWorldWriterProvider {
	
	/**
	 * To be used on ICommandSerializer.start() method to obtain a writer into the world.
	 * <p><p>
	 * Use writer.close() to stop the writer.
	 * 
	 * @return
	 * @throws ConnectionException
	 */
	public WorldWriter getWriter() throws CommunicationException;
	
}
