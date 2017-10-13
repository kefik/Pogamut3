package cz.cuni.amis.nb.pogamut.udk.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import cz.cuni.amis.nb.api.pogamut.base.server.ServerTypesManager;
import cz.cuni.amis.nb.api.pogamut.udk.server.UDKServerDefinition;
import cz.cuni.amis.nb.pogamut.base.server.ServersRootNode;
import cz.cuni.amis.nb.pogamut.unreal.server.UnrealServerNode;
//import cz.cuni.amis.pogamut.udk.server.IUDKServer;
import java.awt.Image;
import java.util.ResourceBundle;
import org.openide.nodes.NodeOperation;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * Root node representing list of connected UT2004 servers.
 * @author ik
 */
public class UDKServersRootNode { /* TODO extends ServersRootNode<ServerDefinition<IUDKServer>> {

    public static final String UDK_SERVERS_ID = "gbudk";
    protected static ResourceBundle bundle = NbBundle.getBundle(UDKServersRootNode.class);

    public UDKServersRootNode() {
        super(ServerTypesManager.getServersManager(UDK_SERVERS_ID));
        setDisplayName(bundle.getString("LBL_UDKServersRootNode"));
        setShortDescription(bundle.getString("HINT_UDKServersRootNode"));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/server/UT2004Servers.gif");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    // TODO uncomment when embeded server will be implemented
 
    @Override
    protected ServerDefinition createNewServer() {
        UDKServerDefinition def = new UDKServerDefinition();

        // show properties or eg. wizard
        UnrealServerNode o = new UDKServerNode(def);
        NodeOperation.getDefault().showProperties(o);
        return def;
    }
                                   
                                   */
}
