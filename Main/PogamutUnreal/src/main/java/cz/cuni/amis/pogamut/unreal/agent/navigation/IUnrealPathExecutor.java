package cz.cuni.amis.pogamut.unreal.agent.navigation;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathExecutor;
import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;

/**
 * UT2004 path executer adding a possibility to provide focus during the 
 * @author Jimmy
 *
 * @param <PATH_ELEMENT>
 */
public interface IUnrealPathExecutor<PATH_ELEMENT extends ILocated> extends IPathExecutor<PATH_ELEMENT> {
	
	/**
	 * Allows you to set focus during the path-execution, i.e., tell the bot where it should be looking.
	 * You may set this focus multiple times during the whole run.
	 * <p><p>
	 * Note that the focus once set is never overridden by the path executer. So if you want to reset it back to the
	 * default focus, set the focus to 'null'.
	 * 
	 * @param located
	 */
	public void setFocus(ILocated located);
	
	/**
	 * Return current focus of the bot, null means "default" provided by the path executor.
	 * @return
	 */
	public ILocated getFocus();

}
