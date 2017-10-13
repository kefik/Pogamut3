package cz.cuni.amis.utils;

import java.util.concurrent.TimeUnit;

/**
 * This class allows you to easily setup cooldown for any effect you need. Just create cooldown with specified amount of time 
 * "for the cooldown" and use {@link Cooldown#tryUse()} to check whether you may use your effect now.
 * <p><p>
 * Opposite functionality (doing something until some time passes by) is captured by {@link Heatup}.
 * 
 * @author Jimmy
 */
/**
 * @author Jimmy
 *
 */
public class Cooldown {

	/**
	 * How long is the cooldown.
	 */
	private long cooldownMillis;
	
	/**
	 * When the cooldown was last used.
	 */
	private long lastUsedMillis;

	public Cooldown(long cooldownMillis) {
		this(cooldownMillis, TimeUnit.MILLISECONDS);
	}

	public Cooldown(long cooldownTime, TimeUnit timeUnit) {
		switch(timeUnit) {
		case DAYS: this.cooldownMillis = cooldownTime * 24 * 60 * 60 * 1000; break;
		case HOURS: this.cooldownMillis = cooldownTime * 60 * 60 * 1000; break;
		case MICROSECONDS: throw new UnsupportedOperationException("Unsupported: MICROSECONDS.");
		case MILLISECONDS: this.cooldownMillis = cooldownTime; break;
		case MINUTES: this.cooldownMillis = cooldownTime * 60 * 1000; break;
		case NANOSECONDS:  throw new UnsupportedOperationException("Unsupported: NANOSECONDS.");
		case SECONDS: this.cooldownMillis = cooldownTime * 1000; break;
		}
	}
	
	/**
	 * Check whether it is {@link Cooldown#isCool()}, if so, save current time as the time of the use and returns true,
	 * otherwise (== effect needs more cooldown, see {@link Cooldown#getRemainingTime()}) returns false.
	 * @return
	 */
	public boolean tryUse() {
		long time = System.currentTimeMillis();
		if (time - lastUsedMillis >= cooldownMillis) {
			this.lastUsedMillis = time;
			return true;
		}
		return false;
	}
	
	/**
	 * Force use of the effect == sets {@link Cooldown#lastUsedMillis} to current time.
	 */
	public void use() {
		lastUsedMillis = System.currentTimeMillis();
	}
	
	/**
	 * Whether you may use the effect, i.e., it was never used before or has cooled down.
	 * @return
	 */
	public boolean isCool() {
		return (System.currentTimeMillis() - lastUsedMillis >= cooldownMillis);
	}
	
	/**
	 * Whether we're not {@link Cooldown#isCool()}.
	 */
	public boolean isHot() {
		return !isCool();
	}
	
	/**
	 * How much time we need to wait before the effect will cool down.
	 * @return
	 */
	public long getRemainingTime() {
		if (isCool()) return 0;
		return cooldownMillis - (System.currentTimeMillis() - lastUsedMillis);
	}
	
	/**
	 * Cools down totally.
	 */
	public void clear() {
		lastUsedMillis = 0;
	}
	
}
