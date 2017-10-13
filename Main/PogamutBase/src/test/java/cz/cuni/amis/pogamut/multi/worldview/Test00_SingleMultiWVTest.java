package cz.cuni.amis.pogamut.multi.worldview;

import static org.junit.Assert.fail;


import java.util.Map;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.component.stub.component.ComponentStub;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.EventDrivenLocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.EventDrivenSharedWorldView;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKeyManager;
import cz.cuni.amis.pogamut.multi.worldview.objects.CheckInstances;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestCompositeObject;
import cz.cuni.amis.pogamut.multi.worldview.objects.TestCompositeObjectMessage;
import cz.cuni.amis.pogamut.multi.worldview.stub.EventDrivenLocalWorldViewStub;
import cz.cuni.amis.pogamut.multi.worldview.stub.EventDrivenSharedWorldViewStub;
import cz.cuni.amis.tests.BaseTest;

/**
 * NOT WORKING				
 * @author Jimmy
 */
@Ignore
public class Test00_SingleMultiWVTest extends BaseTest {

	public EventDrivenLocalWorldView localWV;
	public EventDrivenSharedWorldView sharedWV;
	public TeamedAgentId agentId;
	public static Logger log;
	public static AgentLogger agentLogger;
	public static Random rand;
	
	static
	{
		rand = new Random(System.currentTimeMillis());
		log = Logger.getLogger("GLOBAL");
		ConsoleHandler h = new ConsoleHandler();
		h.setLevel(Level.FINER);
		log.setLevel(Level.FINER);
		log.addHandler(h );
	}
	
	public void setUp()
	{
		agentId = new TeamedAgentId("Agent01");
		agentId.setTeamId( new TeamId("teamRED"));
		agentLogger = new AgentLogger(agentId);
		LifecycleBus bus = new LifecycleBus(agentLogger);
		
		Logger sharedLogger = Logger.getLogger("SharedLogger");
		sharedLogger.addHandler( new ConsoleHandler() );
		sharedLogger.setLevel( Level.INFO );
		agentLogger.setLevel(Level.INFO);
		agentLogger.addDefaultConsoleHandler();
		
		sharedWV = new EventDrivenSharedWorldViewStub( sharedLogger );
		
		ComponentStub starter = new ComponentStub(agentLogger, bus);
		
		localWV = new EventDrivenLocalWorldViewStub( new ComponentDependencies( ComponentDependencyType.STARTS_WITH ).add(starter), bus, agentLogger, sharedWV, agentId);
		
		starter.getController().manualStart("TEST");
		localWV.setCurrentTime( TimeKey.get(0));
	}

	@Test(timeout=180000)
	public void getAllTest()
	{
		setUp();
		
		int objects = 250;
		
		for( long t = 0; t < 100; ++t )
		{
			System.out.println("Time : " + t);
			//log.severe("Time : " + t );
			
			localWV.lockTime(t);
			
			if ( t >= 4)
			{
				localWV.unlockTime(t-4);
			}
			for ( int i = 0; i < objects; ++i)
			{
				WorldObjectId id = WorldObjectId.get("TestObject["+i+"]");
				TestCompositeObjectMessage message = new TestCompositeObjectMessage(id, t,
						"Local string [" + i +"," + t + "]", i + t, "Shared string [" + i +"s," + t +"]" , (long)200+i+5*t, "Static string " + i, (long)i*100);
				localWV.notify( message.createUpdateEvent(t, agentId.getTeamId() ) );
			}
			localWV.setCurrentTime(TimeKey.get(t));
			
			Map<WorldObjectId, TestCompositeObject> map = localWV.getAll(TestCompositeObject.class);
			
			if ( map.size() != objects)
			{
				fail("MapSize fail");
			}
			
			for ( int i = 0; i < objects; ++i)
			{

				WorldObjectId id = WorldObjectId.get("TestObject["+i+"]");
				TestCompositeObject obj = map.get(id);
				if ( obj == null )
				{
					log.severe( id + " : NULL");
					fail("Object is null.");
				}
				if ( obj.getLocalLong() != i + t)
				{
					log.severe( id + " : " + obj.getLocalLong() + " instead of " + (i + t));
					fail("LocalLong fail");
				}
				if ( !obj.getLocalString().equals("Local string [" + i + "," + t + "]"))
				{
					fail("SharedString fail");
				}
				if ( obj.getSharedLong() != 200+i+5*t )
				{	
					log.severe( id + " : " + obj.getSharedLong() + " instead of " + (200+i+5*t));
					fail("SharedLong fail");
				}
				if ( !obj.getSharedString().equals("Shared string [" + i +"s," + t +"]"))
				{
					fail("SharedString fail : " + obj.getSharedString());
				}
				if ( obj.getStaticLong() != i*100 )
				{
					fail("StaticLong fail");
				}
			}
		}
		
		for ( int dT = 0; dT <4; ++dT )
		{
			localWV.unlockTime(99-dT);
		}
		
		localWV = null;
		sharedWV = null;
		agentId = null;
		
		CheckInstances.waitGCTotal();
	}
	
