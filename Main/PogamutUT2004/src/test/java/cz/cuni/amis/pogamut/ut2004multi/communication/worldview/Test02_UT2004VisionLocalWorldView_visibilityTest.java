package cz.cuni.amis.pogamut.ut2004multi.communication.worldview;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.communication.mediator.IMediator;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.lifecyclebus.LifecycleBus;
import cz.cuni.amis.pogamut.base.utils.logging.AgentLogger;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.multi.agent.ITeamedAgentId;
import cz.cuni.amis.pogamut.multi.agent.impl.TeamedAgentId;
import cz.cuni.amis.pogamut.multi.communication.worldview.ILocalWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.ISharedWorldView;
import cz.cuni.amis.pogamut.multi.communication.worldview.impl.BatchAwareLocalWorldView;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BeginMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.component.ComponentStub;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestCompositeViewableObject;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.objects.TestCompositeViewableObjectMessage;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.stubs.MediatorStub;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.stubs.UT2004TestLocalWorldView;
import cz.cuni.amis.pogamut.ut2004multi.communication.worldview.stubs.UT2004TestSharedWorldView;
import cz.cuni.amis.tests.BaseTest;

@Ignore
public class Test02_UT2004VisionLocalWorldView_visibilityTest extends BaseTest {

	private static final long GLOBAL_TIMEOUT_IN_MINUTES = 10;

	public static FileHandler fh;
	
	public static Logger global;
	public static List<IAgentLogger> agentLogs;
	
	
	public static ISharedWorldView sharedWV;
	
	
	public static void setLogLevel( Level level)
	{
		global.setLevel(level);
		for ( IAgentLogger log : agentLogs)
		{
			log.setLevel(level);
		}
	}
	
	public static void initSWV()
	{
		try {
			fh = new FileHandler("./log01");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Formatter f = new SimpleFormatter();
		fh.setFormatter(f);
		
		agentLogs = new LinkedList<IAgentLogger>();
		
		global = Logger.getLogger("Global");
		global.setLevel(Level.FINER);
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.FINER);
		global.addHandler( consoleHandler );
		global.addHandler(fh);
		sharedWV = new UT2004TestSharedWorldView(global);
	}
	
	public static class Handler
	{
		Map<IAgentId, Integer> cycles;
		
		public Handler()
		{
			cycles = new HashMap<IAgentId, Integer>();
		}
		
		public void addNew( IAgentId id, int cycles)
		{
			this.cycles.put(id, cycles);
		}
		
		public boolean allFinished()
		{
			for ( Integer i : cycles.values() )
			{
				if ( i >= 0)
				{
					return false;
				}
			}
			return true;
		}
		
		public synchronized void setEnd( IAgentId id)
		{
			this.cycles.put(id, -1);
		}
		
		public  synchronized void decrease( IAgentId id)
		{
			int n = cycles.get(id);
			--n;
			cycles.put(id, n);
		}
		
		public int cyclesToRun( IAgentId id)
		{
			return ( cycles.get(id ));
		}
	}
	
	public static class VisibilityChecker
	{
		public VisibilityChecker(TeamedAgentId agentId)
		{
			this.agentId = agentId;
		}
		
		TeamedAgentId agentId;
		Map<TimeKey, Set<WorldObjectId>> visibleObjects = new WeakHashMap<TimeKey, Set<WorldObjectId>>();
		
		public Set<WorldObjectId> getAllVisible( long time )
		{
			return visibleObjects.get( TimeKey.get(time) );
		}
		
		public void addVisible( WorldObjectId id, long time )
		{
			Set<WorldObjectId> set = visibleObjects.get( TimeKey.get(time));
			if ( set == null )
			{
				set = new HashSet<WorldObjectId>();
				visibleObjects.put(TimeKey.get(time), set);
			}
			set.add(id);
		}
		
	}
	
	public static Random rand = new Random( System.currentTimeMillis() );
	
	/**
	 * Generates a single batch of events on specified time for specified worldView on run();
	 * @author srlok
	 *
	 */
	public static class EventGenerator extends Thread
	{
		int events;
		long time;
		ILocalWorldView localWV;
		TeamedAgentId agentId;
		
		VisibilityChecker checker;
		
		public EventGenerator( int events, long time, ILocalWorldView localWV, VisibilityChecker visChecker)
		{
			this.events = events;
			this.time = time;
			this.localWV = localWV;
			agentId = (TeamedAgentId) localWV.getAgentId();
			this.checker = visChecker;
		}
		
