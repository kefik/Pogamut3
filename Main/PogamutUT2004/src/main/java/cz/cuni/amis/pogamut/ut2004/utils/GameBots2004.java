package cz.cuni.amis.pogamut.ut2004.utils;

import java.io.File;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.drawing.UT2004ServerProvider;
import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.factory.guice.remoteagent.UT2004ServerModule;
import cz.cuni.amis.pogamut.ut2004.server.impl.UT2004Server;

/**
 * This class is pairing {@link UCCWrapper} and {@link UT2004Server} via composition, use it like this:
 * a) call {@link #start(File, UCCGameType, String)}  
 * b) it start an instance of UCC,
 * c) have UT2004Server auto-connect it,
 * d) then it spawns a thread that will call {@link IGameBots2004Task#start(UCCWrapper, UT2004Server)}.
 * 
 * If you call {@link #stop()} then, it will inform you via {@link IGameBots2004Task#end()} that you should exit the {@link IGameBots2004Task#start(UCCWrapper, UT2004Server)};
 * but it is not necessary to keep the thread running, it's just for you convenience if you need it...
 * 
 * @author Jimmy
 */
public class GameBots2004 {
	
	public static interface IGameBots2004Task {
	
		public void start(UCCWrapper ut2004, UT2004Server server);
		
		public void end();
		
	}

	private UCCWrapper uccWrapper;

	private UT2004ServerProvider serverProvider;
	
	private UT2004Server server;
	
	private Thread thread;

	private IGameBots2004Task task;
	
	public GameBots2004(IGameBots2004Task task) {
		this.task = task;
	}
	
	/**
	 * Starts UCC assuming UT2004 installation is at path 'ut2004Home' starting 'gameType' on 'mapName'.
	 * @param ut2004Home
	 * @param gameType
	 * @param mapName
	 */
	public synchronized void start(File ut2004Home, UCCGameType gameType, String mapName) {
		if (uccWrapper != null) {
			throw new RuntimeException("Cannot be start() as it is already running.");
		}
		UCCWrapperConf conf = new UCCWrapperConf();		
		conf.setUnrealHome(ut2004Home.getAbsolutePath()).setGameType(gameType).setMapName(mapName);
		start(conf);
	}	
	
	public synchronized void start(UCCWrapperConf conf) {
		this.uccWrapper = new UCCWrapper(conf);
		
		UT2004AgentParameters params = new UT2004AgentParameters();
		params.setWorldAddress(new SocketConnectionAddress(uccWrapper.getHost(), uccWrapper.getControlPort()));
		UT2004ServerModule module = new UT2004ServerModule<UT2004AgentParameters>();
		UT2004ServerFactory factory = new UT2004ServerFactory(module);
		UT2004ServerRunner runner = new UT2004ServerRunner(factory);
		runner.setLogLevel(Level.SEVERE);
		server = (UT2004Server) runner.startAgents(params).get(0);
		
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				task.start(uccWrapper, server);
			}
			
		}, "GameBots2004-Thread");
		thread.start();
	}
	
	public synchronized void stop() {
		if (uccWrapper == null) return;

		try {
			task.end();
		} catch (Exception e) {			
		}
		
		try {
			thread.interrupt();
			thread.join(5000);
		} catch (Exception e) {			
		}
		thread = null;
		
		try {
			server.stop();
		} catch (Exception e) {
		}
		server = null;
		
		try {
			uccWrapper.stop();
		} catch (Exception e) {			
		}
		uccWrapper = null;
	}
	
}
