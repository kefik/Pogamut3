package cz.cuni.amis.pogamut.base.communication.connection.impl.socket;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.connection.exception.ConnectionException;
import cz.cuni.amis.pogamut.base.communication.connection.impl.AbstractConnection;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.DefaultPogamutPlatform;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutPlatform;
import cz.cuni.amis.pogamut.base.utils.PogamutPlatformProxy;
import cz.cuni.amis.pogamut.base.utils.PogamutProperty;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import java.util.logging.Level;

@AgentScoped
public class SocketConnection extends AbstractConnection<ISocketConnectionAddress> {
	
	public static final String CONNECTION_DEPENDENCY = "ConnectionDependency";
	public static final String CONNECTION_ADDRESS_DEPENDENCY = "ConnectionAddressDependency";
	
	private Socket socket = null;
	
	private InputStreamReader socketReader = null;
	
	private OutputStreamWriter socketWriter = null;
	
	private Charset encoding;

//	private Boolean useAsciiNormalizer;
	
	@Inject
	public SocketConnection(@Named(CONNECTION_ADDRESS_DEPENDENCY) ISocketConnectionAddress address, @Named(CONNECTION_DEPENDENCY) ComponentDependencies dependencies, IComponentBus bus, IAgentLogger logger) {
		super(address, dependencies, bus, logger);
		initProperties();
	}
	
	public SocketConnection(ComponentDependencies dependencies, IComponentBus bus, IAgentLogger logger) {
		super(dependencies, bus, logger);
		initProperties();
	}
	
	private void initProperties() {
		String encoding = Pogamut.getPlatform().getProperty(PogamutProperty.POGAMUT_SOCKETCONNECTION_ENCODING.getKey());
		if (encoding == null) {
			log.warning("Missing property: " + PogamutProperty.POGAMUT_SOCKETCONNECTION_ENCODING.getKey() + ", using default encoding 'default'");
			encoding = "default";			
		}
		if (encoding.equals("default")) {
			encoding = Charset.defaultCharset().name();
		}
		
		log.info("Using encoding: " + encoding);
		this.encoding = Charset.forName(encoding);

//		this.useAsciiNormalizer = Pogamut.getPlatform().getBooleanProperty(PogamutProperty.POGAMUT_SOCKETCONNECTION_NORMALIZE_ASCII.getKey());
//		if (useAsciiNormalizer == null) {
//			log.warning("Missing property: " + PogamutProperty.POGAMUT_SOCKETCONNECTION_NORMALIZE_ASCII.getKey() + ", setting to FALSE");
//			useAsciiNormalizer = false;
//		}
	}

	@Override
	protected Reader getConnectionReader() throws ConnectionException {
		return socketReader;
	}

	@Override
	protected Writer getConnectionWriter() throws ConnectionException {		
		return socketWriter;
	}

	@Override
	protected void unsyncClose() {
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				if (log.isLoggable(Level.SEVERE)) log.severe("Can't close socket - " + e.getMessage());
			}
			try {
				socketReader.close();
			} catch (Exception e) {
				if (log.isLoggable(Level.SEVERE)) log.severe("Can't close socket reader - " + e.getMessage());
			}
			try {
				socketWriter.close();
			} catch (Exception e) {
				if (log.isLoggable(Level.SEVERE)) log.severe("Can't close socket writer - " + e.getMessage());
			}
			socket = null;
		}
	}

	@Override
	protected void unsyncConnect(ISocketConnectionAddress address) throws ConnectionException {
		log.info("Using encoding: " + encoding);
		socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(address.getHost(), address.getPort()));
			socketReader = new InputStreamReader(socket.getInputStream(), encoding);
			socketWriter = new OutputStreamWriter(socket.getOutputStream(), encoding);
		} catch (IOException e) {
			throw new ConnectionException(e + " (" + address.getHost() + ":" + address.getPort() + ")", log);
		}
	}
	
	@Override
	public String toString() {
		return "SocketConnection["+String.valueOf(address)+",connected:"+(controller == null ? "false" : controller.isRunning())+")";
		
	}

}
