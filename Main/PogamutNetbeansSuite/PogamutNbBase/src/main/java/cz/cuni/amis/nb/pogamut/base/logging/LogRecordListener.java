/*
 * LogRecordListener.java
 *
 * Created on 21. brezen 2007, 9:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.base.logging;

import java.util.Collection;
import java.util.logging.LogRecord;

/**
 * Listener for changes in LogRecordSource object. There are two types of change:
 * <ol>
 *    <li>New record arrives - call to <code>notifyNewLogRecord(LogRecord r)</code></li>
 *    <li>Filter of records source has changed - <code>setNewData(Collection r)</code></li>
 * </ol>
 * @author ik
 * @see LogRecordSource
 */
public interface LogRecordListener {
    /**
     * New log record has arrived.
     * @param r new record.
     */
    public void notifyNewLogRecord(LogRecord r);

    /**
     * Source has completely changed. This event is raised due to change affecting also
     * older records.
     * @param r Collection of new records. 
     */
    public void setNewData(Collection<LogRecord> r);

}
