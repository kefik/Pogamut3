package cz.cuni.amis.pogamut.ut2004.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import cz.cuni.amis.pogamut.base.agent.impl.AgentId;
import cz.cuni.amis.pogamut.base.communication.connection.impl.socket.SocketConnectionAddress;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base.utils.logging.LogPublisher;
import cz.cuni.amis.pogamut.ut2004.factory.direct.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.server.exception.UCCStartException;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.flag.Flag;

/**
 * Wrapper of running instance of UCC server. Implements pooling of instances.
 * Usage scenario is:
 * <code>
 * UCCWrapper ucc = UCCWrapper.create();
 * ...
 * ucc.release();
 * </code>
 * The location of UCC executabe will be determined by an environment variable
 * pogamut.ut2004.home (e.g. c:\Games\UT2004). The property cam be set via <i>java ...
 * -Dpogamut.ut2004.home=c:\Games\UT2004</i>. Another posibility is to set it
 * by code <code>System.setProperty("pogamut.ut2004.home", "c:\\Unreal Anthology\\UT2004");</code>.
 * 
 * @author Ik
 */
public class UCCWrapper {
	
    /** Loger containing all output from running instance of UCC. */
    protected LogCategory uccLog;
    protected static int fileCounter = 0;
    Process uccProcess = null;
    /** Port for bots. */
    protected int gbPort = -1;
    /** Port for server connection. */
    protected int controlPort = -1;
    /** Port for observer connection. */
    protected int observerPort = -1;
    protected IUT2004Server utServer = null;
    /** First port assigned to a ucc instance. */
    protected static final int basePort = 39782;
    protected static Integer nextUccWrapperUID = 0;
    /** ID of the wrapper object. Useful for debuging. */
    protected int uccWrapperUID = 0;
    protected UCCWrapperConf conf = null;
    
    /** 
     * Tells whether UCC is in "IN END GAME" phase, which indicates that the FIRST MATCH has ended. DOES NOT WORK WHEN UCC IS USING MAP-SWITCHES! 
     * Determined by {@link UCCWrapperPatterns#getGameEndingPattern()}.
     **/
    protected Flag<Boolean> gameEnding = new Flag<Boolean>(false);
    //protected String mapToLoad

    /**
     * @return Log with output of UCC. If you want to listen also for messages 
     * from the startup sequence then use UCCWrapper.create(Logger parent). Set
     * Parent logger of this log and register listeners before creating this
     * instance of UCCWrapper.  
     */
    public Logger getLogger() {
        return uccLog;
    }

    public UCCWrapperConf getConfiguration() {
		return conf;
	}

	/**
     * @return Server connected to this UCC instance.
     */
    @SuppressWarnings("unchecked")
	public IUT2004Server getUTServer() {
        stopCheck();
        if (utServer == null) {
            UT2004ServerFactory factory = new UT2004ServerFactory();
            UT2004ServerRunner serverRunner = new UT2004ServerRunner(factory, "NBUTServer", "localhost", controlPort);
            utServer = serverRunner.startAgent();
        }
        return utServer;
    }

    protected String getUnrealHome() {
        if (conf.getUnrealHome() == null) {
            return Pogamut.getPlatform().getProperty(PogamutUT2004Property.POGAMUT_UNREAL_HOME.getKey());
        } else {
            return conf.getUnrealHome();
        }
    }

    public UCCWrapper(UCCWrapperConf configuration) throws UCCStartException {
    	uccLog = new LogCategory("Wrapper");
    	uccLog.addHandler(new LogPublisher.ConsolePublisher(new AgentId("UCC")));
    	if (configuration.log != null) {
            uccLog.setParent(configuration.log);
        }
        this.conf = configuration;
        uccWrapperUID = nextUccWrapperUID++;
        initUCCWrapper();
        Runtime.getRuntime().addShutdownHook(shutDownHook);
    }
    /**
     * Task that will kill the UCC process when user forgets to do so.
     */
    Thread shutDownHook = new Thread("UCC wrapper finalizer") {

        @Override
        public void run() {
            if (uccProcess != null) uccProcess.destroy();
        }
    };

    /**
     * Reads content of the stream and discards it.
     */
    protected class StreamSink extends Thread {

        protected InputStream os = null;

        public StreamSink(InputStream os) {
            setName("UCC Stream handler");
            this.os = os;
        }

        protected void handleInput(String str) {
            if (uccLog.isLoggable(Level.INFO)) uccLog.info("ID" + uccWrapperUID + " " + str);
        }

        @Override
        public void run() {
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(os));

