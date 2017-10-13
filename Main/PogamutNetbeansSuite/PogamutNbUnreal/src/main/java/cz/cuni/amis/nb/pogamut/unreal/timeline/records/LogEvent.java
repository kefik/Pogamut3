/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base.utils.logging.marks.LogEventMark;
import java.io.Serializable;
import java.util.logging.LogRecord;
import java.util.zip.DataFormatException;

/**
 * Class describing log event = sonething that took some time.
 * 
 * Log event is started by logrecord with LogEventMark as one of the parameters
 * and is finished when logrecord is recieved with LogEventMark equal to the one
 * that was used to start the event.
 *
 * @author Honza
 */
public class LogEvent implements Serializable {
    private LogRecord record;
    private LogRecord closingRecord;
    private LogEventMark mark;
    private int slot;

    LogEvent(LogRecord record, int slot) throws DataFormatException {
        this.record = record;
        this.slot = slot;
        
        for (Object parameter : record.getParameters()) {
            if (parameter instanceof LogEventMark) {
                this.mark = (LogEventMark) parameter;
                return;
            }
        }
        throw new DataFormatException("LogRecord " + record.toString() + " doesn't have a log duration mark.");
    }

    LogEvent(LogRecord record, LogEventMark mark, int slot) {
        this.record = record;
        this.mark = mark;
        this.slot = slot;
    }

    public LogEventMark getMark() {
        return mark;
    }

    public String getMessage() {
        return mark.getText();
    }

    public int getSlot() {
        return slot;
    }

    public long getStartTS() {
        return mark.getTime();
    }

    public long getDuration() {
        if (mark.getType() == LogEventMark.Type.START_EVENT) {
            if (closingRecord == null) {
                return mark.getDuration();// Integer.MAX_VALUE
            } else {
                return closingRecord.getMillis() - mark.getTime();
            }
        }
        // else cases = fixed length event or single event... though single shoudn't be here
        return mark.getDuration();
    }

    public long getEndTS() {
        return this.getStartTS() + this.getDuration() - 1;
    }

    void revievedClosingRecord(LogRecord closingRecord) {
        this.closingRecord = closingRecord;
    }
}
