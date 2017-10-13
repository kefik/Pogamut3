package cz.cuni.amis.nb.pogamut.base.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import cz.cuni.amis.nb.util.flag.FlagCollectionChildren;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.server.IWorldServer;
import cz.cuni.amis.utils.collections.ObservableCollection;

/**
 * Handles collection of agents of the server definition.
 * @author ik
 */
public abstract class ServerDefAgents extends FlagCollectionChildren<IWorldServer, IAgent> {

    public ServerDefAgents(ServerDefinition serverDefinition) {
        super(serverDefinition.getServerFlag());
    }

    @Override
    protected ObservableCollection<? extends IAgent> getCollection(IWorldServer val) {
        return val.getAgents();
    }
}
