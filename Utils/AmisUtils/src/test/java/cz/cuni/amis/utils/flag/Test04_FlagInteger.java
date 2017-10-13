package cz.cuni.amis.utils.flag;

import org.junit.Test;

import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class Test04_FlagInteger {
	
	public static class FlagManipulator implements Runnable {

		private FlagInteger iFlag;
		private int num;

		public FlagManipulator(FlagInteger iFlag, int num) {
			this.iFlag = iFlag;
			this.num = num;
		}
		
		@Override
		public void run() {
			for (int i = 0; i < 1000; ++i) {
				if (i % 2 == 0) {
					System.out.println("Thread[" + i + "] (" + (i+1) + " / 1000): incrementing");
					iFlag.increment(1);
				}
				else {
					System.out.println("Thread[" + i + "] (" + (i+1) + " / 1000): decrementing");
					iFlag.decrement(1);
				}
			}
		}
		
	}
	
	@Test
	public void test() {
		FlagInteger flag = new FlagInteger(0);
		
		int threadsNum = 100;
		
		Thread[] threads = new Thread[threadsNum];
		
		for (int i = 0; i < threads.length; ++i) {
			threads[i] = new Thread(new FlagManipulator(flag, i), "FlagManipulator-" + i);
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
		
		System.out.println("Final flag value: " + flag.getFlag());
		
		if (flag.getFlag() != 0) {
			throw new RuntimeException("FLAG VALUE IS NOT 0!!!");
		}
		
		System.out.println("---/// TEST OK ///---");
	}

}
