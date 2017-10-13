/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base.utils.logging.marks.LogMapMark;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Class for storing MapEvents of LogRecorder.
 *
 * @author Honza
 */
public class MapEvents implements Serializable {
    private TLEntity entity;
    /**
     * List of all events
     */
    private LinkedList<MapEvent> allEvents = new LinkedList<MapEvent>();

    /**
     * Events that have undetermined duration are stored here,
     * until their counterparts are recieved.
     */
    private LinkedList<MapEvent> unfinishedEvents = new LinkedList<MapEvent>();

    public MapEvents(TLEntity entity) {
        this.entity = entity;
    }

    public List<MapEvent> getEvents(long time) {
        LinkedList<MapEvent> timeEvents = new LinkedList<MapEvent>();

        for (MapEvent event : allEvents) {
            if (event.timeframeContains(time)) {
                timeEvents.add(event);
            }
        }
        return timeEvents;
    }

    /**
     * Does passed record have LogMarkDuration as one of its parameters
     * @param record
     * @return null if record doesn't have the mark, else first LogMarkDuration found
     */
    private LogMapMark hasMark(LogRecord record) {
        for (Object o : record.getParameters()) {
            if (o instanceof LogMapMark) {
                return (LogMapMark) o;
            }
        }
        return null;
    }

    /**
     *
     * @param record
     */
    void update(LogRecord record) {
        LogMapMark mark = hasMark(record);
        if (mark == null)
            return;

        System.out.println("ME: Got new mark");

        if (mark.getType() == LogMapMark.Type.FIXED_DURATION) {
            MapEvent newMapEvent = new MapEvent(entity, record, mark);
            allEvents.add(newMapEvent);
            return;
        }

        if (mark.getType() == LogMapMark.Type.END_EVENT) {
            MapEvent unfinished = getUnfinishedEventWithMark(mark);

            if (unfinished == null) {
                if (Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).isLoggable(Level.SEVERE)) Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe("Got end of map event, but no such event started. Possibly duplicated end.");
                return;
            }

            finishEvent(unfinished, record);
            return;
        }

        if (mark.getType() == LogMapMark.Type.START_EVENT) {
            MapEvent newMapEvent = new MapEvent(entity, record, mark);
            
            unfinishedEvents.add(newMapEvent);
            allEvents.add(newMapEvent);
            return;
        }

        throw new RuntimeException("I should never got here. Type of event: " + mark.getType());
    }

    /**
     * Get event in unfinishedEvents with mark equal to passed mark.
     * @param mark
     * @return null if no such event exists in unfinishedEvents, else first such event
     */
    private MapEvent getUnfinishedEventWithMark(LogMapMark mark) {
        for (MapEvent mapEvent : unfinishedEvents) {
            if (mark.getId() == mapEvent.getMark().getId()) {
                return mapEvent;
            }
        }
        return null;
    }

    /**
     * Finish the unfinish event. Basically update duration that was unknown(eternal) at the start of event.
     * @param unfinished
     * @param record
     */
    private void finishEvent(MapEvent unfinished, LogRecord record) {
        unfinished.recievedClosingRecord(record);
        unfinishedEvents.remove(unfinished);
    }


    public List<MapEvent> getMapEvents() {
        return Collections.unmodifiableList(allEvents);
    }
}
