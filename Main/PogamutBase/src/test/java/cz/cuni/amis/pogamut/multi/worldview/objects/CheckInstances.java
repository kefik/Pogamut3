package cz.cuni.amis.pogamut.multi.worldview.objects;

import java.lang.ref.WeakReference;

import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKey;
import cz.cuni.amis.pogamut.multi.utils.timekey.TimeKeyManager;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchBeginEventStub;
import cz.cuni.amis.pogamut.multi.worldview.events.BatchEndEventStub;
import cz.cuni.amis.utils.Const;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;

public class CheckInstances {
	
	public static void log() {
		StringBuffer sb = new StringBuffer();
		sb.append("===============================" + Const.NEW_LINE);
		sb.append("TimeKey #instances:                    " + TimeKey.getInstances().getFlag() + Const.NEW_LINE);
		sb.append("BatchBeginEventStub #instances:        " + BatchBeginEventStub.getInstances().getFlag() + Const.NEW_LINE);
		sb.append("BatchEndEventStub #instances:          " + BatchEndEventStub.getInstances().getFlag() + Const.NEW_LINE);
		sb.append("TestCompositeObjectImpl #instances:    " + TestCompositeObjectImpl.getInstances().getFlag() + Const.NEW_LINE);
		sb.append("TestCompositeObjectMessage #instances: " + TestCompositeObjectMessage.getInstances().getFlag() + Const.NEW_LINE);
		sb.append("TestLocalObjectImpl #instances:        " + TestLocalObjectImpl.getInstances().getFlag() + Const.NEW_LINE);
		sb.append("TestSharedObjectImpl #instances:       " + TestSharedObjectImpl.getInstances().getFlag() + Const.NEW_LINE);
		sb.append("TestStaticObjectImpl #instances:       " + TestStaticObjectImpl.getInstances().getFlag() + Const.NEW_LINE);
		sb.append("TimeKeyManager held keys: ");
		boolean first = true;
		for (Long key : TimeKeyManager.get().getHeldKeys()) {
			if (first) first = false;
			else sb.append(", ");
			sb.append(key);
		}
		sb.append(Const.NEW_LINE);
		sb.append("TimeKey key set: ");
		first = true;
		for (WeakReference<TimeKey> keyRef : TimeKey.getAllKeys()) {
			TimeKey key = keyRef.get();
			if (key == null) continue;
			if (first) first = false;
			else sb.append(", ");
			sb.append(key);
		}
		System.out.println(sb.toString());
	}
	
	public static boolean checkAllGCed() {
		return checkAllGCed(0);
	}
	
	public static boolean checkAllGCed(int maxTimeKeyInstances) {
		return 
			TimeKey.getInstances().getFlag()                    <= maxTimeKeyInstances &&
			BatchBeginEventStub.getInstances().getFlag()        == 0 &&
			BatchEndEventStub.getInstances().getFlag()          == 0 &&
			TestCompositeObjectImpl.getInstances().getFlag()    == 0 &&
			TestCompositeObjectMessage.getInstances().getFlag() == 0 &&
			TestLocalObjectImpl.getInstances().getFlag()        == 0 &&
			TestSharedObjectImpl.getInstances().getFlag()       == 0 &&
			TestStaticObjectImpl.getInstances().getFlag()       == 0;
	}
	
	public static void waitGCTotal() {
		waitGCTotal(0);
	}
	
	public static void waitGCTotal(int maxTimeKeyInstances) {
		System.gc();
		int i = 0;
		while (!CheckInstances.checkAllGCed(maxTimeKeyInstances) && i < 20) {			
			CheckInstances.log();
			System.out.println((i+1) + " / 20: Not all instances (objects/events/timekeys) have been GC()ed yet...");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new PogamutInterruptedException(e, CheckInstances.class);
			}
			System.gc();
			++i;
		}
		
		if (i > 0) {
			CheckInstances.log();
		}
		
		if (!CheckInstances.checkAllGCed(maxTimeKeyInstances)) {
			CheckInstances.log();
			System.out.println("[ERROR] Not all instances (objects/events/timekeys) have been GC()ed in 10 secs!");
			throw new RuntimeException("Not all instances (objects/events/timekeys) have been GC()ed in 10 secs!");
		} else {
			int timeKeys = TimeKey.getInstances().getFlag();
			if (timeKeys == 0) {
				System.out.println("[OK] All instances (objects/events/timekeys) have been GC()ed.");
			} else {
				System.out.println("[OK] All instances (objects/events/timekeys) have been GC()ed excepti TimeKey(s), but they are in tolerance " + timeKeys + " <= " + maxTimeKeyInstances + ".");
			}
		}
	}

}
