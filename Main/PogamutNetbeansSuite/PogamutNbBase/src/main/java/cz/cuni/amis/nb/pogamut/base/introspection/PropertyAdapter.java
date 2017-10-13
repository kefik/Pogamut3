package cz.cuni.amis.nb.pogamut.base.introspection;

import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.openide.util.Exceptions;


/**
 * Adapts Pogamut property to Netbeans property.
 * @author ik
 */
public class PropertyAdapter extends PropertySupport.ReadWrite {
    
    /** Property counter used to make unique identifiers. */
    protected static int propertyId = 0;
    
    /** Prefix used to make unique identiiers. */
    protected static final String propertyPrefix = "POGPROP_";
    
    /**
     * Property represented by this adapter.
     */
    protected Property property = null;
    
    /**
     * Unigue identifier of this property.
     */
    protected String propId = null;
    
    /**
     * Creates a new instance of PropertyAdapter, with string property identifier equal to property name.
     * @param propId Unique ID of this property.
     * @param property Pogamut property to be adapted.
     */
    public PropertyAdapter(String propId, Property property) throws IntrospectionException {
        super(  propId,
                property.getType(),
                property.getName(),
                "");
        this.propId = propId;
        this.property = property;
    }
    
    /**
     * 
     * @return Unique ID of this property.
     */
    public String getPropertyID() {
        return propId;
    }
    
    /**
     * 
     * @throws java.lang.IllegalAccessException 
     * @throws java.lang.reflect.InvocationTargetException 
     * @return Value of property.
     */
    @Override
    public Object getValue() throws IllegalAccessException, InvocationTargetException {
        try {
            Object ret = property.getValue();
            return ret;
        } catch (IntrospectionException ex) {
            throw new InvocationTargetException(ex);
        }
    }
    
    /**
     * Sets new value of this property.
     * @param object New value.
     */
    @Override
    public void setValue(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try {
            property.setValue(object);
        } catch (IntrospectionException ex) {
            throw new InvocationTargetException(ex);
        }
    }
    
    /**
     * Automaticaly assign string identifier to the property.
     */ 
    public static class NamedAdapter extends PropertyAdapter {
        public NamedAdapter(Property property) throws IntrospectionException {
            super(propertyPrefix + property.getName() + "_" + propertyId++, property);
        }
    }
}
