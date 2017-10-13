package cz.cuni.amis.pogamut.base.communication.worldview;

import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.worldview.impl.EventDrivenWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.EventBumpedStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.EventChatMessageStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.MediatorStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.ObjectDestroyedEventStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.ObjectItemStub;
import cz.cuni.amis.pogamut.base.communication.worldview.stubs.ObjectPlayerInfoStub;
import cz.cuni.amis.pogamut.base.component.bus.ComponentBus;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;

import cz.cuni.amis.tests.BaseTest;
				
public class Test01_EventDrivenWorldView extends BaseTest {

	private IAgentLogger logger;

	private MediatorStub mediator;

	private AgentId agentId;

	private ComponentBus bus;

	@Before
	public void init() {
		agentId = new AgentId("Test01_EventDrivenWorldView");
		logger = new AgentLogger(agentId);
		logger.addDefaultConsoleHandler();
		logger.setLevel(Level.ALL);
		mediator = new MediatorStub(logger);
		bus = new ComponentBus(logger);
	}


	@Test
	public void test01() {
		
		ComponentStub starter = new ComponentStub(logger, bus);
		
		EventDrivenWorldViewTester test = 
			new EventDrivenWorldViewTester(
				logger, 
				mediator, 
				new EventDrivenWorldView(new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(starter), bus, logger),
				starter.getController()
			);
		
		// INIT EVENTS

		
		Object[] events = new Object[] {
			new EventBumpedStub(1),
			new EventBumpedStub(2),
			new ObjectPlayerInfoStub(0, 100, 0),
			new ObjectPlayerInfoStub(1, 90, 1),
			new ObjectDestroyedEventStub(ObjectPlayerInfoStub.class, ObjectPlayerInfoStub.ID),			
			new EventChatMessageStub("zak", "hey!"),
			new EventBumpedStub(3),
			new ObjectPlayerInfoStub(1, 70, 2),
			new EventBumpedStub(1),
			new EventBumpedStub(2),
			new EventChatMessageStub("zak", "hey!"),
			new EventBumpedStub(3),
			new ObjectItemStub("item1", "Apple", 1, 0),
			new ObjectItemStub("item2", "Pear", 1, 0),
			new ObjectItemStub("item1", "Apple", 2, 1),
			new ObjectItemStub("item2", "Pear", 2, 1),
			new ObjectPlayerInfoStub(0, 100, 3),
			new ObjectPlayerInfoStub(1, 90, 4),
			new EventBumpedStub(1),
			new EventBumpedStub(2),
			new EventChatMessageStub("zak", "hey!"),
			new EventBumpedStub(3),
			new ObjectPlayerInfoStub(1, 70, 5),
			new EventBumpedStub(1),
			new EventBumpedStub(2),
			new EventBumpedStub(3),
			new ObjectItemStub("item1", "Apple", 1, 2),
			new ObjectItemStub("item2", "Pear", 1, 2),
			new ObjectItemStub("item1", "Apple", 2, 3),
			new ObjectItemStub("item2", "Pear", 2, 3),
			new ObjectDestroyedEventStub(ObjectItemStub.class, "item1"),
			new ObjectDestroyedEventStub(ObjectItemStub.class, "item2"),
			new ObjectDestroyedEventStub(ObjectPlayerInfoStub.class, ObjectPlayerInfoStub.ID),
			new ObjectPlayerInfoStub(0, 100, 0),
			new ObjectPlayerInfoStub(1, 90, 1),
			new EventBumpedStub(1),
			new EventBumpedStub(2),
			new EventChatMessageStub("zak", "hey!"),
			new EventBumpedStub(3),
			new ObjectPlayerInfoStub(1, 70, 2),
			new EventBumpedStub(1),
			new EventBumpedStub(2),
			new EventChatMessageStub("zak", "hey!"),
			new EventBumpedStub(3),
			new ObjectItemStub("item1", "Apple", 1, 0),
			new ObjectItemStub("item2", "Pear", 1, 0),
			new ObjectItemStub("item1", "Apple", 2, 1),
			new ObjectItemStub("item2", "Pear", 2, 1),
			new ObjectPlayerInfoStub(0, 100, 3),
			new ObjectPlayerInfoStub(1, 90, 4),
			new EventBumpedStub(1),
			new EventBumpedStub(2),
			new EventChatMessageStub("zak", "hey!"),
			new EventBumpedStub(3),
			new ObjectPlayerInfoStub(1, 70, 5),
			new EventBumpedStub(1),
			new EventBumpedStub(2),
			new EventBumpedStub(3),
			new ObjectItemStub("item1", "Apple", 1, 2),
			new ObjectItemStub("item2", "Pear", 1, 2),
			new ObjectItemStub("item1", "Apple", 2, 3),
			new ObjectItemStub("item2", "Pear", 2, 3),
			new ObjectDestroyedEventStub(ObjectItemStub.class, "item1"),
			new ObjectDestroyedEventStub(ObjectItemStub.class, "item2"),
			new ObjectDestroyedEventStub(ObjectPlayerInfoStub.class, ObjectPlayerInfoStub.ID),
		};

		test.test(events);
	}

	public static void main(String[] args) {

		Test01_EventDrivenWorldView test = new Test01_EventDrivenWorldView();
		test.init();
		test.test01();

	}


}
