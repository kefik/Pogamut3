package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * General descriptor is responsible for all items which are not by default handled by the item translator.
 * Those are usualy user-defined items thus the user can either create its own descriptor or just take this description
 * which contains only the mapping obtained directly from the ITCMessage.
 *
 * @author Ondrej
 */
public class GeneralDescriptor extends ItemDescriptor {
	
	private Map<String, Object> attributes = new HashMap<String, Object>();

	GeneralDescriptor(Object configMsg) {
		for (Field field : configMsg.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				attributes.put(field.getName(), field.get(configMsg));
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} finally {
				field.setAccessible(false);
			}
		}		
	}

	public boolean contains(String key) {
		return attributes.containsKey(key);
	}
	
	public Object get(String key) {
		return attributes.get(key);
	}

	@Override
	public String toString() {
		Set<String> keys = attributes.keySet();
		Iterator<String> iterator = keys.iterator();
		String result = "", actual;
		while (iterator.hasNext()) {
			actual = iterator.next();
			result += actual + ": " + attributes.get(actual) + "\n";
		}
		return result;
	}
}

