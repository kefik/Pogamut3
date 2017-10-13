package cz.cuni.amis.nb.api.pogamut.base.server;

import cz.cuni.amis.pogamut.base.server.IWorldServer;
import java.io.File;

/**
 *
 * @author ik
 */
public abstract class EmbeddedServerDefinition<SERVER extends IWorldServer> extends ServerDefinition<SERVER> {
    File serverHomePath = null;

    public File getServerHomePath() {
        return serverHomePath;
    }

    public void setServerHomePath(File serverHomePath) {
        this.serverHomePath = serverHomePath;
    }





}
