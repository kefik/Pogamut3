package cz.cuni.amis.pogamut.ut2004.analyzer;

import java.io.File;
import java.io.PrintWriter;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.utils.logging.IAgentLogger;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ConfigurationObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.InitializeObserver;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AddInventoryMsg;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.AdrenalineGained;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Bumped;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ChangedWeapon;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ComboStarted;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.FallEdge;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameRestarted;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GlobalChat;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HearNoise;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.HearPickup;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemPickedUp;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.JumpPerformed;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Landed;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.LostChild;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MyInventory;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerJoinsGame;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerLeft;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerScore;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.RecordingEnded;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.RecordingStarted;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ShootingStarted;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ShootingStopped;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Spawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Thrown;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.WallCollision;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.WeaponUpdate;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.observer.impl.UT2004Observer;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Similar to {@link UT2004AnalyzerObserver}, but it may output ALL MESSAGES for a given bot.
 * 
 * @author Jimmy
 */
public class UT2004AnalyzerFullObserver extends UT2004Observer implements IUT2004AnalyzerObserver {
	
	private UnrealId observedBotId;	
	
	private IWorldEventListener<GameRestarted> gameRestartedListener = new IWorldEventListener<GameRestarted>() {

		@Override
		public void notify(GameRestarted event) {
			if (event.isStarted()) {
				gameRestartStarted();
			} else 
			if (event.isFinished()) {
				gameRestartEnd();
			} else {
				throw new PogamutException("GameRestarted has started==false && finished==false as well, invalid!", this);
			}
		}
		
	};
	
	@Inject
	public UT2004AnalyzerFullObserver(UT2004AnalyzerFullObserverParameters params,
			IComponentBus bus, IAgentLogger agentLogger,
			UT2004WorldView worldView, IAct act) {
		super(params, bus, agentLogger, worldView, act);
		observedBotId = UnrealId.get(params.getObservedAgentId());
		getWorldView().addEventListener(GameRestarted.class, gameRestartedListener);
		getWorldView().addObjectListener(Self.class, WorldObjectUpdatedEvent.class, humanLike_selfListener);
		if (params.isHumanLikeObservingEnabled()) {
			initializeHumanLikeObserving(params.getHumanLikeBotName(), params.getHumanLikeWriter());
		}
	}
	
	@Override
	public UT2004AnalyzerFullObserverParameters getParams() {
		return (UT2004AnalyzerFullObserverParameters) super.getParams();
	}

	@Override
	public UnrealId getObservedBotId() {
		return observedBotId;
	}
	
	/**
	 * Returns path to file that should be used for outputting the data
	 * @return
	 */
	public String getOutputFilePath() {
		String path = getParams().getOutputPath();
		if (path == null) path = ".";
		path += File.separator;
		if (getParams().getFileName() != null) {
			path += getParams().getFileName();
		} else {
			path += getObservedBotId().getStringId();
			path += ".csv";
		}
		return path;
	}
	
	/**
	 * Called whenever {@link GameRestart} message with {@link GameRestarted#isStarted()} is received.
	 * <p><p>
	 * You probably won't need to override this method, better override {@link UT2004AnalyzerFullObserver#gameRestartEnd()}, that
	 * is the place where you should reset data collection statistics / start them in case of {@link UT2004AnalyzerObserverParameters#isWaitForMatchRestart()}.
	 * <p><p>
	 * Current implementation is empty.
	 */
	protected void gameRestartStarted() {
	}
	
	/**
	 * Called whenever {@link GameRestart} message with {@link GameRestarted#isFinished()} is received.
	 * <p><p>
	 * Place where you should reset data collection statistics / start them 
	 * in case of {@link UT2004AnalyzerObserverParameters#isWaitForMatchRestart()}.
	 * <p><p>
	 * Current implementation is empty.
	 */
	protected void gameRestartEnd() {
	}
	
	/**
	 * Initialize the observer to listen on the {@link UT2004AnalyzerObserverParameters#getObservedAgentId()} that is obtained from
	 * the {@link UT2004AnalyzerFullObserver#getParams()}.
	 */
	@Override
	protected void startAgent() {
		super.startAgent();
		getAct().act(new InitializeObserver().setId(getParams().getObservedAgentId()));
		configureObserver();
	}
	
