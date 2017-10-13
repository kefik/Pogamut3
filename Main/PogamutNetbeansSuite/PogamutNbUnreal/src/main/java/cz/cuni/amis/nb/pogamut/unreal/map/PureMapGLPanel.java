/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.map;

import cz.cuni.amis.nb.pogamut.unreal.timeline.map.IRenderableUTAgent;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.UTAgentSubGLRenderer;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.utils.collections.CollectionEventListener;
import java.util.Collection;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Simple map that overviews what is happening in the level right now.
 * It listens for changes in server.getAgents() and server.getNativeBots()
 * for adding and removing bots from map.
 *
 * This is intended for one map only, it is not intended to handle change
 * from one map to another, simply discard this, call destroy() and create
 * a new one.
 *
 * This is panel that shows one particular map. After you are finished
 * with using the class, please call destroy() to remove listeners, stops
 * redrawing and do some other clean up. 
 * 
 * @author Honza
 */
public class PureMapGLPanel extends SelectableMapGLPanel implements CollectionEventListener<IUnrealBot> {

    /** Server on which we listen for changes in agents and bots */
    protected IUnrealServer server;
    // Generator used to create colors for new agents.
    protected MapColorGenerator colorGenerator;
    // Timer used to redraw this panel.
    private Timer timer;
    // How often should map be redrawn, be careful, because of traffic overhead,
    // we get the data from somewhere in the net.
    private final int REDRAW_DELAY = 250;

    protected PureMapGLPanel(IUnrealMap map, IUnrealServer server) {
        super(map, Logger.getLogger("PureMapGLPanel"));

        this.server = server;
        this.colorGenerator = new MapColorGenerator();

        // add all found agents in the map
        for (Object agent : server.getAgents()) {
            addAgentRenderer((IUnrealBot) agent);
        }
        for (Object agent : server.getNativeAgents()) {
            addAgentRenderer((IUnrealBot) agent);
        }

        // add listeners so I can update agents
        server.getAgents().addCollectionListener(this);
        server.getNativeAgents().addCollectionListener(this);
    }

    /**
     * Start display loop
     */
    public synchronized void startDisplayLoop() {
        if (timer == null) {
            timer = new Timer("Overview map redrawer");
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    display();
                }
            }, REDRAW_DELAY, REDRAW_DELAY);
        }
    }

    /**
     * Stop display loop
     */
    public synchronized void stopDisplayLoop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Create a renderable representation of agent and add it to renderers.
     * @param agent Agent that will be added to drawn agents
     */
    private void addAgentRenderer(IUnrealBot agent) {
        IRenderableUTAgent renderableUTAgent = new ProxyRenderableAgent(agent, colorGenerator.getUniqueColor(), lastGLName++);
        agentRenderes.addSubRenderer(new UTAgentSubGLRenderer(renderableUTAgent, getMap()));
    }

    private void removeAgentRenderer(IUnrealBot agent) {
        Set<IRenderableUTAgent> drawnAgent = agentRenderes.getDrawnObjects();

        for (IRenderableUTAgent renderableAgent : drawnAgent) {
            if (renderableAgent.getDataSource() == agent) {
                agentRenderes.removeRenderersOf(renderableAgent);
            }
        }
    }

    /**
     * Do nothing.
     * @param toBeAdded
     * @param whereToAdd
     */
    @Override
    public void preAddEvent(Collection<IUnrealBot> toBeAdded, Collection<IUnrealBot> whereToAdd) {
    }

    /**
     * Add renderers representing the agents to the map.
     * @param alreadyAdded
     * @param whereWereAdded
     */
    @Override
    public synchronized void postAddEvent(Collection<IUnrealBot> alreadyAdded, Collection<IUnrealBot> whereWereAdded) {
        for (IUnrealBot agent : alreadyAdded) {
            addAgentRenderer(agent);
        }
    }

    /**
     * Remove renderers that represented the removed agents from the map
     * @param toBeRemoved
     * @param whereToRemove
     */
    @Override
    public synchronized void preRemoveEvent(Collection<IUnrealBot> toBeRemoved, Collection<IUnrealBot> whereToRemove) {
        for (IUnrealBot removedAgent : toBeRemoved) {
            removeAgentRenderer(removedAgent);
        }
    }

    /**
     * Do nothing
     * @param alreadyAdded
     * @param whereWereRemoved
     */
    @Override
    public void postRemoveEvent(Collection<IUnrealBot> alreadyAdded, Collection<IUnrealBot> whereWereRemoved) {
    }

    @Override
    public synchronized void destroy() {
        if (timer != null) {
            timer.cancel();
        }

        server.getAgents().removeCollectionListener(this);
        server.getNativeAgents().removeCollectionListener(this);

        server = null;

        super.destroy();
    }
}
