package cz.cuni.amis.pogamut.ut2004.communication.messages.custom;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;
import cz.cuni.amis.utils.ClassUtils;
import cz.cuni.amis.utils.Tuple3;

/**
 * Reads definition of {@link ICustomControlMessage} implementation interpreting {@link ControlMessageType}, {@link ControlMessageField} and {@link ControlMessageSimType}.
 * and provides {@link ControlMessageMapper#deserialize(ControlMessage)} method for auto-mapping of {@link ControlMessage} onto custom {@link ICustomControlMessage}.
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public class ControlMessageMapper<T extends ICustomControlMessage> {
	
	public static interface ControlMessageGetter<T> {
		
		public T get(ControlMessage msg);
		
	}
	
	@SuppressWarnings("unchecked")
	public static final ControlMessageGetter<Integer>[] getterIntegers = 
		new ControlMessageGetter[] {
			// 0
			null,
			// 1
			new ControlMessageGetter<Integer>() {
				@Override
				public Integer get(ControlMessage msg) {
					return msg.getPI1();
				}
			},
			// 2
			new ControlMessageGetter<Integer>() {
				@Override
				public Integer get(ControlMessage msg) {
					return msg.getPI2();
				}
			},
			// 3
			new ControlMessageGetter<Integer>() {
				@Override
				public Integer get(ControlMessage msg) {
					return msg.getPI3();
				}
			},
		};
	
	@SuppressWarnings("unchecked")
	public static final ControlMessageGetter<Double>[] getterDoubles = 
		new ControlMessageGetter[] {
			// 0
			null,
			// 1
			new ControlMessageGetter<Double>() {
				@Override
				public Double get(ControlMessage msg) {
					return msg.getPF1();
				}
			},
			// 2
			new ControlMessageGetter<Double>() {
				@Override
				public Double get(ControlMessage msg) {
					return msg.getPF2();
				}
			},
			// 3
			new ControlMessageGetter<Double>() {
				@Override
				public Double get(ControlMessage msg) {
					return msg.getPF3();
				}
			},
		};
	
	@SuppressWarnings("unchecked")
	public static final ControlMessageGetter<Double>[] getterStrings = 
		new ControlMessageGetter[] {
			// 0
			null,
			// 1
			new ControlMessageGetter<String>() {
				@Override
				public String get(ControlMessage msg) {
					return msg.getPS1();
				}
			},
			// 2
			new ControlMessageGetter<String>() {
				@Override
				public String get(ControlMessage msg) {
					return msg.getPS2();
				}
			},
			// 3
			new ControlMessageGetter<String>() {
				@Override
				public String get(ControlMessage msg) {
					return msg.getPS3();
				}
			},
		};
	
	@SuppressWarnings("unchecked")
	public static final ControlMessageGetter<Double>[] getterBooleans = 
		new ControlMessageGetter[] {
			// 0
			null,
			// 1
			new ControlMessageGetter<Boolean>() {
				@Override
				public Boolean get(ControlMessage msg) {
					return msg.isPB1();
				}
			},
			// 2
			new ControlMessageGetter<Boolean>() {
				@Override
				public Boolean get(ControlMessage msg) {
					return msg.isPB2();
				}
			},
			// 3
			new ControlMessageGetter<Boolean>() {
				@Override
				public Boolean get(ControlMessage msg) {
					return msg.isPB3();
				}
			},
		};
	
	private Class<T> descriptor;
	
	private String type;
	
	private Constructor<T> constructor;
	
	@SuppressWarnings("rawtypes")
	private List<Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper>> fields = new ArrayList<Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper>>();
	
	private Field simTimeField;
	
	@SuppressWarnings("rawtypes")
	public ControlMessageMapper(Class<T> customControlMessageClass) {
		Map<Integer, Field> integers = new HashMap<Integer, Field>();
		Map<Integer, Field> doubles = new HashMap<Integer, Field>();
		Map<Integer, Field> strings = new HashMap<Integer, Field>();
        Map<Integer, Field> booleans = new HashMap<Integer, Field>();
		
		this.descriptor = customControlMessageClass;
		
		if (!customControlMessageClass.isAnnotationPresent(ControlMessageType.class)) {
			throw new RuntimeException("Cannot create ControlMessageDeserializer for " + customControlMessageClass + " as it is not annotated with ControlMessageType!");
		}
		this.type = customControlMessageClass.getAnnotation(ControlMessageType.class).type();
		
		try {
			constructor = customControlMessageClass.getConstructor();
		} catch (Exception e) {
			throw new RuntimeException("Cannot create ControlMessageDeserializer as its parameter-less contructor is unavailable (either non-existent or Java security related).", e);
		}
		if (constructor == null) {
			throw new RuntimeException("Cannot create ControlMessageDeserializer as its parameter-less contructor is unavailable (either non-existent or Java security related).");
		}
		
		Collection<Class> classes = ClassUtils.getSubclasses(customControlMessageClass);
		for (Class cls : classes) {
			// SANITY-CHECKS
			if (cls.isAnnotation()) continue;
			if (cls.isInterface()) continue;
			
			// PROBE FIELDS
			for (Field field : cls.getDeclaredFields()) {
				
				if (field.isAnnotationPresent(ControlMessageSimType.class)) {
					if (!field.getType().equals(long.class)) {
						throw new RuntimeException("Cannot create ControlMessageDeserializer as its @ControlMessageSimType field " + field.getDeclaringClass() + "." + field.getName() + " is not of type 'long' but '" + field.getType() + "', invalid.");
					}
					if (simTimeField != null) {
						throw new RuntimeException("Cannot create ControlMessageDeserializer as its @ControlMessageSimType field declared twice, first " + simTimeField.getDeclaringClass() + "." + simTimeField.getName() + ", second " + field.getDeclaringClass() + "." + field.getDeclaringClass() + "." + field.getName() + ".");
					}
					simTimeField = field;
					continue;
				}
				
				if (!field.isAnnotationPresent(ControlMessageField.class)) continue;
				
				ControlMessageField info = field.getAnnotation(ControlMessageField.class);
				
				if (info.index() < 0 || info.index() > 3) {
					throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " contains annotation ControlMessageParam(index=" + info.index() + "), unsupported. 1 <= index <= 3.");
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
					throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " is of invalid type " + field.getType() + ", only Integer, Double, String, Boolean, UnrealId, Location is supported.");
				}
				
				if (map.containsKey(info.index())) {
					throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " is referencing index " + info.index() + " that has already been defined/taken by field " + map.get(info.index()).getName() + ".");
				}
				
				map.put(info.index(), field);
				
				if (field.getType() == Integer.class) {
					if (info.index() >= getterIntegers.length) {
						throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage Integer field, I do not have ControlMessageGetter for that!");
					}
					fields.add(
						new Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper>(
							field,
							getterIntegers[info.index()],
							ControlMessageTypeMapper.DIRECT_MAPPER
						)
					);	
				} else
				if (field.getType() == Double.class) {
					if (info.index() >= getterDoubles.length) {
						throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage Double field, I do not have ControlMessageGetter for that!");
					}
					fields.add(
						new Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper>(
							field,
							getterDoubles[info.index()],
							ControlMessageTypeMapper.DIRECT_MAPPER
						)
					);	
				} else 
				if (field.getType() == String.class) {
					if (info.index() >= getterStrings.length) {
						throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage String field, I do not have ControlMessageGetter for that!");
					}
					fields.add(
						new Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper>(
							field,
							getterStrings[info.index()],
							ControlMessageTypeMapper.DIRECT_MAPPER
						)
					);	
				} else
				if (field.getType() == Boolean.class) {
					if (info.index() >= getterBooleans.length) {
						throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage Boolean field, I do not have ControlMessageGetter for that!");
					}
					fields.add(
						new Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper>(
							field,
							getterBooleans[info.index()],
							ControlMessageTypeMapper.DIRECT_MAPPER
						)
					);	
				} else 
				if (field.getType() == UnrealId.class) {
					if (info.index() >= getterStrings.length) {
						throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage String field, I do not have ControlMessageGetter for that!");
					}
					fields.add(
						new Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper>(
							field,
							getterStrings[info.index()],
							ControlMessageTypeMapper.STRING_2_UNREAL_ID_MAPPER
						)
					);	
				} else 
				if (field.getType() == Location.class) {
					if (info.index() >= getterStrings.length) {
						throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " has unexpected index " + info.index() + " for ControlMessage String field, I do not have ControlMessageGetter for that!");
					}
					fields.add(
						new Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper>(
							field,
							getterStrings[info.index()],
							ControlMessageTypeMapper.STRING_2_LOCATION_MAPPER
						)
					);	
				} else {
					throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as field " + field.getDeclaringClass() + "." + field.getName() + " is of invalid type " + field.getType() + ", only Integer, Double, String, Boolean, UnrealId, Location is supported.");
				}
			}
		}
		
		if (simTimeField == null) {
			throw new RuntimeException("Cannot create CustomMessageDeserializer for " + customControlMessageClass + " as no long-typed field was annotated with @CustomMessageSimTime.");
		}
	}
	
	public Class<T> getDescriptor() {
		return descriptor;
	}
	
	public String getType() {
		return type;
	}

	@SuppressWarnings("rawtypes")
	public T deserialize(ControlMessage message) {
		T result;
		
		try {
			result = constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to instantiate new " + descriptor + ".", e);
		}
		
		for (Tuple3<Field, ControlMessageGetter, ControlMessageTypeMapper> field : fields) {
			Object baseValue = field.getSecond().get(message);
			Object value = field.getThird().map(baseValue);
			try {
				field.getFirst().setAccessible(true);
				field.getFirst().set(result, value);
			} catch (Exception e) {
				throw new RuntimeException("Failed to set " + descriptor + "." + field.getFirst().getName() + " with value '" + value + "' mapped from base value '" + baseValue + "'." , e);
			}
			
		}
		
		try {
			simTimeField.setAccessible(true);
			simTimeField.set(result, message.getSimTime());
		} catch (Exception e) {
			throw new RuntimeException("Failed to set " + descriptor + "." + simTimeField.getName() + " with value '" + message.getSimTime() + "'", e);
		}
		
		return result;
	}

}
