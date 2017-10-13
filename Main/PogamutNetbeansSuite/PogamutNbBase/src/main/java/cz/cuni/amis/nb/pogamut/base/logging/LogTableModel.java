/*
 * LogTableModel.java
 *
 * Created on 21. b�ezen 2007, 9:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.base.logging;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.swing.table.DefaultTableModel;
import org.openide.util.NbBundle;

/**
 * Table model designed to cooperate with LogRecordsSource object through
 * LogRecordListener interface.
 * @author ik, kero
 */
public class LogTableModel extends DefaultTableModel implements LogRecordListener {

    /**
     * Maximum of record shown in the table. Older records are being forgotten.
     */
    int limit = 100;
    /**
     * Flag indicating whether the table model should receive new records or not.
     */
    boolean freezed = false;
    /**
     * DataSources providing log records for this table model.
     */
    Set<LogRecordsSource> dataSources = new HashSet<LogRecordsSource>();

    public class LogLevelObject {

        public String toString() {
            return msg.toString();
        }

        public LogLevelObject(Level level, Object msg) {
            this.level = level;
            this.msg = msg;
        }
        public Level level;
        public Object msg;
    }

    /** Creates a new instance of LogTableModel */
    public LogTableModel() {
        ResourceBundle bundle = NbBundle.getBundle(LogViewerPane.class);
        setColumnIdentifiers(new Object[]{bundle.getString("LBL_Time"), bundle.getString("LBL_Level"), bundle.getString("LBL_Message")});
    }

    /** 
     * Freezes or unfreezes the log window. If the window is freezed then new
     * log records are discarded.
     * @param freezed
     */
    public void setFreeze(boolean freezed) {
        this.freezed = freezed;
    }

    public boolean isFreezed() {
        return freezed;
    }

    /**
     * Return all datasources this model wants to listens to.
     */
    public Set<LogRecordsSource> getDataSources() {
        return dataSources;
    }

    /**
     * No cells are editable.
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Adds new data source.
     */
    public void addDataSource(LogRecordsSource source) {
        dataSources.add(source);
        source.addLogRecordListener(this);
    }

    public void removeDataSource(LogRecordsSource source) {
        dataSources.remove(source);
        source.removeLogRecordListener(this);
    }

    public void removeAllDataSources() {
        Iterator i = null;
        while (!dataSources.isEmpty()) {
            i = dataSources.iterator();
            removeDataSource((LogRecordsSource) i.next());
        }
    }
    /**
     * Time formater displaying milliseconds.
     */
    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("H : mm : s.S");

    /**
     * Translates log record to vector - this is required by default table model since
     * it is based on old Java collections.
     */
    protected Vector logRecToVector(LogRecord r) {
        Vector v = new Vector();
        v.add(new LogLevelObject(r.getLevel(), dateFormat.format(new Date(r.getMillis()))));
        v.add(new LogLevelObject(r.getLevel(), r.getLevel()));
        v.add(new LogLevelObject(r.getLevel(), r.getMessage()));
        return v;
    }

    /**
     * Adds new single log record.
     */
    public void notifyNewLogRecord(LogRecord r) {
        if (!freezed) {
            insertRow(0, logRecToVector(r));

            if (this.getRowCount() > limit && this.getRowCount() > 0) {
                  this.removeRow(this.getRowCount() - 1);
              /* TODO maybe concurrency issue ...
                java.lang.ArrayIndexOutOfBoundsException: 220 >= 220
                at java.util.Vector.removeElementAt(Vector.java:511)
                at javax.swing.table.DefaultTableModel.removeRow(DefaultTableModel.java:446)
                at cz.cuni.pogamut.netbeansplugin.logging.LogTableModel.notifyNewLogRecord(LogTableModel.java:135) 
                 */

            }
        }
    }

    public void setRowLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Return maximal number of rows of this table.
     */
    public int getRowLimit() {
        return this.limit;
    }

    /** Delete all data in table model and populate the model with new data. */
    public void setNewData(Collection<LogRecord> r) {
        dataVector.clear();
        for (LogRecord record : r) {
            dataVector.add(logRecToVector(record));
        }
        fireTableDataChanged();
    }
}
