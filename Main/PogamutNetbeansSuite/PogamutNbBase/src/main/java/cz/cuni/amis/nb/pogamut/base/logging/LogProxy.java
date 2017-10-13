/*
 * LogProxy.java
 *
 * Created on 21. březen 2007, 16:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.base.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 * Adapter of standard Java logger to LogRecordsSource object. This class also
 * provides property sheet for GUI.
 * @author ik
 */
public class LogProxy extends LogRecordsSource<LogRecord> {

    /**
     * Names of log levels.
     */
    protected static String[] levelNames = new String[9];
    /**
     * Values of log level constants, indexes correspond to <code>levels</code> array.
     */
    protected static int[] levelVals = new int[9];
    /**
     * Array of all standard log levels.
     */
    protected static Level[] levels = new Level[]{Level.ALL, Level.SEVERE, Level.INFO, Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.OFF};
    /** Mapping between log level identifiers and their indexes in <code>levelNames</code>
     * array.
     */
    protected static Map<Level, Integer> levelToIndex = new HashMap<Level, Integer>();

    /** initialize log levels arrays .. needed for combobox in property sheets */
    {
        for (int i = 0; i < levels.length; i++) {
            levelToIndex.put(levels[i], i);
            levelNames[i] = levels[i].getLocalizedName();
            levelVals[i] = i;
        }
    }
    /**
     * Log handler.
     */
    protected Handler handler = new Handler() {

        public void publish(LogRecord record) {
            addMessage(record);
        }

        public void flush() {
        }

        public void close() throws SecurityException {
        }
    };
    Filter filter = null;

    /**
     * Creates a new instance of LogProxy
     * @param log Logger to be adapted by this LogProxy.
     */
    public LogProxy(Logger log) {
        log.addHandler(handler);
        log.setLevel(Level.ALL);
        this.addFilter(filter = new FilterLog(Level.ALL));
    }

    /** Sets new pattern for regexp filter. */
    public void setRegexpFilterPatter(Pattern pattern) {
        if (pattern == null) {
            removeFilter(regexpFilter);
        } else {
            regexpFilter = (LogRecordsSource.RegexpFilter) replaceFilter(regexpFilter, new RegexpFilter<LogRecord>(pattern) {

                public boolean filter(LogRecord r) {
                    return pattern.matcher(r.getMessage()).matches();
                }
            });
        }
    }

    public Sheet.Set getPropSet() {
        Sheet.Set props = super.getPropSet();

        Node.Property p = new PropertySupport.ReadWrite("loglevel", Integer.class, "Log level", "") {

            public void setValue(Object val) {
                filter = replaceFilter(filter, new LogProxy.FilterLog(levels[(Integer) val]));
            }

            public Object getValue() {
                Integer i = levelToIndex.get(((LogProxy.FilterLog) filter).getLevel());
                return i;
            }
        };

        p.setValue("intValues", levelVals);
        p.setValue("stringKeys", levelNames);
        props.put(p);

        return props;
    }

    /**
     * Pass the log record to the ancestor (LogRecordSource).
     */
    protected LogRecord toLogRecord(LogRecord t) {
        return t;
    }

    protected LogRecord getLogRecParam(LogRecord lr) {
        return lr;
    }

    /**
     * Filter for log records. Records are filtered by their levels.
     */
    protected class FilterLog implements LogRecordsSource.Filter<LogRecord> {

        protected Level level = Level.ALL;

        /**
         * Get last level that will pass through this filter.
         */
        public Level getLevel() {
            return level;
        }

        public FilterLog(Level level) {
            this.level = level;
        }

        public boolean filter(LogRecord t) {
            return t.getLevel().intValue() >= level.intValue();
        }
    }
    
}
