package cz.cuni.amis.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Run {
		
	public static TimerTaskExt once(long delay, Runnable runnable) {
		return getInstance().runOnce(delay, runnable);
	}
	
	public static enum TimerTaskState {
		SCHEDULED,
		RUNNING,
		SUCCESS,
		EXCEPTION,
		CANCELLED
	}

	public static class TimerTaskExt extends TimerTask {
		
		private String name;
		
		private Runnable runnable;
		
		private TimerTaskState state = TimerTaskState.SCHEDULED;
		
		private Throwable exception;
			
		public TimerTaskExt(String name, Runnable runnable) {
			this.runnable = runnable;
		}
		
		public String getName() {
			return name;
		}

		@Override
		public void run() {
			try {
				state = TimerTaskState.RUNNING;
				runnable.run();
			} catch (Exception e) {
				exception = e;
				state = TimerTaskState.EXCEPTION;
				return;
			}
			state = TimerTaskState.SUCCESS;
		}
		
		public TimerTaskState getState() {
			return state;
		}
		
		public Throwable getException() {
			return exception;
		}
		
		public boolean isState(TimerTaskState state) {
			return this.state == state;
		}
		
		public boolean isScheduled() {
			return state == TimerTaskState.SCHEDULED;
		}
		
		public boolean isRunning() {
			return state == TimerTaskState.RUNNING;
		}
		
		public boolean isSuccess() {
			return state == TimerTaskState.SUCCESS;
		}
		
		public boolean isException() {
			return state == TimerTaskState.EXCEPTION;
		}
		
		public boolean isCancelled() {
			return state == TimerTaskState.CANCELLED;
		}
		
		@Override
		public boolean cancel() {
			if (super.cancel()) {
				state = TimerTaskState.CANCELLED;
				return true;
			}
			return false;
		}
	}
		
	public static Run getInstance() {
		if (instance != null) return instance;
		synchronized(mutex) {
			if (instance != null) return instance;
			instance = new Run();
		}
		return instance;
	}
	
	private static Object mutex = new Object();
	
	private static Run instance = null;	
	
	// ==============
	// IMPLEMENTATION
	// ==============
	
	private Calendar calendar = Calendar.getInstance();
	
	private Timer timer = new Timer("PogamutRunDeamon", true);
	
	public Date getFutureDate(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MILLISECOND, (int)millis);
		return calendar.getTime();
	}
	
	public TimerTaskExt runOnce(long delay, Runnable runnable) {
		TimerTaskExt task = new TimerTaskExt("Once", runnable);
		timer.schedule(task, delay);
		return task;
	}

}
