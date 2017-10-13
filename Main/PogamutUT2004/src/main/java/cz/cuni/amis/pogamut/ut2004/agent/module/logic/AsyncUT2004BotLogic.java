package cz.cuni.amis.pogamut.ut2004.agent.module.logic;

import java.util.logging.Logger;

import com.google.inject.Inject;

import cz.cuni.amis.pogamut.base.agent.module.IAgentLogic;
import cz.cuni.amis.pogamut.base.agent.module.LogicModule;
import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectFuture;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencies;
import cz.cuni.amis.pogamut.base.component.controller.ComponentDependencyType;
import cz.cuni.amis.pogamut.base.component.exception.ComponentCantStartException;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

/**
 * {@link UT2004Bot} logic module - does not synchronize the logic together with the {@link IVisionWorldView}. The module 
 * {@link ComponentDependencyType}.STARTS_AFTER the agent's world view.
 * 
 * @author Jimmy
 *
 * @param <BOT>
 */
public class AsyncUT2004BotLogic<BOT extends UT2004Bot> extends UT2004BotLogic<BOT> {

	@Inject
	public AsyncUT2004BotLogic(BOT agent, IAgentLogic logic) {
		this(agent, logic, null, new ComponentDependencies(ComponentDependencyType.STARTS_AFTER).add(agent.getWorldView()));
	}
	
	public AsyncUT2004BotLogic(BOT agent, IAgentLogic logic, Logger log) {
		this(agent, logic, log, new ComponentDependencies(ComponentDependencyType.STARTS_AFTER).add(agent.getWorldView()));
	}
	
	public AsyncUT2004BotLogic(BOT agent, IAgentLogic logic, Logger log, ComponentDependencies dependencies) {
		super(agent, logic, log, dependencies);
		this.logic = logic;
	}
	
}
