package cz.cuni.amis.tests;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Activity logs serves to check "strings" which represents the behavior of some component.
 * <p><p>
 * You may use {@link ActivityLog#expectExactOrder(String...)} and {@link ActivityLog#expectAnyOrder(String...)} 
 * to initialize the object and {@link ActivityLog#activity(String...)} to report the activity.
 * 
 * @author Jimmy
 */
public class ActivityLog {
	
	private static final String NEW_LINE = System.getProperty("line.separator");

	private static interface IActivityCheck {
		
		public void add(String... activities);
		
		public void check(String activity);
		
		public boolean checkNoException(String activity);
		
		public boolean isEmpty();
		
	}
	
	private static abstract class AbstractCheck implements IActivityCheck {
		
		public void check(String activity) {
			if (checkNoException(activity)) return;
			throw new RuntimeException("Unexpected activity: " + activity + NEW_LINE + this);
		}
		
	}
	
	private static class AnyOrder extends AbstractCheck {

		private Set<String> activities = new HashSet<String>();

		public AnyOrder(String... activities) {
			add(activities);
		}
		
		@Override
		public void add(String... activities) {
			for (String activity : activities) {
				this.activities.add(activity);
			}
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("ActivityAnyOrder[");
			if (activities.size() == 0) {
				sb.append("NOTHING! activity.size() == 0");
			} else {
				for (String a : activities) {
					sb.append(NEW_LINE);
					sb.append(a);
				}
			}
			sb.append(NEW_LINE);
			sb.append("]");
			return sb.toString();
		}

		@Override
		public boolean checkNoException(String activity) {
			if (this.activities.contains(activity)) {
				this.activities.remove(activity);
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public boolean isEmpty() {
			return this.activities.size() == 0;
		}
		
	}
	
	private static class ExactOrder extends AbstractCheck {

		private List<String> activities = new LinkedList<String>();

		public ExactOrder(String... activities) {
			add(activities);
		}
		
		@Override
		public void add(String... activities) {
			for (String activity : activities) {
				this.activities.add(activity);
			}
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("ActivityExactOrder[");
			if (activities.size() == 0) {
				sb.append("    NOTHING! activity.size() == 0");
			} else {
				for (String a : activities) {
					sb.append(NEW_LINE);
					sb.append("    ");
					sb.append(a);
				}
			}
			sb.append(NEW_LINE);
			sb.append("]");
			return sb.toString();
		}

		@Override
		public boolean checkNoException(String activity) {
			if (isEmpty()) return false;
			String checkingActivity = this.activities.get(0); 
			if (checkingActivity.equals(activity)) {
				this.activities.remove(0);
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public boolean isEmpty() {
			return this.activities.size() == 0;
		}
		
	}
	
	private List<IActivityCheck> activities = new LinkedList<IActivityCheck>();
	
	private Logger log;
	
	public ActivityLog(Logger log) {
		this.log = log;
	}
	
	public ActivityLog expectAnyOrder(String... activities) {
		if (activities == null || activities.length == 0) return this;
		this.activities.add(new AnyOrder(activities));
		return this;
	}
	
	public ActivityLog expectExactOrder(String... activities) {
		if (activities == null || activities.length == 0) return this;
		this.activities.add(new ExactOrder(activities));
		return this;
	}
	
	public synchronized void activity(String... activity) {
		for (String a : activity) {
			if (activityNoException(a)) {					
				continue;
			}
			throw new RuntimeException("Unexpected activity: " + a + NEW_LINE + this);
		}
	}
	
	public synchronized boolean activityNoException(String... activity) {
		for (String a : activity) {
			if (isEmpty()) return false;
			if (activities.get(0).isEmpty()) {
				activities.remove(0);
				if (isEmpty()) return false;
			}
			if (!activities.get(0).checkNoException(a)) return false;
			log.info("Expected activity: " + a);
			while (!isEmpty() && activities.get(0).isEmpty()) activities.remove(0); 
		}
		return true;
	}
	
	public boolean isEmpty() {		
		return this.activities.size() == 0;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ActivityLog[");
		if (activities.size() == 0) {
			sb.append(NEW_LINE);
			sb.append("No more activities expected!");
		} else {
			for (IActivityCheck a : activities) {
				sb.append(NEW_LINE);
				sb.append(a);
			}
		}
		sb.append(NEW_LINE);
		sb.append("]");
		return sb.toString();
	}

	public void checkNoMoreActivityExpected() {
		if (isEmpty()) {
			log.info("No more activity expected, OK!");
			return;
		}
		throw new RuntimeException("More activity expected!" + NEW_LINE + this);
	}
	
}
