package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.MapEvent;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLLogRecorder;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.state.level0.IAgentState;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of {@link IRenderableUTAgent} for timeline. It proxies data from
 * timeline at current time of db to {@link EntityRenderer}.
 *
 * For now, if something doesn't make sense (like any action from {@link IUTAgent}, 
 * because this is record, not actual stuff that could do something), throw 
 * {@link UnsupportedException}.
 *
 * @author Honza
 */

class TLRenderableUTAgent implements IRenderableUTAgent {

    private TLEntity entity;
    private long time;
    private IFadeLine fadeLine;
    private int glName;

    TLRenderableUTAgent(TLEntity entity, long time, int glName) {
        this.entity = entity;
        this.time = time;
        this.fadeLine = new TLFadeLine(entity, time);
        this.glName = glName;
    }

    @Override
    public Location getLocation() {
        return entity.getLocation(time);
    }

    @Override
    public Rotation getRotation() {
        return entity.getRotation(time);
    }

    @Override
    public Velocity getVelocity() {
        return entity.getVelocity(time);
    }

    @Override
    public Color getColor() {
        return entity.getColor();
    }

    @Override
    public IFadeLine getFadeLine() {
        return fadeLine;
    }

    /**
     * Return text of events associated with the agent.
     * @return
     */
    @Override
    public List<String> getAssociatedInfo() {
        List<String> entityEvents = new LinkedList<String>();

        for (TLLogRecorder recorder : entity.getLogRecorders()) {
            List<MapEvent> mapEvents = recorder.getMapEvents(time);

            // we are only interested in ones that should follow the player
            for (MapEvent mapEvent : mapEvents) {
                if (mapEvent.shouldFollowPlayer()) {
                    entityEvents.add(mapEvent.getMessage());
                }
            }
        }
        return entityEvents;
    }

    @Override
    public List<MapEvent> getMapEvents() {
        List<MapEvent> mapEvents = new LinkedList<MapEvent>();

        for (TLLogRecorder recorder : entity.getLogRecorders()) {
            mapEvents.addAll(recorder.getMapEvents().getEvents(time));
        }

        return mapEvents;
    }

    @Override
    public void respawn() throws PogamutException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
/* TODO
    @Override
    public void setBoolConfigure(BoolBotParam param, boolean value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getBoolConfigure(BoolBotParam param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
*/
    @Override
    public IAct getAct() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /// XXX: This is supposed to be unique, so not implemented for now.
    @Override
    public String getName() {
        return entity.getDisplayName();
    }

    @Override
    public AgentLogger getLogger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * TODO: this should map state of entity in db at current time to agent state.
     * @return
     */
    @Override
    public ImmutableFlag<IAgentState> getState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void start() throws AgentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void pause() throws AgentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resume() throws AgentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void kill() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * TODO: use entity.getFolder(), but it is not same as Folder, because of IntrospectionException
     * @return
     */
    @Override
    public Folder getIntrospection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TLEntity getDataSource() {
        return entity;
    }

    @Override
    public int getGLName() {
        return glName;
    }

    @Override
    public IWorldView getWorldView() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IAgentId getComponentId() {
        // TODO
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
