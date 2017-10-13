
package cz.cuni.amis.nb.pogamut.base.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.Flag;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Reader;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 * Node representing server run from within the IDE. This server can be started/restarted
 * and stopped from the IDE. A window with server log will be also shown.
 * @author ik
 */
public abstract class EmbededServerNode<T extends ServerDefinition> extends ServerNode<T> {

    InputOutput serverIO = null;
    protected Flag<Boolean> serverRunning = new Flag(false);

    public EmbededServerNode() {
    // TODO
        super(null, null, new NodeFactory<IAgent>(){

            @Override
            public Node[] create(IAgent obj) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        });
    }



    /**
     * @return stream with server output
     */
    public abstract Reader getServerConsoleOutput();

    public InputOutput getServerIO() {
        return serverIO;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    new NamedAction("ACT_StartServer") {

            @Override
                        public void action(ActionEvent e) throws PogamutException {
                            startServerInternal();
                        }

                        @Override
                        public boolean isEnabled() {
                            return !serverRunning.getFlag();
                        }
                    },
                    new NamedAction("ACT_RestartServer") {

            @Override
                        public void action(ActionEvent e) throws PogamutException {
                            restartServer();
                        }

                        @Override
                        public boolean isEnabled() {
                            return serverRunning.getFlag();
                        }
                    },
                    new NamedAction("ACT_StopServer") {

            @Override
                        public void action(ActionEvent e) throws PogamutException {
                            stopServerInternal();
                        }

                        @Override
                        public boolean isEnabled() {
                            return serverRunning.getFlag();
                        }
                    },
                    null,
                    new NamedAction("ACT_ShowServerOutput") {

                        @Override
                        protected void action(ActionEvent e) throws PogamutException {
                            getServerIO().select();
                        }

                        @Override
                        public boolean isEnabled() {
                            return getServerIO() != null;
                        }
                    }
                };
    }

    protected void restartServer() throws PogamutException {
        stopServerInternal();
        startServerInternal();
    }

    /**
     * Code for starting the embeded server.
     */
    protected abstract void startServer() throws PogamutException;

    /**
     * Code for stoping the embeded server.
     */
    protected abstract void stopServer() throws PogamutException;

    protected void startServerInternal() throws PogamutException {
        startServer();
        createServerOutput();
        getServerIO().select();
        serverRunning.setFlag(true);
    }

    protected void stopServerInternal() throws PogamutException {
        stopServer();
        serverRunning.setFlag(false);
    }

    protected void createServerOutput() {
        // create the window
        if (serverIO == null) {
            serverIO = IOProvider.getDefault().getIO("Server " + getDisplayName() + " console", true);
        }

        // start thread that will write to the window
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    int ch;
                    while ((ch = getServerConsoleOutput().read()) != -1) {
                        serverIO.getOut().write(ch);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }).start();
    }
}