	@Override
	protected void startPausedAgent() {
		super.startPausedAgent();
		getAct().act(new InitializeObserver().setId(getParams().getObservedAgentId()));
		configureObserver();
	}
	
	/**
	 * Called from the {@link UT2004AnalyzerFullObserver#startAgent()} after {@link InitializeObserver} command
	 * is sent to configure the observer instance.
	 * <p><p>
	 * Actually enables {@link Self}, {@link MyInventory} and async messages (i.e., {@link BotKilled}).
	 */
	protected void configureObserver() {
		if (getParams().isHumanLikeObservingEnabled()) {
			getAct().act(new ConfigurationObserver().setUpdate(0.2).setAll(true).setSelf(true).setAsync(true).setGame(true).setSee(false).setSpecial(false));
		} else {
			getAct().act(new ConfigurationObserver().setUpdate(0.2).setAll(true).setSelf(true).setAsync(true).setGame(false).setSee(false).setSpecial(false));
		}
	}
	
	//
	// HUMAN-LIKE OBSERVING
	//
	
	/**
     * Enemy of the particular observed player.
     */
    private Player humanLike_enemy = null;
    /**
     * Name of the particular observed player.
     */
    private String humanLike_playerName;
    /**
     * Where we should output humanLike log.
     */
    private PrintWriter humanLike_writer; 
    /**
     * Delimiter separating parts of saved log messages. Parts are player name,
     * time, type of message and a GameBots message.
     */
    public static final String humanLike_DELIMITER = "$";
    
