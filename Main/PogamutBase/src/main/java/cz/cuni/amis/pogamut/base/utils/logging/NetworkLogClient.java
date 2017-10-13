package cz.cuni.amis.pogamut.base.utils.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutProperty;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.listener.IListener;
import cz.cuni.amis.utils.listener.Listeners;

/**
 * Client that may be used to obtain logs from arbitrary number of agents that are publishing their logs via {@link NetworkLogManager}.
 * <p><p>
 * It needs to be initialized with address:port and "agent id" which specifies where to connect + which agent's logs you want to receive.
 * <p><p>
 * The class is specifying 3 callback (aka listeners) / events that you may attach to it:
 * <ol>
 * <li>{@link LoggingStarted} event and {@link ILoggingStartedListener} - called whenever successful connection to address:port is made and agent id is sent</li>
 * <li>{@link LogRead} event and {@link ILogReadListener} - called whenever a new log entry has been read providing {@link NetworkLogEnvelope} specifying the log details</li>
 * <li>{@link LoggingStopped} event and {@link ILoggingStoppedListener} - called whenever the logging is terminated</li>
 * </ol>
 * <p><p> 
 * Note that there is a problem with object GC() whenever an anonymous listeners are used (for instance for log collecting). Whenever you start
 * a client, receive logs via listener, DROP THE REFERENCE to the client, the client won't be stopped. It will be stopped whenever the
 * remote socket is closed resulting in {@link IOException} inside the client's thread. Additionally, if you do not hook a {@link ILoggingStoppedListener}
 * detaching your log-reading-callback listener, the client instance will be ignored by the GC().
 * <p><p>
 * Therefore, as an implicit behavior, whenever the logging is stopped, all listeners are actually deleted (which is the way you will 
 * probably need to use the class). If you do not want this behavior, just call {@link NetworkLogClient#setImplicitRemoveListeners(boolean)} with parameter
 * 'false'.
 * <p><p>
 * NOTE: the object must be {@link NetworkLogClient#start()} manually after the instantiation.
 * <p><p>
 * If you want to stop the client prematurely, call {@link NetworkLogClient#stop()}.
 * 
 * @author Jimmy
 */
public class NetworkLogClient {
	
	/**
	 * Event that marks that the client has successfully connected to the remote side
	 * and is ready to receive logs.
	 * 
	 * @author Jimmy
	 */
	public static class LoggingStarted {		
	}
	
	/**
	 * Event/message containing another log-record produced by the remote agent.
	 * 
	 * @author Jimmy
	 */
	public static class LogRead {
		
		/**
		 * Contains log-record details.
		 */
		private NetworkLogEnvelope record;
		
		public LogRead(NetworkLogEnvelope record) {
			this.record = record;
		}

		/**
		 * @return log-record details
		 */
		public NetworkLogEnvelope getRecord() {
			return record;
		}
		
	}
	
	/**
	 * Event that marks that client has been disconnected (or stopped).
	 * 
	 * @author Jimmy
	 */
	public static class LoggingStopped {
		
		private boolean expected;
		
		private Throwable exception;

		public LoggingStopped() {
			this.expected = true;
		}
		
		public LoggingStopped(Throwable e) {
			this.expected = false;
			this.exception = e;
		}
		
		public Throwable getException() {
			return exception;
		}

		public boolean isExpected() {
			return expected;
		}
		
		public boolean isFailure() {
			return !expected;
		}
		
	}
		
	public static interface ILoggingStartedListener extends IListener<LoggingStarted> {		
	}
	
	public static interface ILogReadListener extends IListener<LogRead> {		
	}
	
	public static interface ILoggingStoppedListener extends IListener<LoggingStopped> {		
	}	
	
	private Listeners<ILoggingStartedListener> loggingStartedCallback = new Listeners<ILoggingStartedListener>();
	
	private Listeners.AdaptableListenerNotifier<ILoggingStartedListener> loggingStartedNotifier = new Listeners.AdaptableListenerNotifier<ILoggingStartedListener>();
	
	private Listeners<ILogReadListener> logReadCallback = new Listeners<ILogReadListener>();
	
