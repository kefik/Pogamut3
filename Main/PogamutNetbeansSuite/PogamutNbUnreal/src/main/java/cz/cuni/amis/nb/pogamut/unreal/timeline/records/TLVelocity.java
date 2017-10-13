package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;

/**
 * Special type of {@link TLProperty} that is storing velocity of agent.
 * @author Honza
 */
public class TLVelocity extends TLProperty<Velocity> {

    private final transient IUnrealBot agent;

    public TLVelocity(final IUnrealBot agent) {
        super("tl-velocity", Velocity.class);
        this.agent = agent;
    }

    public void update(long time) {
        addValue(agent.getVelocity(), time);
    }
}
