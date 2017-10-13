package cz.cuni.amis.pogamut.base.utils;

import java.io.InputStreamReader;

import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogClient;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;

/**
 * The key is returned via {@link PogamutProperty#toString()} method.
 * 
 * @author Jimmy
 */
public enum PogamutProperty {
	

	/**
	 * Default logging level for the bot, set inside {@link AgentLogger#AgentLogger(cz.cuni.amis.pogamut.base.agent.IAgentId)} constructor.
	 */
	POGAMUT_LOGGER_LEVEL_DEFAULT("pogamut.logger.level.default"),

	/**
	 * Domain used for MBeans registered from the Pogamut.
	 */
	POGAMUT_JMX_DOMAIN("pogamut.jmx.domain"), 
	
	/**
	 * Subdomain used for agent MBean registered from the Pogamut.
	 */
	POGAMUT_JMX_SUBDOMAIN("pogamut.jmx.subdomain"),
	
	/**
	 * Port of the RMI registry.
	 */
	POGAMUT_JMX_SERVER_RMI_PORT("pogamut.jmx.server.rmi.port"),

	/**
	 * address where the JMX server will run this should be the global IP since
	 * other tools might want to use this address if blank than some IP will be choosen
	 * automatically.
	 */
	POGAMUT_JMX_SERVER_ADDRESS("pogamut.jmx.server.address"),
	
	/**
	 * Changes the log level of the {@link NetworkLogManager} singleton and {@link NetworkLogClient} instances.
	 */
	POGAMUT_NETWORK_LOG_MANAGER_AND_CLIENT_LEVEL("pogamut.network.log.level"),
	
	/**
	 * Which encoding to use when reading Strings, use things like windows-1250, utf8, etc... anything that Charset.forName() accepts at your platform
     * 'default' is reserved for Charset.defaultCharset().
     * <br/><br/>
     * Used by {@link SocketConnection} for {@link InputStreamReader#InputStreamReader(java.io.InputStream, java.nio.charset.Charset)} and Writer.
	 */
	POGAMUT_SOCKETCONNECTION_ENCODING("pogamut.socketconnection.encoding")
//
//	/**
//	 * Whether to use NormalizerAscii to convert non-ascii chars to ascii. Used by {@link SocketConnection}. NOT IMPLEMENTED YET
//	 */
//	POGAMUT_SOCKETCONNECTION_NORMALIZE_ASCII("pogamut.socketconnection.normalize.ascii")
	;

	private String key;

	private PogamutProperty(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
	
	public String toString() {
		return key;
	}
	
}