	private Listeners.AdaptableListenerNotifier<ILogReadListener> logReadNotifier = new Listeners.AdaptableListenerNotifier<ILogReadListener>();
	
	private Listeners<ILoggingStoppedListener> loggingStoppedCallback = new Listeners<ILoggingStoppedListener>();
	
	private Listeners.AdaptableListenerNotifier<ILoggingStoppedListener> loggingStoppedNotifier = new Listeners.AdaptableListenerNotifier<ILoggingStoppedListener>();
	
	private String address;
	private int port;
	private String agentId;
	
	protected LogCategory log;
	
	private boolean implicitRemoveListeners = true;

	public NetworkLogClient(String address, int port, String agentId) {
		log = new LogCategory("NetworkLogClient");
		log.addConsoleHandler();
		String logLevel = Pogamut.getPlatform().getProperty(PogamutProperty.POGAMUT_NETWORK_LOG_MANAGER_AND_CLIENT_LEVEL.getKey());
		if (logLevel != null) {
			log.setLevel(Level.parse(logLevel));
		} else {
			log.setLevel(Level.WARNING);
		}
		this.address = address;
		this.port = port;
		this.agentId = agentId;
		this.logReadingWorker = new LogReadingWorker();
		loggingStartedCallback.setLog(log, "LoggingStarted");
		logReadCallback.setLog(log, "LogRead");
		loggingStoppedCallback.setLog(log, "LoggingStopped");
	}
	
	/**
	 * @return logger used by this instance
	 */
	public LogCategory getLogger() {
		return log;
	}
	
	/**
	 * Note that there is a problem with object GC() whenever an anonymous listeners are used (for instance for log collecting). Whenever you start
	 * a client, receive logs via listener, DROP THE REFERENCE to the client, the client won't be stopped. It will be stopped whenever the
	 * remote socket is closed resulting in {@link IOException} inside the client's thread. Additionally, if you do not hook a {@link ILoggingStoppedListener}
	 * detaching your log-reading-callback listener, the client instance will be ignored by the GC().
	 * <p><p>
	 * Therefore, as an implicit behavior, whenever the logging is stopped, all listeners are actually deleted (which is the way you will 
	 * probably need to use the class). If you do not want this behavior, just call this method with 'false' as a parameter.
	 * 'false'.
	 * 
	 * @param state
	 */
	public void setImplicitRemoveListeners(boolean state) {
		this.implicitRemoveListeners = state;
	}
	
	public boolean isImplicitRemoveListeners() {
		return implicitRemoveListeners;
	}
	
	/**
	 * Does exception occured inside the thread that was/is reading logs?
	 * @return
	 */
	public boolean isException() {
		LogReadingWorker worker = logReadingWorker;
		return worker != null && worker.exception != null;
	}
	
	/**
	 * @return exception that has happened inside the thread that was/is reading logs
	 */
	public Throwable getException() {
		LogReadingWorker worker = logReadingWorker;
		return worker == null ? null : worker.exception;
	}
	
	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public String getAgentId() {
		return agentId;
	}

	public synchronized void start() {
		log.warning("Starting for " + agentId + " @ " + address + ":" + port);
		if (logReadingWorker.running.getFlag()) {
			log.warning("Old connection is still up... closing.");
			stop();
			log.warning("Resuming start for " + agentId + " @ " + address + ":" + port);
		}		
		workerThread = new Thread(logReadingWorker, "NetworkLogClient-" + address + ":" + port);		
		workerThread.start();
		
		logReadingWorker.connected.waitFor(true, false);
		
		if (logReadingWorker.connected.getFlag() == false) {
			throw new PogamutException("Could not start reading logs from the network.", logReadingWorker.exception, this);
		}
	}
	
	public synchronized void stop() {
		log.warning("Stopping for " + agentId + " @ " + address + ":" + port);
		if (logReadingWorker != null) {
			if (logReadingWorker.running.getFlag()) {
				logReadingWorker.kill();
				logReadingWorker.running.waitFor(false);
			}
		}
	}
	
	public Flag<Boolean> getRunning() {
		return logReadingWorker.running;
	}
	
