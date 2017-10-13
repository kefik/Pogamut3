/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base.utils.logging.marks.LogMapMark;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import java.io.Serializable;
import java.util.logging.LogRecord;
import java.util.zip.DataFormatException;

/**
 *
 * @author Honza
 */
public class MapEvent implements Serializable {
    private TLEntity entity;
    private LogRecord record;
    private LogMapMark mapMark;

    private LogRecord closingRecord = null;

    MapEvent(TLEntity entity, LogRecord record) throws DataFormatException {
        this.record = record;
        this.entity = entity;

        for (Object parameter : record.getParameters()) {
            if (parameter instanceof LogMapMark) {
                this.mapMark = (LogMapMark) parameter;
                return;
            }
        }
        throw new DataFormatException("LogRecord " + record.toString() + " doesn't have a log map event as parameter.");
    }

    MapEvent(TLEntity entity, LogRecord record, LogMapMark mark) {
        this.entity = entity;
        this.record = record;
        this.mapMark = mark;
    }

    public String getMessage() {
        return mapMark.getMessage();
    }

    public LogMapMark getMark() {
        return mapMark;
    }

    public long getStartTS(){
        return record.getMillis();
    }
    
    public long getDuration() {
        if (mapMark.getType() == LogMapMark.Type.START_EVENT) {
            if (closingRecord == null) {
                return Integer.MAX_VALUE;
            } else {
                return closingRecord.getMillis() - record.getMillis();
            }
        }

        return mapMark.getDuration();
    }

    void recievedClosingRecord(LogRecord endingRecord) {
        this.closingRecord = endingRecord;
    }

    public boolean timeframeContains(long time) {
        if (time < getStartTS())
            return false;

        if (time > getStartTS() + getDuration())
            return false;

        return true;
    }

    /**
     * Should this map mark follow the player?
     * @return
     */
    public boolean shouldFollowPlayer() {
        return mapMark.getLocation() == null;
    }

    /**
     * Get location of MapEvent at specified time, wheather it follows player or 
     * is placed at some position.
     * @param time time for which we want position of MapEvent
     * @return copy of computed location of map mark
     */
    public Location getLocation(long time) {
        return new Location(mapMark.getLocation(entity.getLocation(time)));
    }

}
