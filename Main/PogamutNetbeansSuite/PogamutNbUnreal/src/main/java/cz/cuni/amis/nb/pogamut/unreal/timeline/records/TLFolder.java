/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used for storing introspectable variables during some
 * period of time. Values are stored as pairs of time,value. Values are taken
 * from {@link Folder} when user calls method {@link TLFolder.update(long timestamp)},
 * it doesn't refresh automaticly.
 *
 * TLFolder can contain properties and subfolder. Everything is updated recursively.
 * TLFolder can be created either by mirroring existing Folder or by creating an
 * empty TLFolder and populating it with properties and subfolders.
 *
 * @see TLProperty
 * @see Folder
 * @author Honza
 */
public class TLFolder implements Serializable {
    /**
     * Name of the storage folder
     */
    protected String name;
    /**
     * List of folders that are in this folder
     */
    public LinkedList<TLProperty> properties = new LinkedList<TLProperty>();
    public LinkedList<TLFolder> subfolders = new LinkedList<TLFolder>();

    /**
     * Create a new folder for storing pairs (timestamp, value). Create
     * recurcively subfolders from passed folder (basically mirror) the folder.
     *
     * @param folder folder we want to mirror.
     * @throws IntrospectionException
     */
    public TLFolder(Folder folder) throws IntrospectionException {
        this.name = folder.getName();

        for (Property prop : folder.getProperties()) {
            this.addProperty(new TLProperty(prop.getName(), prop.getType()));
        }

        for (Folder subFolder : folder.getFolders()) {
            this.addFolder(new TLFolder(subFolder));
        }
    }

    /**
     * Create an empty folder with name.
     * @param name
     */
    public TLFolder(String name) {
        this.name = name;
    }

    /**
     * Add subfolder to the folder.
     * @param newSubfolder
     */
    public void addFolder(TLFolder newSubfolder) {
        subfolders.add(newSubfolder);
    }

    /**
     * Add property to the folder
     * @param newProperty
     */
    public void addProperty(TLProperty newProperty) {
        properties.add(newProperty);
    }

    /**
     * Find if we have subfolder with specified name and return it.
     *
     * @param name name of the subfolder we are searching for
     * @return null if no such subfolder found
     */
    public TLFolder findFolder(String name) {
        for (TLFolder subfolder : getSubfolders()) {
            String folderName = subfolder.getName();

            if (folderName == null ? name == null : folderName.equals(name)) {
                return subfolder;
            }
        }
        return null;
    }

    /**
     * Find if we have property with specified name in properties and return it.
     *
     * @param name name of the property we are searching for
     * @return null if no such property found
     */
    public TLProperty findProperty(String name) {
        for (TLProperty property : getProperties()) {
            String propName = property.getName();

            if (propName == null ? name == null : propName.equals(name)) {
                return property;
            }
        }
        return null;
    }

    /**
     * Return name of the folder.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return the properties
     */
    public List<TLProperty> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    /**
     * @return the subfolders
     */
    public List<TLFolder> getSubfolders() {
        return Collections.unmodifiableList(subfolders);
    }
}