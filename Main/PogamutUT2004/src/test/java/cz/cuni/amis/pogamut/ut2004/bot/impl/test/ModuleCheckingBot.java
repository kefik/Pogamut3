package cz.cuni.amis.pogamut.ut2004.bot.impl.test;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.communication.parser.impl.yylex.YylexParser;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectListener;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemDescriptor;

public class ModuleCheckingBot extends BotModuleTestLogic {

	private void fail(String message) {
		log.severe(message);
		throw new RuntimeException(message);		
	}
	
	boolean prepareBotCalled = false;
	
	boolean logicInitializedCalled = false;
	
	boolean botInitializedCalled = false;
	
	boolean botSpawnedCalled = false;
	
	boolean gameInfoSensed = false;
	
	boolean gameModuleGameInfoSensed = false;
	
	IWorldObjectListener<GameInfo> gameInfoListener = new IWorldObjectListener<GameInfo>() {
		@Override
		public void notify(IWorldObjectEvent<GameInfo> event) {
			gameInfoSensed = true;
			if (event.getObject() == null) {
				throw new RuntimeException("GameInfo event received, but the inner object was NULL!!!");
			}
			gameModuleGameInfoSensed = game.getGameInfo() != null;
		}
	};
	
	@Override
	public void prepareBot(UT2004Bot bot) {
		if (prepareBotCalled) {
			fail("prepareBot(): called twice!!!");
		}
		prepareBotCalled = true;
		if (bot == null) {
			fail("prepareBot(): bot is null!!!");		
		}
		bot.getLogger().getCategory(YylexParser.COMPONENT_ID.getToken()).setLevel(Level.ALL);
		bot.getWorldView().addObjectListener(GameInfo.class, gameInfoListener);
		game.getLog().setLevel(Level.ALL);
		descriptors.getLog().setLevel(Level.ALL);
	}
	
	@Override
	public void logicInitialize(LogicModule logicModule) {
		super.logicInitialize(logicModule);
		if (logicInitializedCalled) {
			fail("logicInitialized(): called twice!!!");
		}
		logicInitializedCalled = true;
		if (logicModule == null) {
			fail("logicInitialize(): logicModule is null!!!");
		}
	}
	
	@Override
	public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
		if (botInitializedCalled) {
			fail("botInitialized(): called twice!!!");
		}
		botInitializedCalled = true;
		if (gameInfo == null) {
			fail("botInitialized(): gameInfo is null!!!");
		}
		if (currentConfig == null) {
			fail("botInitialized(): currentConfig is null!!!");
		}
		if (init == null) {
			fail("botInitialized(): init is null!!!");
		}
	}

    @Override
	public void botFirstSpawn(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init, Self self) {
    	if (botSpawnedCalled) {
    		fail("botSpawned(): called twice!!!");
    	}
    	botSpawnedCalled = true;
    	if (gameInfo == null) {
    		fail("botSpawned(): gameInfo is null!!!");
    	}
    	if (currentConfig == null) {
    		fail("botSpawned(): currentConfig is null!!!");
    	}
    	if (init == null) {
    		fail("botSpawned(): init is null!!!");
    	}
    	if (self == null) {
    		fail("botSpawned(): self is null!!!");
    	}
	}
    
    @Override
    public void logic() {
    	if (!prepareBotCalled) {
    		fail("logic(): prepareBot() not called!!!");
    	}
    	if (!logicInitializedCalled) {
    		fail("logic(): logicInitialized() not called!!!");
    	}
    	if (!botInitializedCalled) {
    		fail("logic(): botInitialized() not called!!!");
    	}
    	if (!botSpawnedCalled) {
    		fail("logic(): botSpawned() not called!!!");
    	}
    	log.info("LOGIC!");
    	moduleCheck();
    	super.logic();
    	
    }

	private void moduleCheck() {
		boolean failure = false;
		log.info("MODULE CHECK");
		StringBuffer sb = new StringBuffer();
		sb.append("moduleCheck(): ERRORS ");
		
		try {
			if (info.getSelf() == null) {
				sb.append("| info.getSelf() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (info.getLocation() == null) {
				sb.append("| info.getLocation() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (info.getCurrentWeapon() == null) {
				sb.append("| info.getCurrentWeapon() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (config.getConfig() == null) {
				sb.append("| config.getConfig() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (game.getGameInfo() == null) {
				sb.append("| game.getGameInfo() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (game.getGameType() == null) {
				sb.append("| game.getGameType() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (game.getMapName() == null) {
				sb.append("| game.getMapName() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (game.getTeamScoreLimit() == null) {
				sb.append("| game.getTeamScoreLimit() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (game.getTimeLimit() == null) {
				sb.append("| game.getTimeLimit() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (descriptors.getDescriptor(UT2004ItemType.ASSAULT_RIFLE) == null) {
				sb
						.append("| descriptors.getDescriptor(ItemType.ASSAULT_RIFLE) is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (items.getRandomItem() == null) {
				sb.append("| items.getRandomItem() is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (players == null) {
				sb.append("| players is null!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (world.getAll(Item.class).size() == 0) {
				sb.append("| there is no Item present in the world view!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		try {
			if (world.getAll(NavPoint.class).size() == 0) {
				sb.append("| there is no NavPoint present in the world view!!!");
				failure = true;
			}
		} catch (Exception e) {
			failure = true;
		}
		if (!gameInfoSensed) {
			sb.append("| GAMEINFO was not sensed by our custom listener!!!");
			failure = true;
		} else {
			sb.append("| GAMEINFO was SENSED by our custom listener OK.");
		}

		if (failure) {
			fail(sb.toString());
		}
	}
	
	@Override
	public void botShutdown() {
		logicInitializedCalled = false;
		botInitializedCalled = false;
		botSpawnedCalled = false;
	}
	
}