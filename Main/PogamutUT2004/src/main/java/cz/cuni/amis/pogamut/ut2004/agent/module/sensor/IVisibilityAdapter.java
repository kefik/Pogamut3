package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;

public interface IVisibilityAdapter {

	/**
	 * Is adapter ready to {@link #isVisible(ILocated, ILocated)} ?
	 * @return
	 */
	public boolean isInitialized();
	
	/**
	 * Is 'target' point visible 'from'?
	 * @param from
	 * @param target
	 * @return
	 */
	public boolean isVisible(ILocated from, ILocated target);
	
}
