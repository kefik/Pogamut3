/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.ut2004.communication.messages;

import cz.cuni.amis.pogamut.base.communication.messages.InfoMessage;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.ut2004.communication.worldview.objects.IGBWorldEvent;

/**
 *
 * @author ik
 */
public class GBEvent extends InfoMessage implements IGBWorldEvent {

	protected long simTime;
	
	public GBEvent(long simTime)
	{
		this.simTime = simTime;
	}
	
    public IWorldEvent getWorldEvent() {
        return this;
    }

	@Override
	public long getSimTime() {
		return this.simTime;
	}

}
