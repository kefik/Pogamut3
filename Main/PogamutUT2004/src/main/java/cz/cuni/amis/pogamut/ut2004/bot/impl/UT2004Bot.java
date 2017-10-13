package cz.cuni.amis.pogamut.ut2004.bot.impl;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.google.inject.Inject;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.java.ReflectionObjectFolder;
import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.agent.jmx.AgentJMXComponents;
import cz.cuni.amis.pogamut.base.agent.jmx.adapter.AgentMBeanAdapter;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateGoingUp;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReact;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base3d.agent.AbstractAgent3D;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.ut2004.agent.module.utils.ProjectileCleanUp;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.navmesh.old.OldNavMesh;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.bot.jmx.BotJMXMBeanAdapter;
import cz.cuni.amis.pogamut.ut2004.bot.params.UT2004BotParameters;
import cz.cuni.amis.pogamut.ut2004.bot.state.impl.BotStateHelloBotReceived;
import cz.cuni.amis.pogamut.ut2004.bot.state.impl.BotStateInited;
import cz.cuni.amis.pogamut.ut2004.bot.state.impl.BotStatePassword;
import cz.cuni.amis.pogamut.ut2004.bot.state.impl.BotStateSendingInit;
import cz.cuni.amis.pogamut.ut2004.bot.state.impl.BotStateSpawned;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.DisconnectBot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.PasswordReply;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Ready;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Respawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HelloBotHandshake;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.LocationUpdate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Password;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.SelfMessage;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.InitCommandRequest;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.ReadyCommandRequest;
import cz.cuni.amis.pogamut.ut2004.utils.PogamutUT2004Property;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.exception.PogamutJMXException;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Ancestor of all UT2004 bots.
 * <p><p>
 * Contains {@link #locationUpdateMessageListener} that auto-updates {@link Self} message with newer location/rotation/velocity info. This means, that {@link Self} updates
 * comes more often then in &lt;3.7.0 versions of Pogamut.
 * <p><p>
 * TODO: [comment me!]
 *
 * @author Jimmy
 */
@AgentScoped
public class UT2004Bot<WORLD_VIEW extends IVisionWorldView, ACT extends IAct, CONTROLLER extends IUT2004BotController> extends AbstractAgent3D<WORLD_VIEW, ACT> implements IUT2004Bot {

    /**
     * If specified - used for the construction of the PasswordReply in createPasswordReply() method.
     */
	private CONTROLLER controller;
	
	/**
	 * Latch that is raised when {@link InitedMessage} comes.
	 */
	private BusAwareCountDownLatch endMessageLatch;

	/**
	 * Whether the {@link IUT2004BotController#botStopped()} was called during the running-phase of the agent.
	 */
	private boolean botStoppedCalled = false;
	
	private EventReact<HelloBotHandshake> helloBotReaction;

	/**
	 * Parameters passed into the constructor/factory/runner (by whatever means the agent has been started).
	 */
	private UT2004BotParameters params;
	
	/**
	 * Abstraction over the UT2004 bot name within the game;
	 */
	private UT2004BotName botName;
	
	/**
	 * Provides clean-up behavior for {@link IncomingProjectile}. Whenever projectile disappears from the view, it is immediately destroyed
	 * as bot does not know what happens to it afterwards (whether it is still flying or already hit something). If we would not do this,
	 * world view would get littered with hanging {@link IncomingProjectile} instances leaking memory. 
	 */
	private ProjectileCleanUp projectileCleanUp;
		
	/**
	 * Auto-updates {@link Self} object within worldview based on {@link LocationUpdate} message. 
	 */
	private IWorldEventListener<LocationUpdate> locationUpdateMessageListener = new IWorldEventListener<LocationUpdate>() {
        @Override
        public void notify(LocationUpdate event) {
            LocationUpdate locationUpdate = event;
            Self self = getSelf();
            if (self == null) {
                return;
            }
            Self newSelf = new SelfMessage(self.getId(), self.getBotId(), self.getName(), self.isVehicle(),
                    locationUpdate.getLoc(), locationUpdate.getVel(), locationUpdate.getRot(), self.getTeam(),
                    self.getWeapon(), self.isShooting(), self.getHealth(), self.getPrimaryAmmo(),
                    self.getSecondaryAmmo(), self.getAdrenaline(), self.getArmor(), self.getSmallArmor(),
                    self.isAltFiring(), self.isCrouched(), self.isWalking(), self.getFloorLocation(),
                    self.getFloorNormal(), self.getCombo(), self.getUDamageTime(), self.getAction(),
                    self.getEmotLeft(), self.getEmotCenter(), self.getEmotRight(), self.getBubble(),
                    self.getAnim());
            log.log(Level.FINEST, "LocationUpdate L:{0}, V:{1}, R:{2}", new Object[]{locationUpdate.getLoc(), locationUpdate.getVel(), locationUpdate.getRot()});
            getWorldView().notifyImmediately(newSelf);
        }
    };
        
	/**
	 * 
	 * @param agentId
	 * @param eventBus
	 * @param logger
	 * @param worldView due to Guice nature, this can't be templated with WORLD_VIEW - Guice can't use it as a key for the injection
	 * @param act due to Guice nature, this can't be templated with ACT - Guice can't use it as a key for the injection
	 * @param init due to Guice nature, this can't be templated with CONTROLLER - Guice can't use it as a key for the injection
	 */
    @Inject
    public UT2004Bot(UT2004BotParameters parameters, IComponentBus eventBus, IAgentLogger logger, IWorldView worldView, IAct act, IUT2004BotController init) {
        super(parameters.getAgentId(), eventBus, logger, (WORLD_VIEW)worldView, (ACT)act);

        this.params = parameters;
        this.controller = (CONTROLLER) init;
        NullCheck.check(this.controller, "init");
        if (log.isLoggable(Level.FINER)) log.finer("Initializing the controller...");
        this.controller.initializeController(this);
        if (log.isLoggable(Level.FINER)) log.finer("Preparing the controller...");
        this.controller.prepareBot(this);
        if (log.isLoggable(Level.FINE)) log.fine("Controller initialized.");
        
        helloBotReaction = new EventReact<HelloBotHandshake>(HelloBotHandshake.class, worldView) {
			@Override
			protected void react(HelloBotHandshake event) {
				if (event.isServerFull()) throw new ComponentCantStartException("Server is full.", UT2004Bot.this);
			}
        };
        
        getWorldView().addEventListener(ReadyCommandRequest.class, readyCommandRequestListener);
        getWorldView().addEventListener(InitCommandRequest.class, initCommandRequestListener);
        getWorldView().addEventListener(Password.class, passwordRequestedListener);
        getWorldView().addObjectListener(InitedMessage.class, WorldObjectUpdatedEvent.class, initedMessageListener);
        getWorldView().addEventListener(BotKilled.class, killedListener);
        getWorldView().addEventListener(LocationUpdate.class, locationUpdateMessageListener);
        // endListener must be attached inside startAgent() as it is removed from the worldview in the end
        // and we want the bot to be restartable
        
        endMessageLatch = new BusAwareCountDownLatch(1, getEventBus(), getWorldView());
        
        projectileCleanUp = new ProjectileCleanUp(this);
        
        botName = new UT2004BotName(this, "undefined");
    }
    
    /**
     * Returns the bot controller passed inside {@link UT2004Bot#AbstractUT2004Bot(IAgentId, IComponentBus, IAgentLogger, IVisionWorldView, IAct, IUT2004BotInitialization)}.
     * @return
     */
    public CONTROLLER getController() {
    	return controller;
    }
    
    /**
     * Returns parameters that were passed into the agent during the construction. 
     * <p><p>
     * This is a great place to parametrize your agent. Note that you may pass arbitrary subclass of {@link UT2004BotParameters}
     * to the constructor/factory/runner and pick them up here.
     * 
     * @return parameters
     */
    public UT2004BotParameters getParams() {
		return params;
	}
    
    ////////  
    //
    // BOT CONTROL METHODS
    //
    ////////    

	@Override
	protected void startAgent() {
    	botStoppedCalled = false;
    	super.startAgent();
    	getWorldView().addEventListener(EndMessage.class, endListener);
   		if (log.isLoggable(Level.INFO)) log.info("Waiting for the handshake to finish for 300s.");
		if (!endMessageLatch.await(300000, TimeUnit.MILLISECONDS)) {
			throw new ComponentCantStartException("The bot did not received first EndMessage in 300 seconds.", this);
		}
		if (log.isLoggable(Level.INFO)) log.info("Handshake finished.");
    }
	
	@Override
	protected void startPausedAgent() {
		botStoppedCalled = false;
		super.startPausedAgent();
		getWorldView().addEventListener(EndMessage.class, endListener);
   		if (log.isLoggable(Level.INFO)) log.info("Waiting for the handshake to finish for 300s.");
		if (!endMessageLatch.await(300000, TimeUnit.MILLISECONDS)) {
			throw new ComponentCantStartException("The bot did not received first EndMessage in 300 seconds.", this);
		}
		if (log.isLoggable(Level.INFO)) log.info("Handshake finished.");
	}
    
	@Override
	protected void preStopAgent() {
		super.preStopAgent();
		try {
			tryDisconnect();
    	} catch (Exception e) {
		}
	}
	
    @Override
    protected void stopAgent() {
    	try {
	    	if (!botStoppedCalled) {
	    		botStoppedCalled = true;
	    		controller.botShutdown();	    		
	    	}	    	
    	} finally {
			try {
				removeBotDisconnector();
			} finally {
				try {
					super.stopAgent();
				} finally {
					endMessageLatch = new BusAwareCountDownLatch(1, getEventBus(), getWorldView());
				}
			}
    	}
    }
    
    @Override
    protected void preKillAgent() {
    	super.preKillAgent();
    	try {
			tryDisconnect();
    	} catch (Exception e) {
		}
    }
    
    @Override
    protected void killAgent() {
       	try {
	    	if (!botStoppedCalled) {
	    		botStoppedCalled = true;
	    		controller.botShutdown();	    		
	    	}
    	} finally {
			try {
				removeBotDisconnector();
			} finally {
				try {
					super.killAgent();
				} finally {
					endMessageLatch = new BusAwareCountDownLatch(1, getEventBus(), getWorldView());
				}
			}
    	}
    }
    
    /**
     * Disconnector thread serves as a last resort for shutting down the bot inside GB2004 in case of JVM failures.
     */
    protected Thread botDisconnectorThread;
    
    /**
     * Sends {@link DisconnectBot} commands to GB2004, eats up all exceptions.
     */
    protected void tryDisconnect() {
    	try {
    		DisconnectBot cmd = new DisconnectBot();
    		try {
    			log.info("Sending " + cmd + " to destroy bot inside UT2004.");
    		} finally {
    			getAct().act(cmd);
    			Thread.sleep(1000);    			
    		}
    	} catch (Exception e) {
    		log.warning(ExceptionToString.process("Failed to disconnect the bot, we hope that GB2004 will remove it when the socket gets closed.", e));
    	}
    }
    
    /**
     * Initializes & registers {@link UT2004Bot#botDisconnectorThread} as a {@link Runtime#addShutdownHook(Thread)}.
     */
    protected void addBotDisconnector() {
    	if (botDisconnectorThread == null) {
    		botDisconnectorThread = new Thread(
    			new Runnable() {
					@Override
					public void run() {
						tryDisconnect();
					}    				
    			},
    			getName() + "-Disconnector"
    		);
    		try {
    			Runtime.getRuntime().addShutdownHook(botDisconnectorThread);
    		} catch (Exception e) {
    			throw new PogamutException("Failed to add BotDisconnectorThread as a JVM shutdown hook.", e, this);
    		}
    	}
    }
    
    /**
     * Removes {@link UT2004Bot#botDisconnectorThread} as a {@link Runtime#removeShutdownHook(Thread)} and nullify the field.
     */
    protected void removeBotDisconnector() {
    	if (botDisconnectorThread != null) {
    		try {
    			Runtime.getRuntime().removeShutdownHook(botDisconnectorThread);
    		} catch (Exception e) {
    			log.warning(ExceptionToString.process("Failed to remove BotDisconnectorThread as a JVM shutdown hook.", e));
    		}
    		botDisconnectorThread = null;
    	}
    }

    // --------------
    // -=-=-=-=-=-=-=
    // READY LISTENER
    // -=-=-=-=-=-=-=
    // --------------
    
    /**
     * This method is called whenever HelloBot message is parsed - the GameBots2004 is awaiting
     * the bot to reply with Ready command to begin the handshake.
     */
    protected void readyCommandRequested() {
        getAct().act(new Ready());
    }
    
    /**
     * Listener that is hooked to WorldView awaiting event ReadyCommandRequest calling
     * setupWorldViewListeners() and then readyCommandRequested() method upon receiving the event.
     */
    private IWorldEventListener<ReadyCommandRequest> readyCommandRequestListener =
            new IWorldEventListener<ReadyCommandRequest>() {

                @Override
                public void notify(ReadyCommandRequest event) {
                	controller.getLog().setLevel(Level.ALL);
                	setState(new BotStateHelloBotReceived("GameBots2004 greeted us, adding custom listeners onto the worldview."));
                    readyCommandRequested();
                    setState(new BotStateHelloBotReceived("READY sent, handshaking."));
                }
            };
            
    // --------------------
    // -=-=-=-=-=-=-=-=-=-=
    // INITIALIZER LISTENER
    // -=-=-=-=-=-=-=-=-=-=
    // --------------------

    /**
     * This method is called whenever handshake with GameBots2004 is over - the GameBots2004 is awaiting
     * the bot to reply with Ready command to begin the handshake. It calls setUpInit() method
     * to obtains Initialize message that is then sent to GameBots2004.
     * <p><p>
     * Left as protected if you need to override it - but you probably wouldn't.
     */
    protected void initCommandRequested() {
    	// NOTIFY OTHERS
    	getWorldView().notifyImmediately(new BotAboutToBeSpawnedForTheFirstTime());
    	
    	if (!inState(IAgentStateGoingUp.class, IAgentStateUp.class)) {
    		// some exception happened
    		log.warning("TERMINATING THE INITIALIZATION, bot in ivalid state: " + getState().getFlag().getClass().getSimpleName());
    		return;
    	}
    	
    	// CONTINUE WITH INITIALIZATION
        Initialize initializeCommand = getController().getInitializeCommand();
        if (initializeCommand == null) {
        	throw new AgentException("getBotInit().getInitializeCommand() method returned null message, can't initialize the agent!", log, this);
        }
        if(initializeCommand.getName() == null) {
            // set agent name shown in Unreal
            initializeCommand.setName(getComponentId().getName().getFlag());
        } else {
            // override original name
            getComponentId().getName().setFlag(initializeCommand.getName());
        }
        botName.setNameBase(initializeCommand.getName());
        if (initializeCommand.getTeam() == null) {
        	initializeCommand.setTeam(params.getTeam());
        }
        if (initializeCommand.getLocation() == null) {
        	initializeCommand.setLocation(params.getInitialLocation());
        }
        if (initializeCommand.getRotation() == null) {
        	initializeCommand.setRotation(params.getInitialRotation());
        }
        try {
            // set the JMX name
            initializeCommand.setJmx(getJMX().enableJMX());
        } catch (Exception e) {
            throw new PogamutJMXException("Error seting up JMX name of the agent.", e, log, this);
        }
        
        String  defaultName  = (String) PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_NAME.getValue();
        String  defaultSkin  = (String) PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKIN.getValue();
        Integer defaultTeam  = (Integer)PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_TEAM.getValue();
        Integer defaultSkill = (Integer)PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_SKILL.getValue();
        
        if ((Boolean)PogamutUT2004Property.POGAMUT_UT2004_BOT_INIT_OVERRIDE_PARAMS.getValue()) {
        	if (defaultName != null) {
        		initializeCommand.setName(defaultName);
        		botName.setNameBase(defaultName);
        	}
        	if (defaultSkin != null) initializeCommand.setSkin(defaultSkin);
        	if (defaultTeam != null) initializeCommand.setTeam(defaultTeam);
        	if (defaultSkill != null) initializeCommand.setDesiredSkill(defaultSkill);
        } else {
        	if (defaultName != null  && initializeCommand.getName() == null) {
        		initializeCommand.setName(defaultName);
        		botName.setNameBase(defaultName);
        	}
        	if (defaultSkin != null  && initializeCommand.getSkin() == null) initializeCommand.setSkin(defaultSkin);
        	if (defaultTeam != null  && initializeCommand.getTeam() == null) initializeCommand.setTeam(defaultTeam);
        	if (defaultSkill != null && initializeCommand.getDesiredSkill() == null) initializeCommand.setDesiredSkill(defaultSkill);
        }

        getAct().act(initializeCommand);
    }
    
    /**
     * Listener that is hooked to WorldView awaiting event InitCommandRequest calling
     * initCommandRequested() method upon receiving the event.
     */
    private IWorldEventListener<InitCommandRequest> initCommandRequestListener =
            new IWorldEventListener<InitCommandRequest>() {
                @Override
                public void notify(InitCommandRequest event) {
                	setState(new BotStateSendingInit("Handshake over, sending INIT."));
                	initCommandRequested();
                	setState(new BotStateSendingInit("Handshake over, INIT sent."));
                }
            };
            
    // -----------------
    // -=-=-=-=-=-=-=-=-
    // PASSWORD LISTENER
    // -=-=-=-=-=-=-=-=-
    // -----------------
    
    /**
     * Listener that is hooked to WorldView awaiting event InitCommandRequest calling
     * initCommandRequested() method upon receiving the event.
     */
    private IWorldEventListener<Password> passwordRequestedListener =
            new IWorldEventListener<Password>() {
                @Override
                public void notify(Password event) {
                    setState(new BotStatePassword("Password requested by the world."));
                    PasswordReply passwordReply = getController().getPassword();
                    if (passwordReply == null) {
                    	if (log.isLoggable(Level.WARNING)) log.warning("createPasswordReply() returned null");
                        passwordReply = new PasswordReply("");
                    }
                    if (log.isLoggable(Level.INFO)) log.info("Password required for the world, replying with '" + passwordReply.getPassword() + "'.");
                    getAct().act(passwordReply);
                    setState(new BotStatePassword("Password sent."));
                }
            };

    // -----------------------
    // -=-=-=-=-=-=-=-=-=-=-=-
    // INITED MESSAGE LISTENER
    // -=-=-=-=-=-=-=-=-=-=-=-
    // -----------------------
    /**
     * Listener that is hooked to WorldView awaiting event InitedMessage calling
     * botInitialized method upon receiving the event.
     */
    private IWorldObjectEventListener<InitedMessage, WorldObjectUpdatedEvent<InitedMessage>> initedMessageListener =
            new IWorldObjectEventListener<InitedMessage, WorldObjectUpdatedEvent<InitedMessage>>() {

                @Override
                public void notify(WorldObjectUpdatedEvent<InitedMessage> event) {
                	setState(new BotStateInited("InitedMessage received, calling botInitialized()."));
                	controller.botInitialized(getWorldView().getSingle(GameInfo.class), getWorldView().getSingle(ConfigChange.class), event.getObject());
                    setState(new BotStateInited("Bot initialized."));                    
                }
            };
            
	// ---------------------------
	// -=-=-=-=-=-=-=-=-=-=-=-=-=-
	// BOT KILLED MESSAGE LISTENER
	// -=-=-=-=-=-=-=-=-=-=-=-=-=-
	// ---------------------------
	/**
	 * Listener that is hooked to WorldView awaiting event {@link BotKilled} calling
	 * botKilled method upon receiving the event.
	 */
	private IWorldEventListener<BotKilled> killedListener =
	        new IWorldEventListener<BotKilled>() {
	            @Override
	            public void notify(BotKilled event) {
	                getController().botKilled(event);
	            }
	        };
	        
    // ------------------
	// -=-=-=-=--=-=-=-=-
	// FIRST END LISTENER
	// -=-=-=-=--=-=-=-=-
	// ------------------
	private IWorldEventListener<EndMessage> endListener = 
			new IWorldEventListener<EndMessage>() {

				@Override
				public void notify(EndMessage event) {
					setState(new BotStateSpawned("First batch of informations received - calling botSpawned()."));
					controller.botFirstSpawn(getWorldView().getSingle(GameInfo.class), getWorldView().getSingle(ConfigChange.class), getWorldView().getSingle(InitedMessage.class), getWorldView().getSingle(Self.class));
					setState(new BotStateSpawned("botSpawned() finished, finalizing controller initialization..."));					
					controller.finishControllerInitialization();
					setState(new BotStateSpawned("finishControllerInitialization() finished, UT2004Bot is running."));
					getWorldView().removeEventListener(EndMessage.class, this);
					endMessageLatch.countDown();
				}
		
	};

	//
	// UTILITY METHODS
	//
	
	/**
	 * Return's bot's {@link Self} object.
	 * 
	 * WARNING: this returns NULL until the first sync-batch is sent by GB2004 (first logic() called).
	 */
	public Self getSelf() {
		return getWorldView().getSingle(Self.class);
	}
	
    /**
     * @return Location of the agent. Null if not set yet.
     */
    public Location getLocation() {
        Self self = getWorldView().getSingle(Self.class);
        if (self != null) {
            return self.getLocation();
        }
        return null;
    }

    /**
     * @return Rotation of the agent. Null if not set yet.
     */
    public Rotation getRotation() {
        Self self = getWorldView().getSingle(Self.class);
        if (self != null) {
            return self.getRotation();
        }
        return null;
    }

    /**
     * @return Velocity of the agent. Null if not set yet.
     */
    public Velocity getVelocity() {
        Self self = getWorldView().getSingle(Self.class);
        if (self != null) {
            return self.getVelocity();
        }
        return null;
    }

    public void respawn() throws PogamutException {
        getAct().act(new Respawn());
    }

    @Override
    protected AgentJMXComponents createAgentJMX() {
        return new AgentJMXComponents<IUT2004Bot>(this) {

            @Override
            protected AgentMBeanAdapter createAgentMBean(ObjectName objectName, MBeanServer mbs) throws MalformedObjectNameException, InstanceAlreadyExistsException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
                return new BotJMXMBeanAdapter(UT2004Bot.this, objectName, mbs);
            }
        };
    }

    public void setBoolConfigure(BoolBotParam param, boolean value) {
        try {
            Configuration configuration = new Configuration();
            // uff, copy all values manually
            ConfigChange confCh = getWorldView().getSingle(ConfigChange.class);
            configuration.copy(confCh);

            param.set(configuration, value);
            param.setField(confCh, value);
            getAct().act(configuration);
        } catch (Exception ex) {
        	// TODO: jimmy - co to je za logging?! ... mame log.severe() ...
        	//       a nemel by se volat FATAL ERROR?! ... nebo tu vyjimku propagovat?
            Logger.getLogger(UT2004Bot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean getBoolConfigure(BoolBotParam param) {
        try {
            return param.get(getWorldView().getSingle(ConfigChange.class));
        } catch (Exception ex) {
        	// TODO: jimmy - co to je za logging?! ... mame log.severe() ...
        	//     	 a nemel by se volat FATAL ERROR?! ... nebo tu vyjimku propagovat?
            Logger.getLogger(UT2004Bot.class.getName()).log(Level.SEVERE, null, ex);
            return false; // TODO
        }
    }

    @Override
    protected Folder createIntrospection() {
        return new ReflectionObjectFolder(AbstractAgent.INTROSPECTION_ROOT_NAME, controller);
    }
    
    @Override
    public WORLD_VIEW getWorldView() {
    	return super.getWorldView();
    }

    public UT2004BotName getBotName() {
            return botName;
    }
    
}
