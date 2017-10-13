package cz.cuni.amis.nb.pogamut.unreal.server;

import cz.cuni.amis.nb.pogamut.unreal.agent.UnrealPlayerNode;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.pogamut.unreal.bot.impl.NativeUnrealBotAdapter;
import java.awt.Image;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 * Node showing all players on the server.
 * @author ik
 */
public class PlayersNode extends ObservableCollectionNode<NativeUnrealBotAdapter> {

    public PlayersNode(final IUnrealServer server) {
        super(server.getNativeAgents(), new NodeFactory<NativeUnrealBotAdapter>() {

            @Override
            public Node[] create(NativeUnrealBotAdapter obj) {
                // if JMX is not present then it is normal player
                return new Node[]{new UnrealPlayerNode(obj, server)};
            }
        });

        setName("Native players");
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/ut2004/server/AgentsNodeIcon.png");
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

}
