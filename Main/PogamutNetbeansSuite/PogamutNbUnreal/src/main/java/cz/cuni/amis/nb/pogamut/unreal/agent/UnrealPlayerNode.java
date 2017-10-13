package cz.cuni.amis.nb.pogamut.unreal.agent;

import cz.cuni.amis.pogamut.unreal.bot.impl.NativeUnrealBotAdapter;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;

import java.awt.Image;
import org.openide.util.ImageUtilities;

/**
 * Node representing human player connected to the Unreal game.
 * @author ik
 */
public class UnrealPlayerNode extends UnrealAgentNode {
    public UnrealPlayerNode(NativeUnrealBotAdapter player, IUnrealServer server) {
        super(player, server);
        //setName(player.getName()); //TODO + " (" + player.getId().getUnrealId() + ")");

    }

    @Override
    public Image loadAgentIcon() {
        return ImageUtilities.loadImage("cz/cuni/amis/nb/pogamut/unreal/agent/UTPlayer.png");
    }

    /*
    @Override
    public Action[] getActions(boolean context) {
    Action[] oldActs = super.getActions(context);
    Action[] acts = Arrays.copyOf(oldActs, oldActs.length + 2);

    // respawn action
    acts[oldActs.length + 1] = new NamedAction("ACT_Respawn", UnrealPlayerNode.class) {

    @Override
    protected void action(ActionEvent e) throws PogamutException {
    // TODO
    }
    };
    return acts;
    }
     */
}
