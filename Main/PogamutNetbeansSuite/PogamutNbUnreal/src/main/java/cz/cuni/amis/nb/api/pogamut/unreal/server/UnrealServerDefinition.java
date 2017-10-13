package cz.cuni.amis.nb.api.pogamut.unreal.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ReconnectingServerDefinition;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStopped;
import cz.cuni.amis.pogamut.base.communication.command.ICommandListener;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.pogamut.unreal.communication.messages.gbcommands.ChangeMap;
import cz.cuni.amis.utils.exception.PogamutException;
import java.io.IOException;
import java.net.URI;

/**
 * Definition of UnrealTournament2004 server.
 * @author ik
 */
public abstract class UnrealServerDefinition<T extends IUnrealServer> extends ReconnectingServerDefinition<T> {

    long mapChangeTimeout = 0;
    static final long MAP_CHANGE_TIMEOUT = 60000;
    transient ICommandListener<ChangeMap> mapChangeListener = null;
    protected transient T server = null;

    @Override
    protected void tryToStartServer() throws PogamutException {
        // kill old server
        if (server != null && !(server.getState().getFlag().isState(IAgentStateStopped.class, IAgentStateFailed.class))) {
            server.stop();
        }
        server = createServer();
        setNewServer(server); // notify listeners
    }

    protected abstract T createServer();

    @Override
    protected void serverStopped(T server) {
        super.serverStopped(server);
        // TODO what to do with types?
        server.getAct().removeCommandListener(ChangeMap.class, mapChangeListener);
    }

    /**
     * Starts Unreal viewer of the server.
     */
    public void spectate() throws PogamutException {
        URI uri = getUriFlag().getFlag();
        if(uri == null) throw new PogamutException("Could not start viewer because the server URI isn't set.", this);
        try {
            startSpectImpl(uri);
        } catch (IOException ex) {
            throw new PogamutException("Viewer start failed.", ex);
        }
    }

    protected abstract void startSpectImpl(URI uri) throws IOException;
}
