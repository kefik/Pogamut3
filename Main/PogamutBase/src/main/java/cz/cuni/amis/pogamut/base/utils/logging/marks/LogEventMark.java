package cz.cuni.amis.pogamut.base.utils.logging.marks;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import java.io.Serializable;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * DO NOT USE THIS CLASS!! Use methods in {@link LogCategory} to add events to the log.
 * <p/>
 * This class is special parameter of the {@link LogRecord} that means some
 * kind of event has happend to the agent. The handler of log records can do some
 * nifty stuff when he finds this object as parameter of {@link LogRecord},
 * like showing in some GUI.
 * <p/>
 * Under normal circumstances, you create new mark using one of create* methods,
 * pass it as parameter of some log message, it gets transfered (probably through JMX)
 * to some handler on the other side (in our particular case to Netbeans plugin
 * and timeline), where some handler will look for {@link LogRecord LogRecords}
 * with this object as one of parameters and when it finds it, it shows the mark
 * in some GUI.
 * {@code
 *  // create an event that with text "Text of event for GUI" that will be
 *  // shown for duration of 2 seconds.
 *  logger.info("Text of event for text logger" , new Object[]{LogEventMark.createFixedLengthEvent(Level.INFO, "Text of event for GUI", null, 2000)});
 * }
 *
 * @see LogMapMark
 * @author Honza
 */
public class LogEventMark implements Serializable {

    /**
     * Type of {@link LogEventMark}. 
     */
    public enum Type {

        /**
         * Single time event. Event of this type has no duration,
         * <p/>
         * Example: I have died.
         */
        SINGLE_EVENT,
        /**
         * Event that lasts for certain specified duration.
         * <p/>
         * Example: Item taken, won't spawn for 32 seconds
         */
        FIXED_DURATION,
        /**
         * Used as start mark of event that has variable duration. This ends
         * only when corresponding end event is send.
         * <p/>
         * Example: bot has been hurt, start event "bot is low on health"
         */
        START_EVENT,
        /**
         * End mark of event with variable duration, shoudn't be created directly,
         * but through {@link LogEventMark#getEndMark() }
         * Example: bot picked health pack, end event "bot is low on health"
         */
        END_EVENT
    }
    /**
     * Generator of unique id. All create* methods are synchronized so you can't
     * generate two marks with same id.
     */
    private static int lastAssignedID = 0;
    /**
     * Unique id of event. Start and end marks of same event have same id.
     */
    private final int id;
    /**
     * Level of event.
     */
    private final Level level;
    /**
     * Text of event.
     */
    private final String text;
    /**
     * Timestamp for when the event has started. In ms since epoch.
     */
    private final long time;
    /**
     * How long should event last, in ms.
     */
    private final long duration;
    /**
     * Type of event.
     */
    private Type type;

    /**
     * Create new log event mark. For use of create* methods.
     * @param id unique id of log event, only start and end marks of same event can have same id.
     * @param level how important is this log event
     * @param text text of event
     * @param time when should event start, in ms since epoch
     * @param duration how long should event last.
     * @param type what is type of mark.
     */
    private LogEventMark(int id, Level level, String text, long time, long duration, Type type) {
        this.id = id;
        this.level = level;
        this.text = text;
        this.time = time;
        this.duration = duration;
        this.type = type;
    }

    /**
     * Create new log event mark. Create id from {@link LogEventMark#lastAssignedID} 
     * and set start time of event from from {@link Calendar#getTimeInMillis() }.
     * Basically wrapper for {@link LogEventMark#LogEventMark(int, java.lang.String, long, long, cz.cuni.amis.pogamut.base.utils.logging.marks.LogEventMark.Type) }.
     * @param level how important is this log event
     * @param text text of event
     * @param duration how long should event last.
     * @param type what is type of mark.
     */
    private LogEventMark(Level level, String text, long duration, Type type) {
        this(++lastAssignedID, level, text, Calendar.getInstance().getTimeInMillis(), duration, type);
    }

    /**
     * Create single event. Such event has no duration.
     * @param level level of log event, should be same as carrying {@link LogRecord}.
     * @param text text of single event
     * @return created single event.
     */
    public static synchronized LogEventMark createSingleLengthEvent(Level level, String text) {
        return new LogEventMark(level, text, 0, Type.SINGLE_EVENT);
    }

    /**
     * Create log event that will last for some time.
     * @param level level of log event, should be same as carrying {@link LogRecord}.
     * @param text Text of event
     * @param duration how long should event last.
     */
    public static synchronized LogEventMark createFixedLengthEvent(Level level, String text, long duration) {
        return new LogEventMark(level, text, duration, Type.FIXED_DURATION);
    }

    /**
     * Create log event that will last until notified it should stop.
     * In order to stop the event, create end mark of event using
     * {@link LogEventMark#getEndMark() }.
     * @param level level of log event, should be same as carrying {@link LogRecord}.
     * @param text text of event.
     * @return created event.
     */
    public static synchronized LogEventMark createVariableLengthEvent(Level level, String text) {
        return new LogEventMark(level, text, Integer.MAX_VALUE, Type.START_EVENT);
    }

    /**
     * Get human readable representation of LogEventMark.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getText();
    }

    /**
     * Get unique id of event. Start and end marks of variable event have 
     * same id, because they mark same event.
     */
    public int getId() {
        return id;
    }

    /**
     * What is level of this event?
     * @return level of event.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * @return the message of this event
     */
    public String getText() {
        return text;
    }

    /**
     * @return when does event start
     */
    public long getTime() {
        return time;
    }

    /**
     * @return the type of event
     */
    public Type getType() {
        return type;
    }

    /**
     * @return the duration of event. 0 for single event, Integer.MAX_VALUE for variable length event
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Get ending mark for variable length event.
     * @return if this mark is {@link Type#START_EVENT start mark}, create new end mark,
     *         if this mark is {@link Type#END_EVENT end mark}, return this mark (since we already are end mark).
     * @throws IllegalStateException if {@link Type type} of mark is not 
     *         {@link Type#START_EVENT start} or {@link Type#END_EVENT end}.
     */
    public LogEventMark getEndMark() {
        switch (getType()) {
            case START_EVENT:
                return new LogEventMark(this.id, this.level, this.text, this.time, this.duration, Type.END_EVENT);
            case END_EVENT:
                return this;
            default:
                throw new IllegalStateException("Unexpected type of mark: " + getType());
        }
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof LogEventMark)) {
            return false;
        }

        LogEventMark other = (LogEventMark) otherObject;

        if (getId() != other.getId()) {
            return false;
        }
        if (!getLevel().equals(other.getLevel())) {
            return false;
        }
        boolean textEqual = getText() == null ? other.getText() == null : getText().equals(other.getText());
        if (!textEqual) {
            return false;
        }
        if (getTime() != other.getTime()) {
            return false;
        }
        if (getDuration() != other.getDuration()) {
            return false;
        }
        if (getType() != other.getType()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        hash = 67 * hash + (this.level != null ? this.level.hashCode() : 0);
        hash = 67 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 67 * hash + (int) (this.time ^ (this.time >>> 32));
        hash = 67 * hash + (int) (this.duration ^ (this.duration >>> 32));
        return hash;
    }
}
