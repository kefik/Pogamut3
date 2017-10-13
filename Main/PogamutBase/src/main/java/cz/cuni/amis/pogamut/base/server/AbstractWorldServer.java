package cz.cuni.amis.pogamut.base.server;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractGhostAgent;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnection;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

/**
 * @author ik
 */
public abstract class AbstractWorldServer<WORLD_VIEW extends IWorldView, ACT extends IAct, A extends IAgent> extends AbstractGhostAgent<WORLD_VIEW, ACT> implements IWorldServer<A> {

    @Inject
    public AbstractWorldServer(IAgentId agentId, IAgentLogger agentLogger, IComponentBus bus, WORLD_VIEW worldView, ACT act) {
        super(agentId, bus, agentLogger, worldView, act);
    }
    
    protected URI worldAddress = null;

    @Override
    public URI getWorldAddress() {
    	if (worldAddress == null) {
    		SocketConnection conn = getEventBus().getComponent(SocketConnection.class);
    		if (conn != null) {
    			try {
					worldAddress = new URI("ut://" + conn.getAddress().getHost() + ":" + conn.getAddress().getPort());
				} catch (URISyntaxException e) {
					throw new RuntimeException("Failed to construct WorldAddress.");
				}
    		}
    	}
        return worldAddress;
    }
    
}
