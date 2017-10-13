package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base.utils.logging.marks.LogEventMark;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Class for storing LogEvents of LogRecorder.
 *
 * @see MapEvents
 * @author Honza
 */
public class LogEvents  implements Serializable {

    private static class LogSlot implements Serializable {
        public LogSlot(LogEvent event, long endTime) {
            this.event = event;
            this.endTime = endTime;
        }

        public void update(LogEvent event) {
            this.event = event;
            this.endTime = event.getEndTS();
        }

        private LogEvent event;
        private long endTime;
    }

    private LinkedList<LogMessage> logMessages = new LinkedList<LogMessage>();

    /**
     * All LogEvents that we have (even just started and unfinished ones).
     */
    private LinkedList<LogEvent> allEvents = new LinkedList<LogEvent>();

    /**
     * Events that has started, but haven't yet encountered ending mark.
     * Some "slots" can be null.
     */
    private ArrayList<LogEvent> unfinishedEvents = new ArrayList<LogEvent>();

    /**
     * Slots for the events
     */
    private ArrayList<LogSlot> slots = new ArrayList<LogSlot>();

    /**
     * Get slot we can change at time <code>time</code>.
     * @param time
     * @return
     */
    private int getFreeSlot(long time) {
        int slotIndex = 0;
        for (LogSlot slot : slots) {
            if (slot.endTime < time) {
                // this is the slot we have been looking for
                return slotIndex;
            }
            slotIndex++;
        }

        // no such slot, create one
        slots.add(new LogSlot(null, Integer.MIN_VALUE));
        return slots.size() - 1;
    }

    /**
     * Does passed record have LogEventMark as one of its parameters
     * @param record
     * @return null if record doesn't have the mark, else first LogEventMark found
     */
    private LogEventMark hasMark(LogRecord record) {
        for (Object o : record.getParameters()) {
            if (o instanceof LogEventMark) {
                return (LogEventMark) o;
            }
        }
        return null;
    }

    /**
     *
     * @param record
     * @return new LogMessage if one was created from the passed record
     */
    LogMessage updateLogMessages(LogRecord record) {
        LogEventMark mark = hasMark(record);
        if (mark == null)
            return null;


        if (mark.getType() == LogEventMark.Type.SINGLE_EVENT) {
            LogMessage newMessage = new LogMessage(record, mark);
            logMessages.add(newMessage);

//            System.out.println("LE: Got new message. Created " + mark.getTime() + ", msg " + mark.getText());

            return newMessage;
        }
        return null;
    }

    /**
     * Take passed record and try to update events according to info in record.
     * 
     * @param record not null, just record, doesn't have to have LogEventMark as one of its parameters
     */
    LogEvent updateLogEvents(LogRecord record) {
        LogEventMark mark = hasMark(record);
        if (mark == null)
            return null;

        if (mark.getType() == LogEventMark.Type.FIXED_DURATION) {
            int slot = getFreeSlot(mark.getTime());

            LogEvent newLogEvent = new LogEvent(record, mark, slot);
            slots.get(slot).update(newLogEvent);
            allEvents.add(newLogEvent);

//            System.out.println("LE: Got new mark. Type " + mark.getType() + ", created " + mark.getTime() + ", msg " + mark.getText());

            return newLogEvent;
        }

        if (mark.getType() == LogEventMark.Type.START_EVENT) {
            int slot = getFreeSlot(mark.getTime());

            LogEvent newLogEvent = new LogEvent(record, mark, slot);
            slots.get(slot).update(newLogEvent);
            unfinishedEvents.add(newLogEvent);
            allEvents.add(newLogEvent);

//            System.out.println("LE: Got new mark. Type " + mark.getType() + ", created " + mark.getTime() + ", msg " + mark.getText());

            return newLogEvent;
        }

        if (mark.getType() == LogEventMark.Type.END_EVENT) {
            LogEvent unfinished = this.getUnfinishedEventWithMark(mark);
            if (unfinished == null) {
                if (Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).isLoggable(Level.SEVERE)) Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe("Got end of log event, but no such event started. Possibly duplicated end. Record message " + record.getMessage());
                return null;
            }
            finishEvent(unfinished, record);
            return null;
        }
        return null;
    }

    /**
     * Go through list of unfinished events and find if there is an event
     * that has been started, but hasn't ended (haven't recieved end mark
     * for it).
     * @param mark End mark of some log event.
     * @return null if no corresponding event found, otherwise log event the end mark is supposed to finish.
     */
    private LogEvent getUnfinishedEventWithMark(LogEventMark mark) {
        for (LogEvent event : unfinishedEvents) {
            // XXX: I should check type too, but that may break things.
            if (event.getMark().getId() == mark.getId()) {
                return event;
            }
        }
        return null;
    }

    /**
     * Finish the event, move from currentEvents to finishedEvents.
     * Slot in currentEvents set to null
     *
     * @param unfinished logevent that is in currentEvents
     */
    private void finishEvent(LogEvent unfinished, LogRecord closingRecord) {
        // unfinished event recieved closing record -> now it knows its duration
        unfinished.revievedClosingRecord(closingRecord);
        slots.get(unfinished.getSlot()).endTime = unfinished.getEndTS();
        unfinishedEvents.remove(unfinished);
    }

    public List<LogMessage> getMessages() {
        return Collections.unmodifiableList(this.logMessages);
    }

    public List<LogEvent> getEvents() {
        return Collections.unmodifiableList(this.allEvents);
    }
}
