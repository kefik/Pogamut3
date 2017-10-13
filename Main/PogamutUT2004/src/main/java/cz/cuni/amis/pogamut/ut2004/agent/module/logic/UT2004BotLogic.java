package cz.cuni.amis.pogamut.ut2004.agent.module.logic;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.exceptions.AgentException;
import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReact;
import cz.cuni.amis.pogamut.base.communication.worldview.react.EventReactOnce;
import cz.cuni.amis.pogamut.base.communication.worldview.react.ObjectEventReactOnce;
import cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GamePaused;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameResumed;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.SelfLocal;
import cz.cuni.amis.utils.flag.Flag;

public class UT2004BotLogic<BOT extends UT2004Bot> extends LogicModule<BOT> {

	private EventReact<GamePaused>            pauseReaction;
	private EventReact<GameResumed>           resumeReaction;
	private ObjectEventReactOnce<Self, ?>     selfReaction;
	private ObjectEventReactOnce<GameInfo, ?> gameInfoReaction;
	private EventReact<EndMessage>            endReaction;
	private BusAwareCountDownLatch            latch;
	
	private Flag<Boolean> gameInfoCame = new Flag<Boolean>(false);
	private Flag<Boolean> selfCame = new Flag<Boolean>(false);
	private Flag<Boolean> endCame = new Flag<Boolean>(false);
	
	@Inject
	public UT2004BotLogic(BOT agent, IAgentLogic logic) {
		this(agent, logic, null, new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agent.getWorldView()));
	}
	
	public UT2004BotLogic(BOT agent, IAgentLogic logic, Logger log) {
		this(agent, logic, log, new ComponentDependencies(ComponentDependencyType.STARTS_WITH).add(agent.getWorldView()));
	}
	
	public UT2004BotLogic(BOT agent, IAgentLogic logic, Logger log, ComponentDependencies dependencies) {
		super(agent, logic, log, dependencies);
		pauseReaction = new EventReact<GamePaused>(GamePaused.class, agent.getWorldView()) {
			@Override
			protected void react(GamePaused event) {
				controller.manualPause("Game paused.");
			}
		};
		resumeReaction = new EventReact<GameResumed>(GameResumed.class, agent.getWorldView()) {
			@Override
			protected void react(GameResumed event) {
				controller.manualResume("Game resumed.");
			}
		};
		gameInfoReaction = new ObjectEventReactOnce<GameInfo, IWorldObjectEvent<GameInfo>>(GameInfo.class, agent.getWorldView()) {
			@Override
			protected void react(IWorldObjectEvent<GameInfo> event) {
				if (event.getObject().isBotsPaused() || event.getObject().isGamePaused()) {
					controller.manualPause("Bot launched into paused game.");
				}
				gameInfoCame.setFlag(true);
			}
		};
		selfReaction = new ObjectEventReactOnce<Self, IWorldObjectEvent<Self>>(Self.class, agent.getWorldView()) {
			@Override
			protected void react(IWorldObjectEvent<Self> event) {
				selfCame.setFlag(true);
			}
		};
		
		endReaction = new EventReactOnce<EndMessage>(EndMessage.class, agent.getWorldView()) {
			@Override
			protected void react(EndMessage event) {
				synchronized(endCame) {
					endCame.setFlag(true);
					latch.countDown();
				}
			}
		};		
		cleanUp();
	}

	@Override
	protected void logicLatch(String threadName) {
		super.logicLatch(threadName);
		if (log.isLoggable(Level.FINE)) log.fine(threadName + ": Waiting for the first End message.");
		if (!latch.await(300, TimeUnit.SECONDS)) {
			throw new ComponentCantStartException("End message was not received in 300secs.", this);
		}		
		if (log.isLoggable(Level.INFO)) log.info(threadName + ": First END message received, starting logic cycles.");
		boolean came = selfCame.waitFor(60000, true);
		if (!came) {
			throw new ComponentCantStartException(threadName + ": SELF message DID NOT COME in 60secs! Even though End message has been received.", this);
		}
		if (!gameInfoCame.getFlag()) {
			if (log.isLoggable(Level.WARNING)) log.warning(threadName + ": GAMEINFO message DID NOT COME! Even though End message has been received. Was it disabled in GameBot2004.ini? Or is it a bug... ?");
		}
	}
	
	@Override
	protected void start(boolean startPaused) throws AgentException {
		super.start(startPaused);
		// this is synchronized also in EndMessage listener, we must not allow the situation
		// 1. END MESSAGE CAME
		// 2. thread1: fires EndMessageListener
		// 3. thread2: start() is called
		// 4. thread1: calls latch.countDown on old version of the latch
		// 5. thread2: latch is initialized
		// ... FIRST END MESSAGE IS TOTALLY MISSED, LOGIC WON'T START!
		synchronized(endCame) {
			latch = new BusAwareCountDownLatch(1, agent.getEventBus(), agent.getWorldView().getComponentId());
			if (endCame.getFlag()) {
				latch.countDown();
			}
		}
	}
	
	@Override
	protected void cleanUp() throws AgentException {
		super.cleanUp();
		gameInfoCame.setFlag(false);
		selfCame.setFlag(false);
		gameInfoReaction.enable();
		selfReaction.enable();
		endReaction.enable();
		endCame.setFlag(false);
	}

}