	public Flag<Boolean> getConnected() {
		return logReadingWorker.connected;
	}
	
	public void addListener(ILoggingStartedListener listener) {
		loggingStartedCallback.addStrongListener(listener);
	}
	
	public void removeListener(ILoggingStartedListener listener) {
		loggingStartedCallback.removeListener(listener);
	}
	
	public void addListener(ILoggingStoppedListener listener) {
		loggingStoppedCallback.addStrongListener(listener);	
	}
	
	public void removeListener(ILoggingStoppedListener listener) {
		loggingStoppedCallback.removeListener(listener);
	}
	
	public void addListener(ILogReadListener listener) {
		logReadCallback.addStrongListener(listener);
	}
	
	public void removeListener(ILogReadListener listener) {
		logReadCallback.removeListener(listener);
	}
	
	// ----
    // --------
    // ==================
    // LOG READING WORKER
    // ==================
    // --------
    // ----

    /**
     * Worker instance - it implements Runnable interface and is continuously
     * reading messages from the connection object and passing them to the
     * callbacks.
     */
    protected LogReadingWorker logReadingWorker = null;
    
    /**
     * Thread of the worker.
     */
    protected Thread workerThread = null;
    
    private static final String NEW_LINE = System.getProperty("line.separator");
    
    /**
     * Handles trailing '\r' in the 'msg'.
     * @param msg
     * @return
     */
    private String checkLineEnd(String msg) {
    	if (msg == null) return null;
    	if (NEW_LINE.length() == 2) {
    		// WINDOWS-STYLE-LINE-ENDS
    		return msg;
    	} else {
    		// CHECK-FOR-TRAILING \r
    		if (msg.charAt(msg.length()-1) == '\r') {
    			// TRAILING \r, CUT IT OF
    			return msg.substring(0, msg.length()-1);
    		}
    		// ALL-OK
    		return msg;
    	}
    }
    
    private class LogReadingWorker implements Runnable {

        /**
         * Simple flag that is telling us whether the Worker should run.
         */
        private volatile boolean shouldRun = true;
        public volatile Flag<Boolean> running = new Flag<Boolean>(false);
        public volatile Flag<Boolean> connected = new Flag<Boolean>(null);
        private volatile boolean exceptionExpected = false;
        public volatile Throwable exception = null;
        private Thread myThread = null;
        
        /**
         * Drops the shouldRun flag, waits for 200ms and then interrupts the
         * thread in hope it helps.
         */
        public void kill() {
            if (!running.getFlag()) {
                return;
            }
            this.shouldRun = false;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            exceptionExpected = true;
            myThread.interrupt();
        }

