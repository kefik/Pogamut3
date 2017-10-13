package cz.cuni.amis.nb.pogamut.base.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import cz.cuni.amis.nb.api.pogamut.base.server.ServersManager;
import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.pogamut.base.server.IWorldServer;
import cz.cuni.amis.utils.exception.PogamutException;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.Node;

/**
 * Root node for servers of same type. Provides action for adding new servers.
 * @author ik
 */
public abstract class ServersRootNode<T extends ServerDefinition> extends ObservableCollectionNode<T> {

    //protected static ResourceBundle bundle = NbBundle.getBundle(ServersRootNode.class);
    protected ServersManager<T> manager = null;

    public ServersRootNode(ServersManager<T> manager) {
        super(manager.getAllServers(), new NodeFactory<T>() {

            @Override
            public Node[] create(T obj) {
                return new Node[]{obj.getViewer()};
            }
        });
        this.manager = manager;
    }

    /**
     * Provides an AddServer action.
     * @param context
     * @return
     */
    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    new NamedAction("ACT_AddServer") {

                        @Override
                        public void action(ActionEvent e) throws PogamutException {
                            T server = createNewServer();
                            manager.getAllServers().add(server);
                            manager.getDefaultServer().setFlag(server);
                        }
                    },
                    null,
                    new NamedAction("ACT_RemoveAll") {

                        @Override
                        protected void action(ActionEvent e) throws PogamutException {
                            List<ServerDefinition<IWorldServer>> servers = new LinkedList<ServerDefinition<IWorldServer>>((Collection<? extends ServerDefinition<IWorldServer>>) manager.getAllServers());
                            manager.getAllServers().clear();
                            for (ServerDefinition<IWorldServer> def : servers) {
                                IWorldServer server = def.getServerFlag().getFlag();
                                if (server != null) {
                                    server.stop();
                                }
                            }
                        }

                        ;
                    }};
    }

    /**
     * Code for creating new server goes here. the server instance can be result
     * of interaction with the user (eg. some wizard).
     * @return
     */
    protected abstract T createNewServer();
}
