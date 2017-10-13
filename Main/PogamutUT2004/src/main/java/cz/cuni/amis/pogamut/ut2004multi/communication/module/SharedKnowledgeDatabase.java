package cz.cuni.amis.pogamut.ut2004multi.communication.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.utils.runner.impl.MultipleAgentRunner;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ICompositeWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ILocalWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedProperty;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.ISharedWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.object.IStaticWorldObject;
import cz.cuni.amis.pogamut.multi.communication.worldview.property.PropertyId;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.UT2004CompositeObjectCreator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.UT2004SharedObjectCreator;
import cz.cuni.amis.utils.concurrency.AtomicLongList;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.maps.HashMapMap;
import cz.cuni.amis.utils.maps.SyncHashMap;
import cz.cuni.amis.utils.maps.WeakHashTriMap;

/**
 * Manages shared knowledge for agent teams.
 * The class is managed - meaning only one instance can exist for each team.
 * 
 * Beware that this class can only be used when you are running all your agents from a single JVM.
 * This means you have to used the new {@link MultipleAgentRunner} to run your team. Then each agent has to register with this
 * database by calling {@link SharedKnowledgeDatabase#addAgent(IAgentId, IVisionWorldView, int)} with the specific parameters. 
 * After that, the database should be ready for use.
 * 
 * @author srlok
 * @author Jimmy
 */
public class SharedKnowledgeDatabase {

	/**
	 * Team number to instance.
	 */
	protected static Map<Integer, SharedKnowledgeDatabase> instances = new HashMap<Integer, SharedKnowledgeDatabase>();
	
	/**
	 * Returns the only instance of SharedKnowledgeDatabase for the specified team.
	 * @param team
	 * @return
	 */
	public static SharedKnowledgeDatabase get( int team )
	{
		synchronized(instances) {
			SharedKnowledgeDatabase instance = instances.get(team);
			if ( instance == null )
			{
				instance = new SharedKnowledgeDatabase(team);
				instances.put(team, instance);
			};
			return instance;
		}
	}
	
	//
	// IMPLEMENTATION
	//

	/**
	 * Which agents are registered here mapped to their agent-numbers.
	 */
	protected SyncHashMap<IAgentId, Integer> registeredAgents = new SyncHashMap<IAgentId, Integer>();
	
	/**
	 * Team number for which the shared knowledge database was constructed for.
	 */
	protected int team;
	
	/**
	 * agentLockTimes[agentNumber] == lock time the agent owns (or -1 for no-lock)
	 */
	protected AtomicLongList agentLockTimes = new AtomicLongList(10, 10);	
	
	/**
	 * Maps the agentIds to their concrete worldViews - these are then used in getting the local object information
	 * and to attach the listeners
	 */
	protected Map<IAgentId, IVisionWorldView> agentWorldViews = new HashMap<IAgentId, IVisionWorldView>();
	
	/**
	 * Remembers all the listeners added for an agent, this is needed to properly remove them when a agent unregisters from the database
	 */
	protected Map<IAgentId, Set<IWorldObjectEventListener>> listeners = new HashMap<IAgentId, Set<IWorldObjectEventListener>>();
	
	/**
	 * Just holds the weakly referenced shared properties for the worldObjects
	 */
	protected WeakHashTriMap<TimeKey, WorldObjectId, PropertyId, ISharedProperty> sharedProperties = new WeakHashTriMap<TimeKey, WorldObjectId, PropertyId, ISharedProperty>();
	
	/**
	 * The most recent versions of sharedProperties
	 */
	protected HashMapMap< WorldObjectId, PropertyId, ISharedProperty> currSharedProperties = new HashMapMap< WorldObjectId, PropertyId, ISharedProperty>(); 
	
	/**
	 * Holds the last time when an update event was recieved from any agent for each object
	 */
	protected Map<WorldObjectId, Long> lastUpdateTime = new HashMap<WorldObjectId, Long>();
	
	/**
	 * Currently 'locked' timeKeys (with agents locking the keys) - we need to keep objects for those
	 */
	protected HashMap<TimeKey,Set<IAgentId>> heldKeys = new HashMap<TimeKey,Set<IAgentId>>();
	
	/**
	 * The current TimeKey handled by an agent
	 */
	protected Map<IAgentId, TimeKey> currentTimeKeys = new HashMap<IAgentId, TimeKey>();
	
	/**
	 * Classes for which the database processes events
	 */
	protected Set<Class> registeredClasses = new HashSet<Class>();
	
