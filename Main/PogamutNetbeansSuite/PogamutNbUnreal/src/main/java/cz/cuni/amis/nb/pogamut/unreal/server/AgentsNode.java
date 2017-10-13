package cz.cuni.amis.nb.pogamut.unreal.server;

import cz.cuni.amis.nb.pogamut.unreal.agent.UnrealBotNode;
import cz.cuni.amis.nb.util.NodeFactory;
import cz.cuni.amis.nb.util.collections.ObservableCollectionNode;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import java.awt.Image;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;

/**
 * Node for all Pogamut bots.
 * @author ik
 */
public class AgentsNode extends ObservableCollectionNode<IUnrealBot> {

    public AgentsNode(final IUnrealServer server) {
        super(server.getAgents(), new NodeFactory<IUnrealBot>() {

            @Override
            public Node[] create(IUnrealBot obj) {
                return new Node[]{new UnrealBotNode((IUnrealBot) obj, server)};
            }
        });


        setName("Pogamut bots");
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
