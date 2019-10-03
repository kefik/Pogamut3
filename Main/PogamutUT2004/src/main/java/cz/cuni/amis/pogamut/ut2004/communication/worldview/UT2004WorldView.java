package cz.cuni.amis.pogamut.ut2004.communication.worldview;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.impl.BatchAwareWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.IGBViewable;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.IGBWorldObjectDisappeared;

/**
 * WorldView understanding some UT2004 semantics.
 * @author ik
 */
@AgentScoped
public class UT2004WorldView extends BatchAwareWorldView {
	
	public static final String WORLDVIEW_DEPENDENCY = "UT2004WorldViewDependency";
	
	private boolean navPointsExported = true;
	
    @Inject
    public UT2004WorldView(@Named(WORLDVIEW_DEPENDENCY) ComponentDependencies dependencies, IMediator mediator, IComponentBus bus, IAgentLogger log) {
        super(dependencies, bus, log);
        mediator.setConsumer(this);
    }

    @Override
    protected boolean isBatchEndEvent(IWorldChangeEvent evt) {
        return evt instanceof EndMessage;
    }
    
	@Override
	protected boolean isBatchBeginEvent(IWorldChangeEvent evt) {
		return evt instanceof BeginMessage;
	}    

    @Override
    protected boolean setDisappearedFlag(IViewable obj) {
    	if (obj instanceof NavPoint && !navPointsExported) {
    		// just do not mess-up with the visibility of navpoints if they are not exported
    		// => visibility is driven manually then, probably by {@link NavPointVisibility} using {@link LevelGeometry}
    		return false;
    	}
        IGBViewable gbView = (IGBViewable) obj;
        IWorldObjectUpdatedEvent dis = gbView.createDisappearEvent();        
        dis.update(obj);
        return true;
    }
    
    @Override
    protected void batchAwareWorldViewNotify(IWorldChangeEvent event) {
    	super.batchAwareWorldViewNotify(event);
    	if (event instanceof ConfigChange) {
    		ConfigChange cfg = (ConfigChange)event;
    		navPointsExported = cfg.isSyncNavpoints();
    	}
    }
}