		protected void generateEvents()
		{
			System.out.println(agentId + " : generating Events [Time:" + time + "]" );
			localWV.notify( new BeginMessage( this.time, true ));
			for ( int i = 0; i < events; ++i)
			{
				//if ( (i % 2 == 0) || ((time % 2 == 0) && (i %2 != 0)) )
				if ( rand.nextInt() % 100 > 30)
				{
					WorldObjectId id = WorldObjectId.get("TestObject["+i+"]");
					TestCompositeViewableObject obj = new TestCompositeViewableObjectMessage(id, this.time, "LS:"+agentId.toString()+"["+i+"]"+"("+time+")",
							i+time, "ShS:"+agentId.getTeamId().toString()+"["+i+"]"+"("+time+")",
							i+1000+time, "StaticString["+i+"]" , (long)i, true);
					checker.addVisible( id, this.time );
					localWV.notify(obj.createUpdateEvent(time, agentId.getTeamId()));
				}
			}
			
			localWV.notify( new EndMessage( this.time, true ) );
			System.out.println(agentId + ": generating end");
		}
	
		@Override
		public void run()
		{
			try {
				generateEvents();
			} catch (Exception e) {
				e.printStackTrace();
				failure = true;
				totalCountDown2();
				return;
			}
			latch2.countDown();
		}
	}
	
	public static class LogicRunner extends Thread
	{
		protected BatchAwareLocalWorldView wv;
		protected ITeamedAgentId id;
		
		int runs;
		int objects;
		long sleepTime;
		Handler handler;
		ComponentStub starter;
		VisibilityChecker checker;
		
		
		public LogicRunner(ITeamedAgentId id, int runs, int objects, long sleepTime, Handler handler, VisibilityChecker visChecker)
		{
			IAgentLogger log = new AgentLogger(id);
			agentLogs.add(log);
			log.setLevel(Level.ALL);
			log.addDefaultConsoleHandler();
			log.addDefaultHandler(fh);
			LifecycleBus bus = new LifecycleBus(log);
			starter = new ComponentStub(log,bus );
			IMediator m = new MediatorStub(log);
			try
			{
			wv = new UT2004TestLocalWorldView( new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(starter),
					m, bus, log, sharedWV, id);
			}
			catch (Exception e)
			
			{
				failure = true;
				e.printStackTrace();
			}
			//wv.setInitialTime(TimeKey.get(0));
			
			this.id = id;
			this.runs = runs;
			this.objects = objects;
			this.sleepTime = sleepTime;
			this.handler = handler;
			this.checker = visChecker;
			handler.addNew(id, runs);
		}
		
		public void startWV()
		{			
			starter.getController().manualStart("Test");
		}
		
		public void setSleepTime( long newSleepTime )
		{
			this.sleepTime = newSleepTime;
		}
		
		@Override
		public void run() 
		{
			try {
				System.out.println( id + " : Logic Runner run()");
				for ( int r = 0; r <= runs; ++r)
				{
					if (failure) throw new RuntimeException("FAILURE DETECTED!");
					wv.lock();
					
					//visibilityCheck
					System.out.println( id + "Runner run ["+r+"] remaining : time " + wv.getCurrentTimeKey().getTime() + ", remaining runs " + handler.cyclesToRun(id) );
				
					Set<WorldObjectId> shouldBeVisible = checker.getAllVisible(wv.getCurrentTimeKey().getTime());
					Set<WorldObjectId> areVisible = wv.getAllVisible( TestCompositeViewableObject.class ).keySet();
					
					if (areVisible.containsAll(shouldBeVisible) && (areVisible.size() == shouldBeVisible.size()))
					{
						System.out.println("VisibilityCheck OK ; visible : " + areVisible.size());
					}
					else
					{ 
						System.err.println(" ShouldBeVisible : " + shouldBeVisible.size() + " ; Visible : " + areVisible.size() );
						System.out.println("Should BE VISIBLE :");
						//shouldBeVisible.removeAll(areVisible);
						for ( WorldObjectId id : shouldBeVisible )
						{
							System.out.println(id);
						}
						System.out.println("VISIBLE :");
						for ( WorldObjectId id : areVisible )
						{
							System.out.println(id);
						}
						failure = true;
						Assert.fail("VisibilityFail");
					}
					
					
					this.sleep(sleepTime);
					wv.unlock();
					handler.decrease(id);
				}
			} catch (Exception e) {
				e.printStackTrace();
				failure = true;
				totalCountDown();
				return;
			}
			latch.countDown();
		}
	}
	