	protected SharedKnowledgeDatabase( int team )
	{
		this.team = team;
	}
	
	//
	//
	// TIMEKEY-related methods
	//
	//
	
	/**
	 * Locks the specified time and all greater times with the agentId
	 */
	protected void addTimeLock(TimeKey timeKey, IAgentId id)
	{
		Set<IAgentId> agentLocks = heldKeys.get(timeKey);
		if ( agentLocks == null )
		{
			agentLocks = new HashSet<IAgentId>();
			heldKeys.put(timeKey, agentLocks);
		}			
		//add the locks for all relevant timeKeys to prevent losing objects we might require in the future
		for ( TimeKey t : heldKeys.keySet() )
		{
			if ( t.getTime() >= timeKey.getTime())
			{
				heldKeys.get(t).add(id);
			}
		}
	}
	
	/**
	 * Removes a single lock for this timeKey, if it is the last, the whole lock is removed
	 * @param timeKey
	 * @param id
	 */
	protected void removeTimeLock( TimeKey timeKey, IAgentId id)
	{
		synchronized (heldKeys)
		{
			Set<IAgentId> agentLocks = heldKeys.get(timeKey);
			agentLocks.remove(id);
			if ( agentLocks.isEmpty() ) //this was the last lock
			{
				heldKeys.remove(timeKey);
			}
		}
	}
		
	/*
	 * MISC methods
	 */
	
	
	
	/*
	 * User functionality methods
	 */
	
	/**
	 * Registers an agent to the database - it will process it's relevant events from now on.
	 * @param id
	 * @param agentWorldView
	 * @param team
	 */
	public void addAgent( IAgentId id, IVisionWorldView agentWorldView, int team)
	{
		if (team != this.team)
		{
			throw new PogamutException("Trying to add an agent of different team than the one registered with this sharedKnowledgeDatabase.", this);
		}
		
		if ( registeredAgents.get(id) != null) {
			return;
		}
		
		registeredAgents.getWriteLock().lock();
		try {
			int unusedNumber = getUnusedAgentNumber();
			registeredAgents.getMap().put(id, unusedNumber);
		} finally {
			registeredAgents.getWriteLock().unlock();
		}
	}
	
	/**
	 * UNSYNC 
	 * @return
	 */
	private int getUnusedAgentNumber() {		
		List<Boolean> used = new ArrayList<Boolean>(registeredAgents.size()+1);
		for (Integer agentNumber : registeredAgents.getMap().values()) {
			used.set(agentNumber, true);
		}
		for (int i = 0; i < used.size(); ++i) {
			if (used.get(i) == null || !used.get(i)) {
				return i;
			}
		}
		// SHOULD NOT REACH HERE!
		throw new RuntimeException("registeredAgents corrupted!");
	}

	/**
	 * Registers the provided class as a class of interest.
	 * The knowledge database will register listeners on all agent worldViews and will start processing events from
	 * the worldViews to collect shared information.
	 * @param c
	 */
	public void addObjectClass(Class c)
	{
		synchronized (registeredClasses)
		{
			registeredClasses.add(c);
			// TODO!!! registeredAgents.getReadLock().lock();
			for ( IAgentId id : registeredAgents.getMap().keySet())
			{
				addClassListener(agentWorldViews.get(id),c, id);
			}			
		}
	}
	
	/**
	 * Stops processing events for the specified class. If the class is not registered, no change is made.
	 * @param c class to process events for
	 * @return false if the class was not registered
	 */
	public boolean removeObjectClass(Class c)
	{
		synchronized (registeredClasses)
		{
			return registeredClasses.remove(c);
		}
	}
	
	/**
	 * Unregister the agent from the database -> the database will no longer process events from this agent.
	 * The method also removes all listeners created by the database on the agent's worldview.
	 * @param id
	 * @param team
	 * @return
	 */
	public boolean removeAgent( IAgentId id)
	{
		synchronized (registeredAgents)
		{
			// TODO!!! registeredAgents.getReadLock().lock();
			if ( !registeredAgents.getMap().containsKey(id) )
			{
				return false;
			}
			IVisionWorldView wv = agentWorldViews.get(id);
			Set<IWorldObjectEventListener> listenerSet = listeners.get(id);
			if ( listenerSet != null)
			{
				for (IWorldObjectEventListener listener : listenerSet )
				{
					wv.removeListener(listener);
				}
			}
			agentWorldViews.remove(id);
			registeredAgents.remove(id);
			listeners.remove(id);
			return true;
		}
	}
	
