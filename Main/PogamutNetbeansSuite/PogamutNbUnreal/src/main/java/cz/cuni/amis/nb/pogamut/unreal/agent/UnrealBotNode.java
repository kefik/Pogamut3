package cz.cuni.amis.nb.pogamut.unreal.agent;

import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import java.awt.Image;
import org.openide.util.ImageUtilities;

/**
 * Node representing Pogamut Unreal bot.
 * @author ik
 */
public class UnrealBotNode extends UnrealAgentNode {

    public UnrealBotNode(IUnrealBot bot, IUnrealServer server) {
        super(bot, server);
    }

    @Override
    public Image loadAgentIcon() {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/unreal/agent/UTPlayer.png");
    }
}