    /**
     * Add inventory message.
     */
    IWorldEventListener<AddInventoryMsg> humanLike_addInventoryMsgListener = new IWorldEventListener<AddInventoryMsg>() {
        @Override
        public void notify(AddInventoryMsg event) {
            processAddInventoryMsgEvent(event);
        }
    };
    /**
     * Adrenaline gained.
     */
    IWorldEventListener<AdrenalineGained> humanLike_adrenalineGainedListener = new IWorldEventListener<AdrenalineGained>() {
        @Override
        public void notify(AdrenalineGained event) {
            processAdrenalineGainedEvent(event);
        }
    };
    /**
     * Bumped.
     */
    IWorldEventListener<Bumped> humanLike_bumpedListener = new IWorldEventListener<Bumped>() {
        @Override
        public void notify(Bumped event) {
            processBumpedEvent(event);
        }
    };
    /**
     * Changed weapon.
     */
    IWorldEventListener<ChangedWeapon> humanLike_changedWeaponListener = new IWorldEventListener<ChangedWeapon>() {
        @Override
        public void notify(ChangedWeapon event) {
            processChangedWeaponEvent(event);
        }
    };
    /**
     * Combo started.
     */
    IWorldEventListener<ComboStarted> humanLike_comboStartedListener = new IWorldEventListener<ComboStarted>() {
        @Override
        public void notify(ComboStarted event) {
            processComboStartedEvent(event);
        }
    };
    /**
     * Fall edge.
     */
    IWorldEventListener<FallEdge> humanLike_fallEdgeListener = new IWorldEventListener<FallEdge>() {
        @Override
        public void notify(FallEdge event) {
            processFallEdgeEvent(event);
        }
    };
    /**
     * Game info.
     */
    IWorldEventListener<GameInfo> humanLike_gameInfoListener = new IWorldEventListener<GameInfo>() {
        @Override
        public void notify(GameInfo event) {
            processGameInfoEvent(event);
        }
    };
    /**
     * Global chat.
     */
    IWorldEventListener<GlobalChat> humanLike_globalChatListener = new IWorldEventListener<GlobalChat>() {
        @Override
        public void notify(GlobalChat event) {
            processGlobalChatEvent(event);
        }
    };
    /**
     * Hear noise.
     */
    IWorldEventListener<HearNoise> humanLike_hearNoiseListener = new IWorldEventListener<HearNoise>() {
        @Override
        public void notify(HearNoise event) {
            processHearNoiseEvent(event);
        }
    };
    /**
     * Hear pickup.
     */
    IWorldEventListener<HearPickup> humanLike_hearPickupListener = new IWorldEventListener<HearPickup>() {
        @Override
        public void notify(HearPickup event) {
            processHearPickupEvent(event);
        }
    };
    /**
     * Incoming projectile.
     */
    IWorldEventListener<IncomingProjectile> humanLike_incomingProjectileListener = new IWorldEventListener<IncomingProjectile>() {
        @Override
        public void notify(IncomingProjectile event) {
            processIncomingProjectileEvent(event);
        }
    };
    /**
     * Item picked up.
     */
    IWorldEventListener<ItemPickedUp> humanLike_itemPickedUpListener = new IWorldEventListener<ItemPickedUp>() {
        @Override
        public void notify(ItemPickedUp event) {
            processItemPickedUpEvent(event);
        }
    };
    /**
     * Jump performed.
     */
    IWorldEventListener<JumpPerformed> humanLike_jumpPerformedListener = new IWorldEventListener<JumpPerformed>() {
        @Override
        public void notify(JumpPerformed event) {
            processJumpPerformedEvent(event);
        }
    };
    /**
     * Landed.
     */
    IWorldEventListener<Landed> humanLike_landedListener = new IWorldEventListener<Landed>() {
        @Override
        public void notify(Landed event) {
            processLandedEvent(event);
        }
    };
    /**
     * Lost child.
     */
    IWorldEventListener<LostChild> humanLike_lostChildListener = new IWorldEventListener<LostChild>() {
        @Override
        public void notify(LostChild event) {
            processLostChildEvent(event);
        }
    };
    /**
     * Player damaged.
     */
    IWorldEventListener<PlayerDamaged> humanLike_playerDamagedListener = new IWorldEventListener<PlayerDamaged>() {
        @Override
        public void notify(PlayerDamaged event) {
            processPlayerDamagedEvent(event);
        }
    };
    /**
     * Player joins game.
     */
    IWorldEventListener<PlayerJoinsGame> humanLike_playerJoinsGameListener = new IWorldEventListener<PlayerJoinsGame>() {
        @Override
        public void notify(PlayerJoinsGame event) {
            processPlayerJoinsGameEvent(event);
        }
    };
    /**
     * Player killed.
     */
    IWorldEventListener<PlayerKilled> humanLike_playerKilledListener = new IWorldEventListener<PlayerKilled>() {
        @Override
        public void notify(PlayerKilled event) {
            processPlayerKilledEvent(event);
        }
    };
    /**
     * Player left.
     */
    IWorldEventListener<PlayerLeft> humanLike_playerLeftListener = new IWorldEventListener<PlayerLeft>() {
        @Override
        public void notify(PlayerLeft event) {
            processPlayerLeftEvent(event);
        }
    };
    /**
     * Player score.
     */
    IWorldEventListener<PlayerScore> humanLike_playerScoreListener = new IWorldEventListener<PlayerScore>() {
        @Override
        public void notify(PlayerScore event) {
            processPlayerScoreEvent(event);
        }
    };
    /**
     * Recording ended.
     */
    IWorldEventListener<RecordingEnded> humanLike_recordingEndedListener = new IWorldEventListener<RecordingEnded>() {
        @Override
        public void notify(RecordingEnded event) {
            processRecordingEndedEvent(event);
        }
    };
    /**
     * Player score.
     */
    IWorldEventListener<RecordingStarted> humanLike_recordingStartedListener = new IWorldEventListener<RecordingStarted>() {
        @Override
        public void notify(RecordingStarted event) {
            processRecordingStartedEvent(event);
        }
    };
    /**
     * Self.
     */
    IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>> humanLike_selfListener = new IWorldObjectEventListener<Self, WorldObjectUpdatedEvent<Self>>() {
        @Override
        public void notify(WorldObjectUpdatedEvent<Self> event) {
            processSelfEvent(event);
        }
    };
    /**
     * Shooting started.
     */
    IWorldEventListener<ShootingStarted> humanLike_shootingStartedListener = new IWorldEventListener<ShootingStarted>() {
        @Override
        public void notify(ShootingStarted event) {
            processShootingStartedEvent(event);
        }
    };
    /**
     * Shooting stopped.
     */
    IWorldEventListener<ShootingStopped> humanLike_shootingStoppedListener = new IWorldEventListener<ShootingStopped>() {
        @Override
        public void notify(ShootingStopped event) {
            processShootingStoppedEvent(event);
        }
    };
    /**
     * Spawn.
     */
    IWorldEventListener<Spawn> humanLike_spawnListener = new IWorldEventListener<Spawn>() {
        @Override
        public void notify(Spawn event) {
            processSpawnEvent(event);
        }
    };
    /**
     * Thrown.
     */
    IWorldEventListener<Thrown> humanLike_thrownListener = new IWorldEventListener<Thrown>() {
        @Override
        public void notify(Thrown event) {
            processThrownEvent(event);
        }
    };
    /**
     * Wall collision.
     */
    IWorldEventListener<WallCollision> humanLike_wallCollisionListener = new IWorldEventListener<WallCollision>() {
        @Override
        public void notify(WallCollision event) {
            processWallCollisionEvent(event);
        }
    };
    /**
     * Weapon update.
     */
    IWorldEventListener<WeaponUpdate> humanLike_weaponUpdateListener = new IWorldEventListener<WeaponUpdate>() {
        @Override
        public void notify(WeaponUpdate event) {
            processWeaponUpdateEvent(event);
        }
    };

