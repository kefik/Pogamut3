/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base.utils.logging.marks.LogEventMark;
import java.io.Serializable;
import java.util.logging.LogRecord;

/**
 * Class that represents one time event that was passed through loggers
 * with mark LogEventMark with type SINGLE_EVENT.
 *
 * @see LogEvent
 * @see LogEvents
 * 
 * @author Honza
 */
public class LogMessage implements Serializable {
    private final LogRecord record;
    private final LogEventMark mark;

    protected LogMessage(LogRecord record, LogEventMark mark) {
        this.record = record;
        this.mark = mark;
    }

    /**
     * I am not using this for now, but it may be useful.
     * @return LogRecord that was used as carrier for the mark (the mark was in the parameters).
     */
    public LogRecord getRecord() {
        return record;
    }

    /**
     * Return timestamp when was message created.
     * @return
     */
    public long getTime() {
        return mark.getTime();
    }

    public String getMessage() {
        return mark.getText();
    }
}
