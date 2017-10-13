package cz.cuni.amis.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads content of the stream and discards it.
 * <p><p>
 * Optionally it may log the content of the stream to some logger (i.e., redirects the output of the stream to some log).
 */
public class StreamSink extends Thread {

    protected InputStream os = null;
	protected Logger log;
	protected String logId;
	protected Level logLevel = Level.INFO;

	/**
	 * Constructs the sink to silently discard all contents of 'os'.
	 * @param name name of the sink thread
	 * @param os stream to be sunk
	 */
    public StreamSink(String name, InputStream os) {
    	this(name, os, null);
    }
    
    /**
     * Constructs the sink to redirect all output from 'os' into 'log' (used log level is {@link Level#INFO}) as default. May
     * be changed by {@link StreamSink#setLogLevel(Level)}.
     * May 
     * @param name name of the sink thread
     * @param os
     * @param log
     */
    public StreamSink(String name, InputStream os, Logger log) {
    	this(name, os, log, null);
    }
    
    /**
     * Constructs the sink to redirect all output from 'os' into 'log' (used log level is {@link Level#INFO}) as default. May
     * be changed by {@link StreamSink#setLogLevel(Level)}. Additionally all messages from the 'os' will be prefixed with 'logId+" "'.
     * 
     * @param name name of the sink thread
     * @param os
     * @param log
     * @param logId
     */
    public StreamSink(String name, InputStream os, Logger log, String logId) {
    	super(name);
        this.log = log;
        this.logId = logId;
        this.os = os;
    }
    
    public Level getLogLevel() {
		return logLevel;
	}

	public StreamSink setLogLevel(Level logLevel) {
		NullCheck.check(logLevel, "logLevel");
		this.logLevel = logLevel;
		return this;
	}
	
	public Logger getLog() {
		return log;
	}
	
	protected StreamSink setLog(Logger log) {
		this.log = log;
		return this;
	}

	public String getLogId() {
		return logId;
	}

	protected StreamSink setLogId(String logId) {
		this.logId = logId;
		return this;
	}
	
	protected void handleInput(String str) {
        if (log != null && log.isLoggable(Level.INFO)) {
        	if (logId != null) {
        		log.info(logId + " " + str);
        	} else {
        		log.info(str);
        	}
        }
    }

    @Override
    public void run() {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(os));

        String s = null;
        try {
            while ((s = stdInput.readLine()) != null) {
                handleInput(s);
            }
            os.close();
        } catch (IOException ex) {
            // the process has been closed so reading the line has failed, 
            // don't worry about it
        }
    }
}