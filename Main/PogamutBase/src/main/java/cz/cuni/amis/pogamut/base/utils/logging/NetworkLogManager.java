package cz.cuni.amis.pogamut.base.utils.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.utils.DefaultPogamutPlatform;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutProperty;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.maps.LazyMap;
import cz.cuni.amis.utils.token.IToken;
import cz.cuni.amis.utils.token.Tokens;


/**
 * A class used for network logging of agents.
 * <p><p>
 * It is a singleton which accepts logs from agents of one JVM and publishes them on certain port, where clients may accept them.
 * 
 * @author Pyroh
 * @author Jimmy
 */
public class NetworkLogManager {
	
	/**
	 * Number of logs we are buffering for every agent, these logs are sent to the client whenever it
	 * opens a socket that wants to listen for a particular agent.
	 */
    private static final int MAXIMUM_LOGS_PER_AGENT = 100;
	
	/**
	 * {@link Charset} that is used by the {@link NetworkLogManager} to send logs over the socket.
	 * <p><p>
	 * Initialized to "UTF-8" as default.
	 */
	public static final Charset USED_CHARSET = Charset.forName("UTF-8");	
	
	/**
	 * How often the logs are flushed to the socket (value < 100
	 * results in unacceptable throughput). 
	 * <p><p>
	 * Initialized to 200 millis;
	 */
	public static final long NETWORK_FLUSH_PERIOD_MILLIS = 200;

	/**
	 * How long do we wait for the agent-id before we drop the connection.
	 * <p><p>
	 * Initialized to 1000 millis. 
	 */
	public static final long NETWORK_LOG_MANAGER_SOCKET_TIMEOUT_MILLIS = 1000;
	
//	  /**
//     * How many logs may be buffered before the network logging starts to lag.
//     */
//    public static final int LOG_BUFFER_LENGTH = 2000;
	
	/**
     * The reference to singleton of {@link NetworkLogManager} (this class).
     */
    private static NetworkLogManager manager = null;
    
    /**
     * Mutex that synchronizes construction and destruction of the {@link NetworkLogManager#manager}.
     */
    private static Object managerMutex = new Object();
    
    /**     
     * @return instance of {@link NetworkLogManager}, if called for the first time, creates an instance, before returning it.
     */
    public static NetworkLogManager getNetworkLogManager() {
    	NetworkLogManager instance = manager;
    	if (instance != null && instance.operating) {
    		return instance;
    	}
    	synchronized(managerMutex) {
    		instance = manager;    		
    		if (instance == null || !instance.operating) {
    			manager = instance = new NetworkLogManager();
    		}
    		return instance;
    	}        
    }
	
//	/**
//	 * Log record that contains its destination ({@link LogSocket} where we should send the log).
//	 * 
//	 * @author Jimmy
//	 */
//	private static class LogSocketEntry {
//		
//		private LogSocket logSocket;
//		
//		private IToken agentId;
//		
//		private NetworkLogEnvelope logRecord;		
//		
//		public LogSocketEntry(LogSocket logSocket, IToken agentId, NetworkLogEnvelope logRecord) {
//			this.logSocket = logSocket;
//			this.agentId = agentId;
//			this.logRecord = logRecord;
//		}
//
//		public LogSocket getSocket() {
//			return logSocket;
//		}
//		
//		public IToken getAgentId() {
//			return agentId;
//		}
//
//		public NetworkLogEnvelope getRecord() {
//			return logRecord;
//		}
//				
//	}
		
	/**
	 * Used for sending {@link NetworkLogEnvelope} down the {@link LogSocket#socket} via {@link LogSocket#writer} using
	 * serialization.
	 * <p><p>
	 * THREAD-UNSAFE! ACCESS TO ALL METHODS MUST BE SYNCHRONIZED!
	 * 
	 * @author Jimmy
	 */
	private static class LogSocket {
				
		/**
		 * {@link CharsetEncoder} that is used to serialize {@link String}s into the {@link LogSocket#socket}, used by
		 * {@link LogSocket#writer}.
		 */
		public CharsetEncoder encoder = USED_CHARSET.newEncoder();
		