	/**
	 * Returns the specified object with the team shared knowledge put in
	 * @param id
	 * @param agentId
	 * @return
	 */
	public IWorldObject getObject(WorldObjectId id, IAgentId agentId)
	{
		synchronized (sharedProperties)
		{
			ICompositeWorldObject agentObject = (ICompositeWorldObject)agentWorldViews.get(agentId).get(id);
			if (agentObject == null)
			{
				return null;
			}
			ILocalWorldObject localPart = agentObject.getLocal();
			IStaticWorldObject staticPart = agentObject.getStatic();
			Map<PropertyId, ISharedProperty> properties = agentObject.getShared().getProperties();
			TimeKey timeKey = TimeKey.get(localPart.getSimTime());
			for ( PropertyId pId : currSharedProperties.get(id).keySet() ) //iterate through shared properties
			{
				ISharedProperty p = sharedProperties.get(timeKey, id, pId);
				if ( p == null )
				{
					p = currSharedProperties.get(id, pId);
				}
				properties.put(p.getPropertyId(), p);
			}
			Class msgClass = localPart.getCompositeClass();
			//now create the sharedObject
			ISharedWorldObject sharedPart = UT2004SharedObjectCreator.create( msgClass , id , properties.values());
			return UT2004CompositeObjectCreator.createObject(localPart, sharedPart, staticPart);
		}
	}
	
	/*
	 * HELPER methods servicing the inner functionality
	 */
	
	/**
	 * Helper method to add a single objectEventListener for Class c to a specific WorldView
	 * @param wv
	 * @param c
	 * @param agentId
	 */
	protected void addClassListener(IVisionWorldView wv, Class c, IAgentId agentId)
	{
		synchronized (listeners)
		{
			IWorldObjectEventListener listener =
				new AgentSpecificObjectEventListener<IWorldObject, IWorldObjectEvent<IWorldObject>>(agentId) {
					@Override
					public void notify(IWorldObjectEvent<IWorldObject> event) 
					{
						SharedKnowledgeDatabase.this.processObjEvent(event, this.agentId);						
					};
				};
			
			wv.addObjectListener(c, listener);
			
			Set<IWorldObjectEventListener> listenerSet = listeners.get(agentId);
			if ( listenerSet == null )
			{
				listenerSet = new HashSet<IWorldObjectEventListener>();
				listeners.put(agentId, listenerSet);
			};
			listenerSet.add(listener);
		}
	}
	
	/**
	 * Handles processing of an object event raised on any of the worldViews.
	 * This method will update all the shared properties correctly and also uses the event information to adjust the heldTimeKeys
	 * @param event
	 */
	protected void processObjEvent(IWorldObjectEvent<IWorldObject> event, IAgentId agentId)
	{
		//handle timeLocking first
		TimeKey currentTime = currentTimeKeys.get(agentId);
		if ( currentTime != null )
		{
			if ( currentTime.getTime() < event.getSimTime()) // NEW BATCH BEGINS... remove old one
			{
				TimeKey newTimeKey = TimeKey.get(event.getSimTime());
				//unlock old time
				this.removeTimeLock(currentTime, agentId);
				currentTimeKeys.put(agentId, newTimeKey);
				this.addTimeLock(newTimeKey, agentId);				
			}
		}
		else
		{
			TimeKey newTimeKey = TimeKey.get(event.getSimTime());
			currentTimeKeys.put(agentId, newTimeKey);
			this.addTimeLock(newTimeKey, agentId);				
		}
		
		WorldObjectId id = event.getId();		
		
		ISharedWorldObject sharedObj = ((ICompositeWorldObject)event.getObject()).getShared();
		
		long lastUpdateTime = -1;
		if ( this.lastUpdateTime.containsKey(id) )
		{
			lastUpdateTime = this.lastUpdateTime.get(id);
		}
		
		//we have recieved an update
		if ( sharedObj.getSimTime() >= lastUpdateTime ) //makes sense to update now
		{
			synchronized (sharedProperties)
			{
			
			
				for ( ISharedProperty p : sharedObj.getProperties().values())
				{
					if ( p.getValue() != null ) //some info in the property
					{
						PropertyId propertyId = p.getPropertyId();
						ISharedProperty old = currSharedProperties.get(id, propertyId);
						for ( TimeKey key : heldKeys.keySet() )
						{
							if ( sharedProperties.get(key, id, propertyId) == null )
							{
								sharedProperties.put(key,id, propertyId, p); //add a old copy
							}
						}
					}
				}
			}
		}
	}
}
