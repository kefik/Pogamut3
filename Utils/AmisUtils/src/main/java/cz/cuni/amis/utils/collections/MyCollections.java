package cz.cuni.amis.utils.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cz.cuni.amis.utils.IFilter;
import cz.cuni.amis.utils.ObjectFilter;

public class MyCollections {
	
	private static Random random = new Random(System.currentTimeMillis());
	
	/**
	 * Returns new list that contains only objects from 'col' that are {@link IFilter#isAccepted(Object)}.
	 * @param <T>
	 * @param col
	 * @param filter
	 * @return
	 */
	public static <T> List<T> getFiltered(Collection<T> col, IFilter filter) {
		if (col == null) return null;
		if (filter == null) return new ArrayList<T>(col);
		ArrayList<T> result = new ArrayList<T>(col.size());
		for (T obj : col) {
			if (filter.isAccepted(obj)) result.add(obj);
		}
		return result;
	}
	
	/**
	 * Returns new array that contains only objects from 'array' that are {@link IFilter#isAccepted(Object)}.
	 * @param <T>
	 * @param col
	 * @param filter
	 * @return
	 */
	public static <T> T[] getFiltered(T[] array, IFilter filter) {
		if (array == null) return null;
		if (filter == null) return Arrays.copyOf(array, array.length);		
		ArrayList<T> result = new ArrayList<T>(array.length);
		for (T obj : array) {
			if (filter.isAccepted(obj)) result.add(obj);
		}
		return (T[]) result.toArray(new Object[0]);
	}
	
	/**
	 * Returns random element from the collection.
	 * <p><p>
	 * <b>WARNING:</b> O(n) time complexity in the worst case scenario!
	 * 
	 * @param <T>
	 * @param col
	 * @return
	 */
	public static <T> T getRandom(Collection<T> col) {
		if (col == null) return null;
		if (col instanceof List) return getRandom((List<T>)col);
		if (col.size() == 0) return null;
		int rnd = random.nextInt(col.size());
		Iterator<T> iter = col.iterator();
		for (int i = 0; i < rnd-1; ++i) iter.next();
		return iter.next();
	}
	
	/**
	 * Returns random element from the list.
	 * <p><p>
	 * O(1) time complexity.
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T> T getRandom(List<T> list) {
		if (list == null) return null;
		if (list.size() == 0) return null;
		return list.get(random.nextInt(list.size()));
	}
	
	/**
	 * Returns random element from the array.
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> T getRandom(T[] array) {
		if (array == null) return null;
		if (array.length == 0) return null;
		return array[random.nextInt(array.length)];
	}
	
	/**
	 * Returns random element from the collection that is {@link IFilter#isAccepted(Object)} by the 'filter'.
	 * <p><p>
	 * <b>WARNING:</b> O(n) time complexity in the worst case scenario!
	 * 
	 * @param <T>
	 * @param col
	 * @param filter if null, performs {@link MyCollections#getRandom(Collection)}
	 * @return
	 */
	public static <T> T getRandomFiltered(Collection<T> col, IFilter filter) {
		if (col == null) return null;
		if (filter == null) return getRandom(col);
		List<T> filtered = getFiltered(col, filter);
		return getRandom(filtered);
	}
	
	/**
	 * Returns random element from the array that is {@link IFilter#isAccepted(Object)} by the 'filter'.
	 * @param <T>
	 * @param array
	 * @return
	 */
	public static <T> T getRandomFiltered(T[] array, IFilter filter) {
		if (array == null) return null;
		if (filter == null) return getRandom(array);
		T[] filtered = getFiltered(array, filter);
		return getRandom(filtered);
	}
	
	/**
	 * Adds 'objects' to 'list'.
	 * @param <T>
	 * @param objects
	 * @param list
	 */
	public static <T> void toList(T[] objects, List<T> list) {
		if (objects == null) return;
		if (list == null) return;
		for (T obj : objects) {
			list.add(obj);
		}
	}
	
	/**
	 * Adds 'objects' that satisfies 'filter' to 'list'.
	 * @param <T>
	 * @param objects
	 * @param list
	 * @param filter
	 */
	public static <T> void toList(T[] objects, List<T> list, ObjectFilter filter) {
		if (filter == null) {
			toList(objects, list);
			return;
		}
		if (objects == null) return;
		if (list == null) return;		
		for (T obj : objects) {			
			if (filter.accept(obj)) {
				list.add(obj);
			}
		}
	}
	
	/**
	 * Adds 'objects' that satisfies 'filter' to 'list'.
	 * @param <T>
	 * @param objects
	 * @param list
	 * @param filter
	 */
	public static <T> void toList(T[] objects, List<T> list, IFilter filter) {
		if (filter == null) {
			toList(objects, list);
			return;
		}
		if (objects == null) return;
		if (list == null) return;		
		for (T obj : objects) {			
			if (filter.isAccepted(obj)) {
				list.add(obj);
			}
		}
	}
	
