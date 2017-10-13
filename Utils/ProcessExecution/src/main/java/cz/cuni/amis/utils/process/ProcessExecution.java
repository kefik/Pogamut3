package cz.cuni.amis.utils.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.StreamSink;
import cz.cuni.amis.utils.Tuple3;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutIOException;
import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;
import cz.cuni.amis.utils.flag.ImmutableFlag;

/**
 * Class that wrapps the execution of the arbitrary process that is configured by {@link ProcessExecutionConfig}.
 * <p><p>
 * The class is not suitable for inheritance ... better copy-paste and adjust for yourself.
 * 
 * @author Jimmy
 */
public class ProcessExecution {
	
	/**
	 * Configuration of the process to run.
	 */
	protected ProcessExecutionConfig config;
	
	/**
	 * Flag whether the process is running, we're synchronizing access to this class on this flag.
	 * <p><p>
	 * Set to TRUE ONLY IN {@link ProcessExecution#start()} and back to FALSE ONLY IN {@link ProcessExecution#shutdown(boolean)}.
	 * Do not alter the flag from anywhere else!
	 */
	protected Flag<Boolean> running = new Flag<Boolean>(false);
	
	/**
	 * When {@link ProcessExecution#start()}ed it contains the process.
	 */
	protected Process process = null;
	
	/**
	 * Sink for the STDOUT of the process, it may be redirected to {@link ProcessExecution#log}.
	 */
	protected StreamSink streamSinkOutput = null;
	
	/**
	 * Sink for the STDERR of the process, it may be redirected to {@link ProcessExecution#log}.
	 */
	protected StreamSink streamSinkError = null;

	/**
	 * Log used by the object, specified during the construction.
	 */
	protected Logger log;

	/**
	 * @param config process execution configuration (contains path to file to run)
	 * @param log log to be used (stdout/stderr of the process will be redirected there as well with {@link Level#INFO})
	 */
	public ProcessExecution(ProcessExecutionConfig config, Logger log) {
		this.log = log;
		this.config = config;
	}
		
	/**
	 * Task that will kill the process when user forgets to do so before the JVM is killed.
     */
    protected Runnable shutDownHook = new Runnable(){

        @Override
        public void run() {
            if (process != null) process.destroy();
        }
        
    };
    
    protected Thread shutDownHookThread;
    
    /**
     * Whether the process timed out or not.
     */
    protected boolean timedout = false;
	
    /**
     * Task that is waiting for the end of the process to switch {@link ProcessExecution#running} back to false
     * and shutdown all other utility threads (like sink).
     */
    protected Runnable waitForEnd = new Runnable() {
    	
		@Override
		public void run() {
			long start = System.currentTimeMillis();
			long timeout = config.getTimeout() == null ? -1 : config.getTimeout();
			try {
				if (timeout > 0) {
					boolean terminated = false;
					while (System.currentTimeMillis() - start < timeout) {
						try {
							process.exitValue();
							terminated = true;
						} catch (Exception e) {
							terminated = false;
						}
						if (terminated) break;
						long waitMillis = Math.min(500, 100 + timeout - (System.currentTimeMillis() - start));
						Thread.sleep(waitMillis);
					}
					if (!terminated) {
						// TIMEOUT!
						synchronized(running) {
							if (waitForEndThread == Thread.currentThread()) {
								// we're still in the middle of the same execution... (shut down only if it is the same process execution)								
								timedout = true;
								if (log != null && log.isLoggable(Level.SEVERE)) {
									log.severe("Process(" + config.getId() + ") TIMEOUT!");
								}
								shutdown(true);
							}
						}
					}
				} else {
					process.waitFor();
				}
				// BOT PROCESS HAS STOPPED WHEN THE JVM REACHES HERE
			} catch (InterruptedException e) {
				// we've been interrupted!
				synchronized(running) {
					if (!running.getFlag()) {
						// okey we're not running, screw it...
						return;
					}
				}
				// hups, we're still running, warn the user!
				if (log != null && log.isLoggable(Level.WARNING)) {
					log.warning("Interrupted while waiting for the process(" + config.getId() + ") to end!");
				}
				
			} finally {
				// perform clean-up
				synchronized(running) {
					if (!running.getFlag()) {
						// okey we're not running, somebody has already shutdown, screw it...
						return;
					}
					if (waitForEndThread == Thread.currentThread()) {
						// we're still in the middle of the same execution... (shut down only if it is the same process execution)
						shutdown(true);
					}
				}
			}
		}
    };
    
