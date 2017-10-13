package cz.cuni.amis.nb.api.pogamut.unreal.server;

import cz.cuni.amis.nb.api.pogamut.base.server.DefaultServersManager;
import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author ik
 */
public class UnrealServersManager extends DefaultServersManager<UnrealServerDefinition> {

    public UnrealServersManager(String serverType) {
        super(serverType);
    }

    /**
     * Deserialize method is here and not in DefaultServersManager because it needs to have
     * access to the classloader of PogamutNbUnreal plugin where the class definitions reside.
     */
    @Override
    public void deserialize() {
        try {
            FileObject serversFile = getServerListFile();
            if (serversFile.getSize() > 0) {
                ObjectInputStream ois = new ObjectInputStream(serversFile.getInputStream());
                List<UnrealServerDefinition> loadedServers = (List<UnrealServerDefinition>) ois.readObject();
                for (ServerDefinition def : loadedServers) {
                    def.init();
                }
                servers.clear();
                servers.addAll(loadedServers);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