	protected void save(String name, Long time, String type, String message) {
        String output = name + humanLike_DELIMITER + time.toString() + humanLike_DELIMITER + type + humanLike_DELIMITER + message + "\n";
        synchronized(humanLike_writer) {
        	humanLike_writer.print(output);
        }
    }

    /**
     * Process add inventory message event.
     *
     * @param event inventory message event
     */
    private void processAddInventoryMsgEvent(AddInventoryMsg event) {
        save(humanLike_playerName, event.getSimTime(), "AddInventoryMsg", event.toString());
    }

    /**
     * Process adrenaline gained event.
     *
     * @param event adrenaline gained event
     */
    private void processAdrenalineGainedEvent(AdrenalineGained event) {
        save(humanLike_playerName, event.getSimTime(), "AdrenalineGained", event.toString());
    }

    /**
     * Process bumped event.
     *
     * @param event bumped event
     */
    private void processBumpedEvent(Bumped event) {
        save(humanLike_playerName, event.getSimTime(), "Bumped", event.toString());
    }

    /**
     * Process changed weapon event.
     *
     * @param event changed weapon event
     */
    private void processChangedWeaponEvent(ChangedWeapon event) {
        save(humanLike_playerName, event.getSimTime(), "ChangedWeapon", event.toString());
    }

    /**
     * Process combo started event.
     *
     * @param event combo started event
     */
    private void processComboStartedEvent(ComboStarted event) {
        save(humanLike_playerName, event.getSimTime(), "ComboStarted", event.toString());
    }

    /**
     * Process fall edge event.
     *
     * @param event fall edge event
     */
    private void processFallEdgeEvent(FallEdge event) {
        save(humanLike_playerName, event.getSimTime(), "FallEdge", event.toString());
    }

    /**
     * Process game info event.
     *
     * @param event game info event
     */
    private void processGameInfoEvent(GameInfo event) {
        save(humanLike_playerName, event.getSimTime(), "GameInfo", event.toString());
    }

    /**
     * Process global chat event.
     *
     * @param event global chat event
     */
    private void processGlobalChatEvent(GlobalChat event) {
        save(humanLike_playerName, event.getSimTime(), "GlobalChat", event.toString());
    }

    /**
     * Process hear noise event.
     *
     * @param event hear noise event
     */
    private void processHearNoiseEvent(HearNoise event) {
        save(humanLike_playerName, event.getSimTime(), "HearNoise", event.toString());
    }

    /**
     * Process hear pickup event.
     *
     * @param event hear pickup event
     */
    private void processHearPickupEvent(HearPickup event) {
        save(humanLike_playerName, event.getSimTime(), "HearPickup", event.toString());
    }

    /**
     * Process incoming projectile event.
     *
     * @param event incoming projectile event
     */
    private void processIncomingProjectileEvent(IncomingProjectile event) {
        save(humanLike_playerName, event.getSimTime(), "IncomingProjectile", event.toString());
    }

    /**
     * Process item picked up event.
     *
     * @param event item picked up event
     */
    private void processItemPickedUpEvent(ItemPickedUp event) {
        save(humanLike_playerName, event.getSimTime(), "ItemPickedUp", event.toString());
    }

