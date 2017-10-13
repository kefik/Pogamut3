package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import java.awt.Color;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;

/**
 * One entity is part of TLDatabase and it contains several TLRecord s
 * that represent various data.
 * 
 * @author Honza
 */
public abstract class TLEntity implements Serializable {

    public enum State {

        INSTANTIATED,
        RECORDING,
        FINISHED
    }
    /**
     * Flag&lt;State&gt; would be nice, but I worry about serialization
     */
    private State entityState = State.RECORDING;
    private Long startTime;
    private Long endTime;
    /**
     * Reference to the database that contains this entity
     */
    protected TLDatabase database;
    /**
     * Name of entity
     */
    private String name;
    /**
     * Color of the entity is assigned
     */
    private Color color = Color.BLACK;
    /**
     * This class is storage for log messages and log events that
     * the entity produces.
     */
    private TreeSet<TLLogRecorder> logRecorders = new TreeSet<TLLogRecorder>();
    protected TLFolder storageFolder;

    // listeners are null, because of serialization
    private transient HashSet<TLEntity.Listener> listeners;

    /**
     * Get all listeners in array
     * @return
     */
    protected TLEntity.Listener[] getListeners() {
        if (listeners == null) {
            listeners = new HashSet<Listener>();
        }
        return listeners.toArray(new TLEntity.Listener[listeners.size()]);
    }

    public void addListener(TLEntity.Listener listener) {
        if (listeners == null) {
            listeners = new HashSet<Listener>();
        }
        listeners.add(listener);
    }
    
    public void removeListener(TLEntity.Listener listener) {
        if (listeners == null) {
            listeners = new HashSet<Listener>();
        }
        listeners.remove(listener);
    }

    public static class Adapter implements TLEntity.Listener {

        @Override
        public void endTimeChanged(TLEntity entity, long previousEndTime, long endTime) {
        }

        @Override
        public void logRecorderAdded(TLEntity entity, TLLogRecorder recorder) {
        }
    }
    public interface Listener {
        void endTimeChanged(TLEntity entity, long previousEndTime, long endTime);
        void logRecorderAdded(TLEntity entity, TLLogRecorder recorder);
    }

    /**
     * Create a new timeline entity belonging to db first seen at timestamp
     * @param database db this entity belongs to
     * @param timestamp Starting timestamp of entity
     */
    protected TLEntity(TLDatabase database, long timestamp) {
        this.database = database;

        startTime = timestamp;
        endTime = timestamp;

    }

    public long getStartTime() {
        assert startTime != null;
        return startTime;
    }

    public long getEndTime() {
        assert endTime != null;
        return endTime;
    }

    /**
     * Set new timestamp indicating when was last seen some info from the entity.
     *
     * @param newEnd
     */
    protected void setEndTime(long newEnd) {
        if (entityState != State.RECORDING) {
            return;
        }

        if (newEnd > endTime) {
            long previousEndTime = endTime;
            endTime = newEnd;

            for (TLEntity.Listener listener : getListeners()) {
                listener.endTimeChanged(this, previousEndTime, endTime);
            }
        }
    }

    /**
     * Finsih this entity, after calling this method, new data are not expected to appear.
     * Should be used if user stops the recording or if underlying entity stops working.
     */
    public void finish() {
        if (entityState != State.FINISHED) {
            entityState = State.FINISHED;
            for (TLLogRecorder logRecorder : this.logRecorders) {
                logRecorder.stopRecording(getEndTime());
            }
            this.database.emitEntityLeft(this);
        }
    }
    
    public TLDatabase getDatabase() {
        return database;
    }

    public abstract String getDisplayName();

    /**
     * Adds TLLogRecorder = this TLEntity will store all log messages that will
     * come from the log. So be very carefull if you have only little memory.
     */
    public synchronized void addLogRecording(Logger logger) {
        if (entityState != State.RECORDING) {
            return;
        }

        TLLogRecorder logRecorder = new TLLogRecorder(logger, this);

        logRecorders.add(logRecorder);
    }

    /**
     * Get unmodifiable set of all log recorders this entity has.
     * Used during widget initialization
     * @return
     */
    public Set<TLLogRecorder> getLogRecorders() {
        return Collections.unmodifiableSet(logRecorders);
    }

    public State getState() {
        return entityState;
    }

    /**
     * Get property with name <code>propName</code> in folder <code>folder</code>.
     *
     * @param folder folder we are looking in.
     * @param propName Name of property
     * @return
     * @throws java.lang.NoSuchFieldException property is not in the folder
     * @throws java.util.zip.DataFormatException If property is not double of value is not specified
     */
    protected Double getDoublePropertyValue(TLFolder folder, String propName, long time) throws NoSuchFieldException, DataFormatException {
        TLProperty prop = folder.findProperty(propName);
        if (prop == null) {
            throw new NoSuchFieldException("Property " + propName + " not found in folder " + folder.getName() + " in entity " + getDisplayName() + ", type " + this.getClass().getSimpleName());
        }

        Object value = prop.getValue(time);
        if (value == null) {
            throw new DataFormatException("No value stored in property " + propName + ", folder " + folder.getName() + " in entity " + getDisplayName() + ", type " + this.getClass().getSimpleName());
        }

        if (prop.getType() != double.class && prop.getType() != Double.class) {
            throw new DataFormatException("Property " + propName + " is not double class in folder " + folder.getName() + ", but " + value.getClass() + " in entity " + getDisplayName() + ", type " + this.getClass().getSimpleName());
        }

        return ((Double) value);
    }

    protected void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract Location getLocation(long time);

    public abstract Rotation getRotation(long time);

    public abstract Velocity getVelocity(long time);

    public TLFolder getFolder() {
        return this.storageFolder;
    }

    public void printInfo(PrintStream stream) {
        stream.println("Entity " + getDisplayName() + " (LR: " + getLogRecorders().size() + ")");
        for (TLLogRecorder logRec : getLogRecorders()) {
            logRec.printInfo(stream);
        }

        stream.println();
    }
}
