package cz.cuni.amis.utils.flag;

import java.util.ArrayList;
import java.util.List;

import cz.cuni.amis.utils.maps.HashMapList;

public class FlagListenerMock<T> implements FlagListener<T> {

	/**
	 * OUTSIDE-READ-ONLY!
	 */
	public List<T> values = new ArrayList<T>();
	
	@Override
	public void flagChanged(T changedValue) {
		values.add(changedValue);
	}
	
	public void checkValuesInOrder(String prefix, T[] check) {
		if (check.length != values.size()) {
			System.out.println(prefix + " [CHECK-ERROR]: Sizes of values array differ, values.size() == " + values.size() + " != check.length == " + check.length);
		}
		for (int i = 0; i < values.size() && i < check.length; ++i) {
			if (values.get(i) != check[i]) {
				System.out.println(prefix + " [CHECK-ERROR]: index " + i + " differs, values[i] = " + values.get(i) + " while it should be check[i] = " + check[i]);
				throw new RuntimeException("check failed");
			}
		}
		
		if (check.length != values.size()) {
			throw new RuntimeException("check failed");
		}
		clearValues();
	}
	
	public void checkValuesAnyOrder(String prefix, T[] check) {
		if (check.length != values.size()) {
			System.out.println(prefix + " [CHECK-ERROR]: Sizes of values array differ, values.size() == " + values.size() + " != check.length == " + check.length);
		}
		HashMapList<T, T> all = new HashMapList<T, T>();
		for (T value : values) {
			all.get(value).add(value);
		}
		for (T value : check) {
			if (all.get(value).size() == 0) {
				System.out.println(prefix + " [CHECK-ERROR]: no more values '" + value + "'");
				throw new RuntimeException("check failed");
			}
			all.get(value).remove(0);
		}
		if (check.length != values.size()) {
			throw new RuntimeException("check failed");
		}
		clearValues();
	}
	
	public void clearValues() {
		values.clear();
	}

}
