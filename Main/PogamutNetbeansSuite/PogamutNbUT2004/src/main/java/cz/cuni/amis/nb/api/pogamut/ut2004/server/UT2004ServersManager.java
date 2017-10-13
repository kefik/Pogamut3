package cz.cuni.amis.nb.api.pogamut.ut2004.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import cz.cuni.amis.nb.api.pogamut.unreal.server.UnrealServerDefinition;
import cz.cuni.amis.nb.api.pogamut.unreal.server.UnrealServersManager;
import cz.cuni.amis.nb.pogamut.ut2004.server.UTServersRootNode;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import org.openide.filesystems.FileObject;

import org.openide.util.Exceptions;

/**
 *
 * @author ik
 */
public class UT2004ServersManager extends UnrealServersManager {

    public UT2004ServersManager() {
        super(UTServersRootNode.UT_SERVERS_ID);
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
