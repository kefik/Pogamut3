package cz.cuni.amis.utils;

import java.util.concurrent.TimeUnit;

public class TimeUnitToMillis {
	
	public static long toMillis(long timeout, TimeUnit unit) {
		switch(unit) {
		case DAYS: return timeout * 24 * 60 * 60 * 1000;
		case HOURS: return timeout * 60 * 60 * 1000;
		case MINUTES: return timeout * 60 * 1000;
		case SECONDS: return timeout * 1000;
		case MILLISECONDS: return timeout;
		case MICROSECONDS: return (long) Math.ceil(((double)timeout) / 1000);
		case NANOSECONDS: return (long) Math.ceil(((double)timeout) / 1000000);
		default: throw new IllegalArgumentException("unhandled time unit: " + unit);
		}
	}

}
