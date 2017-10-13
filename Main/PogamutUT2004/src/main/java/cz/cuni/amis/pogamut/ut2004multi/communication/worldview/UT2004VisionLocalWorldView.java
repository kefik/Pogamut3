package cz.cuni.amis.pogamut.ut2004multi.communication.worldview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.ILifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.worldview.object.IViewable;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.messages.SharedBatchFinishedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ICompositeWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.translator.event.ILocalWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.event.DummyObjectEvent.EventType;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.UT2004CompositeObjectCreator;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.ILocalGBViewable;

/**
 * Implements the logic to appear/disappear objects based on the GB2004 batches.
 * @author srlok
 *
 */
public abstract class UT2004VisionLocalWorldView extends BatchAwareLocalWorldView{

	public UT2004VisionLocalWorldView(ComponentDependencies dependencies,
			ILifecycleBus bus, IAgentLogger logger,
			ISharedWorldView parentWorldView, ITeamedAgentId agentId) {
		super(dependencies, bus, logger, parentWorldView, agentId);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected ICompositeWorldObject createCompositeObject(ILocalWorldObject localObject, ISharedWorldObject sharedObject, IStaticWorldObject staticObject)
	{
		return UT2004CompositeObjectCreator.createObject(localObject, sharedObject, staticObject);
	}
	
	protected Collection<WorldObjectId> lastBatch = new ArrayList<WorldObjectId>();
	protected Collection<WorldObjectId> currentBatch = new ArrayList<WorldObjectId>();
	
	private long currentBatchTime = -1;
	
	@Override
	public synchronized void notify(IWorldChangeEvent event)
	{
		/*if (isBatchBeginEvent(event))
		{
			log.info(event.toString());
		}
		if (isBatchEndEvent(event))
		{
			log.info(event.toString());
		}
		if (event instanceof SharedBatchFinishedEvent)
		{
			log.info(event.toString());
		}
		if ( currentBatchTime < event.getSimTime() )
		{
			currentBatchTime = event.getSimTime();
		}*/
		if ( event instanceof ILocalWorldObjectUpdatedEvent )
		{
			localEventNotify((ILocalWorldObjectUpdatedEvent)event);
		}
		else if ( event instanceof ICompositeWorldObjectUpdatedEvent )
		{
			ILocalWorldObjectUpdatedEvent locEvent = ((ICompositeWorldObjectUpdatedEvent)event).getLocalEvent();
			if ( locEvent != null)
			{
				localEventNotify(locEvent);
			}
		}
		else if ( isBatchEndEvent(event))
		{
			processBatches();
		}
		super.notify(event);
	}
	
	@Override
	protected void disappearObject(WorldObjectId id, long time)
	{
		ILocalGBViewable object = (ILocalGBViewable)getLocal(id, TimeKey.get(time));
		ILocalWorldObjectUpdatedEvent disEvent = object.createDisappearEvent();
		object = (ILocalGBViewable)disEvent.update(object).getObject();
		raiseEvent( new WorldObjectDisappearedEvent((IViewable)this.get(id, TimeKey.get(time)) , time) );
		raiseEvent( new WorldObjectUpdatedEvent(this.get(id, TimeKey.get(time)), time));
	}
	
	/**
	 * Disappears objects that should not be visible (update was not recieved in this batch);
	 */
	private void processBatches()
	{
		log.fine("Processing Batches, lastBatchSize : " + lastBatch.size() );
		if ( !lastBatch.isEmpty() )
		{
			this.lastBatch.removeAll(currentBatch);
			for( WorldObjectId id : lastBatch)
			{
				if (log.isLoggable(Level.FINE) ) log.fine("Disappearing object : " + id);
				//buffer the object disappeared event. 
				super.bufferObjectEvent(id, EventType.DISAPPEARED, currentBatchTime);
			}
		}
		if ( log.isLoggable(Level.FINE ))
		log.fine("Swapping batches : Curr : " + this.currentBatch.size() + " ; last : " + this.lastBatch.size());
		this.lastBatch = this.currentBatch;
		currentBatch = new ArrayList<WorldObjectId>(this.lastBatch.size() + 10);
		if ( log.isLoggable(Level.FINE ))
		log.fine("Swapping finished : Curr : " + this.currentBatch.size() + " ; last : " + this.lastBatch.size());
	}
	
	/**
	 * If the event updates a Viewable object, this method manages making it disappear when no update is recieved for it
	 * - this means the object is not in FoW of our agent.
	 * @param event
	 */
	protected void localEventNotify(ILocalWorldObjectUpdatedEvent event)
	{
		if (log.isLoggable(Level.FINE) ) log.fine("LocalEvent recieved in batch :" + event.getSimTime() + " ; " + event.getId() );
		IWorldObject obj = ((IWorldObjectEvent)event).getObject();
		if ( obj != null)
		{
			if ( obj instanceof ILocalGBViewable )
			{
				if ( ((ILocalGBViewable) obj).isVisible() )
				{					
					currentBatch.add(obj.getId());
				}
			}
		}
	}

}
