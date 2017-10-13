package cz.cuni.amis.pogamut.base.communication.worldview;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import junit.framework.Assert;

import org.easymock.EasyMock;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.AbstractEventStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.AbstractObjectStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.MediatorStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.ObjectDestroyedEventStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.ObjectUpdatedEventStub;
import cz.cuni.amis.pogamut.base.component.controller.ComponentController;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.WaitForFlagChange;
import cz.cuni.amis.utils.maps.LazyMap;

@SuppressWarnings("unchecked")
public class EventDrivenWorldViewTester {

	protected IAgentLogger logger;
	protected LogCategory log;
	protected MediatorStub mediator;
	protected IWorldView worldView;
		
	protected Map<WorldObjectId, IWorldObject> existingObjects = new HashMap<WorldObjectId, IWorldObject>();
	
	protected Map<Class, IWorldEventListener> levelAListeners = new LazyMap<Class, IWorldEventListener>() {

		@Override
		protected IWorldEventListener create(Class key) {
			return EasyMock.createStrictMock(IWorldEventListener.class);
		}
		
	};
	
	protected Map<Class, IWorldObjectListener> levelBListeners = new LazyMap<Class, IWorldObjectListener>() {

		@Override
		protected IWorldObjectListener create(Class key) {
			return EasyMock.createStrictMock(IWorldObjectListener.class);
		}
		
	};

	/**
	 * Class (object) -> Class (event) -> Listener
	 */
	protected Map<Class, Map<Class, IWorldObjectListener>> levelCListeners = new LazyMap<Class, Map<Class, IWorldObjectListener>>() {

		@Override
		protected Map<Class, IWorldObjectListener> create(Class key) {
			return new LazyMap<Class, IWorldObjectListener>() {

				@Override
				protected IWorldObjectListener create(Class key) {
					return EasyMock.createStrictMock(IWorldObjectListener.class);
				}
				
			};
		}
		
	};

	protected Map<WorldObjectId, IWorldObjectListener> levelDListeners = new LazyMap<WorldObjectId, IWorldObjectListener>() {

		@Override
		protected IWorldObjectListener create(WorldObjectId key) {
			return EasyMock.createStrictMock(IWorldObjectListener.class);
		}

	};
	
	protected Map<WorldObjectId, Map<Class, IWorldObjectListener>> levelEListeners = new LazyMap<WorldObjectId, Map<Class, IWorldObjectListener>>() {

		@Override
		protected Map<Class, IWorldObjectListener> create(WorldObjectId key) {
			return new LazyMap<Class, IWorldObjectListener>() {

				@Override
				protected IWorldObjectListener create(Class key) {
					return EasyMock.createStrictMock(IWorldObjectListener.class);
				}
				
			};
		}
		
	};
	
	protected Map<Class, IWorldObjectListener> firstEncounteredListeners = 
		new LazyMap<Class, IWorldObjectListener>() {
			@Override
			protected IWorldObjectListener create(Class key) {
				return EasyMock.createStrictMock(IWorldObjectListener.class);
			}
		};
				
	protected Map<Class, IWorldObjectListener> updateListeners =
		new LazyMap<Class, IWorldObjectListener>() {
			@Override
			protected IWorldObjectListener create(Class key) {
				return EasyMock.createStrictMock(IWorldObjectListener.class);
			}		 
		};
				
	protected Map<Class, IWorldObjectListener> destroyedListeners = 
		new LazyMap<Class, IWorldObjectListener>() {
			@Override
			protected IWorldObjectListener create(Class key) {
				return EasyMock.createStrictMock(IWorldObjectListener.class);
			}		 
		};
		
	private ComponentController worldViewStarter;		
		
	public EventDrivenWorldViewTester(IAgentLogger logger, MediatorStub mediator, IWorldView worldView, ComponentController worldViewStarter) {
		this.logger = logger;
		this.log = logger.getCategory("test");
		this.mediator = mediator;
		this.worldView = worldView;
		this.mediator.setConsumer(this.worldView);
		this.worldViewStarter = worldViewStarter;
	}
		
