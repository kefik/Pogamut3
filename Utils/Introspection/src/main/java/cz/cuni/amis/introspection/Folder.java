/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interface for introspection of internal properties of object.  
 * @author Ik
 */
public abstract class Folder extends Feature {

    
    public Folder(String name) {
        super(name);
    }
    
    /**
     * @return Array of introspectable children objects.
     */
    public abstract Folder[] getFolders()  throws IntrospectionException;

    /**
     * @return Array of properties of this object.
     */
    public abstract Property[] getProperties()  throws IntrospectionException;

    /**
     * Return property of the given name, null if no such property exists.
     * @param name Name of the 
     * @return
     */
    public Property getProperty(String name) throws IntrospectionException {
        return (Property) getFeature(name, getProperties());
    }

    public Folder getFolder(String name) throws IntrospectionException {
        return (Folder) getFeature(name, getFolders());
    }

    private Feature getFeature(String name, Feature[] features) {
        for (Feature feature : features) {
            if (feature.getName().equals(name)) {
                return feature;
            }
        }
        return null;
    }
    
     /**
     * Stores all folders and properties from this Folder to a Java Properties 
     * object that can be used for storing the values.
     *  
     * @return 
     */
    public Properties createProperties() throws IntrospectionException {
        return createProperties("");
    }

    /**
     * Stores all folders and properties from this Folder to a Java Properties 
     * object that can be used for storing the values.
     * @param prefix prefix should be empty for root folder or end with dot for
     * subfolders 
     * @return 
     */
    protected Properties createProperties(String prefix) throws IntrospectionException {
        Properties props = new Properties();
        // add all properties
        String exPrefix = prefix + getName() + ".";
        for (Property prop : getProperties()) {
            String valueStr;
            try {
                valueStr = String.valueOf(prop.getValue());
            } catch (IntrospectionException ex) {
                Logger.getLogger(Folder.class.getName()).log(Level.SEVERE, null, ex);
                valueStr = "IllegalAccessException - failed to obtain the value";
            }
            props.setProperty(exPrefix + prop.getName(), valueStr);
        }
        // add the subfolders
        for (Folder folder : getFolders()) {
            Properties subprop = folder.createProperties(exPrefix);
            props.putAll(subprop);
        }

        return props;
    }

    /**
     * Initializes folder from the properties object.
     * @param props
     */
    public void loadFromProperties(Properties props) throws IntrospectionException {
        loadFromProperties(props, "");
    }
    
    /**
     * Initializes folder from the properties object.
     * @param props
     */
    protected void loadFromProperties(Properties props, String prefix) throws IntrospectionException {
        Enumeration<String> keys = (Enumeration<String>) props.propertyNames();
        prefix = prefix + getName() + ".";
        // load properties in this folder
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.startsWith(prefix)) {
                String posfix = key.replaceFirst(prefix, "");
                if (!posfix.contains(".")) {
                    // the posfix represents a name of a property
                    Property prop = getProperty(posfix);
                    if (prop == null) {
                    	continue;
                    }
                    // exploit the JavaBeans property editors to convert the values from text
                    PropertyEditor editor = PropertyEditorManager.findEditor(prop.getType());
                    if (editor == null) {
                    	continue;
                    }
                    editor.setAsText(props.getProperty(key));
                    prop.setValue(editor.getValue());
                 
                }
            }
            ;
        }

        // load all subfolders
        for (Folder folder : getFolders()) {
            folder.loadFromProperties(props, prefix);
        }

    }
}
