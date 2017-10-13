package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;

/**
 * Special type of {@link TLProperty} that is storing location of agent.
 * @author Honza
 */
public class TLLocation extends TLProperty<Location> {

    private final transient IUnrealBot agent;

    public TLLocation(IUnrealBot agent) {
        super("tl-location", Location.class);

        this.agent = agent;
    }

    public void update(long time) {
        addValue(agent.getLocation(), time);
    }
}
