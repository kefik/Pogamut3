/*
 * FolderUnion.java
 *
 * Created on 27. duben 2007, 15:52
 *
 */

package cz.cuni.amis.introspection;


/**
 * Union of properties and children of two introspectable proxies.
 * @author ik
 */
public class FolderUnion extends AbstractObjectFolder<Folder[]> {
	
	Folder[] folder = null;
    
    /**
     * Creates a new instance of FolderUnion
     */
    public FolderUnion(Folder first, Folder second, String name) {
        super(name, new Folder[] {first, second});
        this.folder = new Folder[] {first, second}; 
    }
    
    protected Property[] computeProperties(final Folder[] object) throws IntrospectionException {
        Property[] arr = new Property[object[0].getProperties().length + object[1].getProperties().length];
        System.arraycopy(object[0].getProperties(),0,arr,0,object[0].getProperties().length);
        System.arraycopy(object[1].getProperties(),0,arr,object[0].getProperties().length,object[1].getProperties().length);
        return arr;
    }
    
    protected Folder[] computeFolders(final Folder[] object) throws IntrospectionException {
        Folder[] arr = new Folder[object[0].getFolders().length + object[1].getFolders().length];
        System.arraycopy(object[0].getFolders(),0,arr,0,object[0].getFolders().length);
        System.arraycopy(object[1].getFolders(),0,arr,object[0].getFolders().length,object[1].getFolders().length);
        return arr;
    }
    
    public String getName() {
        return name;
    }

}
