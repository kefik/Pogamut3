package cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.model.VisibilityLocation;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.visibility.model.VisibilityMatrix;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.FastTrace;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.GetAllInvetories;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.GetAllNavPoints;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FastTraceResponse;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.amis.utils.Tuple3;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.maps.MapWithKeyListeners;
import cz.cuni.amis.utils.maps.MapWithKeyListeners.KeyCreatedEvent;
import cz.cuni.amis.utils.token.Token;
import cz.cuni.amis.utils.token.Tokens;

/**
 * Object that is used to extract visibility information out of any UT2004 map via GameBots2004.
 * 
 * Just fire-up GB2004 server (having BIG NUMBER for time level in GameBots2004.ini)
 * and executes {@link VisibilityCreator#main(String[])}.
 * 
 * @author Jimmy
 */
public class VisibilityCreator {
	
	/**
	 * Min distance between {@link VisibilityLocation}s on one {@link NavPointNeighbourLink}.
	 */
	public static final int MATRIX_DENSITY = 100;

	/**
	 * Second trace delta.
	 */
	public static final Location SECOND_TRACE_DELTA = new Location(0, 0, 50);

	/**
	 * How many threads/fast traces to use for visibility-checking.
	 * Note that 10 is reasonable count, UT2004 server won't answer more parallel requests anyway.
	 */
	public static final int THREAD_COUNT = 10;
	
	private UT2004Server server;
	
	private LogCategory log;

	public VisibilityCreator() {
		UT2004ServerModule serverModule = new UT2004ServerModule();
		UT2004ServerFactory serverFactory = new UT2004ServerFactory(serverModule);
		UT2004ServerRunner serverRunner = new UT2004ServerRunner(serverFactory);
		this.server = (UT2004Server) serverRunner.startAgent();	
		this.log = server.getLogger().getCategory(getClass().getSimpleName());
		this.log.setLevel(Level.INFO);
	}
	
	public VisibilityCreator(UT2004Server server) {
		this.server = server;
		this.log = server.getLogger().getCategory(getClass().getSimpleName());
		this.log.setLevel(Level.INFO);
	}

	public UT2004Server getServer() {
		return server;
	}

	public LogCategory getLog() {
		return log;
	}

	public synchronized VisibilityMatrix create() {
		
		log.info("CREATING VISIBILITY MATRIX");
		
		StopWatch watch = new StopWatch();		
		
		checkServer();
		
		log.info("Time: " + watch.checkStr());
		
		MapPointListObtained mapPoints = obtainMapPoints();
		
		log.info("Time: " + watch.checkStr());
		
		List<Tuple3<Location, NavPoint, NavPointNeighbourLink>> locations = generateLocations(mapPoints);
		
		log.info("Time: " + watch.checkStr());
		
		VisibilityMatrix matrix = precreateMatrix(locations);
		
		log.info("Time: " + watch.checkStr());
		
		fillMatrix(matrix);
		
		log.info("Time: " + watch.checkStr());
		
		log.info("Visibility stats: " + matrix.getVisiblePairCount() + " / " + matrix.getPairCount() + " visible pairs");
		
		return matrix;
	}
	
	public VisibilityMatrix createAndSave(File targetDirectory) {
		VisibilityMatrix matrix = create();
		
		log.info("SAVING VISIBILITY MATRIX");
		
		StopWatch watch = new StopWatch();
		
		save(matrix, targetDirectory);
		
		log.info("Time: " + watch.checkStr());
		
		return matrix;
	}
		
	//
	// CHECKING SERVER
	//

	private void checkServer() {
		log.info("Checking server...");
		if (!server.inState(IAgentStateUp.class)){
			log.info("Server is not running, starting server...");
			server.start();
			log.info("Server started.");
		} else {
			log.info("Server running.");
		}
		log.info("Map name: " + server.getMapName());
	}

	//
	// OBTAINING NAVPOINTS
	//
	
	private MapPointListObtained mapPointsTemp;
	private BusAwareCountDownLatch mapPointsLatch;
	
	private IWorldEventListener<MapPointListObtained> mapPointsListener = new IWorldEventListener<MapPointListObtained>() {

		@Override
		public void notify(MapPointListObtained event) {
			VisibilityCreator.this.mapPointsTemp = event;
			server.getWorldView().removeEventListener(MapPointListObtained.class, this);
			mapPointsLatch.countDown();
		}
		
	};

