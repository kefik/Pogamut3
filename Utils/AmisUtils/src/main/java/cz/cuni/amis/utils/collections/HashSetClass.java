package cz.cuni.amis.utils.collections;

import java.util.Collection;
import java.util.HashSet;

import cz.cuni.amis.utils.ClassUtils;

/**
 * HashSet that provides extended meaning of the .containsClass(cls) method.
 * <p><p> 
 * .contains(cls) first probes for class's ancestors
 * and interfaces. The operation is then run against all of them.
 * 
 * @author Jimmy
 */
public class HashSetClass extends HashSet<Class> {
	
	/**
	 * Returns class that is part of 'arg0' ancestors/interface classes.
	 * @param arg0
	 * @return
	 */
	public Class containsClass(Class arg0) {
		Collection<Class> classes = ClassUtils.getSubclasses((Class)arg0);
		for (Class cls : classes) {
			if (contains(cls)) return cls;
		}
		return null;
	}

}
