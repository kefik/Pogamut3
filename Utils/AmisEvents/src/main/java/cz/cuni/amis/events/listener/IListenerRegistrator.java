package cz.cuni.amis.events.listener;


public interface IListenerRegistrator {

	/**
	 * Register all the listeners the registrator know of.
	 * <p><p>
	 * Can be called only if listeners are not registered (otherwise does nothing).
	 */
	public void addListeners();
	
	/**
	 * Removes (unregister) all the listeners the registrator has created. 
	 */
	public void removeListeners();
	
}