	private MapPointListObtained obtainMapPoints() {
		log.info("Getting navpoints...");
		
		mapPointsLatch = new BusAwareCountDownLatch(1, server.getEventBus(), server.getWorldView());		
		server.getWorldView().addEventListener(MapPointListObtained.class, mapPointsListener);
		server.getAct().act(new GetAllNavPoints());
		server.getAct().act(new GetAllInvetories());
		
		mapPointsLatch.await();
		mapPointsLatch = null;
		
		log.info("Navpoints obtained, total " + mapPointsTemp.getNavPoints().size() + " navpoints in map.");
		
		return mapPointsTemp;
	}

	// 
	// GENERATING LOCATIONS
	// 
	
	private List<Tuple3<Location, NavPoint, NavPointNeighbourLink>> generateLocations(MapPointListObtained mapPoints) {
		log.info("Generating visibility locations...");
		
		if (mapPoints.getNavPoints() == null || mapPoints.getNavPoints().size() == 0) {
			throw new RuntimeException("No navpoints in map?");
		}
		
		Set<NavPoint> finished = new HashSet<NavPoint>();
		
		Set<NavPoint> pending = new HashSet<NavPoint>();
		pending.addAll(mapPoints.getNavPoints().values());
		
		List<Tuple3<Location, NavPoint, NavPointNeighbourLink>> result = new ArrayList<Tuple3<Location, NavPoint, NavPointNeighbourLink>>(mapPoints.getNavPoints().size()*3);
		
		while (pending.size() > 0) {
			NavPoint from = pending.iterator().next();
			pending.remove(from);
			
			// ADD NAVPOINT as VISIBILITY POINT 
			result.add(new Tuple3<Location, NavPoint, NavPointNeighbourLink>(from.getLocation(), from, null));
			
			for (NavPointNeighbourLink link : from.getOutgoingEdges().values()) {
				NavPoint to = link.getToNavPoint();
				if (finished.contains(to)) continue;
				
				List<Location> locations = getLocationsBetween(from, to);				
				for (Location location : locations) {
					result.add(new Tuple3<Location, NavPoint, NavPointNeighbourLink>(location, null, link));
				}
			}				
			
			finished.add(from);
		}
		
		log.info("Done, generated " + (result.size()) + " unique locations for visibility matrix.");
		log.info("Matrix " + result.size() + "x" + result.size() + " == " + (result.size()*result.size()) + " bits == " + (result.size()*result.size()/8) + " bytes.");
		
		return result;
	}
	
	/**
	 * Add locations between 'from' and 'to' ... excluding points 'from' and 'to'.
	 * @param from
	 * @param to
	 * @return
	 */
	private List<Location> getLocationsBetween(NavPoint from, NavPoint to) {
		double distance = from.getLocation().getDistance(to.getLocation());
		int parts = ((int)Math.round(distance)) / MATRIX_DENSITY;
		
		if (parts <= 0) {
			// NAVPOINTS ARE TOO CLOSE TO EACH OTHER
			return new ArrayList<Location>(0);
		}
		
		double oneLength = distance / ((double)parts);
		Location vector = to.getLocation().sub(from.getLocation()).getNormalized().scale(oneLength);
		
		List<Location> result = new ArrayList<Location>(parts-1);
		
		// DO NOT ADD "from" LOCATION as that is added separately in generateLocations()
		//result.add(from.getLocation());
		
		// ADD PARTS IN-BETWEEN from / to
		for (int i = 1; i < parts; ++i) {
			result.add(from.getLocation().add(vector.scale(i)));
		}
		
		// DO NOT ADD "to" LOCATION as that will be added when "TO" navpoint will be examined
		//result.add(to.getLocation());
		
		return result;
	}

	//
	// CREATING MATRIX
	//
	
	private VisibilityMatrix precreateMatrix(List<Tuple3<Location, NavPoint, NavPointNeighbourLink>> locations) {
		
		log.info("Pre-creating visibility matrix, filling visibility-locations...");
		
		VisibilityMatrix result = new VisibilityMatrix(server.getMapName(), locations.size());
		
		int i = 0;
		for (Tuple3<Location, NavPoint, NavPointNeighbourLink> location : locations) {
			Location loc = location.getFirst();
			NavPoint np = location.getSecond();
			NavPointNeighbourLink link = location.getThird();
			
			VisibilityLocation vLoc = new VisibilityLocation();
			vLoc.x = loc.x;
			vLoc.y = loc.y;
			vLoc.z = loc.z;
			
			if (np != null) {
				vLoc.navPoint = np;
				vLoc.navPoint1Id = getNavPointId(server.getMapName(), np);
			} else
			if (link != null) {
				vLoc.link = link;
				vLoc.navPoint1Id = getNavPointId(server.getMapName(), link.getFromNavPoint());
				vLoc.navPoint2Id = getNavPointId(server.getMapName(), link.getToNavPoint());
			} else {
				throw new RuntimeException("Tuple3 has neither NavPoint nor Link information, invalid.");
			}
			
			result.getLocations().put(i, vLoc);
			
			++i;
		}
		
		log.info("Visibility matrix pre-created.");
		
		return result;
	}

