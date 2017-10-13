/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection.java;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.Property;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ik
 */
public class Introspector {

    public static Property getProperty(Field field, Object object) {
        // access also protected and private fields
        field.setAccessible(true);

        // Properties are only primitive types marked by Prop annotation
        if (field.isAnnotationPresent(JProp.class)) {
            // We require that class of the field to be loaded, because we often specify
            // the PropertyEditor there in static block.
            forceInitialization(field.getType());

            //TODO this condition should be used on client when deciding whether to show an editor for it
            if (PropertyEditorManager.findEditor(field.getType()) != null) {
                // add the property
                return new JavaProperty(object, field);
            }
        }
        return null;
    }
    
    
    /**
     * It turns out that having Class object of some class doesn't mean it is initialized.
     * But we need it, e.g. PropertyObject is registering its property
     * editor in its static block.
     * So something like <code>@JProp MyIntrospectionObject obj = null;</code>
     * woudn't execute static blocks.
     *
     * This method is forcing initialization of class that is potential Introspection property.
     * @param cls
     */
    private static void forceInitialization(Class cls) {
        // first skip primitive types
        if (cls.isPrimitive()) {
            return;
        }
        try {
            Class.forName(cls.getName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Introspector.class.getName()).log(Level.SEVERE,
                    "Unable to load class \"" + cls +
                    "\" (needed for static intializators to run).");
        }
    }

    public static Folder getFolder(Field field, Object object) {
         // access also protected and private fields
        field.setAccessible(true);
        
        Object obj;
            try {
                
                if (field.isAnnotationPresent(JFolder.class)) {
                    obj = field.get(object);
                    
                    if (Introspectable.class.isInstance(obj)) {
                        // get the custom proxy
                        Introspectable intro = (Introspectable) obj;
                        Folder folder = intro.getFolder(field.getName());
                        return folder;
                        
                    } else {
                        // create default proxy
                        return new ReflectionObjectFolder(field.getName(), obj);
                    }

                }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
        return null;
    }
    
    public static Folder getFolder(String name, Object object) {
        
            try {
                  if (Introspectable.class.isInstance(object)) {
                        // get the custom proxy
                        Introspectable intro = (Introspectable) object;
                        return intro.getFolder(name);   
                    } else {
                        // create default proxy
                        return new ReflectionObjectFolder(name, object);
                    }
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        return null;
    }
}
