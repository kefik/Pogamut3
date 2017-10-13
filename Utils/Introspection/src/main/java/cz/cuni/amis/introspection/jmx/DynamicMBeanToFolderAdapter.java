/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection.jmx;

import cz.cuni.amis.introspection.AbstractObjectFolder;
import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

/**
 * Adapts arbitrary MBean (packed in DynamicProxy object) to Folder object. 
 * Subfolders of this folder are all MBeans registered onthe same server under the same
 * domain whose type is <code>{type of adapted MBean} + "." + {name of adapted MBean}</code>.
 * For example if JMX ObjectName of MBean being adapted is "myDomain:type=a,name=b" 
 * then MBeans with ObjectNames "myDomain:type=a.b,name=c1" and "myDomain:type=a.b,name=c2" 
 * would be both recognized as subfolders of this folder.
 * @author Ik
 */
public class DynamicMBeanToFolderAdapter extends AbstractObjectFolder<DynamicProxy> {
/**
 * Creates new Folder whose name would be folderMBean.getObjectName().getKeyProperty("name").
 * @param folderMBean bean being adapted
 */
    public DynamicMBeanToFolderAdapter(DynamicProxy folderMBean) {
        super(folderMBean.getObjectName().getKeyProperty("name")
                , folderMBean);
    }

    /**
     * Creates new Folder with custom name.
     * @param folderMBean
     * @param name
     */
    public DynamicMBeanToFolderAdapter(DynamicProxy folderMBean, String name) {
        super(name, folderMBean);
    }
    
    @Override
    protected Property[] computeProperties(DynamicProxy object) {
        MBeanInfo nfo = object.getMBeanInfo();
        MBeanAttributeInfo[] attrs = nfo.getAttributes();
        List<Property> props = new ArrayList<Property>();
        for (MBeanAttributeInfo attr : attrs) {
            props.add(new AttributeToPropertyAdapter(attr, object));
        }
        return props.toArray(new Property[0]);
    }

    @Override
    protected Folder[] computeFolders(DynamicProxy object) throws IntrospectionException {
        try {
            ObjectName objName = object.getObjectName();
         
            ObjectName pattern = new ObjectName(objName.getDomain() + 
                    ":type=" + objName.getKeyProperty("type") + "." + objName.getKeyProperty("name") +
                    ",name=*");
            Set<ObjectName> names = object.getMBeanServerConnection().queryNames(pattern, null);
            List<Folder> subfolders = new ArrayList<Folder>();
            for (ObjectName n : names) {
                subfolders.add(new DynamicMBeanToFolderAdapter(
                        new DynamicProxy(
                        n,
                        object.getMBeanServerConnection())));
            }
            return subfolders.toArray(new Folder[0]);
        } catch (Exception ex) {
            throw new IntrospectionException(ex);
        }
    }
}
