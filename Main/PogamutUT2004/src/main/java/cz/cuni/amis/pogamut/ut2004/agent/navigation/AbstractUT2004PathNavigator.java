package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.List;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorHelper;
import cz.cuni.amis.pogamut.base.agent.navigation.IStuckDetector;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Simple stub of the {@link IUT2004PathNavigator} that implements {@link AbstractUT2004PathNavigator#setBot(UT2004Bot)} and 
 * {@link AbstractUT2004PathNavigator#setExecutor(IPathExecutorHelper)}.
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public abstract class AbstractUT2004PathNavigator<PATH_ELEMENT extends ILocated> implements IUT2004PathNavigator<PATH_ELEMENT>{

	/**
	 * Bot, the navigator is navigating. Set by {@link AbstractUT2004PathNavigator#setBot(UT2004Bot)}.
	 * See {@link IUT2004PathNavigator#setBot(UT2004Bot)}.
	 */
	protected UT2004Bot bot;
	
	/**
	 * Executor who is using the navigator. See {@link IUT2004PathNavigator#setExecutor(IPathExecutorHelper)}
	 * for more info.
	 */
	protected IUT2004PathExecutorHelper<PATH_ELEMENT> executor;

	/**
	 * {@link Self} object that is lazy-initialized inside {@link AbstractUT2004PathNavigator#self}.
	 */
	protected Self self;
	
	protected boolean botWaiting = false;
	
	protected void setBotWaiting(boolean state) {
		botWaiting = state;
		for (IStuckDetector detector : executor.getStuckDetectors()) {
			detector.setBotWaiting(state);
		}
	}
	
	public boolean isBotWaiting() {
		return botWaiting && self.getVelocity().isZero(10);
	}
	
	@Override
	public void setBot(UT2004Bot bot) {
		this.bot = bot;
	}

	@Override
	public void setExecutor(IUT2004PathExecutorHelper<PATH_ELEMENT> owner) {
		this.executor = owner;		
	}
	
	@Override
	public void navigate(ILocated focus) {
		if (bot == null) throw new PogamutException("The 'bot' field is null (or was not set by the executor), can't navigate.", this);
		if (executor == null) throw new PogamutException("The 'executor' field is null (ow was not set by the executor), can't navigate.", this);
		// check that we're running in correct context
		int pathElementIndex = executor.getPathElementIndex();
		if (pathElementIndex < 0 || pathElementIndex >= executor.getPath().size()) throw new PogamutException("Can't navigate as the current path element index is out of path range (index = " + pathElementIndex + ", path.size() = " + executor.getPath().size() + ".", this);
		if (self == null) {
			self = bot.getWorldView().getSingle(Self.class);
			if (self == null) throw new PogamutException("Can't navigate the bot, no Self instance is available in the world view.", this);
		}
		// fire the navigation
		navigate(focus, pathElementIndex);
	}
		
	/**
	 * Does the actual navigation of the bot, it should steer it towards path element of the index 'pathElementIndex'.
	 * Called (after several checks) from {@link AbstractUT2004PathNavigator#navigate()}.
	 * @param pathElementIndex
	 */
	protected abstract void navigate(ILocated focus, int pathElementIndex);

}
