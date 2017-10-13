/*
 * LogRecordsSource.java
 *
 * Created on 21. b�ezen 2007, 9:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.base.logging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 * Source of LogRecords, it caches incomming logrecords and provides observers new
 * collection of records when filter changes. All incoming log 
 * records are being cached so when the filter changes the listeners are provided
 * with a refiltered list of past log records.
 * @author student
 */
abstract public class LogRecordsSource<T> {

    /** Interface for filtering messages. */
    public interface Filter<T> {

        /**
         * Decide if given object (usually parameter of log record) will pass through this
         * filter.
         * @param t object to be filtered.
         * @return true if object was accepted.
         */
        public boolean filter(T t);
    }

    /** Filter based on regexp matching the toString() of the object */
    public class RegexpFilter<T> implements Filter<T> {

        Pattern pattern = null;

        public RegexpFilter(Pattern pattern) {
            this.pattern = pattern;
        }

        public boolean filter(T t) {
            return pattern.matcher(t.toString()).matches();
        }
    }

    public LogRecordsSource() {
        // Default filter for log records.
        filters.add(new Filter() {

            public boolean filter(Object t) {
                return true;
            }
        });
    }
    /**
     * List of all filters.
     */
    protected List<Filter<T>> filters = new ArrayList<Filter<T>>();
    protected Set<LogRecordListener> listeners = new HashSet<LogRecordListener>();
    /**
     * Queue of all log records.
     */
    protected LimitedQueue<LogRecord> queue = new LimitedQueue<LogRecord>();

    public void addLogRecordListener(LogRecordListener l) {
        listeners.add(l);
        notifySetNewData(l);
    }

    public void removeLogRecordListener(LogRecordListener l) {
        listeners.remove(l);
    }

    /**
     * Clears all log records.
     */
    public void clear() {
        queue.clear();
        notifySetNewData();
    }

    /**
     * Notify all listeners that new record has arrived.
     */
    protected void notifyLogRecordsListeners(LogRecord r) {
        //TODO clone LogRecord?
        for (LogRecordListener listener : listeners) {
            listener.notifyNewLogRecord(r);
        }
    }

    /** Format data element as LogRecord. */
    abstract protected LogRecord toLogRecord(T t);

    /** Send new data to all listeners. */
    public void notifySetNewData() {
        Collection<LogRecord> data = getFilteredData();
        for (LogRecordListener listener : listeners) {
            listener.setNewData(data);
        }
    }

    /** Send new data to one listeners. */
    public void notifySetNewData(LogRecordListener listener) {
        listener.setNewData(getFilteredData());
    }

    /**
     * 
     * @return filter used by this source.
     */
    /*public Filter getFilter() {
    return filter;
    }
     */
    /**
     * Apply filter to all cached records and return this new collection.
     */
    protected Collection<LogRecord> getFilteredData() {
        List<LogRecord> list = new ArrayList<LogRecord>();
        synchronized (queue) {
            recLoop:
            for (LogRecord lr : queue.getAll()) {
                T msg = getLogRecParam(lr);
                if (filter(msg)) {
                    list.add(lr);
                }
            }
            return list;
        }
    }

    protected boolean filter(T msg) {
        for (Filter f : filters) {
            if (!f.filter(msg)) {
                // skip this log record
                return false;
            }
        }
        return true;
    }

    public Filter<T> replaceFilter(Filter<T> oldFilter, Filter<T> newFilter) {
        removeFilter(oldFilter);
        addFilter(newFilter);
        return newFilter;
    }
    RegexpFilter regexpFilter = new RegexpFilter(Pattern.compile(""));

    /** Sets new pattern for regexp filter. */
    public void setRegexpFilterPatter(Pattern pattern) {
        if (pattern == null) {
            removeFilter(regexpFilter);
            regexpFilter = null;
        } else {
            regexpFilter = (LogRecordsSource.RegexpFilter) replaceFilter(regexpFilter, new RegexpFilter(pattern));
        }
    }

    /**
     * Change records filter and notify all listeners.
     * @param filter new filter
     */
    public void addFilter(Filter filter) {
        if (this.filters.add(filter)) {
            notifySetNewData();
        }
    }

    public void removeFilter(Filter filter) {
        if (this.filters.remove(filter)) {
            notifySetNewData();
        }
    }

    /** @return parameter of given log record */
    protected T getLogRecParam(LogRecord lr) {
        return (T) lr.getParameters()[0];
    }

    /**
     * Adds new object to queue of objects. This means:
     * <ol>
     *    <li>Construct LogRecord</li>
     *    <li>Memorize it</li>
     *    <li>Notify listeners</li>
     * </ol>
     */
    protected void addMessage(T msg) {
        LogRecord lr = toLogRecord(msg);
        lr.setParameters(new Object[]{msg});

        synchronized (queue) {
            queue.add(lr);
        }
        if (filter(msg)) {
            notifyLogRecordsListeners(lr);
        }
    }

    /**
     * Returns property sheet associated with node representing this source.
     * TODO move it to the node?
     * @return property sheet associated with node representing this source.
     */
    public Sheet.Set getPropSet() {
        Sheet.Set props = Sheet.createPropertiesSet();

        final ResourceBundle bundle = NbBundle.getBundle(LogRecordsSource.class);

        class RegexpFilterProp extends PropertySupport.ReadWrite {
            //Pattern tempPat = Pattern.compile("");
            public RegexpFilterProp() {
                super("regexpFilterProp", Pattern.class,
                        bundle.getString("PROP_RegexpFilter"),
                        bundle.getString("HINT_RegexpFilter"));
            }

            public Object getValue() {
                if (regexpFilter == null) {
                    return null;
                } else {
                    return regexpFilter.pattern;
                }
            }

            public void setValue(Object object) throws IllegalAccessException, IllegalArgumentException {
                setRegexpFilterPatter((Pattern) object);
            }
        }

        props.put(new RegexpFilterProp());

        return props;
    }
}
