package cz.cuni.amis.utils;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.cuni.amis.utils.collections.MyCollections;
import cz.cuni.amis.utils.maps.HashMapMap;

public class ClassUtils {
	
	/**
	 * Cache for results of 'getSubclasses' call.
	 */
	private static Map<Class, SoftReference<Set<Class>>> subclassesCache = 
		new HashMap<Class, SoftReference<Set<Class>>>();
	
	/**
	 * Adds 'interf' and all interfaces it extends into 'interfaces', called recursively.
	 * 
	 * @param interf
	 * @param interfaces
	 */
	@SuppressWarnings("unchecked")
	private static void probeInterface(Class interf, Set<Class> interfaces) {
		interfaces.add(interf);
		for (int i = 0; i < interf.getInterfaces().length; ++i) {
			probeInterface(interf.getInterfaces()[i], interfaces);
		}
	}
	
	/**
	 * Returns all interfaces and super-classes the class 'cls' implements / inherit including
	 * 'cls' itself.
	 * <p><p>
	 * <b>EXCEPT:</b>Object
	 * <p><p>
	 * Don't fear of the performance implications - the results are cached so
	 * every class is probed only once. 
	 * 
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static synchronized Collection<Class> getSubclasses(Class cls) {
		
		// get cached result
		SoftReference<Set<Class>> reference = subclassesCache.get(cls);
		
		Set<Class> classes = null;
		
		// do we have cached result?
		if (reference != null) {
			// probe the soft reference...
			classes = reference.get();			
		}
		
		// if classes are not null we've got cached result, return it...
		if (classes != null) return classes;		
		
		// if not ... probe the class
		classes = new LinkedHashSet<Class>();
		classes.add(cls);
		if (cls.getInterfaces() != null) {
			for (int i = 0; i < cls.getInterfaces().length; ++i) {
				probeInterface(cls.getInterfaces()[i], classes);				
			}
		}
		for (Class superClass = cls.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
			classes.add(superClass);
			if (superClass.getInterfaces() != null) {
				for (int i = 0; i < superClass.getInterfaces().length; ++i) {
					probeInterface(superClass.getInterfaces()[i], classes);				
				}
			}
		}
		classes.remove(Object.class);
		
		// save the result
		subclassesCache.put(cls, new SoftReference(classes));
		
		return classes;
	}
	
	private static HashMapMap<Class, Boolean, SoftReference<List<Field>>> allFieldsCache = new HashMapMap<Class, Boolean, SoftReference<List<Field>>>(); 
	
	public static List<Field> getAllFields(Class cls, boolean includeStatic) {
		SoftReference<List<Field>> fieldsRef;
		synchronized(allFieldsCache) {
			fieldsRef = allFieldsCache.get(cls, includeStatic);
		}
		List<Field> fields = null;
		if (fieldsRef != null) {
			fields = fieldsRef.get();
			if (fields != null) return fields;
		}
		fields = new ArrayList<Field>();
		Class clz = cls;
		if (includeStatic) {
			while (!clz.equals(Object.class)) {
				MyCollections.toList(clz.getDeclaredFields(), fields);
				clz = clz.getSuperclass();
			}
		} else {
			ObjectFilter<Field> filterStatic = new ObjectFilter<Field>() {
				@Override
				public boolean accept(Field object) {
					return !Modifier.isStatic(object.getModifiers());
				}
				
			};
			while (!clz.equals(Object.class)) {
				MyCollections.toList(clz.getDeclaredFields(), fields, filterStatic);
				clz = clz.getSuperclass();
			}
		}
		synchronized(allFieldsCache) {
			allFieldsCache.put(cls, includeStatic, new SoftReference<List<Field>>(fields));
		}
		return fields;
	}
	
	public static String getMethodSignature(Method method) {
		StringBuffer sb = new StringBuffer();
		sb.append(method.getDeclaringClass());
		sb.append("#");
		sb.append(method.getReturnType().getSimpleName());
		sb.append(" ");
		sb.append(method.getName());
		sb.append("(");
		int arg = 0;
		for (Class type : method.getParameterTypes()) {
			if (arg > 0) sb.append(", ");
			sb.append(type.toString());
			sb.append(" ");
			sb.append("arg");
			sb.append(arg++);
		}
		sb.append(")");
		return sb.toString();
	}
	
}
