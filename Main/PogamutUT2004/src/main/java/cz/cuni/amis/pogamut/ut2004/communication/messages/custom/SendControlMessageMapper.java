package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.SendControlMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.Tuple2;
import cz.cuni.amis.utils.Tuple3;

/**
 * Reads definition of {@link ICustomControlMessage} implementation interpreting {@link ControlMessageType}, {@link ControlMessageField} and {@link ControlMessageSimType}.
 * and provides {@link ControlMessageMapper#serialize(ControlMessage)} method for auto-mapping of {@link ControlMessage} onto {@link SendControlMessage} commands.
 * 
 * @author Jimmy
 *
 * @param <T>
 */

public class SendControlMessageMapper {
	
	public static interface SendControlMessageSetter<T> {
		public void set(SendControlMessage msg, T value);
	}
	
	@SuppressWarnings("unchecked")
	public static final SendControlMessageSetter<Integer>[] setterIntegers = 
		new SendControlMessageSetter[] {
			// 0
			null,
			// 1
			new SendControlMessageSetter<Integer>() {
				@Override
				public void set(SendControlMessage msg, Integer value) {
					msg.setPI1(value);
				}
			},
			// 2
			new SendControlMessageSetter<Integer>() {
				@Override
				public void set(SendControlMessage msg, Integer value) {
					msg.setPI2(value);
				}
			},
			// 3
			new SendControlMessageSetter<Integer>() {
				@Override
				public void set(SendControlMessage msg, Integer value) {
					msg.setPI3(value);
				}
			},
		};
	
	@SuppressWarnings("unchecked")
	public static final SendControlMessageSetter<Double>[] setterDoubles = 
		new SendControlMessageSetter[] {
			// 0
			null,
			// 1
			new SendControlMessageSetter<Double>() {
				@Override
				public void set(SendControlMessage msg, Double value) {
					msg.setPF1(value);
				}
			},
			// 2
			new SendControlMessageSetter<Double>() {
				@Override
				public void set(SendControlMessage msg, Double value) {
					msg.setPF2(value);
				}
			},
			// 3
			new SendControlMessageSetter<Double>() {
				@Override
				public void set(SendControlMessage msg, Double value) {
					msg.setPF3(value);
				}
			},
		};
	
	@SuppressWarnings("unchecked")
	public static final SendControlMessageSetter<Double>[] setterStrings = 
		new SendControlMessageSetter[] {
			// 0
			null,
			// 1
			new SendControlMessageSetter<String>() {
				@Override
				public void set(SendControlMessage msg, String value) {
					msg.setPS1(value);
				}
			},
			// 2
			new SendControlMessageSetter<String>() {
				@Override
				public void set(SendControlMessage msg, String value) {
					msg.setPS2(value);
				}
			},
			// 3
			new SendControlMessageSetter<String>() {
				@Override
				public void set(SendControlMessage msg, String value) {
					msg.setPS3(value);
				}
			},
		};
	
	@SuppressWarnings("unchecked")
	public static final SendControlMessageSetter<Double>[] setterBooleans = 
		new SendControlMessageSetter[] {
			// 0
			null,
			// 1
			new SendControlMessageSetter<Boolean>() {
				@Override
				public void set(SendControlMessage msg, Boolean value) {
					msg.setPB1(value);
				}
			},
			// 2
			new SendControlMessageSetter<Boolean>() {
				@Override
				public void set(SendControlMessage msg, Boolean value) {
					msg.setPB2(value);
				}
			},
			// 3
			new SendControlMessageSetter<Boolean>() {
				@Override
				public void set(SendControlMessage msg, Boolean value) {
					msg.setPB3(value);
				}
			},
		};
	
	private Class<? extends ICustomControlMessage> descriptor;
	
	private String type;
	
	private List<Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper>> fields = new ArrayList<Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper>>();
	
