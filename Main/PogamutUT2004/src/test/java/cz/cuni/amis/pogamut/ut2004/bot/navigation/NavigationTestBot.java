package cz.cuni.amis.pogamut.ut2004.bot.navigation;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTestController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

/**
 * Allows you to specify start-end navpoint that should be run through (tested).
 * <p><p>
 * Initialize with {@link NavigationTestBotParameters} or set start-end navpoint pairs
 * via {@link NavigationTestBot#setStartNavPointId(String)} and {@link NavigationTestBot#setEndNavPointId(String)}
 * (navpoint from the params are taken only iff navpoint pair is not already set).
 * 
 * @author Jimmy
 */
public class NavigationTestBot extends UT2004BotTestController<UT2004Bot> {
	
	private enum State {
		INIT,
		PREPARE_TEST,
		SPAWNED,
		COMMAND_ISSUED,
		RUNNING_BACK
	}
	
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
		if (bot.getParams() instanceof NavigationTestBotParameters) {
			if (startNavPointId == null) {
				setStartNavPointId(((NavigationTestBotParameters)bot.getParams()).getStartNavPointId());
			}
			if (endNavPointId == null) {
				setEndNavPointId(((NavigationTestBotParameters)bot.getParams()).getEndNavPointId());
			}
			if (totalRepetitions == null) {
				totalRepetitions = ((NavigationTestBotParameters)bot.getParams()).getNumOfRepetitions();
			}
			walkInCircles = ((NavigationTestBotParameters)bot.getParams()).isWalkInCircles();
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
			navigation.getPathExecutor().followPath(navigation.getPathPlanner().computePath(info.getLocation(), endNavPoint));
			state = State.COMMAND_ISSUED;
			return;
			
		case COMMAND_ISSUED:
			if (navigation.getPathExecutor().isExecuting()) return;
			if (info.isAtLocation(endNavPoint)) {
				if (walkInCircles) {
					navigation.getPathExecutor().followPath(navigation.getPathPlanner().computePath(info.getLocation(), startNavPoint));
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
				setFailure("Failed to arrive to '" + endNavPointId.getStringId() + "'.");
				return;
			}
			
		case RUNNING_BACK:
			if (navigation.getPathExecutor().isExecuting()) return;
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
				setFailure("Failed to arrive back at '" + startNavPointId.getStringId() + "'.");
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
