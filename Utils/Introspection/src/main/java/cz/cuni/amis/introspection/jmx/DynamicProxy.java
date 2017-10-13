
package cz.cuni.amis.introspection.jmx;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * Proxy object of remote DynamicMBean exposing this object through the same interface.
 * @author Ik
 */
public class DynamicProxy implements DynamicMBean {

    /** Name of the wrapped object. */
    protected ObjectName objectName;
    /** Connection to server where the MBean lives. */
    MBeanServerConnection mbsc;

    /**
     * Create proxy for DynamicMBean registered under the objectName on mbsc server.
     * @param objectName the name of proxied object
     * @param mbsc connection to server where the MBean is registered
     */
    public DynamicProxy(ObjectName objectName, MBeanServerConnection mbsc) {
        this.objectName = objectName;
        this.mbsc = mbsc;
    }

    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        try {
            return mbsc.getAttribute(objectName, attribute);
        } catch (Exception ex) {
            throw new MBeanException(ex);
        }
    }

    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        try {
            mbsc.setAttribute(objectName, attribute);
        } catch (Exception ex) {
            throw new MBeanException(ex);
        }
    }

    public AttributeList getAttributes(String[] attributes) {
        try {
            return mbsc.getAttributes(objectName, attributes);
        } catch (Exception ex) {
            Logger.getLogger(DynamicProxy.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public AttributeList setAttributes(AttributeList attributes) {
        try {
            return mbsc.setAttributes(objectName, attributes);
        } catch (Exception ex) {
            Logger.getLogger(DynamicProxy.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        try {
            return mbsc.invoke(objectName, actionName, params, signature);
        } catch (Exception ex) {
            throw new MBeanException(ex);
        }
    }

    public Object invokeNoException(String actionName, Object[] params, String[] signature) {
        try {
            return invoke(actionName, params, signature);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public MBeanInfo getMBeanInfo() {
        try {
            return mbsc.getMBeanInfo(objectName);
        } catch (InstanceNotFoundException ex) {
            Logger.getLogger(DynamicProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IntrospectionException ex) {
            Logger.getLogger(DynamicProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ReflectionException ex) {
            Logger.getLogger(DynamicProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DynamicProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /** 
     * 
     * @return connection to server where the MBean is registered
     */
    public MBeanServerConnection getMBeanServerConnection() {
        return mbsc;
    }

    public ObjectName getObjectName() {
        return objectName;
    }
    
    
}
