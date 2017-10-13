/*
 * JavaReflectionProxy.java
 *
 * Created on 14. duben 2007, 11:48
 *
 */
package cz.cuni.amis.introspection.java;

import cz.cuni.amis.introspection.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of automatic object introspection using Java Reflection API.
 * Primitive public fields of object (see javadoc fo list of primitive types)
 * marked by PogProp annotation are returned as Properties,
 * unknown more complicated types implementing <code>Introspectable</code> interface
 * are returned as Children for further introspection.
 *
 *TODO annotations arent working ... fix it
 * <p>
 * All returned properties are for now uneditable. Just for the sake of simplicity.
 * <p>
 * <b> THIS EXAMPLE IMPLEMENTATION DOESN'T DEAL WITH INHERITANCE. </b>
 *
 * @author ik
 */
public class ReflectionObjectFolder extends AbstractObjectFolder<Object> {

    /**
     * Creates a new instance of JavaReflectionProxy
     */
    public ReflectionObjectFolder(String name, Object object) {
        super(name, object);
    }


    /**
     * Get all Properties from given class.
     */
    protected Collection<cz.cuni.amis.introspection.Property> getDeclaredProperties(Class cls, Object object) {
        // we will push all supported properties to a list
        List<Property> list = new ArrayList<Property>();

        Field[] fields = cls.getDeclaredFields();

        for (final Field field : fields) {
            Property prop = Introspector.getProperty(field, object);
            if (prop != null) {
                list.add(prop);
            }
        }
        return list;
    }

    /**
     * Get all IntrospectableProxies from given class.
     */
    protected Collection<Folder> getDeclaredProxies(Class cls, Object object) {
        // we will push all fields that implement Introspectable interface to a list
        List<Folder> list = new ArrayList<Folder>();

        Field[] fields = cls.getDeclaredFields();
        for (final Field field : fields) {
            Folder folder = Introspector.getFolder(field, object);

            if (folder != null) {
                list.add(folder);
            }
        }
        return list;
    }

    /**
     * All data fields which are marked by @PogProp annotation and
     * PropertyEditorManager can find editor for them are recognized as
     * Properties of introspectable proxy and are returned by this method.
     */
    @Override
    protected cz.cuni.amis.introspection.Property[] computeProperties(final Object object) {
        // we will push all supported properties to a list
        List<Property> list = new ArrayList<Property>();
        if (object != null) {
            // search for properties in this class and all the ancestors
            Class objClass = object.getClass();

            // walk down the inheritance and collect all Properties there
            while (objClass != null) {
                list.addAll(getDeclaredProperties(objClass, object));
                objClass = objClass.getSuperclass();
            }
        }
        // convert list to array of requested type
        return list.toArray(new Property[]{});
    }

    /**
     * All data fields of introspected object that implement <code>Introspectable</code> interface are added to the list of children.
     */
    @Override
    protected Folder[] computeFolders(final Object object) {
        // we will push all fields that implement Introspectable interface to a list
        List<Folder> list = new ArrayList<Folder>();
        if (object != null) {
            Class objClass = object.getClass();
            while (objClass != null) {
                list.addAll(getDeclaredProxies(objClass, object));
                objClass = objClass.getSuperclass();
            }
        }
        // convert list to array of requested type
        return list.toArray(new Folder[]{});
    }
}