    protected Thread waitForEndThread;

	private Integer lastExitValue = null;
	
    public static final Pattern SIMPLE_PARAM = Pattern.compile("\\$([a-zA-Z0-9_]+)");
    
    public static final Pattern FORMAL_PARAM = Pattern.compile("\\$\\{(.+)\\}");
    
    /**
     * Searches for $XXX or ${XXX} and substitute them with {@link System#getenv()} (prioritized) or {@link System#getProperty(String)}.
     * @param command
     * @return
     */
    public static String substituteParams(String command, Logger log) {
    	List<Tuple3<Integer, Integer, String>> params = new ArrayList<Tuple3<Integer, Integer, String>>();
    	Matcher m;
    	
    	for (int i = 0; i < 2; ++i) {
    		m = (i == 0 ? SIMPLE_PARAM.matcher(command) : FORMAL_PARAM.matcher(command));
    		while (m.find()) {
    			params.add(
    					new Tuple3<Integer, Integer, String>(
    						m.start(),          // where does the parameter start
    						m.group().length(), // how long the param is
    						m.group(1)          // name of the parameter matched
    					)
    			);
    		}
    	}
    	
    	if (params.size() == 0) return command;
    	
    	Collections.sort(
    		params, 
    		new Comparator<Tuple3<Integer, Integer, String>>() {
				@Override
				public int compare(Tuple3<Integer, Integer, String> o1,	Tuple3<Integer, Integer, String> o2) {
					return o2.getFirst() - o1.getFirst(); // descending order!
				}
    		}
    	);
    	
    	String result = command;
    	
    	if (log != null && log.isLoggable(Level.INFO)) log.info("Substituing parameters for: " + command);
    	
    	for (Tuple3<Integer, Integer, String> param : params) {
    		String paramValue = System.getenv(param.getThird());
    		if (paramValue == null) {
    			paramValue = System.getProperty(param.getThird());
    		}
    		if (paramValue == null) {
    			if (log != null && log.isLoggable(Level.WARNING)) log.warning("Parameter '" + param.getThird() + "' not found! Both System.getenv(\"" + param.getThird() + "\") and System.getProperty(\"" + param.getThird() + "\") evaluates to null!");
    			return null;
    		}
    		result = result.substring(0, param.getFirst()) + paramValue + result.substring(param.getFirst() + param.getSecond());
    	}
    	
    	if (log != null && log.isLoggable(Level.INFO)) log.info("Substitution result: " + result);
    	
    	return result;
    }
    
