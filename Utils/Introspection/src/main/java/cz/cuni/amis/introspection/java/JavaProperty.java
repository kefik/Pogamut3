/*
 * JavaProperty.java
 *
 * Created on April 24, 2007, 3:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection.java;

import cz.cuni.amis.introspection.*;

import java.lang.reflect.Field;

/**
 * Wrapps JavaReflection way of manipulating with fields of objects.
 * @author ik
 */
public class JavaProperty extends Property {

    /**
     * Field to be changed.
     */
    protected Field field = null;
    /**
     * Object where is the field.
     */
    protected Object object = null;

    /**
     * Creates a new instance of JavaProperty
     * @param field Field that is represented by this property
     * @param object Object at which the new value will be set.
     */
    public JavaProperty(Object object, Field field) {
        super(field.getName());
        this.field = field;
        this.object = object;
    }

    @Override
    public Object getValue() throws IntrospectionException {
        try {
            return field.get(object);
        } catch (Exception ex) {
            throw new IntrospectionException(ex);
        }
    }

    @Override
    public void setValue(Object newValue) throws IntrospectionException {
        try {
            field.set(object, newValue);
        } catch (Exception ex) {
            throw new IntrospectionException(ex);
        }
    }

    @Override
    public Class getType() {
        return field.getType();
    }
}
