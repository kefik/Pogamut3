package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendControlMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;

/**
 * This class should be "subclassed" as e.g. "TagMessages" or "HideAndSeekMessages" and {@link ControlMessages#register(Class...)} all implementations of {@link ICustomControlMessage} 
 * within the contructor to encapsulate a functionality "that understands the whole set of some messages".
 * 
 * @author Jimmy
 */
public class ControlMessages {

	protected Map<String, ControlMessageMapper> deserializers = new HashMap<String, ControlMessageMapper>();
	protected Map<String, SendControlMessageMapper> serializers = new HashMap<String, SendControlMessageMapper>();
	
	protected void register(Class<? extends ICustomControlMessage>... customControlMessageClasses) {
		for (Class<? extends ICustomControlMessage> customControlMessageClass : customControlMessageClasses) {
			ControlMessageMapper deserializer = new ControlMessageMapper(customControlMessageClass);
			if (deserializers.containsKey(deserializer.getType())) {
				throw new RuntimeException("Cannot register same message type '" + deserializer.getType() + " twice. Following two classes have the same @ControlMessageType value: " + customControlMessageClass + ", " + deserializers.get(deserializer.getType()).getDescriptor());
			}
	 		deserializers.put(deserializer.getType(), deserializer);
	 		
	 		SendControlMessageMapper serializer = new SendControlMessageMapper(customControlMessageClass);
	 		if (serializers.containsKey(serializer.getType())) {
				throw new RuntimeException("Cannot register same message type '" + deserializer.getType() + " twice. Following two classes have the same @ControlMessageType value: " + customControlMessageClass + ", " + serializers.get(serializer.getType()).getDescriptor());
			}
	 		serializers.put(serializer.getType(), serializer);
		}
	}

	
	public ICustomControlMessage read(ControlMessage message) {
		ControlMessageMapper deserializer = deserializers.get(message.getType());
		if (deserializer == null) {
			throw new RuntimeException("Cannot deserialize " + message + ", no deserializer registered for the message type '" + message.getType() + "'.");
		}
		return deserializer.deserialize(message);
	}
	
	public SendControlMessage write(ICustomControlMessage message) {
		if (!message.getClass().isAnnotationPresent(ControlMessageType.class)) throw new RuntimeException("Cannot map object of class " + message.getClass() + " onto SendControlMessage as there is no @ControlMessageType annotation present.");
		String type = ((ControlMessageType)message.getClass().getAnnotation(ControlMessageType.class)).type();
		SendControlMessageMapper serializer = serializers.get(type);
		if (serializer == null) {
			throw new RuntimeException("Cannot serialize " + message + ", no serializer registered for the message type '" + type + "'.");
		}
		SendControlMessage command = serializer.serialize(message);
		return command;
	}
	
}
