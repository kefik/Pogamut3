package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.List;
import java.util.logging.Logger;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutor;
import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutorHelper;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPointNeighbourLink;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;

/**
 * Navigator purpose is to actually move the bot through the UT2004 environment - if you did not read {@link IPathExecutor} 
 * and {@link UT2004PathExecutor} documentation, do it now. If you did, read on.
 * <p><p>
 * The {@link IUT2004PathNavigator} navigator is the easiest-to-implement piece of {@link UT2004PathExecutor} in
 * terms that it has only two methods to implement. On the other hand - it is acrually very hard to navigate the bot
 * through UT2004 environment. But! If you need to actually change the way how bot is running inside the UT2004,
 * implementing own {@link IUT2004PathNavigator} is the best thing to do (you should probably start
 * by copy-pasting the code from {@link UT2004PathExecutorNavigator} into your new class and experiment with it a bit).
 * <p><p>
 * This navigator interface is actually used by {@link UT2004PathExecutor} that covers the tricky part when and how 
 * to call its methods {@link IUT2004PathNavigator#navigate()} and {@link IUT2004PathNavigator#reset()}.
 * 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public interface IUT2004PathNavigator<PATH_ELEMENT extends ILocated> {

	/**
	 * Sets the {@link UT2004Bot} instance that the navigator should navigate. Use its {@link UT2004Bot#getAct()}
	 * to pass commands to the bot.
	 * 
	 * @param bot
	 */
	public void setBot(UT2004Bot bot);
	
	/**
	 * Sets the {@link IPathExecutorHelper} who is using the navigator, i.e., are calling its
	 * {@link IUT2004PathNavigator#navigate(Self)} and {@link IUT2004PathNavigator#reset()}
	 * methods.
	 * <p><p>
	 * Used by {@link IPathExecutorHelper} implementation to inject its instance into the navigator,
	 * so the navigator may call methods such as {@link IPathExecutorHelper#checkStuckDetectors()},
	 * {@link IPathExecutorHelper#switchToAnotherPathElement(int)}, {@link IPathExecutorHelper#stuck()}
	 * and {@link IPathExecutorHelper#targetReached()}.
	 *  
	 * @param owner
	 */
	public void setExecutor(IUT2004PathExecutorHelper<PATH_ELEMENT> owner);
	
	/**
	 * This method is regularly called by {@link UT2004PathExecutor} to continue the navigation of the bot
	 * inside the UT2004.
	 * 
	 * @param focus where the bot should have its focus
	 */
	public void navigate(ILocated focus);
	
	/**
	 * Returns current link the bot is following (if such link exist... may return null).
	 * @return
	 */
	public NavPointNeighbourLink getCurrentLink();
	
	/**
	 * {@link UT2004PathExecutor} reports that execution of current path has been terminated - clean up your internal data
	 * structure and prepare to navigate the bot along the new path in the future. 
	 */
	public void reset();
	
	/**
	 * {@link UT2004PathExecutor} reports that new path has been received and the {@link IUT2004PathNavigator#navigate()}
	 * is about to be called in near future. The new path is passed as a parameter.
	 * 
	 * @param path
	 */
	public void newPath(List<PATH_ELEMENT> path, ILocated focus);
	
	/**
	 * Path has been prolonged ... some elements (already passed) has been removed, some added.
	 * 
	 * @param path
	 * @param currentPathIndex path index into new path (points to the same element as in previous path)
	 */
	public void pathExtended(List<PATH_ELEMENT> path, int currentPathIndex);
	
	public Logger getLog();
	
	/**
	 * Whether the bot is waiting (i.e. SELF is not updated at all!)
	 * @return
	 */
	public boolean isBotWaiting();
	
}
