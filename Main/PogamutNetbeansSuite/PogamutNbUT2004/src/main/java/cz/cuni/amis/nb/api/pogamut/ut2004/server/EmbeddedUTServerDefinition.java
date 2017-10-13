package cz.cuni.amis.nb.api.pogamut.ut2004.server;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

import cz.cuni.amis.nb.pogamut.base.server.ServerNode;
import cz.cuni.amis.nb.pogamut.ut2004.server.UTEmbededServerNode;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.utils.flag.Flag;

/**
 *
 * @author ik
 */
public class EmbeddedUTServerDefinition extends UTServerDefinition {

    Flag<String> serverHomePath = new Flag<String>();
    Flag<String> serverExec = new Flag<String>();
    transient UCCWrapper uccWrapper = null;
    transient Logger log = null;

    public Flag<String> getServerHomePathFlag() {
        return serverHomePath;
    }

    public Flag<String> getServerExecFlag() {
        return serverExec;
    }

    @Override
    public ServerNode getViewer() {
        return new UTEmbededServerNode(this);
    }

    /**
     * Starts process of UCC server.
     */
    public void startEmbeddedServer() throws UCCStartException {
        if (uccWrapper == null) {
            // set intitalization
            UCCWrapperConf conf = new UCCWrapperConf();

            conf.setLogger(log = Logger.getAnonymousLogger());
            
            // prepare output window
            final InputOutput io = IOProvider.getDefault().getIO("UCC " + getServerName(), false);

            io.select();
            //io.getOut().println(cmdline); //XXX
            //io.getOut().println(); //XXXd

            // redirect log to the output
            log.addHandler(new Handler() {

                @Override
                public void publish(LogRecord record) {
                    io.getOut().println(record.getMessage());
                }

                @Override
                public void flush() {
                    io.getOut().flush();
                }

                @Override
                public void close() throws SecurityException {
                    io.closeInputOutput();
                }
            });

            // start the server
            uccWrapper = new UCCWrapper(conf);
        } else {
            // TODO server already set
        }
    }

    /**
     * Stops UCC server.
     */
    public void stopEmbeddedServer() {
        uccWrapper.stop();
        uccWrapper = null;
    }
}
