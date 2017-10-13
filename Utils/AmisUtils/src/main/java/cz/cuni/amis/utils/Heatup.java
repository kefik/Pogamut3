package cz.cuni.amis.utils;

import java.util.concurrent.TimeUnit;

/**
 * This class allows you to easily setup heatup for any effect you need. I.e., something happens and you need to pursue
 * some behavior/effect for amount of time. Just create heatup with specified amount of time 
 * "for the heat" and use {@link Heatup#isHot()} to check whether you should still be using your behavior/effect.
 * <p><p>
 * To make your heatup hot, just call {@link Heatup#heat()}.
 * 
 * @author Jimmy
 */
public class Heatup {

	/**
	 * How long is the heatup.
	 */
	private long heatupMillis;
	
	/**
	 * When the cooldown was last used.
	 */
	private long lastUsedMillis;

	public Heatup(long cooldownMillis) {
		this(cooldownMillis, TimeUnit.MILLISECONDS);
	}

	public Heatup(long cooldownTime, TimeUnit timeUnit) {
		switch(timeUnit) {
		case DAYS: this.heatupMillis = cooldownTime * 24 * 60 * 60 * 1000; break;
		case HOURS: this.heatupMillis = cooldownTime * 60 * 60 * 1000; break;
		case MICROSECONDS: throw new UnsupportedOperationException("Unsupported: MICROSECONDS.");
		case MILLISECONDS: this.heatupMillis = cooldownTime; break;
		case MINUTES: this.heatupMillis = cooldownTime * 60 * 1000; break;
		case NANOSECONDS:  throw new UnsupportedOperationException("Unsupported: NANOSECONDS.");
		case SECONDS: this.heatupMillis = cooldownTime * 1000; break;
		}
	}
	
	/**
	 * Check whether it is still hot.
	 * @return
	 */
	public boolean isHot() {
		return (System.currentTimeMillis() - lastUsedMillis < heatupMillis);
	}
	
	/**
	 * Force use of the effect == sets {@link Heatup#lastUsedMillis} to current time.
	 */
	public void heat() {
		lastUsedMillis = System.currentTimeMillis();
	}
	
	/**
	 * How much time we still have until the object becomes cold, i.e., !{@link Heatup#isHot()}.
	 * @return
	 */
	public long getRemainingTime() {
		if (!isHot()) return 0;
		return heatupMillis - (System.currentTimeMillis() - lastUsedMillis);
	}

	/**
	 * Removes the heat...
	 */
	public void clear() {
		lastUsedMillis = 0;
	}

	/**
	 * Check whether we're cool == !{@link Heatup#isHot()}.
	 * @return
	 */
	public boolean isCool() {
		return !isHot();
	}
	
}
