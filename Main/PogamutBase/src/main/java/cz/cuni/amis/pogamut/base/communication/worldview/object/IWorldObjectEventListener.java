package cz.cuni.amis.pogamut.base.communication.worldview.object;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;

/**
 * Listens on object events.
 * <p><p>
 * Don't be afraid of class's parameters :-) ... it allows you to specify type of objects you want to listen to
 * and type of events you want to accept.
 * <p><p>
 * If you want to listen to more then one type of events / objects then specify these parameters as
 * common ancestor of events / objects you want to accept inside the listener's notify() method.
 * <p><p>
 * This approach allows you to work with specific types of object / events directly in the notify() method
 * sparing you of casting these event to the correct class.
 * <p><p>
 * Note: if you do not care about the EVENT class (which is very common), try using {@link IWorldObjectListener} that
 * only requires to specify and object class as template parameters.
 * 
 * @author Jimmy
 * @param OBJECT class of objects you're listening to
 */
public interface IWorldObjectEventListener<OBJECT extends IWorldObject, EVENT extends IWorldObjectEvent<OBJECT>>
         extends IWorldEventListener<EVENT> {

}