		/**
		 * Socket that is used for sending logs down the hole.
		 */
		private Socket socket;
		
		/**
		 * Whether the socked should be opened, i.e., it was not closed via {@link LogSocket#close()}.
		 */
		private boolean opened = true;
			
		/**
		 * {@link Writer} that is being used for sending the logs down through the {@link LogSocket#socket}.
		 */
		private PrintWriter writer;
		
		/**
		 * What is the last time we have performed {@link LogSocket#flush()} operation. Frequency of {@link LogSocket#flush()} operations
		 * are influencing a throughput.
		 */
		private long lastFlush = System.currentTimeMillis();
		
		/**
		 * Wraps 'socket' with a service layer providing a means for sending {@link NetworkLogEnvelope}s through it.
		 * @param socket
		 * @throws IOException
		 */
		public LogSocket(Socket socket) throws IOException {
			this.socket = socket;
			this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), encoder));
		}
		
		/**
		 * Whether the socket is opened.
		 * <p><p>
		 * If the socket is not opened, you CAN'T CALL {@link LogSocket#send(NetworkLogEnvelope)} or {@link LogSocket#flush()} as they would
		 * result in {@link NullPointerException}.
		 * @return
		 */
		public boolean isOpened() {
			return opened;
		}
		
		/**
		 * Closes the {@link LogSocket#socket} and frees internal data structures.
		 */
		public void close() {
			if (!opened) return;
			opened = false;
			try {
				socket.close();
			} catch (Exception e) {					
			}
			encoder = null;
			writer = null;
			socket = null;
		}
		
		/**
		 * Serialize the {@link NetworkLogEnvelope} into the {@link LogSocket#socket} via {@link LogSocket#writer}.
		 * 
		 * @param log
		 * @throws IOException
		 */
		public void send(NetworkLogEnvelope log) throws IOException {
			writer.println(log.getCategory());
			writer.println(log.getLevel());			
			writer.println(String.valueOf(log.getMillis()));
			writer.println(log.getMessage());
			writer.println("</end>");
			checkFlush();
		}
		
		/**
		 * Unconditionally performing {@link Writer#flush()} operation to send the byte buffer through the {@link LogSocket#socket}.
		 * @throws IOException
		 */
		public void flush() throws IOException {
			writer.flush();
			lastFlush = System.currentTimeMillis();
		}
		
		/**
		 * Checks whether the time has come to perform {@link LogSocket#socket} based on the {@link LogSocket#lastFlush} and {@link NetworkLogManager#NETWORK_FLUSH_PERIOD_MILLIS}.
		 * @throws IOException
		 */
		public void checkFlush() throws IOException {
			if (System.currentTimeMillis() - lastFlush > NETWORK_FLUSH_PERIOD_MILLIS) {
				flush();
			}	
		}
		
	}
    