	@Test(timeout=180000)
	public void shadowCopyTest()
	{
		
		setUp();
		
		agentLogger.setLevel(Level.FINER);
		
		int objects = 200;
		
		int logicCountdown = 2;
		int dT = 2;
		
		
		for( long t = 0; t < 100; ++t )
		{
			System.out.println("ShadowCopyTest : Time : " + t);
			//log.severe("Time : " + t );
			
			localWV.lockTime(t);
			
			if ( t >= 5)
			{
				localWV.unlockTime(t-5);
			}
			for ( int i = 0; i < objects; ++i)
			{
				WorldObjectId id = WorldObjectId.get("TestObject["+i+"]");
				TestCompositeObjectMessage message = new TestCompositeObjectMessage(id, t,
						"Local string [" + i +"," + t + "]", i + t, "Shared string [" + i +"s," + t +"]" , (long)200+i+5*t, "Static string " + i, (long)i*100);
				localWV.notify( message.createUpdateEvent(t, agentId.getTeamId() ) );
			}
			
		
			if ( logicCountdown == 0)
			{	
				localWV.setCurrentTime( TimeKey.get( t-dT ) );
				
				for ( int i = 0; i <objects; ++i)
				{
					
					WorldObjectId id = WorldObjectId.get("TestObject["+i+"]");
					TestCompositeObject obj = (TestCompositeObject)localWV.get(id);
					
					log.fine("Object " + id + " get() successful.");
					
					if ( obj.getSharedLong() != 200+i+5*(t-dT) )
					{					
						log.severe( id + " : " + obj.getSharedLong() + " instead of " + (200+i+5*(t-dT)));
						fail("SharedLong fail");
					}
					if ( obj.getLocalLong() != i + t-dT)
					{
						log.severe( id + " : " + obj.getLocalLong() + " instead of " + (i + t-dT));
						fail("LocalLong fail");
					}
					if ( !obj.getLocalString().equals("Local string [" + i + "," + (t-dT) + "]"))
					{
						fail("SharedString fail");
					}
					
					if ( !obj.getSharedString().equals("Shared string [" + i +"s," + (t-dT) +"]"))
					{
						fail("SharedString fail : " + obj.getSharedString());
					}
					if ( obj.getStaticLong() != i*100 )
					{
						fail("StaticLong fail");
					}
				}
				
				logicCountdown = rand.nextInt(3) + 1;
				dT = logicCountdown;
			}
			--logicCountdown;
		}
		
		for ( dT = 0; dT <5; ++dT )
		{
			localWV.unlockTime(99-dT);
		}
		
		localWV = null;
		sharedWV = null;
		agentId = null;
		
		CheckInstances.waitGCTotal();
	}
	
	@Test(timeout=180000)
	public void simple1000Objects()
	{
		setUp();
		localWV.setCurrentTime(TimeKey.get(0));
		for ( long t = 0; t < 50; ++t)
		{					
			localWV.lockTime(t);
			for (int i = 0; i <1000; ++i)
			{
				WorldObjectId id = WorldObjectId.get("TestObject[" + i + "]");
				TestCompositeObjectMessage message = new TestCompositeObjectMessage(id, t,
						"Local string [" + i +"," + t + "]", i + t, "Shared string [" + i +"s," + t +"]" , (long)200+i+5*t, "Static string " + i, (long)i*100);
				
				localWV.notify( message.createUpdateEvent(t, agentId.getTeamId() ) );
			}
			
			localWV.setCurrentTime(TimeKey.get(t));
			
			for ( int i = 0; i <1000; ++i)
			{
				WorldObjectId id = WorldObjectId.get("TestObject["+i+"]");
				TestCompositeObject obj = (TestCompositeObject)localWV.get(id);
				
				if ( obj.getLocalLong() != i + t)
				{
					fail("LocalLong fail");
				}
				if ( !obj.getLocalString().equals("Local string [" + i + "," + t + "]"))
				{
					fail("SharedString fail");
				}
				if ( obj.getSharedLong() != 200+i+5*t )
				{					
					fail("SharedLong fail");
				}
				if ( !obj.getSharedString().equals("Shared string [" + i +"s," + t +"]"))
				{
					fail("SharedString fail : " + obj.getSharedString());
				}
				if ( obj.getStaticLong() != i*100 )
				{
					fail("StaticLong fail");
				}
			}
			localWV.unlockTime(t);
			System.out.println("SimpleObjectTest : TimeKey " + t + " FINISHED!");
		}	
		
		localWV = null;
		sharedWV = null;
		agentId = null;
		
		CheckInstances.waitGCTotal();
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
	
