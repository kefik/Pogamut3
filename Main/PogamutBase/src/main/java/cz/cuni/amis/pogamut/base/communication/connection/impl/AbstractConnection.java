package cz.cuni.amis.pogamut.base.communication.connection.impl;

import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnection;
import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;
import cz.cuni.amis.pogamut.base.communication.connection.WorldReader;
import cz.cuni.amis.pogamut.base.communication.connection.WorldWriter;
import cz.cuni.amis.pogamut.base.communication.connection.exception.AlreadyConnectedException;
import cz.cuni.amis.pogamut.base.communication.connection.exception.ConnectionException;
import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.IStoppedEvent;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentControlHelper;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.IComponentControlHelper;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.StringCutter;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Implementation of the basic connection to the world server. Note that it has some nice features :-)
 * <p><p>
 * This implementation is THREAD-SAFE! It is {@link IComponent}!
 * <p><p>
 * Calling getReader().read(), getWriter().write() is synchronized as well!
 * <p><p>
 * Reader and writer can be got in advance (no need to start() the connection before getReader() or getWriter() is invoked).
 * <p><p>
 * Calling reader.close() or writer.close() will stop() the connection as well ... you don't have to have
 * this instance referenced directly.
 * <p><p>
 * Whenever an exception is thrown during read/write operation, the connection is immediately stop()ed,
 * not kill()ed and {@link IStoppedEvent} is broadcasted. The flag will be changed correctly as well.
 * <p><p>
 * ... if you're waiting on the reader.read() and the socket is closed meanwhile, be ready
 * to catch the SocketException ...
 * <p><p>
 * The instance of the class can be reused (e.g. you may start(), stop() it repeatedly).
 * <p><p>
 * All you have to implement:
 * <ol>
 * <li>unsyncConnect(IConnectionAddress address) - no need to care of anything else then connection to 'address' + provide correct behavior for getConnectionReader(), getConnectionWriter()</li>
 * <li>unsyncClose() - just close the connection, no need to care of anything else</li>
 * <li>Reader getConnectionReader() - if connection is up, return raw reader for your connection (no extra assumptions), it will be wrapped with ConnectionReader ... if conection is down, return null</li>
 * <li>Writer getConnectionWriter() - if connection is down, return raw writer for your connection (no extra assumptions), it will be wrapped with ConnectionWriter ... if conection is down, return null</li>
 * </ol>
 * You might want to override method getMessageEnd() to return correct "message-end" string, so the messages are correctly split
 * by the readers/writers.
 * <p><p>
 * Ignores {@link IComponentControlHelper#startPaused()}, performs {@link IComponentControlHelper#start()} in both start cases.
 * 
 * @author Jimmy
 */
@AgentScoped
public abstract class AbstractConnection<ADDRESS extends IWorldConnectionAddress> implements IWorldConnection<ADDRESS> {
	
	public static final Token COMPONENT_ID = Tokens.get("Connection");

	public static final String DEFAULT_LINE_END = "\r\n";
	
	/**
	 * Now this is complicated ... what's this? :-)
	 * <BR><BR>
	 * We've got this problem: <BR>
	 * 1) we want this class to be thread-safe <BR>
	 * BUT <BR>
	 * 2) we're giving access to writer/reader to any thread ...
	 * <BR><BR>
	 * What should happen if read/write fails? <BR>
	 * 1) if it is the same connection -> close it <BR>
	 * 2) if it is different connection -> just raise the exception
	 * <BR><BR>
	 * Therefore we need some kind of marker in which connection we are. Study the code if you want to learn more.<BR>
	 * Note that reader/writer provided by this object may be used many times across different connections.
	 */
	private int connectionToken = 0;

	/**
	 * Used to synchronize the behavior of the object.
	 */
	private Object mutex = new Object();
				
	/**
	 * Current remote side address of the connection.
	 */
	protected ADDRESS address = null;
	
	/**
	 * Writer of the connection. Serves for sending messages through the connection.
	 */
	private ConnectionWriter writer = new ConnectionWriter(this);
	
	/**
	 * Reader of the connection. Serves for reading messages from the connection.
	 */
	private ConnectionReader reader = new ConnectionReader(this);
	
	/**
	 * Special category for the connection.
	 */
	protected LogCategory log = null;
	
	/**
	 * Event bus of the agent.
	 */
	protected IComponentBus eventBus;
	
	/**
	 * Control helper starting/stopping the component.
	 */
	protected ComponentController<IComponent> controller;
	
	//
	//
	// ABSTRACT METHODS
	//
	//
	
	/**
	 * Inner implementation of connect, unsynchronized, this is called from
	 * connect(IConnectionDescriptor). This is called only iff the connection is down
	 * and the address is a new address.
	 * 
	 * @throws ConnectionException
	 */
	protected abstract void unsyncConnect(ADDRESS address) throws ConnectionException;
	
	/**
	 * Inner unsynchronized implementation of the close(), should close the connection
	 * to the remote side without throwing any exception. You may be sure that the connection
	 * is up (according to the flag) when this method is called.
	 */
	protected abstract void unsyncClose();
	
	/**
	 * This should return plain reader for the current connection. If connection is down,
	 * this should throw WorldConnectionException. We will wrap this reader with our own
	 * implementation that is capable of sniffing messages as they come (if required).
	 * 
	 * @return
	 */
	protected abstract Reader getConnectionReader() throws ConnectionException;
	
	/**
	 * This should return plain writer for the current connection. If connection is down,
	 * this should throw WorldConnectionException. We will wrap this writer with our own
	 * implementation that is capable of sniffing messages as they go (if required).
	 * 
	 * @return
	 */
	protected abstract Writer getConnectionWriter() throws ConnectionException;
	
	//
	//
	// CONSTURCTOR
	//
	//
	
	public AbstractConnection(
			ComponentDependencies dependencies, 
			IComponentBus bus, 
			IAgentLogger logger
	) {
		this(null, dependencies, bus, logger);
	}
	
	public AbstractConnection(
			ADDRESS address,
			ComponentDependencies dependencies, 
			IComponentBus bus, 
			IAgentLogger logger
	) {
		log = logger.getCategory(getComponentId().getToken());
		NullCheck.check(this.log, "log initialization");
		eventBus = bus;
		NullCheck.check(this.eventBus, "eventBus");
		controller = new ComponentController(this, control, eventBus, log, dependencies);
		if (address != null) {
			setAddress(address);
		}
	}
	
	//
	//
	// COMPONENT CONTROL
	//
	//
	
	private IComponentControlHelper control = new ComponentControlHelper() {
		
		@Override
		public void stop() {
			cleanUp();
		}
		
		@Override
		public void startPaused() {
			start();
		}
		
		@Override
		public void start() {
			synchronized(mutex) {
				if (address == null) throw new ConnectionException("address is null, can't connect()", log, this);
				if (log.isLoggable(Level.WARNING)) log.warning("Connecting to " + address + ".");
				unsyncConnect(address);	
			}
		}
		
		@Override
		public void kill() {
			cleanUp();
		}
		
		@Override
		public void reset() {
			cleanUp();
		}
		
		private void cleanUp() {
			synchronized(mutex) {
				try {
					reader.reader = null;
					writer.writer = null;
					unsyncClose();
				} finally {
					++connectionToken;
				}
			}
		}
		
	};	

	//
	//
	// PUBLIC INTERFACE
	//
	//
	
	@Override
	public Token getComponentId() {
		return COMPONENT_ID;
	}

	public LogCategory getLog() {
		return log;
	}
	
	@Override
	public void setAddress(ADDRESS address) throws ConnectionException {
		synchronized(mutex) {
			if (controller.isRunning()) throw new AlreadyConnectedException("Can't set address when connected.", log, this);
			this.address = address;
		}
	}	
	
	@Override
	public WorldWriter getWriter() throws ConnectionException {
		return this.writer;
	}

	@Override
	public WorldReader getReader() throws ConnectionException {
		return this.reader;
	}

	@Override
	public ADDRESS getAddress() {
		return address;
	}

	@Override
	public String toString() {
		if (this != null) {
			return this.getClass().getSimpleName() + "["+String.valueOf(address)+",connected:"+controller.isRunning()+"]";
		} else {
			return "AbstractConnection["+String.valueOf(address)+",connected:"+controller.isRunning()+")";
		}
	}
			
	@Override
	public void setLogMessages(boolean logMessages) {
		this.reader.setLogMessages(logMessages);
		this.writer.setLogMessages(logMessages);
	}
	
	public String getMessageEnd() {
		return DEFAULT_LINE_END;
	}
	
	//
	//       ................
	//    -----------------------
	// ==============================
	// INNER CLASS DEFINITION FOLLOWS
	// ==============================
	//    -----------------------
	//       ................
	//
		
	/**
	 * Reader for the connection (wrapper for the getConnectionReader()),
	 * that takes care of sniffing messages (if required) + makes reader persistent
	 * over the connect() calls of the connection.  
	 * 
	 * @author Jimmy
	 */
	private class ConnectionReader extends WorldReader {
		
		/**
		 * Owner of the ConnectionReader (because of close() method).
		 */
		private AbstractConnection<ADDRESS> owner = null;
		
		/**
		 * Used when the observer is hooked to the connection.
		 */
		private StringCutter line = new StringCutter(getMessageEnd());

		/**
		 * Cached reader of the connection.
		 */
		private Reader reader = null;
		
		/**
		 * Connection token - we use it to distinguish between connect() calls to be able
		 * to correctly close the connection if the read fails (we have to close the
		 * connection iff it is the same of the cached reader)
		 */
		private int currentConnectionToken = -1;
		
		/**
		 * Mutex that handles access to logMessages field.
		 */
		private Object logMessagesMutex = new Object();
		
		/**
		 * Whether we have to sniff (log) messages from the reader.
		 */
		private boolean logMessages = false;

		public ConnectionReader(AbstractConnection<ADDRESS> owner) {
			this.owner = owner;
		}

		/**
		 * Sets whether we have to log messages from reader.
		 * @param state
		 */
		public void setLogMessages(boolean state) {
			synchronized(logMessagesMutex) {
				if (logMessages == state) return;
				logMessages = state;
				if (logMessages) line.clear();
			}
		}
		
		@Override
		public void close() {
			if (controller.isRunning()) {
				this.owner.controller.manualStop("connection close() requested");
			}
		}
		
		@Override
		public boolean ready() throws PogamutIOException {
			try {
				if (!controller.isRunning()) return false;
				Reader currentReader = this.getReader();
				if (currentReader != null) return currentReader.ready();
			} catch (Exception e) {
				handleException(e);
			}			
			return false;
		}
		
		@Override
		public synchronized int read(char[] ac, int i, int j) throws ComponentNotRunningException, ComponentPausedException, PogamutIOException {
			try {
				if (controller.isPaused()) {
					throw new ComponentPausedException(controller.getState().getFlag(), this);
				}
				if (!controller.isRunning()) {
					throw new ComponentNotRunningException(controller.getState().getFlag(), this);
				}
				Reader currentReader  = this.getReader();
				if (currentReader == null) {
					throw new PogamutIOException("inner reader of the connection is null, can't read", this);
				}			
			
				int result = currentReader.read(ac, i, j);
				
				// should we log the messages?
				if (logMessages){
					synchronized(logMessagesMutex) {
						if (logMessages){
							String[] lines = line.add(new String(ac, i, result));
							for (int index = 0; index < lines.length; ++index) {
								if (log.isLoggable(Level.INFO)) log.info("Message read: " + lines[index]);
							}
							return result;
						}
					}
				}
					
				return result;
							
			} catch (Exception e) {
				handleException(e);
				return 0;
			}
		}
		
		/**
		 * Inner method to get the reader of current connection. It always check whether the
		 * reader hasn't been changed - so it always returns a current one.
		 * @return
		 * @throws PogamutIOException
		 */
		private Reader getReader() throws PogamutIOException {
			synchronized(mutex) {
				if (currentConnectionToken != connectionToken || this.reader == null) {
					currentConnectionToken = connectionToken;
					line.clear();
					this.reader = getConnectionReader();
				}
				return this.reader;
			}
		}
		
		private void handleException(Throwable e) throws PogamutIOException {
			if (e instanceof PogamutIOException) throw (PogamutIOException)e;
			if (e instanceof ComponentPausedException) throw (ComponentPausedException)e;
			if (e instanceof ComponentNotRunningException) throw (ComponentNotRunningException)e;
			if (!controller.isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
			throw new PogamutIOException(e, this);
		}
		
		public String toString() {
			return AbstractConnection.this.getClass().getSimpleName() + "-Reader";
		}

	}
	
	/**
	 * Writer for the connection (wrapper for the getConnectionWriter()),
	 * that takes care of sniffing messages (if required) + makes writer persistent
	 * over the connect() calls of the connection.  
	 * 
	 * @author Jimmy
	 */
	private class ConnectionWriter extends WorldWriter {
		
		/**
		 * Owner of the ConnectionWriter (because of close() method).
		 */
		private AbstractConnection<ADDRESS> owner = null;
		
		/**
		 * Used when the observer is hooked to the connection.
		 */
		private StringCutter line = new StringCutter(getMessageEnd());

		/**
		 * Cached writer of the connection.
		 */
		private Writer writer = null;
		
		/**
		 * Connection token - we use it to distinguish between connect() calls to be able
		 * to correctly close the connection if the write fails (we have to close the
		 * connection iff it is the same of the cached writer)
		 */
		private int currentConnectionToken = -1;
		
		/**
		 * Mutex that handles access to logMessages field.
		 */
		private Object logMessagesMutex = new Object();
		
		/**
		 * Whether we have to sniff (log) messages from the writer.
		 */
		private boolean logMessages = false;

		public ConnectionWriter(AbstractConnection<ADDRESS> owner) {
			this.owner = owner;
		}

		/**
		 * Sets whether we have to log messages from writer.
		 * @param state
		 */
		public void setLogMessages(boolean state) {
			synchronized(logMessagesMutex) {
				if (logMessages == state) return;
				logMessages = state;
				if (logMessages) line.clear();
			}			
		}
				
		@Override
		public void close() {
			if (controller.isRunning()) {
				controller.manualStop("connection close() requested");
			}
		}
		
		@Override
		public void flush() throws PogamutIOException {
			try {
				Writer currentWriter = getWriter();
				if (currentWriter != null) currentWriter.flush();
			} catch (Exception e) {
				handleException(e);
			}
		}
		
		@Override
		public boolean ready() throws PogamutIOException {
			try {
				if (!controller.isRunning()) return false;			
				Writer currentWriter = this.getWriter();
				return currentWriter != null;
			} catch (Exception e) {
				handleException(e);
				return false;
			}
		}
				
		@Override
		public synchronized void write(char cbuf[], int off, int len) throws PogamutIOException, ComponentNotRunningException {
			try {
				if (controller.isPaused()) {
					throw new ComponentPausedException(controller.getState().getFlag(), this);
				}
				if (!controller.isRunning()) {					
					throw new ComponentNotRunningException(controller.getState().getFlag(), this);
				}
				Writer currentWriter = this.getWriter();
				if (currentWriter == null) {
					throw new PogamutIOException("inner reader of the connection is null, can't read", this);
				}
				currentWriter.write(cbuf, off, len);
				if (logMessages) {
					synchronized(logMessagesMutex) {
						// should we log the messages?
						if (logMessages){
							String[] lines = line.add(new String(cbuf, off, len));
							for (int index = 0; index < lines.length; ++index) {
								if (log.isLoggable(Level.INFO)) log.info("Message written: " + lines[index]);
							}
						}
					}
				}
			} catch (Exception e) {
				handleException(e);
			}
		}
		
		/**
		 * Inner method to get the writer of current connection. It always check whether the
		 * writer hasn't been changed - so it always returns a current one.
		 * @return
		 * @throws PogamutIOException
		 */
		private Writer getWriter() throws PogamutIOException {
			synchronized(mutex) {
				if (currentConnectionToken != connectionToken || this.writer == null) {
					currentConnectionToken = connectionToken;
					line.clear();
					this.writer = getConnectionWriter();
				}
				return this.writer;
			}
		}
		
		private void handleException(Throwable e) throws PogamutIOException {
			if (e instanceof PogamutIOException) throw (PogamutIOException)e;
			if (e instanceof ComponentPausedException) throw (ComponentPausedException)e;
			if (e instanceof ComponentNotRunningException) throw (ComponentNotRunningException)e;
			if (!controller.isRunning()) throw new ComponentNotRunningException(controller.getState().getFlag(), this);
			throw new PogamutIOException(e, this);
		}
		
		public String toString() {
			return AbstractConnection.this.getClass().getSimpleName() + "-Writer";
		}
		
	}	

}