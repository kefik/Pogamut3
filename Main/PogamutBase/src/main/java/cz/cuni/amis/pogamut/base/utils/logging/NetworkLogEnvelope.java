/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.base.utils.logging;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import cz.cuni.amis.pogamut.base.agent.IAgentId;

/**
 * Used to send/receive log messages that {@link NetworkLogManager} publishes.
 * 
 * @author Pyroh
 * @author Jimmy
 */
public class NetworkLogEnvelope implements Serializable {
	
	/**
	 * Randomly generated UID for the class. 
	 */
	private static final long serialVersionUID = 4641171456863931174L;
	
	private String category;
    private Level level;
    private long millis;
    private String message;

    public NetworkLogEnvelope(String cat, Level lev, long mil, String mes) {
    	category = cat;
        level = lev;
        millis = mil;
        message = mes;
    }
    
    public NetworkLogEnvelope(String category, String level, String time, String message) {
		this.category = category;
		this.level = Level.parse(level);
		this.millis = Long.valueOf(time).longValue();
		this.message = message;
	}
    
    public LogRecord asLogRecord() {
    	LogRecord result = new LogRecord(getLevel(), getMessage());
    	result.setMillis(getMillis());
        result.setLoggerName(getCategory());
    	return result;
    }

	public String getCategory() {
		return category;
	}

	public Level getLevel() {
		return level;
	}

	public long getMillis() {
		return millis;
	}

	public String getMessage() {
		return message;
	}

	@Override
    public String toString()  {
        return "<" + category + "> [" + level.toString() + "] " + millis + " " + message;
    }
}
