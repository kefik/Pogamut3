package cz.cuni.amis.pogamut.base.communication.worldview;

import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentNotRunningException;
import cz.cuni.amis.pogamut.base.component.bus.exception.ComponentPausedException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public interface ILockableWorldView extends IWorldView {
	
	/**
	 * Lock the worldview, preventing it from raising any new events.
	 * <p><p>
	 * When locked - the worldview must store all incoming events and
	 * process them during unlock.
	 * <p><p>
	 * Note that it is implementation-dependent whether this method is blocking or not.
	 */
	public void lock() throws PogamutInterruptedException, ComponentNotRunningException, ComponentPausedException;
	
	/**
	 * Unlock the worldview, processing all events that came between lock() / unlock() calls.
	 */
	public void unlock() throws ComponentNotRunningException, ComponentPausedException;
	
	/**
	 * Whether the worldview is locked.
	 * @return
	 */
	public boolean isLocked();
	
}
