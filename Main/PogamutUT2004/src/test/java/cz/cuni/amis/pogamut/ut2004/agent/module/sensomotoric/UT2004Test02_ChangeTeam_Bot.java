package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import org.junit.Test;

import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTest;
import cz.cuni.amis.pogamut.ut2004.bot.UT2004BotTestController;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.ChangeTeam;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.exception.PogamutException;

public class UT2004Test02_ChangeTeam_Bot extends UT2004BotTest {

	public static class TeamBot extends UT2004BotTestController<UT2004Bot> {


	    @Override
	    public void prepareBot(UT2004Bot bot) {
	    }

	    @Override
	    public Initialize getInitializeCommand() {
	        return new Initialize().setName("TeamTest").setTeam(0);
	    }
	    

	    @Override
	    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
	    }


	    @Override
	    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
	       
	    }

	    private int state = 0;
	    
	    @Override
	    public void logic() throws PogamutException {
	       
	    	if (isFailure() || isSuccess()) return;
	    	
	    	switch(state) {
	    	case 0:
	    		if (world.getSingle(Self.class).getTeam() != 0) {
	    			setFailure("SELF.TEAM IS NOT 0!");
	    		}
	    		state = 1;
	    		break;
	    	case 1:
	    		getAct().act(new ChangeTeam().setId(world.getSingle(Self.class).getId()).setTeam(1));
	    		state = 2;
	    		break;
	    	case 2:
	    	case 3:
	    	case 4:
	    		++state;
	    	case 5:
	    		if (world.getSingle(Self.class).getTeam() != 1) {
	    			setFailure("CHANGE TEAM COMMAND SEND BUT SELF.TEAM IS NOT 1!");
	    		}
	    		setSuccess();
	    		break;
	    	}
	    }

	    /**
	     * Called each time the bot dies. Good for reseting all bot's state dependent variables.
	     *
	     * @param event
	     */
	    @Override
	    public void botKilled(BotKilled event) {
	    }

	}

	@Override
	protected String getGameType() {
		return "GameBots2004.BotCTFGame";
	}
	
	@Override
	protected String getMapName() {
		return "CTF-1on1-Joust";
	}

	@Test
	public void test() {
		startTest(TeamBot.class, 1, 1);
	}
	
}