    /**
     * Process jump performed event.
     *
     * @param event jump performed event
     */
    private void processJumpPerformedEvent(JumpPerformed event) {
        save(humanLike_playerName, event.getSimTime(), "JumpPerformed", event.toString());
    }

    /**
     * Process landed event.
     *
     * @param event landed event
     */
    private void processLandedEvent(Landed event) {
        save(humanLike_playerName, event.getSimTime(), "Landed", event.toString());
    }

    /**
     * Process lost child event.
     *
     * @param event lost child event
     */
    private void processLostChildEvent(LostChild event) {
        save(humanLike_playerName, event.getSimTime(), "LostChild", event.toString());
    }

    /**
     * Process player damaged event.
     *
     * @param event player damaged event
     */
    private void processPlayerDamagedEvent(PlayerDamaged event) {
        save(humanLike_playerName, event.getSimTime(), "PlayerDamaged", event.toString());
    }

    /**
     * Process player joins game event.
     *
     * @param event player joins game event
     */
    private void processPlayerJoinsGameEvent(PlayerJoinsGame event) {
        save(humanLike_playerName, event.getSimTime(), "PlayerJoinsGame", event.toString());
    }

    /**
     * Process player killed event.
     *
     * @param event player killed event
     */
    private void processPlayerKilledEvent(PlayerKilled event) {
        save(humanLike_playerName, event.getSimTime(), "PlayerKilled", event.toString());
    }

    /**
     * Process player left event.
     *
     * @param event player left event
     */
    private void processPlayerLeftEvent(PlayerLeft event) {
        save(humanLike_playerName, event.getSimTime(), "PlayerLeft", event.toString());
    }

    /**
     * Process player score event.
     *
     * @param event player score event
     */
    private void processPlayerScoreEvent(PlayerScore event) {
        save(humanLike_playerName, event.getSimTime(), "PlayerScore", event.toString());
    }

    /**
     * Process recording ended event.
     *
     * @param event recording ended event
     */
    private void processRecordingEndedEvent(RecordingEnded event) {
        save(humanLike_playerName, event.getSimTime(), "RecordingEnded", event.toString());
    }

    /**
     * Process recording started event.
     *
     * @param event recording started event
     */
    private void processRecordingStartedEvent(RecordingStarted event) {
        save(humanLike_playerName, event.getSimTime(), "RecordingStarted", event.toString());
    }

    /**
     * READ-ONLY, {@link Self} object of the observed bot.
     */
	private Self botSelf;
	
	/**
     * READ-ONLY, {@link Self} object of the observed bot.
     */
    public Self getBotSelf() {
		return botSelf;
	}
    
    /**
     * Process self event.
     *
     * @param event self event
     */
    private void processSelfEvent(WorldObjectUpdatedEvent<Self> event) {
    	this.botSelf = event.getObject();
    	if (getParams().isHumanLikeObservingEnabled()) {
    		save(humanLike_playerName, event.getObject().getSimTime(), "Self", event.getObject().toString());
    		processEnemyEvent(event);
    	}
    }

    /**
     * Process shooting started event.
     *
     * @param event shooting started event
     */
    private void processShootingStartedEvent(ShootingStarted event) {
        save(humanLike_playerName, event.getSimTime(), "ShootingStarted", event.toString());
    }

    /**
     * Process shooting stopped event.
     *
     * @param event shooting stopped event
     */
    private void processShootingStoppedEvent(ShootingStopped event) {
        save(humanLike_playerName, event.getSimTime(), "ShootingStopped", event.toString());
    }

    /**
     * Process spawn event.
     *
     * @param event spawn event
     */
    private void processSpawnEvent(Spawn event) {
        save(humanLike_playerName, event.getSimTime(), "Spawn", event.toString());
    }

    /**
     * Process thrown event.
     *
     * @param event thrown event
     */
    private void processThrownEvent(Thrown event) {
        save(humanLike_playerName, event.getSimTime(), "Thrown", event.toString());
    }

    /**
     * Process wall collision event.
     *
     * @param event wall collision event
     */
    private void processWallCollisionEvent(WallCollision event) {
        save(humanLike_playerName, event.getSimTime(), "WallCollision", event.toString());
    }

