/*
 * Property.java
 *
 * Created on April 24, 2007, 2:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection;

/**
 * Interface for property models. Porperty model can get and set values directly from (and to) running instance of code. 
 * @author student
 */
public abstract class Property extends Feature {
    
    
    public Property(String name) {
        super(name);
    }

    /**
     * Get value of property.
     * 
     * @return Value of property
     * @throws java.lang.IllegalAccessException 
     */
    public abstract Object getValue() throws IntrospectionException;

    /**
     * Set value of property.
     * @param newValue 
     * @throws java.lang.IllegalAccessException 
     */
    public abstract void setValue(Object newValue) throws IntrospectionException;

    /**
     * 
     * @return Type of this property.
     */
    public abstract Class getType() throws IntrospectionException;
}
