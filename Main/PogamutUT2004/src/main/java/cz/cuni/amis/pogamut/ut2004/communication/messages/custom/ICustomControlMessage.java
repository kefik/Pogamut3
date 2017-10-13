package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;

/**
 * Every implementation must be:
 * <ol>
 * <li>Typed declaring {@link ControlMessage#getType()} via {@link CustomMessageType}.</li>
 * <li>Have every relevant field (that should be linked to some {@link ControlMessage} field) annotated with {@link ControlMessageField}</li>
 * <li>Have simType field of 'long' type annotated with {@link ControlMessageSimType}</li>
 * <li>Declare public parameter-less constructor.</li>
 * </ol>
 * 
 * You can use {@link BaseCustomControlMessage} as the ancestor for all {@link ICustomControlMessage} implementations.
 * 
 * @author Jimmy
 */
public interface ICustomControlMessage extends IWorldEvent, IWorldChangeEvent {

}
