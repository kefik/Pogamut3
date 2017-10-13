package cz.cuni.amis.pogamut.multi.worldview;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.EventDrivenLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.EventDrivenSharedWorldView;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKeyManager;
import cz.cuni.amis.pogamut.multi.worldview.objects.CheckInstances;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestCompositeObjectMessage;
import cz.cuni.amis.pogamut.multi.worldview.stub.EventDrivenLocalWorldViewStub;
import cz.cuni.amis.pogamut.multi.worldview.stub.EventDrivenSharedWorldViewStub;
import cz.cuni.amis.tests.BaseTest;
	
/**
 * NOT WORKING				
 * @author Jimmy
 */
@Ignore
public class Test01_SharedWorldViewMultiAgent extends BaseTest {
	
	static EventDrivenSharedWorldView sharedWV;
	static Logger globalLogger;
	
	public static void createShared()
	{
		globalLogger = Logger.getLogger("globalLogger");
		ConsoleHandler handler = new ConsoleHandler();
		globalLogger.addHandler(handler);
		handler.setLevel(Level.INFO);
		//LogFormatter formatter = new LogFormatter();
		//handler.setFormatter(formatter);
		globalLogger.setLevel( Level.INFO );
		sharedWV = new EventDrivenSharedWorldViewStub(globalLogger);
	}
	
	public static class TestAgent implements Runnable
	{

		EventDrivenLocalWorldView localWV;
		ComponentStub starter;
		private ITeamedAgentId agentId;
		
		TestAgent( ITeamedAgentId agentId, ISharedWorldView sharedWV )
		{
			System.out.println("Constructing agent : " + agentId);
			AgentLogger logger = new AgentLogger(agentId);
			logger.setLevel( Level.INFO );
			//UDELAT MANUALNI KOMPONENTU
			logger.addDefaultConsoleHandler();
			LifecycleBus bus = new LifecycleBus(logger);
			starter = new ComponentStub(logger, bus);
			this.localWV = new EventDrivenLocalWorldViewStub( new ComponentDependencies( ComponentDependencyType.STARTS_WITH).add(starter) , bus , logger, sharedWV, agentId);
			localWV.setInitialTime( TimeKey.get(1));
			//sharedWV.addComponentBus(agentId, bus, new ComponentDependencies());
			isRunning = false;
			this.agentId = agentId;
		}
		
		boolean isRunning;
		
		@Override
		public void run() {
			//System.out.println("Agent : " + localWV.getAgentId() + " RUN.");
			if ( !isRunning )
			{
				starter.getController().manualStart("Running agent " + localWV.getAgentId());
				System.out.println(localWV.getAgentId() + " running.");
				isRunning = true;
			}
			
			try
			{
				long time = 1;
				for ( int i = 0; i <500; ++i)
				{
					WorldObjectId id = WorldObjectId.get("TestObject["+i+"]");
					TestCompositeObjectMessage m = new TestCompositeObjectMessage(id, time, "LS[" + i+"]" + agentId.getName() , (long)i,
							"SharedString[" + i +"]" + agentId.getTeamId().toString() , (long)i*1000, "StaticString["+i+"]", (long)i+1000);
					IWorldChangeEvent event = m.createUpdateEvent(1, agentId.getTeamId());
					localWV.notify(event);
					//System.out.println(localWV.getAgentId() + "Event : " + i);
				}
			}
			catch ( Exception e)
			{
				System.out.println("Exception in agent " + localWV.getAgentId() + " : " + e);
			}
			System.out.println("Agent : " + localWV.getAgentId() + " finished.");
		}
	}
	
	@Test(timeout=180000)
	public void agentRunTest()
	{
		createShared();
		TestAgent[] agents = new TestAgent[20];
		for(int i=0;i<20;++i)
		{
			TeamedAgentId agentId = new TeamedAgentId("Agent["+i+"]");
			if (i%2 == 0)
			{
				agentId.setTeamId( new TeamId("Sudy") );
			}
			else
			{
				agentId.setTeamId( new TeamId("Lichy") );
			}
			agents[i] = new TestAgent(agentId, sharedWV);
		}
		Thread[] threads = new Thread[20];
		for ( int i= 0; i <20; ++i)
		{
			System.out.println("Thread " + i + " starting...");
			Thread t = threads[i] = new Thread(agents[i]);
			System.out.println("Thread " + i + "created");
			try
			{
				t.start();
			}
			catch (Exception e)
			{
				System.out.println( "Exception " + e);
			}
			
		}
		
		for (Thread t : threads) {
			while (t.isAlive()) {
				try {				
					t.join(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				CheckInstances.log();
			}
		}
		
		threads = null;	
		agents = null;
		sharedWV = null;
		
		try
		{
			CheckInstances.waitGCTotal();
		}
		catch (Exception e )
		{
			System.out.println("WARNING : ");
			//e.printStackTrace();
		}
		System.out.println("---/// TEST OK ///---");
	}
	
	@After
	public void afterTest() {
		try {
			TimeKeyManager.get().unlockAll();
		} catch (Exception e) {			
		}
		try {
			TimeKey.clear();
		} catch (Exception e) {			
		}
	}
	
}
