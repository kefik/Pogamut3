package cz.cuni.amis.pogamut.base.communication.worldview.object;


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
 * 
 * @author Jimmy
 * @param OBJECT class of objects you're listening to
 */
public interface IWorldObjectListener<OBJECT extends IWorldObject> extends IWorldObjectEventListener<OBJECT, IWorldObjectEvent<OBJECT>> {	
}
