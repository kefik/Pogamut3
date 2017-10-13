/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.map;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.DefaultFadeLine;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.IFadeLine;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.IRenderableUTAgent;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.MapEvent;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simple proxy from IUTAgent to IRenderableUTAgent.
 * @author Honza
 */
final class ProxyRenderableAgent implements IRenderableUTAgent {
    private int glName;
    private IUnrealBot agent;
    private Color color;

    private static IFadeLine emptyFade = new DefaultFadeLine();

    public ProxyRenderableAgent(IUnrealBot agent, Color color, int glName) {
        this.agent = agent;
        this.glName = glName;
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public IFadeLine getFadeLine() {
        return emptyFade;
    }

    @Override
    public List<String> getAssociatedInfo() {
        return Arrays.asList(getName());
    }

    @Override
    public List<MapEvent> getMapEvents() {
        return new ArrayList<MapEvent>();
    }

    @Override
    public Object getDataSource() {
        return agent;
    }

    @Override
    public int getGLName() {
        return glName;
    }

    @Override
    public void respawn() throws PogamutException {
        agent.respawn();
    }
/*TODO
    @Override
    public void setBoolConfigure(BoolBotParam param, boolean value) {
        agent.setBoolConfigure(param, value);
    }

    @Override
    public boolean getBoolConfigure(BoolBotParam param) {
        return getBoolConfigure(param);
    }
*/
    @Override
    public IAct getAct() {
        return agent.getAct();
    }

    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public IAgentLogger getLogger() {
        return agent.getLogger();
    }

    @Override
    public ImmutableFlag<IAgentState> getState() {
        return agent.getState();
    }

    @Override
    public void start() throws AgentException {
        agent.start();
    }

    @Override
    public void pause() throws AgentException {
        agent.pause();
    }

    @Override
    public void resume() throws AgentException {
        agent.resume();
    }

    @Override
    public void stop() {
        agent.stop();
    }

    @Override
    public void kill() {
        agent.kill();
    }

    @Override
    public Folder getIntrospection() {
        return agent.getIntrospection();
    }

    @Override
    public Location getLocation() {
        return agent.getLocation();
    }

    @Override
    public Velocity getVelocity() {
        return agent.getVelocity();
    }

    @Override
    public Rotation getRotation() {
        return agent.getRotation();
    }

    @Override
    public IWorldView getWorldView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IAgentId getComponentId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IComponentBus getEventBus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void startPaused() throws ComponentCantStartException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
