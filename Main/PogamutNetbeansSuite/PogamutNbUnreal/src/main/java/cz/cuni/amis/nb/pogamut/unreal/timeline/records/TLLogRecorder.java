package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.nb.pogamut.unreal.timeline.view.TLTools;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;

/**
 * Class for recording all log events that will come from source log.
 *
 * It stores the messages and handles the dispatch events.
 *
 * @author Honza
 */
public class TLLogRecorder implements Comparable, Serializable {

    private transient Logger logger;
    private TLEntity entity;
    private transient HashSet<TLLogRecorderListener> listeners = new HashSet<TLLogRecorderListener>();
    private String name;
    private LogEvents logEvents;
    private MapEvents mapEvents;
    private transient Handler handler = new Handler() {

        @Override
        public void publish(final LogRecord record) {
            TLTools.runAndWaitInAWTThread(new Runnable() {

                @Override
                public void run() {
                    addRecord(record);
                }
            });
        }

        /**
         * Empty function because we immediatelly forward everything.
         */
        @Override
        public void flush() {
        }

        /**
         * Empty function because we immediatelly forward everything.
         */
        @Override
        public void close() throws SecurityException {
        }
    };

    TLLogRecorder(Logger logger, TLEntity entity) {
        this.entity = entity;
        this.logger = logger;
        this.mapEvents = new MapEvents(entity);
        this.logEvents = new LogEvents();
        this.name = logger.getName();

        this.logger.addHandler(handler);
    }

    /**
     * Get all map events that were in progress at passed time.
     * @param time time from which we want map events
     * @return Map events from time.
     */
    public List<MapEvent> getMapEvents(long time) {
        return mapEvents.getEvents(time);
    }

    public String getName() {
        return name;
    }

    public TLEntity getEntity() {
        return entity;
    }

    public Logger getSourceLogger() {
        return logger;
    }

    /**
     * Take new record, update last timestamp of entity and emit it to all listeners.
     * @param record
     */
    protected void addRecord(LogRecord record) {
        // handle records so I have consistent info.
        // All records are stored in the records
        //logRecords.add(record);

        // update log events according to info in record. In many cases nothing will happen.
        LogMessage logMessage = logEvents.updateLogMessages(record);
        if (logMessage != null) {
            emitNewLogMessage(logMessage);
        }

        LogEvent logEvent = logEvents.updateLogEvents(record);
        if (logEvent != null) {
            emitNewLogEvent(logEvent);
        }

        // update map events according to info in record. In many cases nothing will happen.
        mapEvents.update(record);

        long milis = record.getMillis();

        this.entity.setEndTime(milis);
    }

    void stopRecording(long timestamp) {
        if (this.logger != null) {
            this.logger.removeHandler(handler);
        }
    }

    /**
     * @return the listeners
     */
    protected HashSet<TLLogRecorderListener> getListeners() {
        if (listeners == null) {
            listeners = new HashSet<TLLogRecorderListener>();
        }
        return listeners;
    }

    /**
     * Listener for new log messages.
     */
    public interface TLLogRecorderListener {

        public void onNewLogEvent(LogEvent newEvent);

        public void onNewLogMessage(LogMessage newMessage);
    }

    private void emitNewLogEvent(LogEvent newEvent) {
        TLLogRecorderListener[] listenersArray = this.getListeners().toArray(new TLLogRecorderListener[]{});

        for (TLLogRecorderListener listener : listenersArray) {
            listener.onNewLogEvent(newEvent);
        }
    }

    private void emitNewLogMessage(LogMessage newMessage) {
        TLLogRecorderListener[] listenersArray = this.getListeners().toArray(new TLLogRecorderListener[]{});

        for (TLLogRecorderListener listener : listenersArray) {
            listener.onNewLogMessage(newMessage);
        }
    }

    public void addLogRecordListener(TLLogRecorderListener listener) {
        this.getListeners().add(listener);
    }

    public void removeLogRecordListener(TLLogRecorderListener listener) {
        this.getListeners().remove(listener);
    }

    @Override
    public int compareTo(Object o) {
        TLLogRecorder other = (TLLogRecorder) o;

        return this.getSourceLogger().getName().compareTo(other.getSourceLogger().getName());
    }

    public void printInfo(PrintStream stream) {
        stream.println("LogRecorder " + this.getName());

        for (LogMessage lMessage : logEvents.getMessages()) {
            stream.println(" * LM: " + lMessage.getMessage());
        }

        for (LogEvent lEvent : logEvents.getEvents()) {
            stream.println(" * LE: " + lEvent.getMessage());
        }

        for (MapEvent mEvent : mapEvents.getMapEvents()) {
            stream.println(" * " + mEvent.getMessage());
        }
        stream.println();
    }

    /**
     * Get all map events that were ever recorded/ing in this TLLogRecorder.
     * @return All map events from this recorder.
     */
    public MapEvents getMapEvents() {
        return mapEvents;
    }

    /**
     * Get all log events that were ever recorded/ing in this TLLogRecorder.
     * @return All log events from this recorder.
     */
    public LogEvents getLogEvents() {
        return logEvents;
    }
}

