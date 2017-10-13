/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.logging.LogRecord;

/**
 * Because LogRecord is not serializing parameters, I have to pass the log record in the 
 * container with parameters beside anbd reassemble at the destination.
 * 
 * Only serializable parameters are stored.
 *
 * Passing from JMXLogRecordNotification to LogCategoryJMXProxy
 * @author Honza
 */
public class JMXLogRecordContainer implements Serializable {
    private LogRecord record;
    private LinkedList parameters = new LinkedList();;
    
    public JMXLogRecordContainer(LogRecord record) {
        this.record = record;
        
        Object[] allParameters = record.getParameters();
        if(allParameters != null){
            for (Object parameter : allParameters) {
                if (Serializable.class.isAssignableFrom(parameter.getClass())) {
                    parameters.add(parameter);
                }
            }
        }
    }

    public void printInfo() {
        System.out.println("JMXContainer record: " + record + ", " + parameters.size() + " parameters");
        for (Object o : parameters) {
            System.out.println( " * " + o);
        }
    }

    public LogRecord getRecordWithParameters() {
        record.setParameters(parameters.toArray());
        return record;
    }
}
