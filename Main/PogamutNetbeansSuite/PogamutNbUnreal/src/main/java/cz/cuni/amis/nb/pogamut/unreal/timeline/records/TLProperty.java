package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class stores value of some named property along with timestamps and allows
 * easy retrieval.
 * 
 * @author Honza
 */
public class TLProperty<T> implements Serializable {

    public static class Record<T> implements Serializable {

        public Record(T value, long timestamp) {
            this.timestamp = timestamp;
            this.value = value;
        }
        long timestamp;
        T value;
    }
    private String name;
    private Class type = null;
    private List<Record> values = Collections.synchronizedList(new LinkedList<Record>());
    private long lastTimestamp = 0;

    /**
     * Create a new named proprty that will store values of type propertyType.
     */
    public TLProperty(String name, Class propertyType) {
        this.name = name;
        this.type = propertyType;
    }

    public synchronized Object getValue() {
        Record tail = getLast();
        if (tail == null) {
            return null;
        }

        return tail.value;
    }

    /**
     * Get latest value of property that is stored before time.
     * @param time
     * @return
     */
    public synchronized T getValue(long time) {
        T last = null;

        for (Record<T> record : values) {
            if (record.timestamp > time) {
                break;
            }
            last = record.value;
        }
        return last;
    }

    public void setValue(Object arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Class getType() {
        return type;
    }

    public synchronized void addValue(Object value, long timestamp) {
        // if this is first value inserted
        if (values.size() == 0) {
            values.add(new Record(value, timestamp));
            lastTimestamp = timestamp;
            return;
        }

        if (getLastTS() >= timestamp) {
            throw new UnsupportedOperationException("Value can be added only to the end.");
        }

        // get last record
        Record tail = values.get(values.size() - 1);
        Record newRecord = new Record(value, timestamp);

        // if tail and newRecord are equal
        if (tail.value == null ? newRecord.value == null : tail.value.equals(value)) {
            lastTimestamp = timestamp;
        } else {
            values.add(newRecord);
        }

    }


    private long getLastTS() {
        return lastTimestamp;
    }

    public List<Record> getValues() {
        return Collections.unmodifiableList(values);
    }

    protected synchronized Record getLast() {
        if (values.size() == 0) {
            return null;
        }

        return values.get(values.size() - 1);
    }

    /**
     * Get name of property. 
     * @return non-null string
     */
    public String getName() {
        return name;
    }

    public void printDebug() {
        System.out.println("Variable " + this.getName());

        Calendar calendar = Calendar.getInstance();
        List<Record> lst = new LinkedList<Record>(values);
        for (Record record : lst) {
            calendar.setTimeInMillis(record.timestamp);
            System.out.println(" * " + calendar.getTime() + " " + record.value);
        }

    }
}
