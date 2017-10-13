package cz.cuni.amis.introspection;

/**
 * Used for caching state and changes on a folder.
 * @author ik
 */
public class FolderShadow extends Folder {

    Folder folder = null;
    FolderShadow[] folders = null;
    PropertyShadow[] properties = null;

    public FolderShadow(Folder folder) throws IntrospectionException {
        super(folder.getName());
        this.folder = folder;
        // make folder shadows
        int n = folder.getFolders().length;
        folders = new FolderShadow[n];
        Folder[] oldFolders = folder.getFolders();
        for (int i = 0; i < n; i++) {
            folders[i] = new FolderShadow(oldFolders[i]);
        }

        // make props shadows
        n = folder.getProperties().length;
        properties = new PropertyShadow[n];
        Property[] oldProps = folder.getProperties();
        for (int i = 0; i < n; i++) {
            properties[i] = new PropertyShadow(oldProps[i]);
        }
    }

    /**
     * Synchronizes state of this shadow folder with the real folder.
     */
    public void synchronize() throws IntrospectionException {
        // synchronize properties
        for (PropertyShadow prop : properties) {
            prop.synchronize();
        }
        // synchronize subfolders
        for (FolderShadow fol : folders) {
            fol.synchronize();
        }
    }

    @Override
    public Folder[] getFolders() throws IntrospectionException {
        return folders;
    }

    @Override
    public Property[] getProperties() throws IntrospectionException {
        return properties;
    }
}
