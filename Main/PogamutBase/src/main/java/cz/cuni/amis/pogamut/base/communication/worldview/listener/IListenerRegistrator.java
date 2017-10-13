package cz.cuni.amis.pogamut.base.communication.worldview.listener;

import cz.cuni.amis.pogamut.base.communication.worldview.listener.exception.ListenersAlreadyRegisteredException;

public interface IListenerRegistrator {

	/**
	 * Register all the listeners the registrator know of.
	 * <p><p>
	 * Can be called only if listeners are not registred.
	 * 
	 * @throws ListenersAlreadyRegisteredException
	 */
	public void addListeners() throws ListenersAlreadyRegisteredException;
	
	/**
	 * Removes (unregister) all the listeners the registrator has created. 
	 */
	public void removeListeners();
	
}
