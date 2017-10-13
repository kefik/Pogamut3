package cz.cuni.amis.utils.maps;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class Test02_WeakHashTriMap {

	static int counter = 0;
	
	public class StringKey {
		private String s;

		StringKey(String in) {
			s = in;
			++counter;
			System.out.println("Created : " + in);
		};

		public void finalize() {
			--counter;
			System.out.println("Finalized : " + s);
		};
	}

	@Test
	public void testWeakReferencing() {
		
		WeakHashTriMap<StringKey, Integer, Integer, Integer> testMap = new WeakHashTriMap<StringKey, Integer, Integer, Integer>();
		for (int primary = 0; primary < 100; ++primary) {
			StringKey priKey = new StringKey("Key" + primary);
			for (int secondary = 0; secondary < 10; ++secondary) {
				for (int tertiary = 0; tertiary < 20; ++tertiary) {
					testMap.put(priKey, secondary, tertiary, primary*secondary*tertiary);					
				}
			}
			System.out.println("Primary key exists: " + counter);
		}

		int i = 0;
		while (counter > 0 && i < 20) {
			System.gc();
			System.out.println("Waiting for gc()...");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}
			++i;
		}
		
		int c = counter;
		System.out.println("Primary key exists: " + c);
		if (c > 0) {			
			System.out.println("[ERROR] Not all primary keys have been gc()ed!");
			throw new RuntimeException("[ERROR] Not all primary keys have been gc()ed!");
		}
		System.out.println("[OK] All primary keys have been gc()ed!");

		System.out.println("---/// TEST OK ///---");
	}
}
