package cz.cuni.amis.nb.api.pogamut.base.server;

import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateFailed;
import cz.cuni.amis.pogamut.base.agent.state.level2.IAgentStateStopped;
import cz.cuni.amis.pogamut.base.server.IWorldServer;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.Timer;
import java.util.TimerTask;
import org.openide.util.Exceptions;

/**
 * States are:
 * <ul>
 *  <li>Connecting</li>
 *  <li>Connected</li>
 *  <li>Waiting before another connection attempt</li>
 * </ul>
 *
 * @author ik
 */
// TODO refactore to wrapper so it can be used with Embedded server
public abstract class ReconnectingServerDefinition<T extends IWorldServer> extends ServerDefinition<T> {

    transient FlagListener<IAgentState> flagListener = flagListener = new FlagListener<IAgentState>() {

        @Override
        public void flagChanged(IAgentState changedValue) {
            if (changedValue.isState(IAgentStateStopped.class, IAgentStateFailed.class)) {
                // try to reconnect, maybe some error occured
                serverStopped(getServerFlag().getFlag());
                startServer();
            }
        }
    };

    public ReconnectingServerDefinition() {
        getServerFlag().addListener(new FlagListener<T>() {

            @Override
            public void flagChanged(T changedValue) {
                if (changedValue != null) {
                    changedValue.getState().addListener(flagListener);
                }
            }
        });
    }

    /**
     * Called when the running server stopped. Can be used for removing listeners etc.
     */
    protected void serverStopped(T server) {
        server.getState().removeListener(flagListener);
        setNewServer(null);
    }

    @Override
    public void stopServer() {
        T server = getServerFlag().getFlag();
        if (server != null) {
            // removes the listener that would otherwise restart the connection
            server.getState().removeListener(flagListener);
            server.stop();
        }
    }

    /**
     * Nonblocking implementation.
     */
    @Override
    public void startServer() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                boolean notConnected = true;
                while (notConnected) {
                    try {
                        tryToStartServer();     // connecting state
                        notConnected = false;
                    } catch (Exception ex) {
                        if (getServerFlag().getFlag() != null) {
                            setNewServer(null);
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException ex1) {
                            Exceptions.printStackTrace(ex1);
                        }
                    }
                }
            }
        }, "UTServer-reconnecting-thread").start();
    }

    protected abstract void tryToStartServer() throws PogamutException;
}
