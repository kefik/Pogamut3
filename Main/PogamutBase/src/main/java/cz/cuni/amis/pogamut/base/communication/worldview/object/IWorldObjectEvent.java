package cz.cuni.amis.pogamut.base.communication.worldview.object;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;

/**
 * IWorldObject related event.
 * <p><p>
 * Every such event will occur on some object ... therefore you can see its 'id' and of course
 * get a hand on its instance.
 */
public interface IWorldObjectEvent<OBJECT extends IWorldObject> extends IWorldEvent {
	
	/**
	 * Id of the object where the event has occurred.
	 * @return
	 */
	public WorldObjectId getId();

	/**
	 * Instance of the object.
	 * @return
	 */
	public OBJECT getObject();
        
}
