package cz.cuni.pogamut.ut2004.levelgeom.utils;

public class StopWatch {
	
	private long start;
	
	private double time = -1;

	public StopWatch() {
		start();
	}
	
	/**
	 * Watches are start()ed during construction, this will just refresh the start time. 
	 */
	public void start() {
		start = System.nanoTime();
	}
	
	/**
	 * In millis
	 * @return
	 */
	public double stop() {
		time = (((double)System.nanoTime()) - start)/1000000;
		start = System.nanoTime();
		return time;
	}
	
	/**
	 * In millis
	 * @return
	 */
	public double check() {
		time = (((double)System.nanoTime()) - start)/1000000;		
		return time;
	}
	
	/**
	 * In millis... returns last stop()/check() time. (Use stopStr() and then obtain time with time().)
	 * @return
	 */
	public double time() {
		if (time == -1) {
			return (((double)System.nanoTime()) - start)/1000000;
		}
		return time;
	}
	
	public String stopStr() {
		return String.format("%.3f", stop()) + " ms";
	}
	
	public String checkStr() {
		return String.format("%.3f", check()) + " ms";
	}

}