	private String getNavPointId(String mapName, NavPoint np) {
		String id = np.getId().getStringId();
		String result = id.substring(mapName.length()+1); 
		return result;
	}
	
	//
	// FILLING MATRIX
	// 
	
	private int totalRaycast;
	private int jobsGenerated;
	private FlagInteger jobsCompleted = new FlagInteger(0);
	private MapWithKeyListeners<Token, FastTraceResponse> fastTraceResponses;
	private VisibilityMatrix matrix;
	
	private void fillMatrix(VisibilityMatrix matrix) {
		
		log.info("Gathering visibility information...");
		
		this.matrix = matrix; 
		jobsGenerated = 0;
		jobsCompleted.setFlag(0);
		fastTraceResponses = new MapWithKeyListeners<Token, FastTraceResponse>();
		
		totalRaycast = (matrix.getLocations().size()) * (matrix.getLocations().size()-1) / 2;
		
		log.info("Estimated number of raycasts to perform: " + totalRaycast);

		log.info("Registering FastTraceResponse listener...");
		server.getWorldView().addEventListener(FastTraceResponse.class, fastTraceListener);
		
		try {
		
			log.info("Starting thread pool executor...");
			
			ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_COUNT, THREAD_COUNT, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1000));
			
			try {
				log.info("Generating jobs...");
				
				int locations = matrix.getLocations().size();
				for (int i = 0; i < locations; ++i) {
					
					synchronized(matrix) {
						matrix.getMatrix().set(i, i); // DIAGONAL
					}
					
					for (int j = i+1; j < locations; ++j) {
						TraceJob job = new TraceJob(i, matrix.getLocation(i), j, matrix.getLocation(j));	
						++jobsGenerated;
						if (jobsGenerated % 1000 == 0) {
							log.info("Generated " + jobsGenerated + " / " + totalRaycast + " jobs generated...");
						}
						
						// SUBMIT THE JOB
						while (true) {
							try {
								executor.execute(job);
								// JOB ACCEPTED
								break;
							} catch (RejectedExecutionException e1) {
								// BUFFER IS FULL 
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e2) {
									throw new RuntimeException("Interrupted while asleep.", e2);
								}
							} 
						}	
					}
				}
				
				log.info("Generated all " + jobsGenerated + " jobs, waiting their completion...");
				
				jobsCompleted.waitFor(new Integer[]{jobsGenerated});
				
