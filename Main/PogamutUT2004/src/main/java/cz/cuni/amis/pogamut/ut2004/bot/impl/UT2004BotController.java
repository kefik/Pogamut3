package cz.cuni.amis.pogamut.ut2004.bot.impl;

import java.util.logging.Level;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReactOnce;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.bot.IUT2004BotController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.PasswordReply;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Item;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.MapListEnd;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events.MapPointListObtained;

@AgentScoped
public class UT2004BotController<BOT extends UT2004Bot> implements IUT2004BotController<BOT> { 

	/**
	 * Name of the log category of the user log.
	 */
    public static final String USER_LOG_CATEGORY_ID = "User";

    /**
     * Instance of the bot we're controlling.
     */
	protected BOT bot;

    /**
     * Alias for user's log.
     */
    protected LogCategory log = null;
    
    private EventReactOnce<MapPointListObtained> onceMapPointListObtained;

    @Override
    public void initializeController(BOT bot) {
    	this.bot = bot;
        log = bot.getLogger().getCategory(USER_LOG_CATEGORY_ID);
        // set user-log to accept every message
        log.setLevel(Level.ALL);
        
        onceMapPointListObtained = new EventReactOnce<MapPointListObtained>(MapPointListObtained.class, bot.getWorldView()) {
    		@Override
    		protected void react(MapPointListObtained event) {
    			mapInfoObtainedInternal();
    		}
    	};
    }
    
    @Override
	public void prepareBot(BOT bot) {		
	}

    @Override
	public PasswordReply getPassword() {
		return new PasswordReply().setPassword("unspecified");
	}
    
    void mapInfoObtainedInternal() {
    	mapInfoObtained();
    }
    
    @Override
    public void mapInfoObtained() {    	
    }
    
    @Override
	public Initialize getInitializeCommand() {
		return new Initialize();
	}
    
    @Override
	public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
	}
    
    @Override
	public void botFirstSpawn(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init, Self self) {
	}
    
    @Override
	public void finishControllerInitialization() {
	}
    
	@Override
	public void botKilled(BotKilled event) {		
	}

	@Override
	public void botShutdown() {
	}
	
	@Override
	public IVisionWorldView getWorldView() {
		return bot.getWorldView();
	}
	
	@Override
	public IAct getAct() {
		return bot.getAct();
	}
	
	@Override
	public BOT getBot() {
		return bot;
	}
	
	public UT2004BotName getName() {
		return bot.getBotName();
	}

    @Override
    public LogCategory getLog() {
        return log;
    }

}
