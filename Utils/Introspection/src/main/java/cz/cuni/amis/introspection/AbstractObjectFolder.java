/*
 * Folder.java
 *
 * Created on 14. duben 2007, 10:53
 *
 */

package cz.cuni.amis.introspection;

/**
 * This is wrapper class for introspectable objects. You have to give it
 * object to be introspected in the constructor and implement methods
 * computing array of properties and array of children objects
 * (<code>computeProperties()</code> and <code>computeChildren()</code>).
 * This abstract implementation caches computed results and returns them by
 * <code>getProperties()</code> and <code>getChildren()</code> methods.
 * @author ik
 */
public abstract class AbstractObjectFolder<T> extends  Folder {
    
    /**
     * Array of introspectable children of object represented by this proxy computed by <code>computeChildren(object)</code>.
     */
    private Folder[] children = null;
    /**
     * Array of properties of object represented by this proxy computed by <code>computeProperties(object)</code>.
     */
    private Property[] properties = null;
    
    /**
     * Object represented by this proxy.
     */
    private T object = null;
    
    /**
     * Creates a new instance of Folder and bind it with given object.
     * Then name is set to object.getClass().getSimpleName();
     * @param object Object to be introspected.
     */
    public AbstractObjectFolder(String name, T object) {     
        super(name);
        // store the object for possible later use
        this.object = object;
        //this.name = object.getClass().getSimpleName();
    }

    /**
     * Returns lazy initialized list of children.
     * @return list of children
     */
    public Folder[] getFolders()  throws IntrospectionException {
        if(children == null) children = computeFolders(getProxiedObject()); 
        return children;
    }

    /**
     * Returns lazy initialized list of properties.
     * @return list of properties
     */
    public Property[] getProperties()  throws IntrospectionException {
        if(properties == null) properties = computeProperties(getProxiedObject());
        return properties;
    }
      
    /**
     * Computes list of properties of object to be introspected.
     * @param object Object to be introspected.
     * @return Array of properties.
     */
    protected abstract Property[] computeProperties(final T object) throws IntrospectionException;

    /**
     * Computes list of introspectable children of object to be introspected.
     * @param object Object to be introspected.
     * @return Array of introspectable children.
     */
    protected abstract Folder[] computeFolders(final T object)  throws IntrospectionException;
    
    /**
     * Return object proxied by this Folder.
     * @return An object proxied by this Folder.
     */
    protected T getProxiedObject() {
        return object;
    };
    
    //TODO add default implementation for proxz changed event
    
}
