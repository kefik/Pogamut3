package cz.cuni.amis.pogamut.ut2004.agent.module.logic;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.communication.command.ICommandListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReact;
import cz.cuni.amis.pogamut.base.communication.worldview.react.ObjectEventReact;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base3d.ILockableVisionWorldView;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Respawn;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;

public class SyncUT2004BotLogic<BOT extends UT2004Bot<? extends ILockableVisionWorldView, ?, ?>> extends UT2004BotLogic<BOT> {

	private ObjectEventReact<ConfigChange, ?> configChangeReaction;
	
	private EventReact<EndMessage>        endReactionAfterRespawn;
	private int						      shouldExecuteLogicLatch = 0;
	
	protected ConfigChange config;
	
	private ICommandListener<Respawn> respawnListener = new ICommandListener<Respawn>() {		

		@Override
		public void notify(Respawn event) {			
			synchronized(respawnListener) {
				endReactionAfterRespawn.enable();
				shouldExecuteLogicLatch = 2;
			}
		}
		
	};

	@Inject
	public SyncUT2004BotLogic(BOT agent, IAgentLogic logic) {
		this(agent, logic, null, new ComponentDependencies(ComponentDependencyType.STARTS_AFTER).add(agent.getWorldView()));
	}
	
	public SyncUT2004BotLogic(BOT agent, IAgentLogic logic, Logger log) {
		this(agent, logic, log, new ComponentDependencies(ComponentDependencyType.STARTS_AFTER).add(agent.getWorldView()));
	}
	
	public SyncUT2004BotLogic(BOT agent, IAgentLogic logic, Logger log, ComponentDependencies dependencies) {
		super(agent, logic, log, dependencies);
		endReactionAfterRespawn = new EventReact<EndMessage>(EndMessage.class, agent.getWorldView()) {
			@Override
			protected void react(EndMessage event) {
				synchronized(respawnListener) {
					if (shouldExecuteLogicLatch > 0) {
						--shouldExecuteLogicLatch;
					}
				}
			}
		};
		agent.getAct().addCommandListener(Respawn.class, respawnListener);
		
		configChangeReaction = new ObjectEventReact<ConfigChange, IWorldObjectEvent<ConfigChange>>(ConfigChange.class, agent.getWorldView()) {
			@Override
			protected void react(IWorldObjectEvent<ConfigChange> event) {
				config = event.getObject();
				//setLogicFrequency(1 / (Math.max(0.05, event.getObject().getVisionTime() - 0.049)));
				// HOT FIX
				setLogicFrequency(60);
			}
		};
	}
	
	protected long logicStartMillis = 0;
	
	@Override
	protected void beforeLogic(String threadName) {
		super.beforeLogic(threadName);
		if (log.isLoggable(Level.FINEST)) log.finest(threadName + ": Locking world view.");
		agent.getWorldView().lock();
		if (log.isLoggable(Level.FINER)) log.finer(threadName + ": World view locked.");
		logicStartMillis = System.currentTimeMillis();
	}
	
	@Override
	protected void afterLogic(String threadName) {
		super.afterLogic(threadName);
		long logicTime1 = System.currentTimeMillis() - logicStartMillis;
				
		if (log.isLoggable(Level.FINEST)) log.finest(threadName + ": Unlocking world view.");
		agent.getWorldView().unlock();
		if (log.isLoggable(Level.FINER)) log.finer(threadName + ": World view unlocked.");
		
		long logicTime2 = System.currentTimeMillis() - logicStartMillis;
		
		long visionTime = (config == null ? 250 : (long)(config.getVisionTime() * 1000));
		
		if (logicTime1 > visionTime - 20) {
			if (log.isLoggable(Level.WARNING)) log.warning("!!! Bot logic takes too long: " + logicTime1 + " ms ~>" + visionTime + " == vision time, you will probably miss next update from GB2004.");
		} else
		if (logicTime2 > visionTime - 20) {
			if (log.isLoggable(Level.WARNING)) log.warning("!!! Bot logic (" + logicTime1 + "ms) + WorldView unlocking (" + (logicTime2 - logicTime1) + "ms) takes too long: " + logicTime2 + " ms ~>" + visionTime + " == vision time, you will probably miss next update from GB2004.");
		} 
	}
	
	@Override
	protected void afterLogicException(String threadName, Throwable e) {
		super.afterLogicException(threadName, e);
		if (agent.getWorldView().isLocked()) {
			if (log.isLoggable(Level.FINEST)) log.finest("Unlocking world view.");
			agent.getWorldView().unlock();
			if (log.isLoggable(Level.FINER)) log.finer("World view unlocked.");
		}
	}
	
	@Override
	protected boolean shouldExecuteLogic() {
		synchronized(respawnListener) {
			if (shouldExecuteLogicLatch != 0) {
				if (log.isLoggable(Level.INFO)) log.info("Respawn command sensed - waiting for the bot respawn to execute logic with correct world view state.");
				return false;
			} else {
				return true;
			}
		}			
	}
	
}