//	  /**
//     * Logs we need to publish.
//     */
//    private ArrayBlockingQueue<LogSocketEntry> logsToSend = new ArrayBlockingQueue<LogSocketEntry>(LOG_BUFFER_LENGTH);
    
    /**
     * Used for logging stuff of this class.
     */
    private static LogCategory log = new LogCategory("NetworkLogManager");
    
    static {
    	String level = Pogamut.getPlatform().getProperty(PogamutProperty.POGAMUT_NETWORK_LOG_MANAGER_AND_CLIENT_LEVEL.getKey());
    	if (level == null) level = "WARNING";
    	log.setLevel(Level.parse(level));
    	log.addConsoleHandler();    	
    }
    
    /**
     * Mutex synchronizing construction and destruction of the {@link NetworkLogManager#serverWorker}.
     */
    private Object serverWorkerMutex = new Object();
    
    /**
     * This maps agents to various sockets which accept the agent's logs.
     */
    private LazyMap<IToken, ConcurrentLinkedQueue<LogSocket>> idToSocketList = new LazyMap<IToken, ConcurrentLinkedQueue<LogSocket>>() {

		@Override
		protected ConcurrentLinkedQueue<LogSocket> create(IToken key) {
			return new ConcurrentLinkedQueue<LogSocket>();
		}
    	
    };
    
    /**
     * This maps agents to their logs in memory. It does not keep all of the agent's logs; these which are sent are deleted from the map.
     */
    private LazyMap<IToken, ConcurrentLinkedQueue<NetworkLogEnvelope>> idToEnvelopeList = new LazyMap<IToken, ConcurrentLinkedQueue<NetworkLogEnvelope>>() {

		@Override
		protected ConcurrentLinkedQueue<NetworkLogEnvelope> create(IToken key) {
			return new ConcurrentLinkedQueue<NetworkLogEnvelope>();
		}
    	
    };
    
    /**
     * Flag that tells whether the {@link NetworkLogManager} is operating. Once {@link NetworkLogManager#shutdown()} this
     * flag is set to false and no new threads are created / no logs are published, etc. 
     */
	private boolean operating = true;    

    /**
     * A constructor, initializes {@link NetworkLogManager#serverSocket} and the manager's logger.
     * <p><p>
     * Called from {@link NetworkLogManager#getNetworkLogManager()} (lazy-singleton initialization).
     */
    private NetworkLogManager() {
    	start();
    }
    
    /**
     * Called from the constructor to startup this instance of {@link NetworkLogManager}.
     * <p><p>
     * Initialize {@link NetworkLogManager#serverWorker}.
     * <p><p>
     * MUST NOT BE CALLED AGAIN! (Constructor-call / one-time only). 
     */
	private void start() {
    	synchronized(serverWorkerMutex) {
    		if (log != null && log.isLoggable(Level.FINER)) log.finer("Starting...");
     		try {
				serverWorker = new ServerWorker();
			} catch (IOException e) {
				throw new PogamutIOException("Could not initialize NetworkLogManager, could not open server socket.", e, log, this);
			}
    		serverWorkerThread = new Thread(serverWorker, "NetworkLogManager-ServerSocket");
    		serverWorkerThread.start();
    		
//    		logSendingWorker = new LogSendingWorker();
//    		workerThread = new Thread(logSendingWorker, "NetworkLogManager-LogSender");
//    		workerThread.start();
    		    		
    		log.fine("Started.");
    	}
    }    
    
    /**
     * Whether this instance is active, i.e., it was not {@link NetworkLogManager#shutdown()}.
     * 
     * @return whether the manager is running, i.e., accepting new connections and publishing logs
     */
    public boolean isRunning() {
		return operating;
	}

    /**
     * Method called from {@link DefaultPogamutPlatform#close()} to shutdown the network
     * logging, terminating its thread to let JVM die gracefully.
     * <p><p>
     * YOU DO NOT PROBABLY WANT TO CALL THIS METHOD MANUALLY!!!
     * <p><p> 
     * But if you do, it will shutdown all sockets and the worker, but it will start it up again if there is any agent will wish 
     * to log something again. Therefore it is a good idea to call the shutdown after all Pogamut's agents are stopped/killed (i.e., dead). 
     */
    public void kill()  {
    	// we're shutting down == we're not operating any more!
    	operating = false; 
    	synchronized(managerMutex) {
    		// nullify the static manager var, but only iff it is still us!
			if (manager == this) {
				manager = null;
			}
		}
    	if (log != null && log.isLoggable(Level.WARNING)) log.warning("Shutting down!");
    	// shutdown the logging
    	synchronized(serverWorkerMutex) {    		    		

    		serverWorker.kill();
    		serverWorker = null;
    		
    		while (idToSocketList.size() > 0) {
    			IToken agent;
    			synchronized(idToSocketList) {
    				if (idToSocketList.size() == 0) break;
    				agent = idToSocketList.keySet().iterator().next();
    			}
    			removeAgent(agent);
    		}
    		
            log.severe("Shutdown.");        
    	}
    }

    /**
     * @return port on which the serverSocket is listening or -1 if the socket is not initialized (i.e., there is no logging agent registered inside the manager).
     */
    public int getLoggerPort() {
    	if (!operating) return -1;
    	ServerWorker serverWorker = this.serverWorker;
    	if (serverWorker == null) return -1;
        return serverWorker.serverSocket.getLocalPort();
    }
    
    /**
     * @return host where we're listening for logging client or null if the socket is not initialized (i.e., there is no logging agent regist9ered inside the manager).
     */
    public String getLoggerHost() {
		if (!operating) return null;
		if (serverWorker == null) return null;		
		try {
			byte[] addr = InetAddress.getLocalHost().getAddress();
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new PogamutException("Could not determine host IP address.", e, this);
		}
        //return serverWorker.serverSocket.getInetAddress().getHostAddress();
	}
    
    /**
     * Initializes logging for the 'agent'.
     * @param agent
     */
    public void addAgent(IToken agent) {
    	if (!operating) {
    		return;
    	}
    	if (log != null && log.isLoggable(Level.FINE)) log.fine("Adding network logging for agent: " + agent.getToken());
    	synchronized(idToSocketList) {
    		idToSocketList.get(agent);
    	}
    	synchronized(idToEnvelopeList) {
    		idToEnvelopeList.get(agent);
    	}
    }

    /**
     * Removes an agent from the manager - stops accepting its logs, closes sockets where its logs are being sent etc...
     * @param agent The agent whose logging is to be stopped.
     */
    public void removeAgent(IToken agent) {
    	
    	// NOTE: possible memory leaks when removeAgent() is called together with processLog() for a same agent at the same time
    	//       which is totally weird (should not happen!)
    	
    	if (log != null && log.isLoggable(Level.WARNING)) log.warning("Removing network logging for agent: " + agent);		
		
    	if (log != null && log.isLoggable(Level.INFO)) log.info("Closing logging sockets for: " + agent);
		ConcurrentLinkedQueue<LogSocket> agentSockets;
    	synchronized(idToSocketList) {
    		agentSockets = idToSocketList.get(agent);
    		idToSocketList.remove(agent);
    	}
    	for (LogSocket socket : agentSockets) {
    		synchronized (socket){
    			try {
					socket.flush();
				} catch (IOException e) {
				}
    			socket.close();
    		}
		}
    	
    	log.info("Removing bruffered logs for: " + agent);
   		synchronized(idToEnvelopeList) {
   			idToEnvelopeList.remove(agent);
   		}
   		
    }
    
    /**
     * A method called from NetworkLogPublisher when a log is to be published.
     * @param record The envelope containing informations to be published
     * @param id Who has sent the log.
     */
    public void processLog(NetworkLogEnvelope record, IToken agent) {
    	if (!operating) {
    		return;
    	}
    	
    	if (log != null && log.isLoggable(Level.FINEST)) log.finest("Processing log: (" + agent.getToken() + ") " + record);
    	
    	// NOTE: possible memory leaks when processLog() is called together with removeAgent() for a same agent at the same time
    	
    	ConcurrentLinkedQueue<LogSocket> agentSockets = null;
    	ConcurrentLinkedQueue<NetworkLogEnvelope> agentLogs;
    	
    	synchronized(idToSocketList) {
    		if (idToSocketList.containsKey(agent)) {
    			agentSockets = idToSocketList.get(agent);
    		}
    	}    	
    	if (agentSockets != null) {
    		Iterator<LogSocket> iter = agentSockets.iterator();
    		while (iter.hasNext()) {
    			LogSocket logSocket = iter.next();
//    			if (logSocket.isOpened()) {
//    				try {
//						logsToSend.put(new LogSocketEntry(logSocket, agent, record));
//					} catch (InterruptedException e) {
//						throw new PogamutInterruptedException(e, this);
//					}
//    			}
    			synchronized(logSocket) {
    				if (!logSocket.isOpened()) {
    					logSocket.close();
    					iter.remove();
    					continue;
    				}
    				try {
						logSocket.send(record);								
					} catch (Exception e) {
						logSocket.close();
    					iter.remove();
    					continue;
					}
    			}
	    	}
    	}
    	
    	synchronized(idToEnvelopeList) {
    		agentLogs = idToEnvelopeList.get(agent);
    	}
    	
    	agentLogs.add(record);
    	
    	while(agentLogs.size() > MAXIMUM_LOGS_PER_AGENT) {
    		try {
    			agentLogs.remove();
    		} catch (Exception e) {    			
    		}
    	}
    }
    
    /**
     * Returns a logger that the {@link NetworkLogManager} is using.
     * @return
     */
    public static LogCategory getLog() {
    	return log;
    }
    