    /**
     * Process weapon update event.
     *
     * @param event weapon update event
     */
    private void processWeaponUpdateEvent(WeaponUpdate event) {
        save(humanLike_playerName, event.getSimTime(), "WeaponUpdate", event.toString());
    }

    /**
     * Process enemy event.
     *
     * @param event enemy event
     */
    private void processEnemyEvent(WorldObjectUpdatedEvent<Self> event) {
        locateEnemy(event);
        if (humanLike_enemy != null) {
            save(humanLike_playerName, event.getObject().getSimTime(), "Enemy", humanLike_enemy.toString());
        }
    }

    /**
     * Locate player's enemy. Save reference on player's enemy. If there is no
     * new enemy, it will keep the last one in memory.
     *
     * @param event enemy update
     */
    private void locateEnemy(WorldObjectUpdatedEvent<Self> event) {
        humanLike_enemy = DistanceUtils.getNearestVisible(getWorldView().getAllVisible(Player.class).values(), event.getObject().getLocation());
        if (humanLike_enemy != null && humanLike_enemy.getName().toString().equals(humanLike_playerName)) {
            humanLike_enemy = null;
        }
    }

    /**
     * Initialize listeners. We call this method manually - we register
     * listeners here.
     *
     * @param observedPlayer name of observed player
     * @param printWriter 
     */
    protected void initializeHumanLikeObserving(String observedPlayer, PrintWriter printWriter) {

    	// init writer
    	humanLike_writer = printWriter;
    	if (humanLike_writer == null) {
    		throw new RuntimeException("humanLike_writer is null! Invalid observer parameters passed...");
    	}
    	
        // initialize listeners
        getWorldView().addEventListener(AddInventoryMsg.class, humanLike_addInventoryMsgListener);
        getWorldView().addEventListener(AdrenalineGained.class, humanLike_adrenalineGainedListener);
        getWorldView().addEventListener(Bumped.class, humanLike_bumpedListener);
        getWorldView().addEventListener(ChangedWeapon.class, humanLike_changedWeaponListener);
        getWorldView().addEventListener(ComboStarted.class, humanLike_comboStartedListener);
        getWorldView().addEventListener(FallEdge.class, humanLike_fallEdgeListener);
        getWorldView().addEventListener(GameInfo.class, humanLike_gameInfoListener);
        getWorldView().addEventListener(GlobalChat.class, humanLike_globalChatListener);
        getWorldView().addEventListener(HearNoise.class, humanLike_hearNoiseListener);
        getWorldView().addEventListener(HearPickup.class, humanLike_hearPickupListener);
        getWorldView().addEventListener(IncomingProjectile.class, humanLike_incomingProjectileListener);
        getWorldView().addEventListener(ItemPickedUp.class, humanLike_itemPickedUpListener);
        getWorldView().addEventListener(JumpPerformed.class, humanLike_jumpPerformedListener);
        getWorldView().addEventListener(Landed.class, humanLike_landedListener);
        getWorldView().addEventListener(LostChild.class, humanLike_lostChildListener);
        getWorldView().addEventListener(PlayerDamaged.class, humanLike_playerDamagedListener);
        getWorldView().addEventListener(PlayerJoinsGame.class, humanLike_playerJoinsGameListener);
        getWorldView().addEventListener(PlayerKilled.class, humanLike_playerKilledListener);
        getWorldView().addEventListener(PlayerLeft.class, humanLike_playerLeftListener);
        getWorldView().addEventListener(PlayerScore.class, humanLike_playerScoreListener);
        getWorldView().addEventListener(RecordingEnded.class, humanLike_recordingEndedListener);
        getWorldView().addEventListener(RecordingStarted.class, humanLike_recordingStartedListener);        
        getWorldView().addEventListener(ShootingStarted.class, humanLike_shootingStartedListener);
        getWorldView().addEventListener(ShootingStopped.class, humanLike_shootingStoppedListener);
        getWorldView().addEventListener(Spawn.class, humanLike_spawnListener);
        getWorldView().addEventListener(Thrown.class, humanLike_thrownListener);
        getWorldView().addEventListener(WallCollision.class, humanLike_wallCollisionListener);
        getWorldView().addEventListener(WeaponUpdate.class, humanLike_weaponUpdateListener);

        // save player name
        humanLike_playerName = observedPlayer;
    }

}
