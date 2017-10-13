package cz.cuni.amis.nb.pogamut.ut2004.server;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import cz.cuni.amis.nb.api.pogamut.base.server.ServerTypesManager;
import cz.cuni.amis.nb.api.pogamut.ut2004.server.EmbeddedUTServerDefinition;
import cz.cuni.amis.nb.api.pogamut.ut2004.server.UTServerDefinition;
import cz.cuni.amis.nb.pogamut.base.NamedAction;
import cz.cuni.amis.nb.pogamut.base.server.ServersRootNode;
import cz.cuni.amis.nb.pogamut.unreal.server.UnrealServerNode;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.utils.exception.PogamutException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.nodes.NodeOperation;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 * Root node representing list of connected UT2004 servers.
 *
 * @author ik
 */
public class UTServersRootNode extends ServersRootNode<ServerDefinition<IUT2004Server>> {

    public static final String UT_SERVERS_ID = "gb04";
    protected static ResourceBundle bundle = NbBundle.getBundle(UTServersRootNode.class);

    public UTServersRootNode() {
        super(ServerTypesManager.getServersManager(UT_SERVERS_ID));
        setDisplayName(bundle.getString("LBL_UTServersRootNode"));
        setShortDescription(bundle.getString("HINT_UTServersRootNode"));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/server/UT2004Servers.gif");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] actions = super.getActions(context);
        Action[] newActions = Arrays.copyOf(actions, actions.length + 1, Action[].class);

        newActions[newActions.length - 1] = new NamedAction("ACT_AddEmbedServer") {

            @Override
            protected void action(ActionEvent e) throws PogamutException {
                EmbeddedUTServerDefinition server = createEmbeddedServer();
                manager.getAllServers().add(server);
                manager.getDefaultServer().setFlag(server);
            }
        };
        return newActions;
    }

    protected EmbeddedUTServerDefinition createEmbeddedServer() {
        EmbeddedUTServerDefinition def = new EmbeddedUTServerDefinition();
        // show properties or eg. wizard
        UTEmbededServerNode o = new UTEmbededServerNode(def);
        NodeOperation.getDefault().showProperties(o);
        return def;
    }

    @Override
    protected ServerDefinition createNewServer() {
        UTServerDefinition def = new UTServerDefinition();
        // show properties or eg. wizard
        UnrealServerNode o = new UTServerNode(def);
        NodeOperation.getDefault().showProperties(o);
        return def;
    }
}
