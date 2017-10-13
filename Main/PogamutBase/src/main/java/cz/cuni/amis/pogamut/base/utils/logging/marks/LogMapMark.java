package cz.cuni.amis.pogamut.base.utils.logging.marks;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

import java.io.Serializable;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * DON'T USE THIS CLASS! Use methods in {@link LogCategory} to place mark on the map.
 * <p/>
 * This is an object that is passed through log infrastructure to appear on the other side
 * through JMX and it is utilized there to add mark on the map.
 * <p/>
 * Basically, if you want to use it directly (DON'T), create LogMapMark using some of
 * the create*Event method and pass it to log as parameter of log message.
 * {@code
 *  // create a mark that with text "text to show in map" that will be
 *  // shown at the position of agent for its duration of 2secibds.
 *  logger.info("Text to log" , new Object[]{LogMapMark.createFixedLengthEvent(Level.INFO, "text to show in map", null, 2000)});
 * }
 * @author Honza
 */
public class LogMapMark implements Serializable {

    public enum Type {

        FIXED_DURATION,
        START_EVENT,
        END_EVENT
    }
    /**
     * Generator of unique id. All create* methods are synchronized so you can't
     * generate two marks with same id.
     */
    private static int lastAssignedID = 0;
    /**
     * Custom generated id. Start and end mark of variable elngth map mark have same id.
     */
    private final int id;
    /**
     * What level is this mark? 
     */
    private final Level level;
    /**
     * text shown by this mark
     */
    private final String message;
    /**
     * Location, where is mark placed. If null, follow agent.
     */
    private final Location location;
    /**
     * Timestamp when should mark first appear in the map.
     */
    private final long created;
    /**
     * For how long should map appear in the map.
     */
    private final long duration;
    /**
     * Type of mark. There are basically only two types: with fixed duration and
     * varible length. Mark with variable length starts with START_EVENT and is finished
     * by END_EVENT.
     */
    private Type type;

    /**
     * Create new map mark.
     * @param id unique id of map mark, only start and end mark of same map mark have same id.
     * @param level what importance is this map mark? User can filter out some levels.
     * @param message What is the text that should be displayed on the mark?
     * @param location location, at which map mark should be located. If null, than mark will follow agent.
     * @param created when should map mark first appear in map
     * @param duration for how long should map mark be shown in the map.
     * @param type What type is this mark.
     */
    private LogMapMark(int id, Level level, String message, Location location, long created, long duration, Type type) {
        this.id = id;
        this.level = level;
        this.message = message;
        this.location = location != null ? new Location(location) : null;
        this.created = created;
        this.duration = duration;
        this.type = type;
    }

    /**
     * Create new map mark. For parameters see {@link LogMapMark#LogMapMark(int, java.util.logging.Level, java.lang.String, cz.cuni.amis.pogamut.base3d.worldview.object.Location, long, long, cz.cuni.amis.pogamut.base.utils.logging.marks.LogMapMark.Type)
     */
    private LogMapMark(Level level, String message, Location location, long created, long duration, Type type) {
        this(++lastAssignedID, level, message, location, created, duration, type);
    }

    /**
     * Create mark on the map that will stay there for some time.
     * @param level level of mark, should be same as the level of carrying {@link LogRecord}
     * @param message text of map mark
     * @param location location, at which map mark should be located. If null, than mark will follow agent.
     * @param duration how long should map mark be shown in the map.
     * @return
     */
    public synchronized static LogMapMark createFixedLengthEvent(Level level, String message, Location location, long duration) {
        long time = Calendar.getInstance().getTimeInMillis();
        return new LogMapMark(level, message, location, time, duration, Type.FIXED_DURATION);
    }

    /**
     * Create map mark that appear in the map for certain time and will be always shown
     * at the current position of agent.
     * @param level level of mark, should be same as the level of carrying {@link LogRecord}
     * @param message text of mark 
     * @param duration how long should be mark shown
     * @return created mark.
     */
    public synchronized static LogMapMark createAgentFixedLengthEvent(Level level, String message, long duration) {
        long time = Calendar.getInstance().getTimeInMillis();
        return new LogMapMark(level, message, null, time, duration, Type.FIXED_DURATION);
    }

    /**
     * Create mark that will always be shown at the position of agent.
     * <p/>
     * If you don't want to show mark in the map anymore, you have to create 
     * end mark and pass it to log infrastucture. You can create end mark
     * by calling {@link LogMapMark#getEndMark()} on the starting mark (the one
     * returned by this method).
     * @param level level of mark, should be same as the level of carrying {@link LogRecord}
     * @param message text to be shown
     * @return created mark.
     */
    public synchronized static LogMapMark createAgentVariableLengthEvent(Level level, String message) {
        long time = Calendar.getInstance().getTimeInMillis();
        return new LogMapMark(level, message, null, time, Integer.MAX_VALUE, Type.START_EVENT);
    }

    /**
     * Create starting map mark.
     * <p/>
     * In order to end the map mark, create end mark by calling
     * {@link LogMapMark#getEndMark()} on the starting mark (the one
     * returned by this method) and pass it to same log as starting mark.
     * @param message text of mark
     * @param location location, at which map mark should be located. If null, than mark will follow agent.
     * @return starting map mark
     */
    public synchronized static LogMapMark createVariableLengthEvent(Level level, String message, Location location) {
        long time = Calendar.getInstance().getTimeInMillis();
        return new LogMapMark(level, message, location, time, Integer.MAX_VALUE, Type.START_EVENT);
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }
        if (!(otherObject instanceof LogMapMark)) {
            return false;
        }

        LogMapMark other = (LogMapMark) otherObject;

        if (this.getId() != other.getId()) {
            return false;
        }
        if (getType() != other.getType()) {
            return false;
        }
        boolean msgEqual = this.getMessage() == null ? other.getMessage() == null : this.getMessage().equals(other.getMessage());
        if (!msgEqual) {
            return false;
        }

        boolean locEqual = this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation());
        if (!locEqual) {
            return false;
        }
        if (getCreated() != other.getCreated()) {
            return false;
        }
        if (getDuration() != other.getDuration()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 71 * hash + (this.location != null ? this.location.hashCode() : 0);
        hash = 71 * hash + (int) (this.created ^ (this.created >>> 32));
        hash = 71 * hash + (int) (this.duration ^ (this.duration >>> 32));
        return hash;
    }

    /**
     * Get unique id of this map mark
     * @return id of mark
     */
    public int getId() {
        return id;
    }

    /**
     * Get level of mark, used mainly to send end marks at same level as starting.
     * @return level of the mark.
     */
    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Get location of mark. If location is null, mark is supposed to
     * follow agent.
     * @see LogMapMark#getLocation(cz.cuni.amis.pogamut.base3d.worldview.object.Location) 
     * @return copy of marks location or null.
     */
    public Location getLocation() {
        return location != null ? new Location(location) : null;
    }

    /**
     * Get location, where mark is supposed to be.
     * <p/>
     * If marks location is null, return location of entity
     * @param entityLoc location of agent this map mark belongs to
     * @return copy of location, where mark is supposed to be.
     */
    public Location getLocation(Location entityLoc) {
        Location markLocation = getLocation();
        return markLocation != null ? markLocation : new Location(entityLoc);
    }

    public long getCreated() {
        return created;
    }

    public long getDuration() {
        return duration;
    }

    public Type getType() {
        return type;
    }

    /**
     * Get end mark to make some map mark disappear from map.
     * <p/>
     * This is method that should be used only by map marks without defined 
     * duration (starting map marks).
     * @return end mark to be passed to same log as the starting mark was.
     */
    public LogMapMark getEndMark() {
        if (this.getType() == Type.START_EVENT || this.getType() == Type.END_EVENT) {
            LogMapMark endMark = new LogMapMark(this.id, this.level, this.message, this.location, this.created, this.duration, Type.END_EVENT);
            return endMark;
        } else {
            throw new IllegalStateException("Mark is not in a state that can be changed to END_EVENT, it is in " + getType());
        }
    }
}
