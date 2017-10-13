package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldChangeEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.utils.ClassUtils;

/**
 * Can be used as ancestor for all {@link ICustomControlMessage} sparing you of the necessity of {@link BaseCustomControlMessage#simTime} declaration
 * and {@link IWorldEvent#getSimTime()} / {@link IWorldChangeEvent#getSimTime()} implementation.
 * <p><p>
 * Note that both {@link ControlMessageMapper} and {@link SendControlMessageMapper} are using {@link ClassUtils#getSubclasses(Class)} and introspect the full class hierarchy
 * of the {@link ICustomControlMessage} implementation handling all {@link ControlMessageField}, {@link ControlMessageSimType} annotated fields along the way. 
 * 
 * @author Jimmy
 */
public class BaseCustomControlMessage implements ICustomControlMessage {

	@ControlMessageSimType
	private long simTime;
	
	@Override
	public long getSimTime() {
		return simTime;
	}

}
