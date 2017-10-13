/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection.jmx;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * Adapter exposing the Folder as a DynamicMBean.
 * @author Ik
 */
public class FolderMBean implements DynamicMBean {

    protected Folder folder = null;
    //protected String path = null;
    public FolderMBean(Folder folder/*, String path*/) {
        this.folder = folder;
    //this.path = path;

    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        try {
            return folder.getProperty(attribute).getValue();
        } catch (IntrospectionException ex) {
            throw new MBeanException(ex);
        }
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        try {
            folder.getProperty(attribute.getName()).setValue(attribute.getValue());
        } catch (IntrospectionException ex) {
            throw new MBeanException(ex);
        }
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributeList = new AttributeList();
        for (String atr : attributes) {
            try {
                attributeList.add(new Attribute(atr, folder.getProperty(atr).getValue()));
            } catch (IntrospectionException ex) {
                Logger.getLogger(FolderMBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return attributeList;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        List<String> keys = new ArrayList<String>();
        for (Object atr : attributes) {
            try {
                Attribute a = (Attribute) atr;
                keys.add(a.getName());
                setAttribute(a);
            } catch (Exception ex) {
                Logger.getLogger(FolderMBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return getAttributes(keys.toArray(new String[0]));
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        // there are no methods to be invoked
        return null;
    }
    MBeanInfo mBeanInfo = null;

    @Override
    public MBeanInfo getMBeanInfo() {
        if (mBeanInfo == null) {
            mBeanInfo = new MBeanInfo("FolderMBean",
                    "MBean for introspection folder",
                    getAttributeInfos(), null, null, null);

        }
        return mBeanInfo;
    }

    MBeanAttributeInfo[] getAttributeInfos() {
        Property[] props;
        try {
            props = folder.getProperties();
            MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[props.length];
            for (int i = 0; i < props.length; i++) {
                Property p = props[i];
                try {
                    attrs[i] = new MBeanAttributeInfo(p.getName(), p.getType().getName(), null, true, true, false);
                } catch (IntrospectionException ex) {
                    attrs[i] = new MBeanAttributeInfo(p.getName(), "unknown", null, true, true, false);
                }

            }
            return attrs;

        } catch (IntrospectionException ex) {
            Logger.getLogger(FolderMBean.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Export this folder to a JMX MBeanServer.
     * @param mBeanServer MBeanServer where this folder will be registered
     * @param domain domain under which the folder will appear, eg. "MyDomain"
     * @throws javax.management.JMException
     */
    public void registerFolderHierarchyInJMX(MBeanServer mBeanServer, String domain, String path) throws JMException, IntrospectionException {
        // register this folder
        ObjectName objectName = ObjectName.getInstance(domain + ":type=" + path + ",name=" + folder.getName());
        mBeanServer.registerMBean(this, objectName);

        // register all subfolders
        for (Folder f : folder.getFolders()) {
            new FolderMBean(f).registerFolderHierarchyInJMX(mBeanServer, domain, path + "." + folder.getName());
        }
    }

    public static void exportFolderHierarchy(Folder folder, MBeanServer mBeanServer, String domain, String rootName) throws JMException, IntrospectionException {
        new FolderMBean(folder).registerFolderHierarchyInJMX(mBeanServer, domain, rootName);
    }

}
