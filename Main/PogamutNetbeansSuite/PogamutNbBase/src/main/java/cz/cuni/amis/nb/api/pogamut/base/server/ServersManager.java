package cz.cuni.amis.nb.api.pogamut.base.server;

import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.flag.Flag;

/**
 * Manager for all instances of servers of the same type.
 * @author ik
 */
public interface ServersManager<T extends ServerDefinition> {
    /**
     * Returns the default server. This server can be used as target for some actions.
     * @return
     */
    Flag<T> getDefaultServer();

    /**
     * Returns collection of all servers.
     * @return
     */
    ObservableCollection<T> getAllServers();

    /**
     * TODO 
     */
    void deserialize();

    void serialize();

    /**
     * String type of the servers. Usually a protocol that they can handle.
     * @return
     */
    String getServerType();

    /**
     * Removes this servel from the list.
     * @param server
     */
    void removeServer(T server);
}