	public SendControlMessageMapper(Class<? extends ICustomControlMessage> customControlMessageClass) {
		Map<Integer, Field> integers = new HashMap<Integer, Field>();
		Map<Integer, Field> doubles = new HashMap<Integer, Field>();
		Map<Integer, Field> strings = new HashMap<Integer, Field>();
        Map<Integer, Field> booleans = new HashMap<Integer, Field>();
		
		this.descriptor = customControlMessageClass;
		
		if (!customControlMessageClass.isAnnotationPresent(ControlMessageType.class)) {
			throw new RuntimeException("Cannot create SendControlMessageSerializer for " + customControlMessageClass + " as it is not annotated with ControlMessageType!");
		}
		this.type = customControlMessageClass.getAnnotation(ControlMessageType.class).type();
		
		Collection<Class> classes = ClassUtils.getSubclasses(customControlMessageClass);
		for (Class cls : classes) {
			// SANITY-CHECKS
			if (cls.isAnnotation()) continue;
			if (cls.isInterface()) continue;
		
			for (Field field : cls.getDeclaredFields()) {
				
				if (!field.isAnnotationPresent(ControlMessageField.class)) continue;
				
				ControlMessageField info = field.getAnnotation(ControlMessageField.class);
				
				if (info.index() < 0 || info.index() > 3) {
					throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " contains annotation ControlMessageParam(index=" + info.index() + "), unsupported. 1 <= index <= 3.");
				}
				
				Map<Integer, Field> map;
				
				if (field.getType() == Integer.class) {
					map = integers;
				} else
				if (field.getType() == Double.class) {
					map = doubles;
				} else
				if (field.getType() == String.class) {
					map = strings;
				} else
				if (field.getType() == Boolean.class) {
					map = booleans;
				} else
				if (field.getType() == UnrealId.class) {
					map = strings;
				} else 
				if (field.getType() == Location.class) {
					map = strings;
				} else {
					throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " is of invalid type " + field.getType() + ", only Integer, Double, String, Boolean, UnrealId, Location is supported.");
				}
				
				if (map.containsKey(info.index())) {
					throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " is referencing index " + info.index() + " that has already been defined/taken by field " + map.get(info.index()).getName() + ".");
				}
				
				map.put(info.index(), field);
				
				if (field.getType() == Integer.class) {
					if (info.index() >= setterIntegers.length) {
						throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage Integer field, I do not have SendControlMessageSetter for that!");
					}
					fields.add(
						new Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper>(
							field,
							setterIntegers[info.index()],
							ControlMessageTypeMapper.DIRECT_MAPPER
						)
					);	
				} else
				if (field.getType() == Double.class) {
					if (info.index() >= setterDoubles.length) {
						throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage Double field, I do not have SendControlMessageSetter for that!");
					}
					fields.add(
						new Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper>(
							field,
							setterDoubles[info.index()],
							ControlMessageTypeMapper.DIRECT_MAPPER
						)
					);	
				} else 
				if (field.getType() == String.class) {
					if (info.index() >= setterStrings.length) {
						throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage String field, I do not have SendControlMessageSetter for that!");
					}
					fields.add(
						new Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper>(
							field,
							setterStrings[info.index()], 
							ControlMessageTypeMapper.DIRECT_MAPPER
						)
					);	
				} else
				if (field.getType() == Boolean.class) {
					if (info.index() >= setterBooleans.length) {
						throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage Boolean field, I do not have SendControlMessageSetter for that!");
					}
					fields.add(
						new Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper>(
							field,
							setterBooleans[info.index()], 
							ControlMessageTypeMapper.DIRECT_MAPPER
						)
					);	
				} else
				if (field.getType() == UnrealId.class) {
					if (info.index() >= setterStrings.length) {
						throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage String field, I do not have SendControlMessageSetter for that!");
					}
					fields.add(
						new Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper>(
							field,
							setterStrings[info.index()], 
							ControlMessageTypeMapper.UNREAL_ID_2_STRING_MAPPER
						)
					);	
				} else 
				if (field.getType() == Location.class) {
					if (info.index() >= setterStrings.length) {
						throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage String field, I do not have SendControlMessageSetter for that!");
					}
					fields.add(
						new Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper>(
							field,
							setterStrings[info.index()], 
							ControlMessageTypeMapper.LOCATION_2_STRING_MAPPER
						)
					);	
				} else {
					throw new RuntimeException("Cannot create SendControlMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " is of invalid type " + field.getType() + ", only Integer, Double, String, Boolean, UnrealId, Location is supported.");
				}
			}
		}
	}
	
	public Class<? extends ICustomControlMessage> getDescriptor() {
		return descriptor;
	}
	
	public String getType() {
		return type;
	}

	public SendControlMessage serialize(ICustomControlMessage message) {
		SendControlMessage result = new SendControlMessage();
		
		for (Tuple3<Field, SendControlMessageSetter, ControlMessageTypeMapper> field : fields) {
			Object baseValue;
			try {
				field.getFirst().setAccessible(true);
				baseValue = field.getFirst().get(message);
			} catch (Exception e) {
				throw new RuntimeException("Failed to get " + descriptor + "." + field.getFirst().getName() + " value." , e);
			}
			Object value = field.getThird().map(baseValue);
			field.getSecond().set(result, value);
		}

		result.setType(type);
		return result;
	}

}