            String s = null;
            try {
                while ((s = stdInput.readLine()) != null) {
                    handleInput(s);
                }
                os.close();
            } catch (IOException ex) {
                // the process has been closed so reading the line has failed, 
                // don't worry about it
                //ex.printStackTrace();
            }
        }
    }

    /**
     * Scanns the output of UCC for some specific srings (Ports bounded. START MATCH). 
     */
    public class ScannerSink extends StreamSink {

        public long startingTimeout = 2 * 60 * 1000;
        /** Exception that ended the startig. Should be checked after the latch is raised. */
        public UCCStartException exception = null;

        public ScannerSink(InputStream is) {
            super(is);
            timer.schedule(task = new TimerTask() {

                @Override
                public void run() {
                    exception = new UCCStartException("Starting timed out. Ports weren't bound in the required time (" + startingTimeout + " ms).", this);
                    timer.cancel();
                    portsBindedLatch.countDown();
                }
            }, startingTimeout);
        }
        public CountDownLatch portsBindedLatch = new CountDownLatch(1);
        public int controlPort = -1;
        public int botsPort = -1;
        /**
         * Thread that kills ucc process after specified time if the ports aren't 
         * read from the console. This prevents freezing the ScannerSink when ucc
         * fails to start.
         */
        Timer timer = new Timer("UCC start timeout");
        TimerTask task = null;
        Matcher matcher;
        
        @Override
        protected void handleInput(String str) {
            super.handleInput(str);
            //System.out.println("UCCPRNT " + str);
            if (portsBindedLatch.getCount() != 0) {
                // ports still haven't been found, try to scan the line                
                if (conf.getPatterns().getObserverPortPattern() != null) {
                	matcher = conf.getPatterns().getObserverPortPattern().matcher(str);
                	if (matcher.find()) {
                		observerPort = Integer.parseInt(matcher.group(1));
                	}
                }
                if (conf.getPatterns().getControlPortPattern() != null) {
                	matcher = conf.getPatterns().getControlPortPattern().matcher(str);
                	if (matcher.find()) {
                		controlPort = Integer.parseInt(matcher.group(1));
                	}
                }
                if (conf.getPatterns().getBotPortPattern() != null) {
	                matcher = conf.getPatterns().getBotPortPattern().matcher(str);
	                if (matcher.find()) {
	                    botsPort = Integer.parseInt(matcher.group(1));
	                    raiseLatch();
	                }
                }
                if (conf.getPatterns().getCommandletNotFoundPattern() != null) {
	                matcher = conf.getPatterns().getCommandletNotFoundPattern().matcher(str);
	                if (matcher.find()) {
	                    exception = new UCCStartException("UCC failed to start due to: Commandlet server not found.", this);
	                    raiseLatch();
	                }
                }
                if (conf.getPatterns().getMapNotFoundPattern() != null) {
	                matcher = conf.getPatterns().getMapNotFoundPattern().matcher(str);
	                if (matcher.find()) {
	                    exception = new UCCStartException("UCC failed to start due to: Map not found.", this);
	                    raiseLatch();
	                }
                }
                
                if (conf.getPatterns().getExitingErrorPattern() != null) {
	                matcher = conf.getPatterns().getExitingErrorPattern().matcher(str);
	                if (matcher.find()) {
	                	exception = new UCCStartException("UCC failed to start to due to some error.", this);
	                	raiseLatch();
	                }
                }

                if (conf.getPatterns().getMatchStartedPattern() != null) {
	                matcher = conf.getPatterns().getMatchStartedPattern().matcher(str);
	                if (matcher.find()) {
	                    // The match has started, raise the latch
	                    raiseLatch();
	                }
                } else {
                	exception = new UCCStartException("conf.getPatterns().getMatchStartedPattern() is NULL, there is no way how to recognize successful UCC startup.", this);
                	raiseLatch();
                }
            } else {
            	if (conf.getPatterns().getGameEndingPattern() != null) {
            		matcher = conf.getPatterns().getGameEndingPattern().matcher(str);
            		if (matcher.find()) {
            			gameEnding.setFlag(true);
            		}            		
            	}
            }

        }

        protected void raiseLatch() {
            timer.cancel();
            task.cancel();
            portsBindedLatch.countDown();
        }
    }
    public static long stamp = System.currentTimeMillis();

    protected void initUCCWrapper() throws UCCStartException {
    	boolean exception = false;
        try {
            // start new ucc instance
            String id = System.currentTimeMillis() + "a" + fileCounter++;
            String fileWithPorts = "GBports" + id;
            String uccHomePath = getUnrealHome();
            String systemDirPath = uccHomePath + File.separator + "System" + File.separator;

            // default ucc executable for Windows
            String uccFile = "ucc.exe";

            // determine OS type, if it isn't win then add option to ucc 
            String options = "";
            if (!System.getProperty("os.name").contains("Windows")) {
                options = " -nohomedir";
                uccFile = "ucc";
                if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                    uccFile = "ucc-bin";
                    if (System.getProperty("os.arch").toLowerCase().contains("amd64")) {
                        Logger.getLogger("UCCWrapper").info("64bit arch detected (os.arch property contains keyword amd64). Using 64bit binarry.");
                        uccFile += "-linux-amd64";
                    }
                }
            }

            String execStr = "\"" + systemDirPath + uccFile + "\"";
            String portsSetting = conf.startOnUnusedPort ? "?PortsLog=" + fileWithPorts + "?bRandomPorts=true" : "";
            String playerPortSetting = conf.playerPort != -1 ? "-port=" + conf.playerPort : "";

            String parameter = conf.mapName
                    + "?game=" + conf.gameBotsPack + "." + conf.gameType
                    + portsSetting + conf.options + options;

            ProcessBuilder procBuilder = new ProcessBuilder(execStr, "server", parameter, playerPortSetting);
            procBuilder.directory(new File(systemDirPath));

            uccProcess = procBuilder.start();
            ScannerSink scanner = new ScannerSink(uccProcess.getInputStream());
            scanner.start();
            new StreamSink(uccProcess.getErrorStream()).start();

            scanner.portsBindedLatch.await(3, TimeUnit.MINUTES);
            if (scanner.exception != null) {
                // ucc failed to start 
                try {
                	uccProcess.destroy();
                } catch (Exception e) {                	
                }
                uccProcess = null;
                throw scanner.exception;
            }
            if (scanner.portsBindedLatch.getCount() > 0) {
            	scanner.interrupt();
            	try {
            		uccProcess.destroy();
            	} catch (Exception e) {            		
            	}
            	uccProcess = null;
            	throw new UCCStartException("UCC did not start in 3 minutes, timeout.", this);
            }

            controlPort = scanner.controlPort;
            gbPort = scanner.botsPort;
        } catch (InterruptedException ex1) {
        	exception = true;
            throw new UCCStartException("Interrupted.", ex1);          
        } catch (IOException ex2) {
        	exception = true;
            throw new UCCStartException("IO Exception.", ex2);
        } catch (UCCStartException ex3) {
        	exception = true;
        	throw ex3;
        } catch (Exception ex3) {
        	exception = true;
            throw new UCCStartException("Exception.", ex3);
        } finally {
        	if (exception) {
        		try {
        			stop();
        		} catch (Exception e){        			
        		}
        	}
        }
    }

    /**
     * Process of the
     * @return
     */
    public Process getProcess() {
        return uccProcess;
    }
    /** Was this instance already released? */
    protected boolean stopped = false;

    /**
     * Stops the UCC server.
     */
    public synchronized void stop() {
        stopped = true;
        if (uccProcess != null) {
        	uccProcess.destroy();
        	Runtime.getRuntime().removeShutdownHook(shutDownHook);
        	uccProcess = null;
        	try {
				Thread.sleep(1000);
				// give the process some time to terminate
			} catch (InterruptedException e) {

			}
        }
    }

    /**
     * @return Port for GameBots connection.
     */
    public int getBotPort() {
        stopCheck();
        return gbPort;
    }
    
    /**
     * @return Port of the Observer of GameBots2004.
     */
    public int getObserverPort() {
    	stopCheck();
    	return observerPort;
    }

    /**
     * @return Port for control connection.
     */
    public int getControlPort() {
        stopCheck();
        return controlPort;
    }

    protected void stopCheck() {
        if (stopped) {
            throw new PogamutException("UCC already stopped.", this);
        }
    }

	public String getHost() {
		return "localhost";
	}
	
	public SocketConnectionAddress getBotAddress() {
		if (getBotPort() <= 0) {
			throw new RuntimeException("Bot port is unavailable, wrong bot-port matching pattern?");
		}
		return new SocketConnectionAddress(getHost(), getBotPort());
	}
	
	public SocketConnectionAddress getServerAddress() {
		if (getControlPort() <= 0) {
			throw new RuntimeException("Control port is unavailable, wrong control-port matching pattern?");
		}
		return new SocketConnectionAddress(getHost(), getControlPort());
	}
	
	public SocketConnectionAddress getObserverAddress() {
		if (getObserverPort() <= 0) {
			throw new RuntimeException("Observer port is unavailable, wrong observer-port matching pattern?");
		}
		return new SocketConnectionAddress(getHost(), getObserverPort());
	}

	/**
	 * Returns flag that is indicating FIRST GAME end.
	 * @return
	 */
	public Flag<Boolean> getGameEnding() {
		return gameEnding;
	}
	
}