	public static <T> List<T> toList(T... objects) {
		if (objects == null) return null;
		List<T> list = new ArrayList<T>(objects.length);
		for (T object : objects) {
			list.add(object);
		}
		return list;
	}
	
	public static <T> List<T> asList(Collection<T> objects) {
		if (objects == null) return null;
		List<T> list = new ArrayList<T>(objects.size());
		for (T object : objects) {
			list.add(object);
		}
		return list;
	}
	
	public static <T> List<T> asList(T[] objects, ObjectFilter filter) {
		if (filter == null) return toList(objects);
		if (objects == null) return null;
		List<T> list = new ArrayList<T>(objects.length);
		for (T object : objects) {
			if (filter.accept(object)) {
				list.add(object);
			}
		}
		return list;
	}
	
	public static <T> List<T> asList(Collection<T> objects, ObjectFilter filter) {
		if (filter == null) return asList(objects);
		if (objects == null) return null;
		List<T> list = new ArrayList<T>(objects.size());
		for (T object : objects) {
			if (filter.accept(object)) {
				list.add(object);
			}
		}
		return list;
	}
	
	public static <T> List<T> asList(T[] objects, IFilter filter) {
		if (filter == null) return toList(objects);
		if (objects == null) return null;
		List<T> list = new ArrayList<T>(objects.length);
		for (T object : objects) {
			if (filter.isAccepted(object)) {
				list.add(object);
			}
		}
		return list;
	}
	
	public static <T> List<T> asList(Collection<T> objects, IFilter filter) {
		if (filter == null) return asList(objects);
		if (objects == null) return null;
		List<T> list = new ArrayList<T>(objects.size());
		for (T object : objects) {
			if (filter.isAccepted(object)) {
				list.add(object);
			}
		}
		return list;
	}
	
	public static String toString(Object objToString, String[] prefixes, String[] postfixes, String[] separators, IToString toString) {
		
		StringBuffer sb = new StringBuffer(200);
		
		String[] newPrefixes = null;
		String[] newPostfixes = null;
		String[] newSeparators = null;
		
		boolean first = true;
		
		sb.append(prefixes != null && prefixes.length > 0 ? prefixes[0] : "");
		
		String separator = separators != null && separators.length > 0 ? separators[0] : "";
		
		if (objToString.getClass().isArray()) {
			objToString = toList((Object[])objToString);
		}
		if (objToString instanceof Collection) {
		
			for (Object obj : (Collection)objToString) {
				
				if (first) first = false;
				else sb.append(separator);
					
				if (obj instanceof Collection) {
					if (newPrefixes == null) {
						if (prefixes != null && prefixes.length > 1) {
							newPrefixes = new String[prefixes.length-1];
							System.arraycopy(prefixes, 1, newPrefixes, 0, prefixes.length-1);
						} else {
							newPrefixes = new String[0];
						}
						if (postfixes != null && postfixes.length > 1) {
							newPostfixes = new String[postfixes.length-1];
							System.arraycopy(postfixes, 1, newPostfixes, 0, postfixes.length-1);
						} else {
							newPostfixes = new String[0];
						}
						if (separators != null && separators.length > 1) {
							newSeparators = new String[separators.length-1];
							System.arraycopy(separators, 1, newSeparators, 0, separators.length-1);
						} else {
							newSeparators = new String[0];
						}
					}
					sb.append(MyCollections.toString(obj, newPrefixes, newPostfixes, newSeparators, toString));
				} else {
					sb.append(toString.toString(obj));
				}
			}
		} else {
			sb.append(toString.toString(objToString));
		}
			
		sb.append(postfixes != null && postfixes.length > 0 ? postfixes[0] : "");
		
		return sb.toString();
	}

	public static String toString(Object obj, String prefix, String postfix, String separator) {
		return toString(obj, new String[]{prefix}, new String[]{postfix}, new String[]{separator}, TO_STRING );
	}
	
	public static String toString(Object obj, String prefix, String postfix, String separator, IToString toString) {
		return toString(obj, new String[]{prefix}, new String[]{postfix}, new String[]{separator}, toString );
	}
	
	public static final IToString TO_STRING = new IToString() {

		@Override
		public String toString(Object obj) {
			return obj.toString();
		}
		
	};
	
	public static interface IToString {
		
		public String toString(Object obj);
		
	}

	/**
	 * Moves data from 'array' to 'col'.
	 * 
	 * NPE shielded.
	 * 
	 * @param array
	 * @param col
	 */
	public static <T> void toCollection(T[] array, Collection<T> col) {
		if (array == null) return;
		if (col == null) return;
		for (T t : array) {
			col.add(t);
		}
	}

}
