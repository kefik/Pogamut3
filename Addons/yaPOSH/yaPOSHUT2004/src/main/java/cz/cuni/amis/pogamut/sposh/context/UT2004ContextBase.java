package cz.cuni.amis.pogamut.sposh.context;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.communication.command.IAct;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotController;
import cz.cuni.amis.pogamut.ut2004.teamcomm.bot.UT2004TCClient;

public class UT2004ContextBase<BOT extends UT2004Bot> extends Context<BOT> implements IUT2004Context<BOT> {

	protected LogCategory log;
	
	public UT2004ContextBase(String name, BOT bot) {
		super(name, bot);
		log = bot.getLogger().getCategory(UT2004BotController.USER_LOG_CATEGORY_ID);
        log.setLevel(Level.ALL);
	}

	protected void initialize() {   
		
	}
	
	protected void initializeModules(BOT bot) {
	}
	
	
	public LogCategory getLog() {
		return log;
	}
	
	public IVisionWorldView getWorldView() {
		return bot.getWorldView();
	}
	
	public IAct getAct() {
		return bot.getAct();
	}
	
	public void mapInfoObtainedInternal() {
		
	}
	
	public void finishControllerInitialization() {		
		
	}

	@Override
	public void finishInitialization() {
		finishControllerInitialization();
	}
	
	/**
     * This method is called before the SPOSH iteration is invoked. You may clear previous-state variables here.
     */
    public void logicIteration() {    	
    }
	
}