    /**
     * Start the process. Throws {@link PogamutIOException} if it fails to start the JVM.
     * <p><p>
     * It is wise to observe the state of the {@link ProcessExecution#getRunning()} flag to obtain the state of the process
     * (whether the process is running or is dead). Usage of {@link FlagListener} is advised, see {@link ImmutableFlag#addListener(FlagListener)}.
     */
	public void start() throws PogamutIOException {
		synchronized(running) {
			if (running.getFlag()) {
				throw new PogamutException("Could not start the process again, it is already running! stop() it first!", log, this);
			}
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("Starting process with config: " + Const.NEW_LINE + config);
			}
			
			if (config.getPathToProgram() == null) {
				throw new PogamutException("Could not start the process according to config " + config + " as the path to program was not specified (is null).", this);
			}
			
			String command = config.getPathToProgram();
			
			List<String> commandParts = new ArrayList<String>();
			commandParts.add(command);
			
			String fullCommand = command;
			
			if (config.getArgs() != null) {
				for (String arg : config.getArgs()) {
					commandParts.add(arg);
				}
			}
			
			for (int i = 0; i < commandParts.size(); ++i) {
				String part = substituteParams(commandParts.get(i), log);
				if (part == null) {
					throw new PogamutException("Could not substitute one of the command paramters, FAILURE!", log, this);
				}
				commandParts.set(i, part);
				if (i == 0) fullCommand = part;
				else fullCommand = fullCommand + " " + part;
			}
			
			File directory;
			
			if (config.getExecutionDir() != null) {
				directory = new File(config.getExecutionDir());
			} else {
				if (new File(config.getPathToProgram()).getParentFile() != null) directory = new File(config.getPathToProgram()).getParentFile();
				else directory = new File(".");
			}
			
			if (log != null && log.isLoggable(Level.INFO)) {
				log.info("Executing command: " + fullCommand);
				log.info("Base directory:    " + directory.getAbsolutePath());
			}
			
			ProcessBuilder procBuilder = 
				new ProcessBuilder(
					commandParts.toArray(new String[commandParts.size()])						
				);				            
			
			procBuilder.directory(directory);
	        
	        try {
	        	timedout = false;
	        	lastExitValue = null;
	        	process = procBuilder.start();
	        } catch (IOException e) {
	        	// failed to start the process
	        	if (log != null && log.isLoggable(Level.SEVERE)) {
	        		log.severe("Could not start the process: " + e.getMessage());
	        	}
	        	process = null;
	        	throw new PogamutIOException("Failed to start the process(" + config.getId() + "). IOException: " + e.getMessage(), e, this);
	        }
	        
	        // INITIALIZE ALL THREADS
	        if (config.isRedirectStdErr()) {
	        	streamSinkError = new StreamSink(config.getId()+"-StdErrSink", process.getErrorStream(), log, config.getId()+"-StdErr");
	        } else {
	        	streamSinkError = new StreamSink(config.getId()+"-StdErrSink", process.getErrorStream());
	        }
	        streamSinkError.start();
	        if (config.isRedirectStdOut()) {
	        	streamSinkOutput = new StreamSink(config.getId()+"-StdOutSink", process.getInputStream(), log, config.getId()+"-StdOut");
	        } else {
	        	streamSinkOutput = new StreamSink(config.getId()+"-StdOutSink", process.getInputStream());
	        }
	        streamSinkOutput.start();
	        shutDownHookThread = new Thread(shutDownHook, config.getId()+"-JVMShutdownHook");
	        Runtime.getRuntime().addShutdownHook(shutDownHookThread);
	        waitForEndThread = new Thread(waitForEnd, config.getId()+"-WaitForProcessEnd");
	        running.setFlag(true);    // we're ONLINE!
	        waitForEndThread.start(); // execute the thread that will wait for the end of the process to switch 'running' back to false
		}
	}
	
	/**
	 * Shutdowns the process and cleans-up everything, preparing the object to be {@link ProcessExecution#start()}ed again.
	 * @param waitForEndThread whether we're being called from the {@link ProcessExecution#waitForEnd} thread.
	 */
	protected void shutdown(boolean waitForEndThread) {
		Process p = process;
		synchronized(running) {
			if (!running.getFlag()) {
				// we're not running, screw it!
				return;
			}
			if (process != p) {
				// DIFFERENT PROCESS WERE STARTED! WE SHOULD NOT STOP IT! Someone was faster...
				return;
			}
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("Shutting down process(" + config.getId() + ")!");
			}

			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... destroying process(" + config.getId() + ").");
			}
			try {
				lastExitValue = process.exitValue();
			} catch (Exception e) {
			}
			if (process != null) {
				try {
					process.destroy();
				} catch (Exception e) {
				}
			}
			
			process = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... destroying streamSinkError(" + config.getId() + ").");
			}
			try {
				if (streamSinkError != null) streamSinkError.interrupt();
			} catch (Exception e) {
			}
			streamSinkError = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... destroying streamSinkOutput(" + config.getId() + ").");
			}
			try {
				if (streamSinkOutput != null) streamSinkOutput.interrupt();
			} catch (Exception e) {
			}
			streamSinkOutput = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... destroying waitForEnd(" + config.getId() + ").");
			}
			if (!waitForEndThread) {
				try {
					if (this.waitForEndThread != null) this.waitForEndThread.interrupt();
				} catch (Exception e) {
				}
			}
			this.waitForEndThread = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... removing shutDownHook(" + config.getId() + ").");
			}
			if (shutDownHookThread != null) {
				try {
					Runtime.getRuntime().removeShutdownHook(shutDownHookThread);
				} catch (Exception e) {
				}	
			}
			shutDownHookThread = null;
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("... setting running-flag(" + config.getId() + ") to FALSE.");
			}
			running.setFlag(false);
			
			if (log != null && log.isLoggable(Level.WARNING)) {
				log.warning("Shutdown(" + config.getId() + ") finished.");
			}
		}
	}
	
	/**
	 * Shuts down the process (if it is running, otherwise does nothing).
	 */
	public void stop() {
		shutdown(false);
	}
    
    /**
     * Process of the process (non-null if started).
     * @return
     */
    public Process getBotProcess() {
        return process;
    }
	
    /**
     * Flag of the state of the process. True == process is running (has been started using {@link ProcessExecution#start()}), 
     * False == process was not started or has been already terminated, either by itself or via {@link ProcessExecution#stop()}.
     * @return
     */
	public ImmutableFlag<Boolean> getRunning() {
		return running.getImmutable();
	}
	
	/**
	 * Flag of the state of the process. True == process is running (has been started using {@link ProcessExecution#start()}), 
     * False == process was not started or has been already terminated, either by itself or via {@link ProcessExecution#stop()}.
	 * @return
	 */
	public boolean isRunning() {
		return running.getFlag();
	}
	
	/**
	 * Returns exit value of the process. If null is returned == process is still running / has never finished before.
	 * @return
	 */
	public Integer getExitValue() {
		synchronized(running) {
			return lastExitValue;
		}
	}
	
	/**
	 * If {@link ProcessExecutionConfig#getTimeout()} is specified, this will indicate whether the started process has
	 * timed out or not.
	 * 
	 * @return
	 */
	public boolean isTimeout() {
		synchronized(running) {
			return timedout;
		}
	}

	/**
	 * Tells whether the process has failed == timed out / exit value is not 0.
	 * <p><p>
	 * True == process has failed.
	 * <p><p>
	 * False == process is running or process has finished OK.
	 * 
	 * @param report whether the reason of failure / success should be reported
	 * @return
	 */
	public boolean isFailed(boolean report) {
		if (isTimeout() || getExitValue() == null || getExitValue() != 0) {
			// FAILURE
			if (report && log != null && log.isLoggable(Level.WARNING)) {
				if (isTimeout()) {
					log.warning("Process TIMED OUT!");
				} else
				if (getExitValue() == null) {
					log.warning("Process FAILED to return exit value!");
				} else {
					log.warning("Process EXIT VALUE is " + getExitValue() + " != 0!");
				}
			}
			return true;
		}
		if (report && log != null && log.isLoggable(Level.INFO)) {
			if (isRunning()) {
				log.info("Process is still running.");
			} else {
				log.info("Process has finished OK.");
			}
		}
		return false;
	}
	
	/**
	 * Tells whether the process has failed == timed out / exit value is not 0.
	 * <p><p>
	 * True == process has failed.
	 * <p><p>
	 * False == process is running or process has finished OK.
	 * 
	 * @return
	 */
	public boolean isFailed() {
		return isFailed(false);
	}
	
}