	/**
	 * Manages different event generators...
	 * this is responsible for increasing timeKey and generating batches of events for all worldViews.
	 * @author srlok
	 *
	 */
	public static class EventGeneratorHandler extends Thread
	{
		Map<TeamedAgentId, ILocalWorldView> localWorldViews;
		Map<TeamedAgentId, VisibilityChecker> checkers;
		int eventsPerCycle;
		long currentTime;
		Handler handler;
		long sleepTime;
		
		public EventGeneratorHandler( int eventsPerCycle, long initTime, long sleepTime, Handler handlerInstance)
		{
			this.eventsPerCycle = eventsPerCycle;
			this.currentTime = initTime;
			this.handler = handlerInstance;
			this.sleepTime = sleepTime;
			localWorldViews = new HashMap<TeamedAgentId,ILocalWorldView>();
			checkers = new HashMap<TeamedAgentId,VisibilityChecker>();
		}
		
		public void addWorldView( ILocalWorldView wv, VisibilityChecker checker)
		{
			this.localWorldViews.put( (TeamedAgentId)wv.getAgentId() , wv);
			this.checkers.put( (TeamedAgentId)wv.getAgentId(), checker);
		}
		
		@Override
		public void run()
		{
			try {
				while ( !handler.allFinished() )
				{
					if (failure) throw new RuntimeException("FAILURE DETECTED!");
					List<Thread> thrds = new LinkedList<Thread>();
					for ( TeamedAgentId id : localWorldViews.keySet() )
					{
						if ( handler.cyclesToRun(id) >= 0)
						{
							thrds.add( new EventGenerator(eventsPerCycle, currentTime, localWorldViews.get(id), checkers.get(id) ) );
						}
					}
					latch2 = new CountDownLatch(thrds.size());
					for (Thread t : thrds)
					{
						t.start();
					}
					latch2.await(GLOBAL_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);					
					if (latch2.getCount() > 0 || failure) {
						throw new RuntimeException("FAILURE DETECTED!");
					}
					++currentTime;
					sleep(sleepTime);					
				}
			} catch (Exception e) {
				e.printStackTrace();
				failure = true;
				totalCountDown();	
				return;
			}
			latch.countDown();
		}		
		
	}
	
	static boolean failure = false;
	
	static CountDownLatch latch;
	static CountDownLatch latch2;
	
	@Test
	public void simpleTest()
	{
		/*
		initSWV();
		int events = 30; // number of events per batch
		int runs = 150;   // number of logic runs
		long sleepTime = 100; // sleep time between batches (generated)
		long runnerSleepTime = 20; // sleep time inside logic
		Handler hndlr = new Handler();
		List<Thread> thrds = new LinkedList<Thread>();
		int agents = 1;
		latch = new CountDownLatch(agents+1);
		TeamId tId = new TeamId("RED");
		
		EventGeneratorHandler eventHandler = new EventGeneratorHandler(events, 0, sleepTime, hndlr);
		
		StopWatch watch = new StopWatch();
		
		for ( int i = 0; i < agents; ++i )
		{
			TeamedAgentId aId = new TeamedAgentId("Agent["+i+"]");
			aId.setTeamId(tId);
			VisibilityChecker checker = new VisibilityChecker(aId);
			LogicRunner r = new LogicRunner(aId, runs, events, runnerSleepTime,  hndlr, checker);			
			eventHandler.addWorldView(r.wv, checker);
			thrds.add(r);
			r.startWV();
		}
		setLogLevel(Level.FINE);
		for ( Thread t : thrds)
		{
			t.start();
		}
		eventHandler.start();
		
		try {
			
			latch.await(GLOBAL_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
			if (latch.getCount() > 0 || failure) {
				failure = true;
				throw new RuntimeException("FAILURE!!!");
			}
			
		} catch (InterruptedException e) {
			failure = true;
			throw new RuntimeException(e);
		}
		
		System.out.println("Test took: " + watch.stopStr());
		
		thrds = null;
		eventHandler = null;
		sharedWV = null;
		
		try
		{
			//CheckInstances.waitGCTotal();
		}
		catch (Exception e )
		{
			System.out.println("WARNING : ");
			//e.printStackTrace();
		}
		
		System.out.println("---/// TEST OK ///---");
		*/
	}
	

	public static void totalCountDown2() {
		while (latch2.getCount() > 0) latch2.countDown();
	}

	public static void totalCountDown() {
		while (latch.getCount() > 0) latch.countDown();
	}
}
