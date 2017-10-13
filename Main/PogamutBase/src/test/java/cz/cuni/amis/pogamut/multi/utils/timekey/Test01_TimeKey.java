package cz.cuni.amis.pogamut.multi.utils.timekey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Test;

import cz.cuni.amis.tests.BaseTest;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
				
public class Test01_TimeKey extends BaseTest {
	
	@Test
	public void test() {
		int num = 500000;
		log.info("Generating " + num + " keys...");
		List<TimeKey> keys = new ArrayList<TimeKey>();
		Random rnd = new Random(System.currentTimeMillis());
		for (int i = 0; i < num; ++i) {
			keys.add(TimeKey.get(rnd.nextLong()));
		}
		log.info("Sleeping 200ms...");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
		log.info("Clearing array holding TimeKey(s)...");
		keys.clear();
		keys = null;
		
		int i = 0;
		while (TimeKey.getInstances().getFlag() > 0 && i < 50) {
			System.gc();
			log.info("TimeKey.instances = " + TimeKey.getInstances().getFlag());
			log.info("(" + (i+1) + " / 50) Waiting for gc(), sleeping 200ms...");
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, this);
			}
			++i;
		}
		
		log.info("TimeKey.instances = " + TimeKey.getInstances().getFlag());
		
		if (is32Bit()) {
			if (TimeKey.getInstances().getFlag() > 0) {
				testFailed("TimeKeys were not gc()ed!!!");
			}
		} else {
			if (TimeKey.getInstances().getFlag() > 10) {
				testFailed("TimeKeys were not gc()ed enough!!!");
			}
		}
		
		int size = TimeKey.keys.size();
		
		boolean error = false;
		
		if (is32Bit() && size > 0) error = true;
		if (is64Bit() && size > 10) error = true;
		
		if (error) {
			if (is32Bit()) {
				testFailed("TimeKey.keys.size() == " + size + " > 0 TimeKey.finalize() IS NOT WORKING CORRECTLY!!!");
			} else {
				testFailed("TimeKey.keys.size() == " + size + " > 10 TimeKey.finalize() IS NOT WORKING CORRECTLY!!!");
			}
		}
		
		if (size == 0) {
			log.info("TimeKey.keys are empty as well!");
		} else {
			log.info("TimeKey.keys are almost-empty as well (64-bit Java)!");
		}
		testOk();
	}
	
	@After
	public void afterTest() {
		try {
			TimeKeyManager.get().unlockAll();
		} catch (Exception e) {			
		}
		try {
			TimeKey.clear();
		} catch (Exception e) {			
		}
	}

}
