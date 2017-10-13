package cz.cuni.amis.nb.api.pogamut.udk.server;

import cz.cuni.amis.nb.api.pogamut.unreal.server.UnrealServerDefinition;
import cz.cuni.amis.nb.pogamut.base.server.ServerNode;
import cz.cuni.amis.nb.pogamut.udk.server.UDKServerNode;
/*
 import cz.cuni.amis.pogamut.udk.factory.direct.remoteagent.UDKServerFactory;
import cz.cuni.amis.pogamut.udk.server.IUDKServer;
import cz.cuni.amis.pogamut.udk.server.impl.UDKServer;
import cz.cuni.amis.pogamut.udk.utils.UDKServerRunner;
import cz.cuni.amis.pogamut.udk.utils.UDKWrapper;
*/
import java.io.IOException;
import java.net.URI;

/**
 *
 * @author ik
 */
public class UDKServerDefinition {/* TODO extends UnrealServerDefinition<IUDKServer> {

    @Override
    public ServerNode getViewer() {
        return new UDKServerNode(this);
    }

    @Override
    protected IUDKServer createServer() {
        UDKServerFactory factory = new UDKServerFactory();
        UDKServerRunner serverRunner = new UDKServerRunner(factory, "NBUDKServer", getUri().getHost(), getUri().getPort());
        return (UDKServer) serverRunner.startAgent();
    }

    @Override
    protected void startSpectImpl(URI uri) throws IOException {
    UDKWrapper.launchSpectate(uri);
    }

*/
}
