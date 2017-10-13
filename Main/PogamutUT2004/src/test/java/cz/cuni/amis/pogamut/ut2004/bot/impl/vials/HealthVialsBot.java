package cz.cuni.amis.pogamut.ut2004.bot.impl.vials;

import java.io.File;
import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004PositionStuckDetector;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.stuckdetector.UT2004TimeStuckDetector;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.UT2004ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.parser.UT2004Parser;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004SyncLockableWorldView;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.UT2004WorldView;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.utils.collections.MyCollections;

public class HealthVialsBot extends UT2004BotModuleController<UT2004Bot> {
	
	@Override
	public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
		super.botInitialized(gameInfo, currentConfig, init);
		bot.getLogger().addDefaultFileHandler(new File("vials-bot.log"));
		bot.getLogger().getCategory(UT2004Parser.COMPONENT_ID.getToken()).setLevel(Level.ALL);
		bot.getLogger().getCategory(UT2004SyncLockableWorldView.COMPONENT_ID.getToken()).setLevel(Level.ALL);
		
		navigation.getPathExecutor().addStuckDetector(new UT2004TimeStuckDetector(bot, 3000, 10000)); // if the bot does not move for 3 seconds, considered that it has stuck
		navigation.getPathExecutor().addStuckDetector(new UT2004PositionStuckDetector(bot));   // watch over the position history of the bot, if the bot does not move sufficiently enough, consider that it is stuck
	}
	
	Item target;
	boolean onceExecuted;
	
	@Override
	public void logic() {
		if (target == null) {
			target = MyCollections.getRandom(items.getSpawnedItems(UT2004ItemType.MINI_HEALTH_PACK).values());
		}
		
		if (target == null) {
			log.warning("NO MORE SPAWNED VIALS TO RUN TO!");
			return;
		}
		
		boolean visible = target.isVisible();
		boolean spawned = items.getSpawnedItems(UT2004ItemType.MINI_HEALTH_PACK).containsKey(target.getId());
		
		log.warning("Target:         " + target);
		log.warning("Target ID:      " + target.getId().getStringId());
		log.warning("Target visible: " + visible);
		log.warning("Target spawned: " + spawned);
		if (navigation.getPathExecutor().isExecuting()) {
			return;
		}
		
		if (visible || spawned) {
			navigation.getPathExecutor().followPath(navigation.getPathPlanner().computePath(bot.getLocation(), target));			
		} else {
			log.warning("TARGET IS NEITHER VISIBLE NOR SPAWNED! GOING FOR ANOTHER VIAL!");
			target = null;
			onceExecuted = false;
			logic();
		}
		
	}
	
	public static void main(String[] args) {
		new UT2004BotRunner(HealthVialsBot.class, "HealthVialsBot").setMain(true).startAgents(1);
	}

}
