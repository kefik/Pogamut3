package cz.cuni.amis.nb.api.pogamut.base.server;

import cz.cuni.amis.nb.pogamut.base.server.ServerNode;
import cz.cuni.amis.pogamut.base.server.IWorldServer;
import cz.cuni.amis.utils.flag.Flag;
import java.io.Serializable;
import java.net.URI;

/**
 * Model object representing arbitrary server.
 * @author ik
 */
public abstract class ServerDefinition<SERVER extends IWorldServer> implements Serializable {

    public ServerDefinition() {
        init();
    }

    String name = "Initialize server name";
    transient Flag<String> nameFlag = null;
    URI uri = null;
    transient Flag<URI> uriFlag = null;
    private transient Flag<SERVER> serverFlag = null;

    public void init() {
        nameFlag = new Flag<String>(name);
        uriFlag = new Flag<URI>(uri);
        serverFlag = new Flag<SERVER>(null);
        if(getUri() != null) startServer();
    }

    public void setServerName(String name) {
        this.name = name;
        nameFlag.setFlag(name);
    }

    public String getServerName() {
        return name;
    }

    public Flag<String> getServerNameFlag() {
        return nameFlag.getImmutable();
    }

    public void setUri(URI uri) {
        this.uri = uri;
        uriFlag.setFlag(uri);
        startServer();
    }

    public URI getUri() {
        return uri;
    }

    public Flag<URI> getUriFlag() {
        return uriFlag.getImmutable();
    }


    /**
     * Change current server instance.
     * @param server
     */
    protected void setNewServer(SERVER server) {
        serverFlag.setFlag(server);
    }

    abstract public ServerNode getViewer();

    public Flag<SERVER> getServerFlag() {
        return serverFlag.getImmutable();
    }

    /**
     * Restars the server eg. after address change.
     */
    abstract public void startServer();

    /**
     * Stops connection to the server.
     */
    abstract public void stopServer();
}
