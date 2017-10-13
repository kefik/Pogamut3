
package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.MapEvent;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import java.awt.Color;
import java.util.List;

/**
 * This interface is used for passing info about agent to {@link EntityRenderer}.
 * 
 * It is supposed to be unified interface for both timeline and overview map. 
 * Timeline implementation will provide available info at current time of db.
 *
 * @param <T> Type of object this object uses to get its data from
 * @author Honza
 */
public interface IRenderableUTAgent extends IUnrealBot {

    /**
     * Get color of agent. It should not change if possible, otherwise it can confuse a user.
     * @return Color that will be used to render agent in map.
     */
    public Color getColor();

    /**
     * Get fade line (line of past places the agent was).
     * @return
     */
    public IFadeLine getFadeLine();

    /**
     * Return text info associated with the agent. Agent can have multiple infos
     * associated (e.g. current state, like "I am in water" and "I am looking for ammo")
     * @return List of infos about the agent.
     */
    public List<String> getAssociatedInfo();

    /**
     * Return list of all map events this agent has at the time.
     * XXX: It is possible it would be better to provide List &lt;ISubGLRenderer&gt;s,
     *      but it would clash with model-view-control design pattern.
     * @return List of map events belonging to this agent.
     */
    public List<MapEvent> getMapEvents();

    /**
     * Return source of all data that are providing stuff used.
     * Why do I want it? The selection, I am putting this stuff to lookup and
     * some other component can look it up and select nodes representing data sources.
     *
     * XXX: return type should be more general, but this saves trouble and no need to
     * generalize too soon.
     */
    public Object getDataSource();


    /**
     * Return OpenGl name used for selection (see selction mode of opengl).
     * Basically after all is rendered, whe get glNames(ints) of objects that were
     * rendered in viewvolume. But we need to map it back. That is what this is for.
     * @return
     */
    public int getGLName();
}
