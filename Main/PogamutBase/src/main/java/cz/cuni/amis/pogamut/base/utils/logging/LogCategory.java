package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.utils.logging.marks.LogEventMark;
import cz.cuni.amis.pogamut.base.utils.logging.marks.LogMapMark;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 * Instance that serves as a gateway for your log messages.
 * <p><p>
 * The trick with categories is that every log record may contain
 * object parameters. So we're appending instance of this class as
 * a very first parameter to the log record and filter those messages
 * according to them.
 * 
 * @author Jimmy
 */
public class LogCategory extends Logger implements Comparable<LogCategory> {
	
	private String categoryName;
	
	private Object mutex = new Object();

	private Logger parent;
	
	public LogCategory(String categoryName) {
		this(categoryName, null);
	}
	
	public LogCategory(String categoryName, Logger parent) {
		super(categoryName, null);
		this.parent = parent;
		this.categoryName = categoryName;
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	
	@Override
	public void setLevel(Level logLevel) throws SecurityException {
		if (logLevel == null) return;
		if (getLevel() == logLevel) return;
		if (getLevel() != null) { 
			log(getLevel(), "Log level set to " + logLevel + ".");
		} else {
			log(Level.WARNING, "Log level set to " + logLevel + ".");
		}
		super.setLevel(logLevel);
	}
	
	/**
	 * Adds default console handler with 'Platform' (== platform logging) agent id.
	 * <p><p>
	 * Use only when using {@link LogCategory} separately, i.e., outside {@link IAgentLogger}.
	 * 
	 * @return this instance
	 */
	public LogCategory addConsoleHandler() {
		addHandler(new LogPublisher.ConsolePublisher(new AgentId("Platform")));
		return this;
	}
	
	/**
	 * Returns new LogHandler with null ILogPublisher that is appended
	 * to the logger and filters log messages for this category.
	 * <p><p>
	 * The handler will use LogPublisher.ConsolePublisher as default.
	 * <p><p>
	 * This is the quickest way to obtain new output from the log. 
	 * 
	 * @return
	 */
	public LogHandler addHandler() {
		return addHandler((ILogPublisher)null);
	}
	
	/**
	 * Returns new LogHandler with specified ILogPublisher that is appended
	 * to the logger and filters log messages for this category.
	 * <p><p>
	 * The handler will use LogPublisher.ConsolePublisher as default.
	 * <p><p>
	 * This is the quickest way to obtain new output from the log. 
	 * 
	 * @return
	 */
	public LogHandler addHandler(ILogPublisher publisher) {
		LogHandler handler = new LogHandler();
		handler.setFilter(new LogCategoryFilter(this));
		handler.setPublisher(publisher);
		this.addHandler(handler);
		return handler;
	}
		
	/**
	 * All other logging methods is calling this one. Synchronized!
	 */
	@Override
	public void log(Level level, String msg, Object[] params) {
		synchronized(mutex) {
			if (params.length == 0) {
				super.log(level, msg, this.getCategoryName());
				if (parent != null) parent.log(level, msg, this.getCategoryName());
			} else {
				Object[] finalParams = new Object[params.length+1];
				finalParams[0] = this.getCategoryName();
				System.arraycopy(params, 0, finalParams, 1, params.length);
				super.log(level, msg, finalParams);
				if (parent != null) parent.log(level, msg, this.getCategoryName());
			}
		}
	}
	
	@Override
	public void log(Level level, String msg) {
		log(level, msg, new Object[0]);
	}
	
	@Override
	public void log(Level level, String msg, Object param) {
		log(level, msg, new Object[]{param});
	}
	
	@Override
	public void finest(String msg) {
		log(Level.FINEST, msg);
	}
	
	public void finest(String msg, Object param) {
		log(Level.FINEST, msg, param);
	}
	
	public void finest(String msg, Object[] params) {
		log(Level.FINEST, msg, params);
	}
	
	@Override
	public void finer(String msg) {
		log(Level.FINER, msg);
	}
	
	public void finer(String msg, Object param) {
		log(Level.FINER, msg, param);
	}
	
	public void finer(String msg, Object[] params) {
		log(Level.FINER, msg, params);
	}
	
	@Override
	public void fine(String msg) {
		log(Level.FINE, msg);
	}
	
	public void fine(String msg, Object param) {
		log(Level.FINE, msg, param);
	}
	
	public void fine(String msg, Object[] params) {
		log(Level.FINE, msg, params);
	}
	
	@Override
	public void info(String msg) {
		log(Level.INFO, msg);
	}
	
	public void info(String msg, Object param) {
		log(Level.INFO, msg, param);
	}
	
	public void info(String msg, Object[] params) {
		log(Level.INFO, msg, params);
	}
	
	@Override
	public void warning(String msg) {
		log(Level.WARNING, msg);
	}
	
	public void warning(String msg, Object param) {
		log(Level.WARNING, msg, param);
	}
	
	public void warning(String msg, Object[] params) {
		log(Level.WARNING, msg, params);
	}
	
	@Override
	public void severe(String msg) {
		log(Level.SEVERE, msg);
	}
	
	public void severe(String msg, Object param) {
		log(Level.SEVERE, msg, param);
	}
	
	public void severe(String msg, Object[] params) {
		log(Level.SEVERE, msg, params);
	}

	@Override
	public String toString() {
		return "LogCategory("+getName()+")";
	}

	@Override
	public int compareTo(LogCategory o) {
		if (o.categoryName == null) {
			if (categoryName == null) return 0;
			return -1;
		} else
		if (categoryName == null) {
			return 1;
		}
		return o.categoryName.compareTo(categoryName);
	}


    /**
     * Add mark to the map for specified time and add notice to the logs.
     * @param level What level is this mark? If too low, it can be filtered out.
     * @param msg What text should be shown on the map at the specified place.
     * @param location Location, where should be mark placed. If null, mark will follow the agent.
     * @param duration How long should be mark shown? In ms.
     */
    public void addMapMark(Level level, String msg, Location location, long duration) {
        String text4log = "[Map mark](" + duration + "ms) " + msg;
        LogMapMark mark = LogMapMark.createFixedLengthEvent(level, msg, location, duration);

        log(level, text4log, mark);
    }

    /**
     * Add mark to the map. If you want to remove the mark, use {@link LogCategory#removeMapMark(java.lang.Object) }
     *
     * @param level What level is this mark? If too low, it can be filtered out.
     * @param msg What text should be shown on the map at the specified place.
     * @param location Location, where should be mark placed. If null, mark will follow the agent.
     * @return object that can be later used in {@link LogCategory#removeMapMark(java.lang.Object) } in order to remove the mark from the map.
     */
    public LogMapMark addMapMark(Level level, String msg, Location location) {
        LogMapMark mark = LogMapMark.createVariableLengthEvent(level, msg, location);
        String text4log = "[Map mark](START:" + mark.getId() + ") " + msg;

        log(level, text4log, mark);

        return mark;
    }

    /**
     * Remove mark from the map. Mark should be previosly inserted using {@link LogCategory#addMapMark(java.util.logging.Level, java.lang.String, cz.cuni.amis.pogamut.base3d.worldview.object.Location) }
     * @param mark object that was returned by {@link LogCategory#addMapMark(java.util.logging.Level, java.lang.String, cz.cuni.amis.pogamut.base3d.worldview.object.Location) }
     *             during addition of mark to the map.
     */
    public void removeMapMark(LogMapMark mark) {
        assert mark!= null;

        String text4log = "[Map mark](END:" + mark.getId() + ") " + mark.getMessage();

        log(mark.getLevel(), text4log, mark.getEndMark());
    }

    /**
     * Add log message to the log. Log message is log event with zero duration.
     * @param text text of log message.
     */
    public void addLogMessage(Level level, String text) {
        LogEventMark mark = LogEventMark.createSingleLengthEvent(level, text);
        String text4log = "[Log event](message) " + text;

        log(level, text4log, mark);
    }

    /**
     * Add new log event to the log, the event starts now.
     * @param level level of event. 
     * @param text text of event
     * @param duration how long should event last?
     */
    public void addLogEvent(Level level, String text, long duration) {
        LogEventMark mark = LogEventMark.createFixedLengthEvent(level, text, duration);
        String text4log = "[Log event](" + duration + "ms) " + text;

        log(level, text4log, mark);
    }

    /**
     * Add new log event to the log, event won't end, until removed using {@link LogCategory#removeLogEvent(java.lang.Object) }.
     * @param level level of event.
     * @param text text of event
     * @return object that can be later used in {@link LogCategory#removeLogEvent(java.lang.Object)  } in order to remove the event from the log.
     */
    public LogEventMark addLogEvent(Level level, String text) {
        LogEventMark mark = LogEventMark.createVariableLengthEvent(level, text);
        String text4log = "[Log event](START:" + mark.getId()+ ") " + text;

        log(level, text4log, mark);

        return mark;
    }

    /**
     * Remove log event with undefined duration. The event should be created by {@link LogCategory#addLogEvent(java.util.logging.Level, java.lang.String) }
     * @param mark object returned by {@link LogCategory#addLogEvent(java.util.logging.Level, java.lang.String) }
     *                     during addition of log event to the log.
     */
    public void removeLogEvent(LogEventMark mark) {
        assert mark!= null;

        String text4log = "[Log event](END:" + mark.getId() + ") " + mark.getText();

        log(mark.getLevel(), text4log, mark.getEndMark());
    }
}
