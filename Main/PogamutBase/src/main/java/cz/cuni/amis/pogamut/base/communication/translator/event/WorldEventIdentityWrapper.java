/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.base.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

/**
 * Simplest WorldChangeEvent to WorldEvent adapter.
 * // TODO better name
 * @author ik
 */
public abstract class WorldEventIdentityWrapper implements IWorldEventWrapper, IWorldEvent {

    @Override
    public IWorldEvent getWorldEvent() {
        return this;
    }

	@Override
	public abstract long getSimTime();

}
