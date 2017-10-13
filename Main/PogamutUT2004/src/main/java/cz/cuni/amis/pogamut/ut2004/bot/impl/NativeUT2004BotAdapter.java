package cz.cuni.amis.pogamut.ut2004.bot.impl;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStopException;
import cz.cuni.amis.pogamut.unreal.bot.impl.NativeUnrealBotAdapter;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Kick;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Respawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 *
 * @author ik
 */
public class NativeUT2004BotAdapter extends NativeUnrealBotAdapter {

    public NativeUT2004BotAdapter(Player player, IUnrealServer server, IAct act, IWorldView worldView) {
        super(player, server, act, worldView);
    }

    public void respawn() throws PogamutException {
        act.act(new Respawn(player.getId(), null, null));
    }

    public void stop() throws ComponentCantStopException {
        act.act(new Kick(player.getId()));
    }
}