				log.info("All " + jobsGenerated + " jobs finished!");
				
			} finally {
				executor.shutdownNow();
			}
			
		} finally {
			try {
				log.info("Removing FastTraceResponse listener...");
			} finally {
				try {
					server.getWorldView().removeEventListener(FastTraceResponse.class, fastTraceListener);
				} finally {
					this.matrix = null;
				}
			}
		}
		
		log.info("Visibility information gathered.");	
	}
	
	private class TraceJob implements Runnable {
		
		private VisibilityLocation from;
		private VisibilityLocation to;
		private int indexFrom;
		private int indexTo;

		public TraceJob(int indexFrom, VisibilityLocation from, int indexTo,  VisibilityLocation to) {
			this.indexFrom = indexFrom;
			this.from = from;
			this.indexTo = indexTo;
			this.to = to;
		}
		
		@Override
		public void run() {
			// SET AS NOT-VISIBLE
			synchronized(matrix) {
				matrix.getMatrix().unset(indexFrom, indexTo);
				matrix.getMatrix().unset(indexTo, indexFrom);
			}
			
			final Token id1 = getNextFastTraceId();
			final Token id2 = getNextFastTraceId();
			
			final BusAwareCountDownLatch latch = new BusAwareCountDownLatch(2, server.getEventBus(), server.getWorldView()); 
			
			final FastTrace fastTrace1 = new FastTrace().setId(id1.getToken()).setFrom(from.getLocation()).setTo(to.getLocation());
			final FastTrace fastTrace2 = new FastTrace().setId(id2.getToken()).setFrom(from.getLocation().add(SECOND_TRACE_DELTA)).setTo(to.getLocation().add(SECOND_TRACE_DELTA));
			
			MapWithKeyListeners.IKeyCreatedListener<Token, FastTraceResponse> listener = new MapWithKeyListeners.IKeyCreatedListener<Token, FastTraceResponse>() {
				@Override
				public void notify(KeyCreatedEvent<Token, FastTraceResponse> event) {
					synchronized(fastTraceResponses) {
						fastTraceResponses.remove(event.getKey());
					}
					if (!event.getValue().isResult()) {
						synchronized(matrix) {
							// no hit == VISIBLE!!!
							matrix.getMatrix().set(indexFrom, indexTo);
							matrix.getMatrix().set(indexTo, indexFrom);
						}
						while (latch.getCount() > 0) latch.countDown();
					} else {
						latch.countDown();
						if (latch.getCount() > 0) {
							// try to perform second trace to validate NON-VISIBILITY
							server.getAct().act(fastTrace2);
						}
					}					
				}
			};
			
			fastTraceResponses.addWeakListener(id1, listener);
			fastTraceResponses.addWeakListener(id2, listener);
			
			// TRACE 1 ... second one is made only if first indicates "not visible"
			server.getAct().act(fastTrace1);			
			
			latch.await();
			
			fastTraceResponses.removeListener(id1, listener);
			fastTraceResponses.removeListener(id2, listener);
			
			returnId(id1);
			returnId(id2);
			
			jobsCompleted.increment(1);
			int num = jobsCompleted.getFlag();
			if (num % 50 == 0) {
				log.info("Raycast " + num + " / " + totalRaycast);
			}
			if (num % 1000 == 0) {
				log.info("Existing IDs: " + nextId);
			}
		}
		
	}

	private IWorldEventListener<FastTraceResponse> fastTraceListener = new IWorldEventListener<FastTraceResponse>() {

		@Override
		public void notify(FastTraceResponse event) {
			fastTraceResponses.put(Tokens.get(event.getId()), event);			
		}
		
	};
	
	private Object nextIdMutex = new Object();
	private int nextId = 0;
	private ConcurrentLinkedQueue<Token> availableTokens = new ConcurrentLinkedQueue<Token>();
	
	private Token getNextFastTraceId() {
		try {
			if (availableTokens.size() > 0) {
				return availableTokens.remove();
			}
		} catch (Exception e) {			
		}		
		int myId;
		synchronized(nextIdMutex) {
			myId = nextId++;			
		}
		return Tokens.get("FTID-" + myId);
	}
	
	private void returnId(Token token) {
		availableTokens.add(token);
	}
	
	//
	// SAVING MATRIX
	//
	
	private void save(VisibilityMatrix matrix, File targetDirectory) {
		
		log.info("Saving visibility matrix into: " + targetDirectory.getAbsolutePath());
		
		if (targetDirectory.exists()) {
			if (targetDirectory.isFile()) {
				throw new RuntimeException("'targetDirectory' points to " + targetDirectory.getAbsolutePath() + " which is FILE not DIRECTORY");
			} else
			if (!targetDirectory.isDirectory()) {
				throw new RuntimeException("'targetDirectory' points to " + targetDirectory.getAbsolutePath() + " which is not DIRECTORY");
			}
		} else {
			if (!targetDirectory.mkdirs()) {
				throw new RuntimeException("Failed to create 'targetDirectory' -> " + targetDirectory.getAbsolutePath());
			}
		}
		
		matrix.save(targetDirectory);
		
		log.info("Visibility matrix saved.");
	}

	/**
	 * Initializes {@link VisibilityCreator}, connects it to the localhost:3001 (localhost GB2004 server connection)
	 * and calls {@link VisibilityCreator#createAndSave(File)} method. Note that it may take HUGE AMOUNT of time
	 * to fill up visibility matrix for given level. 
	 * 
	 * Be sure to have "map time" in GameBots2004.ini set to BIG NUMBER.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		VisibilityCreator creator = new VisibilityCreator();
		try {
			creator.createAndSave(new File("."));
		} finally {
			try {
				creator.getServer().stop();
			} finally {
				Pogamut.getPlatform().close();
			}
		}
	}
	
}
