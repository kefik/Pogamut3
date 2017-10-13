package cz.cuni.amis.pogamut.ut2004multi.bot.impl;

import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.AgentInfo;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004multi.communication.module.SharedKnowledgeDatabase;
import cz.cuni.amis.utils.flag.Flag;

public class UT2004BotMultiController extends UT2004BotModuleController<UT2004Bot> {
	
	protected SharedKnowledgeDatabase shared;
	
	/**
	 * Initialize {@link UT2004BotMultiController#shared} field.
	 */
	@Override
	public void finishControllerInitialization() {
		super.finishControllerInitialization();
		if (   info.getTeam() == AgentInfo.TEAM_RED 
			|| info.getTeam() == AgentInfo.TEAM_BLUE
			|| info.getTeam() == AgentInfo.TEAM_GREEN
			|| info.getTeam() == AgentInfo.TEAM_GOLD
		) {
			log.fine("Initializing shared knowledge database.");			
			shared = SharedKnowledgeDatabase.get(info.getTeam());
			shared.addAgent(bot.getComponentId(), world, info.getTeam());
			log.info("Shared knowledge database initialized.");
			configureSharedKnowledgeDatabase();
		} else {
			log.warning("Shared knowledge database not initialized as the bot is not playing team game! It's team is neither RED, BLUE, GREEN nor GOLD.");
			log.warning("Setting 'shared' field to NULL!");
			shared = null;
		}
	}

	protected void configureSharedKnowledgeDatabase() {
		log.info("As default, shared knowledge database is initialized to share Player, Item and Flag objects.");
		shared.addObjectClass(Player.class);
		shared.addObjectClass(Item.class);
		shared.addObjectClass(Flag.class);		
	}

	/**
	 * Returns team-shared knowledge database. Initialized only for team-games (CTF, Domination, ...).
	 * @return
	 */
	public SharedKnowledgeDatabase getShared() {
		return shared;
	}

}
