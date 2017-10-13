package cz.cuni.amis.pogamut.ut2004.bot.navigation2;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.Senses;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004GetBackToNavGraph;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.UT2004PathExecutor;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.floydwarshall.FloydWarshallMap;
import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTestController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

/**
 * Allows you to specify start-end navpoint that should be run through (tested).
 * <p><p>
 * Initialize with {@link Navigation2TestBotParameters} or set start-end navpoint pairs
 * via {@link Navigation2TestBot#setStartNavPointId(String)} and {@link Navigation2TestBot#setEndNavPointId(String)}
 * (navpoint from the params are taken only iff navpoint pair is not already set).
 * 
 * @author Jimmy
 */
public class Navigation2TestBot extends UT2004BotTestController<UT2004Bot> {
	
	private enum State {
		INIT,
		PREPARE_TEST,
		SPAWNED,
		RUN_TO,
		RUNNING_BACK
	}
	
	UT2004Navigation navig;
	
	State state = State.INIT;
	
	NavPoint startNavPoint = null;
	NavPoint endNavPoint = null;
	
	UnrealId startNavPointId = null;
	UnrealId endNavPointId = null;
	
	Integer totalRepetitions = null;
	int repetitions = 0;
	boolean walkInCircles = false;
	
	String name;
	
	@Override
	public void prepareBot(UT2004Bot bot) {
		navig = new UT2004Navigation(bot, info, move);
		if (bot.getParams() instanceof Navigation2TestBotParameters) {
			if (startNavPointId == null) {
				setStartNavPointId(((Navigation2TestBotParameters)bot.getParams()).getStartNavPointId());
			}
			if (endNavPointId == null) {
				setEndNavPointId(((Navigation2TestBotParameters)bot.getParams()).getEndNavPointId());
			}
			if (totalRepetitions == null) {
				totalRepetitions = ((Navigation2TestBotParameters)bot.getParams()).getNumOfRepetitions();
			}
			walkInCircles = ((Navigation2TestBotParameters)bot.getParams()).isWalkInCircles();
		}
	}
	
	@Override
	public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
		super.botInitialized(gameInfo, currentConfig, init);
		navigation.getPathExecutor().getLog().setLevel(Level.ALL);
	}
	
	@Override
	public void botFirstSpawn(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init, Self self) {
		name = self.getName();
	}
	
	@Override
	public void logic() {
		if (isFailure()) {
			log.severe("FAILED");
			return;
		} else 
		if (isSuccess()) {
			log.severe("SUCCEEDED");
			return;
		}
		switch (state) {
		case INIT:
			if (startNavPointId == null) {
				setFailure("startNavPointId not set!");
				return;
			}
			if (endNavPointId == null) {
				setFailure("endNavPointId not set!");
			}			
			
			startNavPoint = world.getAll(NavPoint.class).get(startNavPointId);			
			if (startNavPoint == null) {
				setFailure("Could not find start navpoint '" + startNavPointId.getStringId() + "'!");
				return;
			}
			
			endNavPoint = world.getAll(NavPoint.class).get(endNavPointId);
			if (endNavPoint == null) {
				setFailure("Could not find end navpoint '" + endNavPointId.getStringId() + "'!");
				return;
			}
			
			if (totalRepetitions == null) {
				setFailure("Number of repetitions for the test was not set!");
				return;
			}
			
			repetitions = totalRepetitions;
			if (repetitions <= 0) {
				setFailure("Number of repetitions for the test <= 0!");
				return;
			}
			
			body.getAction().respawn(startNavPoint);
			state = State.PREPARE_TEST;
			return;
			
		case PREPARE_TEST:
			body.getAction().respawn(startNavPoint);
			state = State.SPAWNED;
			return;
			
		case SPAWNED:
			if (!info.isAtLocation(startNavPoint)) {
				log.warning("Bot is not at " + startNavPoint.getId().getStringId() + ", respawning again!");
				body.getAction().respawn(startNavPoint);
				return;
			}		
			log.warning("Navigation test " + (totalRepetitions - repetitions + 1) + " / " + totalRepetitions);
			config.setName(name + " " + (totalRepetitions - repetitions + 1) + " / " + totalRepetitions);
			navig.navigate(endNavPoint);
			state = State.RUN_TO;
			return;
			
		case RUN_TO: 
			navig.navigate(endNavPoint);
			if (info.isAtLocation(endNavPoint)) {
				if (walkInCircles) {
					navig.navigate(startNavPoint);
					state = State.RUNNING_BACK;
					return;
				}
				--repetitions;				
				if (repetitions == 0) {
					setSuccess("Successfully arrived to '" + endNavPointId.getStringId() + "'.");
				} else {
					log.info("Successfully arrived to '" + endNavPointId.getStringId() + "'.");
					state = State.PREPARE_TEST;
				}
				return;
			} else {
				if (!navig.isNavigating()) {
					setFailure("Failed to arrive to '" + endNavPointId.getStringId() + "'.");
				}
				return;
			}
									
		case RUNNING_BACK:
			if (info.isAtLocation(startNavPoint)) {
				--repetitions;				
				if (repetitions == 0) {
					setSuccess("Successfully arrived back to '" + startNavPointId.getStringId() + "'.");
				} else {
					log.info("Successfully arrived back to '" + startNavPointId.getStringId() + "'.");
					state = State.SPAWNED;
				}
				return;
			} else {
				if (!navig.isNavigating()) {
					setFailure("Failed to arrive back at '" + startNavPointId.getStringId() + "'.");
				}
				return;
			}
		}
		
	}

	public UnrealId getStartNavPointId() {
		return startNavPointId;
	}

	public void setStartNavPointId(String startNavPointId) {
		this.startNavPointId = UnrealId.get(startNavPointId);
	}

	public UnrealId getEndNavPointId() {
		return endNavPointId;
	}

	public void setEndNavPointId(String endNavpointId) {
		this.endNavPointId = UnrealId.get(endNavpointId);
	}

	public void setStartNavPointId(UnrealId startNavPointId) {
		this.startNavPointId = startNavPointId;
	}

	public void setEndNavPointId(UnrealId endNavpointId) {
		this.endNavPointId = endNavpointId;
	}

	public int getRepetitions() {
		return totalRepetitions == null ? 0 : totalRepetitions;
	}

	public void setRepetitions(int repetitions) {
		this.totalRepetitions = repetitions;
	}
	
}
