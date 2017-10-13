package cz.cuni.amis.hashmap;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.junit.Test;

import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.flag.FlagInteger;
import cz.cuni.amis.utils.flag.ImmutableFlag;

public class Test01_WeakHashMap {

	private static class Key {
		
		private static FlagInteger instances = new FlagInteger(0);
		
		public static ImmutableFlag<Integer> getInstances() {
			return instances.getImmutable();
		}
		
		public static void logInstances() {
			System.out.println("Key #instances: " + instances.getFlag());
		}
		
		public static boolean isInstancesGC() {
			return instances.getFlag() == 0;
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			instances.decrement(1);
		}
		
		private int key;

		public Key(int key) {
			instances.increment(1);
			this.key = key;
		}
		
		@Override
		public int hashCode() {
			return key;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (!(obj instanceof Key)) return false;
			return ((Key)obj).key == key;
		}
		
		@Override
		public String toString() {
			return super.toString() + "[" + key + "]";
		}
		
	}
	
	private static Map<Key, WeakReference<Key>> weakKeys = new WeakHashMap<Key, WeakReference<Key>>();
	
	private static Key getKey(int keyNum) {
		Key key = new Key(keyNum);
		WeakReference<Key> originalRef = weakKeys.get(key);
		if (originalRef != null) {
			Key original = originalRef.get();
			if (original != null) return original;
			synchronized(weakKeys) {
				originalRef = weakKeys.get(key);
				if (originalRef == null) {
					weakKeys.put(key, new WeakReference<Key>(key));
					return key;
				}
				original = originalRef.get();
				if (original != null) return original;
				weakKeys.put(key, new WeakReference<Key>(key));
				return key;
			}
			
		}
		synchronized(weakKeys) {
			originalRef = weakKeys.get(key);
			if (originalRef == null) {
				weakKeys.put(key, new WeakReference<Key>(key));
				return key;
			}
			Key original = originalRef.get();
			if (original != null) return original;
			weakKeys.put(key, new WeakReference<Key>(key));
			return key;
		}
	}
	
	private static class KeyManipulator implements Runnable {

		private int ref;

		public KeyManipulator(int ref) {
			this.ref = ref;
		}
		
		@Override
		public void run() {
			for (int i = 0; i < 1000; ++i) {
				Key key = getKey(i);
				System.out.println("Thread[" + ref + "]: got key: " + key);
			}
			
		}
		
	}
	
	@Test
	public void test() {
		int threadNum = 100;
		
		Thread[] threads = new Thread[threadNum];
		
		for (int i = 0; i < threads.length; ++i) {
			threads[i] = new Thread(new KeyManipulator(i), "KeyManipulator-" + i);
		}
		
		for (int i = 0; i < threads.length; ++i) {
			threads[i].start();
		}
		
		for (int i = 0; i < threads.length; ++i) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}
		}
		
		int i = 0;
		System.gc();
		while (!Key.isInstancesGC() && i < 20) {
			Key.logInstances();		
			System.out.println("Not all instances of Key has been gc()ed!");
			System.gc();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}							
			++i;
		}
		
		Key.logInstances();
		if (!Key.isInstancesGC()) {
			throw new RuntimeException("Key instances were not gc()ed!");
		}
		
		System.out.println("---/// TEST OK ///---");
	}
	
	
}