	protected void configureEvent(AbstractEventStub event) {
		levelAListeners.get(IWorldEvent.class).notify(
			WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(AbstractEventStub.class).notify(
			WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(event.getClass()).notify(
			WorldEventsMatcher.eqEvent(event)
		);
	}
		
	
	protected void configureObjectFirstEncountered(WorldObjectFirstEncounteredEvent event) {
		levelAListeners.get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(WorldObjectFirstEncounteredEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelBListeners.get(event.getObject().getClass()).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(WorldObjectFirstEncounteredEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelDListeners.get(event.getId()).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(WorldObjectFirstEncounteredEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
	}
	
	protected void configureObjectUpdated(WorldObjectUpdatedEvent event) {
		levelAListeners.get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(WorldObjectUpdatedEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelBListeners.get(event.getObject().getClass()).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(WorldObjectUpdatedEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelDListeners.get(event.getId()).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(WorldObjectUpdatedEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
	}
	
	protected void configureObjectDestroyed(WorldObjectDestroyedEvent event) {
		levelAListeners.get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelAListeners.get(WorldObjectDestroyedEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelBListeners.get(event.getObject().getClass()).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelCListeners.get(event.getObject().getClass()).get(WorldObjectDestroyedEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelDListeners.get(event.getId()).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(IWorldEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(IWorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(WorldObjectEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
		levelEListeners.get(event.getId()).get(WorldObjectDestroyedEvent.class).notify(
				WorldEventsMatcher.eqEvent(event)
		);
	}
	
	protected void preconfigureEvent(AbstractEventStub event) {
		mediator.pushEvent(event);
		configureEvent(event);
	}
	
	
	protected void preconfigureObject(AbstractObjectStub object) {
		ObjectUpdatedEventStub updated = new ObjectUpdatedEventStub(object);
		mediator.pushEvent(updated);
		WorldObjectUpdatedEvent updatedEvent = new WorldObjectUpdatedEvent(object, 0);		
		if (existingObjects.containsKey(object.getId())) {
			configureObjectUpdated(updatedEvent);
		} else {
			WorldObjectFirstEncounteredEvent encounteredEvent = new WorldObjectFirstEncounteredEvent(object, object.getSimTime());			
			configureObjectFirstEncountered(encounteredEvent);
			configureObjectUpdated(updatedEvent);
		}
		existingObjects.put(object.getId(), object);
	}
	
	protected void preconfigureObjectDestroyed(ObjectDestroyedEventStub event) {
		if (!existingObjects.containsKey(event.getId())) {
			Assert.fail("Can't destroy object of id " + event.getId() + " as it does not currently exists.");
		}
		mediator.pushEvent(event);
		IWorldObject object = existingObjects.remove(event.getId());
		WorldObjectDestroyedEvent destroyedEvent = new WorldObjectDestroyedEvent(object, event.getSimTime());
		configureObjectDestroyed(destroyedEvent);
	}
	
	protected void configuration(Object[] eventsObjects) {
		for (Object obj : eventsObjects) {
			if (obj instanceof AbstractObjectStub) {
				preconfigureObject((AbstractObjectStub)obj);
			} else
			if (obj instanceof AbstractEventStub) {
				preconfigureEvent((AbstractEventStub)obj);
			} else 
			if (obj instanceof ObjectDestroyedEventStub) {
				preconfigureObjectDestroyed((ObjectDestroyedEventStub)obj);
			}
		}
	}
	
	protected void registerListeners() {
		for (Entry<Class, IWorldEventListener> entry : levelAListeners.entrySet()) {
			worldView.addEventListener(entry.getKey(), entry.getValue());
		}
		for (Entry<Class, IWorldObjectListener> entry : levelBListeners.entrySet()) {
			worldView.addObjectListener(entry.getKey(), entry.getValue());
		}
		for (Entry<Class, Map<Class, IWorldObjectListener>> entry1 : levelCListeners.entrySet()) {
			for (Entry<Class, IWorldObjectListener> entry2 : entry1.getValue().entrySet()) {
				worldView.addObjectListener(entry1.getKey(), entry2.getKey(), entry2.getValue());
			}
		}
		for (Entry<WorldObjectId, IWorldObjectListener> entry : levelDListeners.entrySet()) {
			worldView.addObjectListener(entry.getKey(), entry.getValue());
		}
		for (Entry<WorldObjectId, Map<Class, IWorldObjectListener>> entry1 : levelEListeners.entrySet()) {
			for (Entry<Class, IWorldObjectListener> entry2 : entry1.getValue().entrySet()) {
				worldView.addObjectListener(entry1.getKey(), entry2.getKey(), entry2.getValue());
			}
		}
		for (Entry<Class, IWorldObjectListener> entry : firstEncounteredListeners.entrySet()) {
			worldView.addObjectListener(entry.getKey(), WorldObjectFirstEncounteredEvent.class, entry.getValue());
		}
		for (Entry<Class, IWorldObjectListener> entry : updateListeners.entrySet()) {
			worldView.addObjectListener(entry.getKey(), WorldObjectUpdatedEvent.class, entry.getValue());
		}
		for (Entry<Class, IWorldObjectListener> entry : destroyedListeners.entrySet()) {
			worldView.addObjectListener(entry.getKey(), WorldObjectDestroyedEvent.class, entry.getValue());
		}
	}
	
	protected void switchToReplay() {
		for (Entry<Class, IWorldEventListener> entry : levelAListeners.entrySet()) {
			EasyMock.replay(entry.getValue());
		}
		for (Entry<Class, IWorldObjectListener> entry : levelBListeners.entrySet()) {
			EasyMock.replay(entry.getValue());
		}
		for (Entry<Class, Map<Class, IWorldObjectListener>> entry1 : levelCListeners.entrySet()) {
			for (Entry<Class, IWorldObjectListener> entry2 : entry1.getValue().entrySet()) {
				EasyMock.replay(entry2.getValue());
			}
		}
		for (Entry<WorldObjectId, IWorldObjectListener> entry : levelDListeners.entrySet()) {
			EasyMock.replay(entry.getValue());
		}
		for (Entry<WorldObjectId, Map<Class, IWorldObjectListener>> entry1 : levelEListeners.entrySet()) {
			for (Entry<Class, IWorldObjectListener> entry2 : entry1.getValue().entrySet()) {
				EasyMock.replay(entry2.getValue());
			}
		}
		for (IWorldObjectListener listener : firstEncounteredListeners.values()) {
			EasyMock.replay(listener);
		}
		for (IWorldObjectListener listener : updateListeners.values()) {
			EasyMock.replay(listener);
		}
		for (IWorldObjectListener listener : destroyedListeners.values()) {
			EasyMock.replay(listener);
		}
	}
	
	protected void verifyListeners() {
		for (Entry<Class, IWorldEventListener> entry : levelAListeners.entrySet()) {
			EasyMock.verify(entry.getValue());
		}
		for (Entry<Class, IWorldObjectListener> entry : levelBListeners.entrySet()) {
			EasyMock.verify(entry.getValue());
		}
		for (Entry<Class, Map<Class, IWorldObjectListener>> entry1 : levelCListeners.entrySet()) {
			for (Entry<Class, IWorldObjectListener> entry2 : entry1.getValue().entrySet()) {
				EasyMock.verify(entry2.getValue());
			}
		}
		for (Entry<WorldObjectId, IWorldObjectListener> entry : levelDListeners.entrySet()) {
			EasyMock.verify(entry.getValue());
		}
		for (Entry<WorldObjectId, Map<Class, IWorldObjectListener>> entry1 : levelEListeners.entrySet()) {
			for (Entry<Class, IWorldObjectListener> entry2 : entry1.getValue().entrySet()) {
				EasyMock.verify(entry2.getValue());
			}
		}
		for (IWorldObjectListener listener : firstEncounteredListeners.values()) {
			EasyMock.verify(listener);
		}
		for (IWorldObjectListener listener : updateListeners.values()) {
			EasyMock.verify(listener);
		}
		for (IWorldObjectListener listener : destroyedListeners.values()) {
			EasyMock.verify(listener);
		}	
	}

	protected void test() {
		
		try {
			if (log.isLoggable(Level.INFO)) log.info("Starting worldview...");
			worldViewStarter.manualStart("starting worldview");
			if (log.isLoggable(Level.INFO)) log.info("Starting mediator...");
			mediator.start();
		} catch (PogamutException e) {
			e.printStackTrace();
			Assert.fail("WorldView failed to start.");
		}

		testBody();
		
		new WaitForFlagChange(mediator.getRunning(), false).await(900000, TimeUnit.MILLISECONDS);
		
		if (mediator.getEventQueueLength() != 0) {
			Assert.fail("Mediator failed to process all events in the queue...");
		}

		// STOP THE WORLDVIEW
		if (log.isLoggable(Level.INFO)) log.info("Stopping worldview...");
		worldViewStarter.manualStop("Stopping the test.");

		// VERIFY LISTENERS
		if (log.isLoggable(Level.INFO)) log.info("Verifying listeners...");
		verifyListeners();	
		
		Assert.assertFalse("Mediator did not stopped.", mediator.getRunning().getFlag());
	}
	
	/**
	 * Provided as custom hook of the test ... called after the WV/Mediator is started.
	 */
	protected void testBody() {
	}

	/**
	 * @param eventsObjects can be only of types IWorldEvent and IWorldObject
	 */
	public void test(Object[] eventsObjects) {
		if (log.isLoggable(Level.INFO)) log.info("Configuring listeners and mediator...");
		configuration(eventsObjects);

		if (log.isLoggable(Level.INFO)) log.info("Switching listeners to replay...");
		switchToReplay();
		
		if (log.isLoggable(Level.INFO)) log.info("Registering listeners...");
		registerListeners();

		if (log.isLoggable(Level.INFO)) log.info("Testing...");
		test();
		
		if (log.isLoggable(Level.INFO)) log.info("Verifying...");
		verifyListeners();
		
		if (log.isLoggable(Level.INFO)) log.info("---/// TEST OK ///---");
	}

}
