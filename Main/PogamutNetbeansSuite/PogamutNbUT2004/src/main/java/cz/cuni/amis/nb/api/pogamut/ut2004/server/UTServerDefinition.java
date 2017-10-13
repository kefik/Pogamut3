package cz.cuni.amis.nb.api.pogamut.ut2004.server;

import cz.cuni.amis.nb.api.pogamut.unreal.server.UnrealServerDefinition;
import cz.cuni.amis.nb.pogamut.base.server.ServerNode;
import cz.cuni.amis.nb.pogamut.ut2004.server.UTServerNode;
import cz.cuni.amis.pogamut.ut2004.factory.direct.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004Wrapper;
import cz.cuni.amis.utils.exception.PogamutException;
import java.io.IOException;
import java.net.URI;

/**
 * Definition of UnrealTournament2004 server.
 * @author ik
 */
public class UTServerDefinition extends UnrealServerDefinition<IUT2004Server> {

    @Override
    public ServerNode getViewer() {
        return new UTServerNode(this);
    }

    @Override
    protected IUT2004Server createServer() {
        UT2004ServerFactory factory = new UT2004ServerFactory();
        UT2004ServerRunner serverRunner = new UT2004ServerRunner(factory, "NBUTServer", getUri().getHost(), getUri().getPort());
        return (UT2004Server) serverRunner.startAgent();
    }

    @Override
    protected void startSpectImpl(URI uri) throws IOException {
        UT2004Wrapper.launchSpectate(uri);
    }
}
