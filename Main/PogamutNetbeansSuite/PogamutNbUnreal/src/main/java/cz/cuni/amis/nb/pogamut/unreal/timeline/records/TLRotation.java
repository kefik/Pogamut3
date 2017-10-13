package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;

/**
 * Special type of {@link TLProperty} that is storing rotation of agent.
 * @author Honza
 */
public class TLRotation extends TLProperty<Rotation> {

    private final transient IUnrealBot agent;

    public TLRotation(final IUnrealBot agent) {
        super("tl-rotation", Rotation.class);
        this.agent = agent;
    }

    public void update(long time) {
        addValue(agent.getRotation(), time);
    }
}
