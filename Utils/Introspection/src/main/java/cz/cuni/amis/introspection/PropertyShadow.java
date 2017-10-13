package cz.cuni.amis.introspection;

/**
 * Used for caching value of given property.
 * @author ik
 */
public class PropertyShadow extends Property {

    Property prop = null;
    Class type = null;
    Object val = null;
    boolean changed = false;

    public PropertyShadow(Property prop) throws IntrospectionException {
        super(prop.getName());
        this.prop = prop;
        type = prop.getType();

    }

    /**
     * Updates shadowed value with current property value. Sets the new value if
     * if was changed.
     */
    public void synchronize() throws IntrospectionException {
        // set value if changed
        if(changed) {
            prop.setValue(val);
            changed = false;
        }
        val = prop.getValue();
    }

    @Override
    public Object getValue() throws IntrospectionException {
        return val;
    }

    @Override
    public void setValue(Object newValue) throws IntrospectionException {
        val = newValue;
        changed = true;
    }

    @Override
    public Class getType() throws IntrospectionException {
        return type;
    }
}