//    // ----
//    // --------
//    // ==================
//    // LOG SENDING WORKER
//    // ==================
//    // --------
//    // ----
//
//    /**
//     * Worker instance - it implements Runnable interface and is continuously
//     * reading messages from the connection object and passing them to the
//     * receiver.
//     */
//    protected LogSendingWorker logSendingWorker = null;
//    
//    /**
//     * Thread of the worker.
//     */
//    protected Thread workerThread = null;
//    
//    private class LogSendingWorker implements Runnable {
//
//        /**
//         * Simple flag that is telling us whether the Worker should run.
//         */
//        private volatile boolean shouldRun = true;
//        private volatile boolean running = false;
//        private volatile boolean exceptionExpected = false;
//        private Thread myThread;
//
//        /**
//         * Drops the shouldRun flag, waits for 200ms and then interrupts the
//         * thread in hope it helps.
//         */
//        public void kill() {
//            if (!running) {
//                return;
//            }
//            this.shouldRun = false;
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//            }
//            exceptionExpected = true;
//            myThread.interrupt();
//        }
//
//        private void send(LogSocketEntry entry) {
//        	synchronized(entry.logSocket) {
//	        	try {
//	    			if (entry.getSocket().isOpened()) {
//	    				log.finest("Sending log: " + entry.getAgentId().getToken() + " " + entry.getRecord() + " -> " + entry.getSocket().socket.socket().getRemoteSocketAddress());
//	    				entry.getSocket().send(entry.getRecord());
//	    			} else {
//	    				// could not send data through the socket...
//	        			log.info("Logging socket closed from the remote side for the agent: " + entry.getAgentId() + ", removing it from the socket list.");
//	        			entry.getSocket().close();
//	        			idToSocketList.get(entry.getAgentId()).remove(entry.getSocket());
//	    			}
//	    		} catch (IOException e) {
//	    			// could not send data through the socket...
//	    			log.info("Logging socket closed from the remote side for the agent: " + entry.getAgentId() + ", removing it from the socket list.");
//	    			entry.getSocket().close();
//	    			idToSocketList.get(entry.getAgentId()).remove(entry.getSocket());
//	    		}
//        	}
//        }
//        
//        /**
//         * Contains main while cycle that is continuously reading messages from
//         * the connection (using parser), notifying listeners and then passing
//         * them to the message receiver.
//         */
//        @Override
//        public void run() {
//            
//            myThread = Thread.currentThread();
//
//            // set the running flag, we've been started
//            running = true;
//
//            // notify that gateway started
//            log.info("LogSendingWorker started.");
//
//            List<LogSocketEntry> buffer = new ArrayList<LogSocketEntry>(LOG_BUFFER_LENGTH);
//            
//            int performanceLimit = (int) (LOG_BUFFER_LENGTH * 0.9);
//            
//            try {
//                while (operating && shouldRun && !myThread.isInterrupted()) {
//                	try {
//                		LogSocketEntry entry = logsToSend.take();
//                		if (!operating || !shouldRun || myThread.isInterrupted()) break;
//                		send(entry);
//                		if (logsToSend.size() > performanceLimit) {
//                			log.warning("Network logging reached its performance limit! Lower the levels of logging! logsToSend.size() == " + logsToSend.size());
//                		}
//                		logsToSend.drainTo(buffer);
//                		if (!operating || !shouldRun || myThread.isInterrupted()) break;
//                		for (LogSocketEntry logSocketEntry : buffer) {
//                			send(logSocketEntry);
//                    		if (!operating || !shouldRun || myThread.isInterrupted()) break;
//                		}
//                		buffer.clear();
//                	} catch (Exception e) { 
//                		if (!exceptionExpected) {
//                            log.severe(ExceptionToString.process("Exception at LogSendingWorker.", e));
//                        } else {
//                            log.fine(ExceptionToString.process("Exception at LogSendingWorker, expected.", e));
//                        }	
//                		break;
//                	}
//                }
//            } catch (Exception e) {
//            	if (!exceptionExpected) {
//                    log.severe(ExceptionToString.process("Exception at LogSendingWorker.", e));
//                } else {
//                    log.fine(ExceptionToString.process("Exception at LogSendingWorker, expected.", e));
//                }		                
//            } finally {
//            	running = false;
//            }
//
//            log.warning("LogSendingWorker Stopped.");
//        }
//    }
    
    // ----
    // --------
    // =============
    // SERVER WORKER
    // =============
    // --------
    // ----
    
    
    protected ServerWorker serverWorker  = null;
    
    /**
     * Thread of the server worker.
     */
    protected Thread serverWorkerThread = null;

    /**
     * Used to store socket where we awaits the 'agent id' that should we be logging.
     * @author Jimmy
     */
    private class DanglingSocket {
    	
    	CharsetEncoder encoder = USED_CHARSET.newEncoder();
        CharsetDecoder decoder = USED_CHARSET.newDecoder();
    	
    	public Socket socket;
    	public final long created = System.currentTimeMillis();
    	private PrintWriter writer;
    	private BufferedReader reader;
    	
    	private StringBuffer agentId = new StringBuffer();
    	
    	private char[] buf = new char[128];
    	
    	public DanglingSocket(Socket socket) throws IOException {
    		this.socket = socket;
    		this.writer = new PrintWriter(socket.getOutputStream());
    		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	}
    	
    	public String readAgentId() throws IOException {    		
			int read = reader.read(buf);
			if (read == 0) return null;
			if (buf[read-1] == '\n') {
				int minus = 1;
				if (read > 1 && buf[read-2] == '\r') {
					minus = 2;
				}
				agentId.append(buf, 0, read-minus);
				return agentId.toString();
			} else {
				agentId.append(buf, 0, read);
				return null;
			}    			
    		
    	}
    	
    }
    
    /**
     * Accepts new connections via {@link ServerWorker#serverSocket}, reads agent id that we should be logging on new sockets,
     * populates {@link NetworkLogManager#idToSocketList}.
     * 
     * @author Jimmy
     */
    private class ServerWorker implements Runnable {
    	
        /**
         * The only instance of {@link ServerSocket} which accepts new clients.
         */
        private ServerSocket serverSocket;

        /**
         * Simple flag that is telling us whether the Worker should run.
         */
        private volatile boolean shouldRun = true;
        private volatile boolean running = false;
        private volatile boolean exceptionExpected = false;
        private Thread myThread;
        
        /**
         * List of sockets where we did not received AgentID yet.
         */
        private List<DanglingSocket> danglingSockets = new LinkedList<DanglingSocket>();
        
        /**
         * Opens {@link ServerWorker#serverSocket}.
         * 
         * @throws IOException
         */
        public ServerWorker() throws IOException {
        	serverSocket = new ServerSocket();
        	serverSocket.bind (new InetSocketAddress (0));
            //serverSocket.configureBlocking(true);
        }

        /**
         * Drops the {@link ServerWorker#shouldRun} flag, waits for 100ms and then interrupts the
         * thread in hope it helps.
         */
        public void kill() {
            if (!running) {
                return;
            }
            this.shouldRun = false;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            exceptionExpected = true;
            myThread.interrupt();
        }

        /**
         * Contains main while cycle that is awaiting new connections at {@link ServerWorker#serverSocket}
         * and populating {@link NetworkLogManager#idToSocketList} with them.
         */
        @Override
        public void run() {
            
            myThread = Thread.currentThread();

            // set the running flag, we've been started
            running = true;

            // notify that gateway started
            if (log != null && log.isLoggable(Level.INFO)) log.info("ServerWorker started.");

            try {
                while (operating && shouldRun && !myThread.isInterrupted()) {
                	try {
                		Socket socket = null;
                		
                		if (danglingSockets.size() == 0) {
                			//serverSocket.configureBlocking(false);
                			socket = serverSocket.accept();
                		} else {
                			//serverSocket.configureBlocking(true);
                			serverSocket.setSoTimeout(100);
                			try {
                				socket = serverSocket.accept();                				
                			} catch (SocketTimeoutException ex) {
                			}
                		}
                		
                		if (socket != null) {
                			log.fine("Accepted new connection from " + socket.getRemoteSocketAddress());
                			//socket.configureBlocking(false);
	            			try {
	            				danglingSockets.add(new DanglingSocket(socket));
	            			} catch (IOException e1) {
	            				try {
	            					socket.close();
	            				} catch (Exception e2) {                					
	            				}
	            			}
                		}
                		
                		Iterator<DanglingSocket> iter = danglingSockets.iterator();
                		while (iter.hasNext()) {
                			DanglingSocket danglingSocket = iter.next();
                			String agentId = danglingSocket.readAgentId();
                			if (agentId != null) {
                				// agentId has been received!
                				IToken token = Tokens.get(agentId);
                				log.fine("Connection " + danglingSocket.socket.getRemoteSocketAddress() + " sent agent id '" + token.getToken() + "'.");
                				Iterator<NetworkLogEnvelope> iter2;
            					synchronized(idToEnvelopeList) {
            						ConcurrentLinkedQueue<NetworkLogEnvelope> queue = idToEnvelopeList.get(token);
            						int size = queue.size();
            						log.finer("Sending buffered " + size + " log records to the " + danglingSocket.socket.getRemoteSocketAddress());
            						iter2 = queue.iterator();
            					}            					
                				LogSocket logSocket = new LogSocket(danglingSocket.socket);
                				synchronized(logSocket) {
                					synchronized(idToSocketList) { 
                						idToSocketList.get(token).add(logSocket);
                					}   
                					//System.out.println("SENDING FIRST LOGS!");
                					while(iter2.hasNext()) {
                						NetworkLogEnvelope env = iter2.next();
                						//System.out.println(env);
                						logSocket.send(env);
                					}
                					//System.out.println("FINISHED");
                				}                				
                				iter.remove();
                				continue;
                			}
                			if (System.currentTimeMillis() - danglingSocket.created < NETWORK_LOG_MANAGER_SOCKET_TIMEOUT_MILLIS) {
                				log.warning("Connection " + danglingSocket.socket.getRemoteSocketAddress() + " timed out. We did not receive agent id in " + NETWORK_LOG_MANAGER_SOCKET_TIMEOUT_MILLIS + " ms. Closing the socket.");
                				try {
                					danglingSocket.socket.close();
                				} catch (IOException e) {                					
                				}
                				iter.remove();
                				continue;
                			}
                		}                		
                	} catch (Exception e) { 
                		if (!exceptionExpected) {
                            log.severe(ExceptionToString.process("Exception at ServerWorker.", e));
                        } else {
                            log.fine(ExceptionToString.process("Exception at ServerWorker, expected.", e));
                            break;
                        }	
                	}
                }
            } catch (Exception e) {
            	if (!exceptionExpected) {
                    log.severe(ExceptionToString.process("Exception at ServerWorker.", e));
                } else {
                    log.fine(ExceptionToString.process("Exception ServerWorker, expected.", e));
                }		                
            } finally {
            	running = false;            	
            	for (DanglingSocket socket : danglingSockets) {
            		try {
						socket.socket.close();
					} catch (Exception e) {
					}
            	}
            	danglingSockets.clear();
            	try {
					serverSocket.close();
				} catch (IOException e) {
				}
            }

            log.warning("ServerWorker Stopped.");
        }

    }

}