        /**
         * Contains main while cycle that is continuously reading messages from
         * the connection (using parser), notifying listeners and then passing
         * them to the message receiver.
         */
        @Override
        public void run() {
            
        	// initialize
            myThread = Thread.currentThread();
            exception = null;
            shouldRun = true;
            exceptionExpected = false;
            // set the running flag, we've been started
            running.setFlag(true);
            connected.setFlag(null);

            // notify that logger started
            log.info("LogReadingWorker: Started.");
            
            SocketChannel socket = null;
            
            try {
				socket = SocketChannel.open(new InetSocketAddress(address, port));
			} catch (IOException e1) {
				try {
					socket.close();
				} catch (Exception e2) {					
				}
				log.severe("LogReadingWorker: Could not open socket for " + address + ":" + port + ".");
				socket = null;
				exception = e1;
				loggingStoppedNotifier.setEvent(new LoggingStopped(new PogamutIOException("Could not open " + address + ":" + port + ".", e1)));
				loggingStoppedCallback.notifySafe(loggingStoppedNotifier, log);
				connected.setFlag(false);
				running.setFlag(false);
				log.warning("LogReadingWorker stopped.");
				return;
			}
			
			log.fine("LogReadingWorker: Socket opened for " + address + ":" + port + ".");

			CharsetEncoder encoder = NetworkLogManager.USED_CHARSET.newEncoder();
	        CharsetDecoder decoder = NetworkLogManager.USED_CHARSET.newDecoder();
	        
	        PrintWriter writer = new PrintWriter(Channels.newWriter(socket, encoder, -1));
			BufferedReader reader = new BufferedReader(Channels.newReader(socket, decoder, -1));
			
			try {
				writer.println(agentId);
				writer.flush();
			} catch (Exception e1) {
				try {
					socket.close();
				} catch (Exception e2) {					
				}
				socket = null;
				exception = e1;
				loggingStoppedNotifier.setEvent(new LoggingStopped(new PogamutIOException("Could not send 'agent id' to " + address + ":" + port + ".", e1)));
				loggingStoppedCallback.notifySafe(loggingStoppedNotifier, log);
				connected.setFlag(false);
				running.setFlag(false);				
				log.warning("LogReadingWorker: stopped.");
				return;
			}
			
			log.fine("LogReadingWorker: Agent id sent.");
			
			loggingStartedNotifier.setEvent(new LoggingStarted());
			loggingStartedCallback.notify(loggingStartedNotifier);
			
			log.fine("LogReadingWorker: Starting reading logs.");
			
            try {
            	
            	StringBuffer message = new StringBuffer();
            	
            	connected.setFlag(true);
            	
            	String category = null;
        		String level    = null;
        		String time     = null;
            	
                while (shouldRun && !myThread.isInterrupted()) {
                	try {
                		try {
	                		category = checkLineEnd(reader.readLine());
	                		level    = checkLineEnd(reader.readLine());
	                		time     = checkLineEnd(reader.readLine());
	                		
	                		if (message.length() > 0) {
	                			message.delete(0, message.length());
	                		}
	                		
	                		boolean first = true;
	                		
	                		while (true) {
	                			String msg = checkLineEnd(reader.readLine());
	                			if (msg.equals("</end>")) break;
	                			if (first) first = false;
	                			else message.append(NEW_LINE);
	                			message.append(msg);
	                		}
                		} catch (Exception e) {
                			log.severe(ExceptionToString.process("LogReadingWorker: Exception while reading data from the socket, connection closed from the remote side? Shutting down...", e));
                			break;
                		}
                		
                		NetworkLogEnvelope logEnvelope = null;
                		try {
                			logEnvelope = new NetworkLogEnvelope(category, level, time, message.toString());
                		} catch (Exception e) {
                			log.warning(ExceptionToString.process("LogReadingWorker: MALFORMED log record, category='" + category + "', level='" + level + "', time='" + time + "', message='" + message + "'", e));
                			continue;
                		}
                		logReadCallback.notify(logReadNotifier.setEvent(new LogRead(logEnvelope)));                		
                	} catch (Exception e) { 
                		if (!exceptionExpected) {
                            log.severe(ExceptionToString.process("LogReadingWorker: Exception at LogSendingWorker.", e));
                            exception = e;
                        } else {
                            log.fine(ExceptionToString.process("LogReadingWorker: Exception at LogSendingWorker, expected.", e));
                        }	
                		break;
                	}
                }
            } catch (Exception e) {
            	if (!exceptionExpected) {
                    log.severe(ExceptionToString.process("LogReadingWorker: Exception at LogSendingWorker.", e));
                    exception = e;
                } else {
                    log.fine(ExceptionToString.process("LogReadingWorker: Exception at LogSendingWorker, expected.", e));
                }		                
            } finally {
            	try {
            		socket.close();
            	} catch (Exception e) {            		
            	}
            	connected.setFlag(false);
            	running.setFlag(false);
            	if (exception != null) {
            		loggingStoppedCallback.notifySafe(loggingStoppedNotifier.setEvent(new LoggingStopped(exception)), log);
            	} else {
            		loggingStoppedCallback.notifySafe(loggingStoppedNotifier.setEvent(new LoggingStopped()), log);
            	}
            	if (implicitRemoveListeners) {
                	log.warning("LogReadingWorker: Removing all listeners...");
                	loggingStartedCallback.clearListeners();
                	logReadCallback.clearListeners();
                	loggingStoppedCallback.clearListeners();
                }

                log.warning("LogSendingWorker: Stopped.");
            }
        }

    }

}
