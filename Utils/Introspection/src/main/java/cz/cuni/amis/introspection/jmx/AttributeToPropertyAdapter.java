package cz.cuni.amis.introspection.jmx;

import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import javax.management.Attribute;
import javax.management.DynamicMBean;
import javax.management.MBeanAttributeInfo;

/**
 * Adapts a JMX Attribute to AMIS introspection Property.
 * @author Ik
 */
public class AttributeToPropertyAdapter extends Property {

    Class cls = null;
    DynamicMBean folderMBean = null;
    MBeanAttributeInfo info = null;

    public AttributeToPropertyAdapter(MBeanAttributeInfo info, DynamicMBean folderMBean) {
        super(info.getName());
        this.folderMBean = folderMBean;
        this.info = info;

        // find the attr info
        /*for (MBeanAttributeInfo nfo : folderMBean.getAttributeInfos()) {
        if (info.getName().equals(attributeName)) {
        this.info = nfo;
        break;
        }
        }
         */
    }

    @Override
    public Object getValue() throws IntrospectionException {
        try {
            return folderMBean.getAttribute(getName());
        } catch (Exception ex) {
            throw new IntrospectionException(ex);
        }
    }

    @Override
    public void setValue(Object newValue) throws IntrospectionException {
        try {
            folderMBean.setAttribute(new Attribute(getName(), newValue));
        } catch (Exception ex) {
            throw new IntrospectionException(ex);
        }
    }

    @Override
    public Class getType() throws IntrospectionException {
        try {
            if (cls == null) {
                cls = PrimitiveTypeToClassTranslator.get(info.getType());
                if (cls == null) {
                    cls = getClass().getClassLoader().loadClass(info.getType());
                }
            }
            return cls;
        } catch (Exception ex) {
            throw new IntrospectionException(ex);
        }
    }
}
