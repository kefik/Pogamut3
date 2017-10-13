package cz.cuni.amis.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Used to produce text strings during tests, those strings are then consumed by the
 * test case.
 * <p><p>
 * Used to control that everything is OK in tested objects / classes.
 * 
 * @author Jimmy
 */
public class TestOutput {
	
	/**
	 * Flag whether to print the output to console.
	 */
	private static boolean log = false;
	
	/**
	 * Queue with strings somebody produce.
	 */
	private Queue<String> output = new LinkedList<String>();

	private String name;
	
	public TestOutput(String name) {
		this.name = name;
	}
	
	/**
	 * Adds next String to the queue.
	 * @param output
	 */
	public void push(String output) {		
		this.output.add(output);
	}
	
	/**
	 * Consume many strings from the output - if not equals, returns false as failure.
	 * @param outputs
	 * @return
	 */
	public boolean consumeMany(String... outputs) {
		for (String output : outputs) {
			if (!consume(output)) return false;
		}
		return true;
	}
	
	/**
	 * Consume string symbol from the queue - false is failure (string didn't match).
	 * @param output
	 * @return
	 */
	public boolean consume(String output) {
		if (log) System.out.println("CONSUME "+name+" expected : " + String.valueOf(output));
		if (this.output.size() == 0) {
			if (log) System.out.println("CONSUME "+name+": no output, can't consume: " + output);
			return false;
		}
		if (output == null) {	
			String str = this.output.poll();
			boolean result = str == null;
			if (log) 
				if (result) {
					System.out.println("CONSUME "+name+" got      : "+String.valueOf(str));
				} else {
					System.out.println("CONSUME "+name+" got      : "+String.valueOf(str));
				}
			return result;
		}
		String str = this.output.poll();
		boolean result = output.equals(str);
		if (log) 
			if (result) {
				System.out.println("CONSUME "+name+" got      : "+String.valueOf(str));
			} else {
				System.out.println("CONSUME "+name+" got      : "+String.valueOf(str));
			}
		return result;		
	}
	
	/**
	 * Try to consume many strings, false - one of them didn't match, failure.
	 * @param output
	 * @return
	 */
	public boolean consume(String[] output) {
		for (String str : output) {
			if (!consume(str)) return false;		
		}
		return true;
	}
	
	/**
	 * Consume output in any order, returns false if first output.length string in 
	 * the queue can't be matched to the output strings.
	 * @param output
	 * @return
	 */
	public boolean consumeAnyOrder(String[] output) {
		Set<String> outputSet = new HashSet<String>();
		for (String str : output) {
			if (log) System.out.println("CONSUME expected : " + String.valueOf(str));
			outputSet.add(str);
		}
		if (this.output.size() < output.length) {
			if (log) {
				System.out.println("CONSUME "+name+": not enough output (size = " + this.output.size() + "), needed " + output.length);
				while (this.output.size() != 0) {
					System.out.println("CONSUME "+name+" listing left output: " + String.valueOf(this.output.poll()));
				}
			}
			return false;
		}
		for (int i = 0; i < output.length; ++i) {
			String str = this.output.poll();
			if (outputSet.contains(str)) {
				if (log) System.out.println("CONSUME "+name+" got      : "+str);
				outputSet.remove(str);
			} else {
				if (log) {
					System.out.println("CONSUME "+name+" got wrong: " + str);
					while (this.output.size() != 0) {
						System.out.println("CONSUME "+name+" listing left output: " + String.valueOf(this.output.poll()));
					}
				}
				return false;
			}
		}
		return true;	
	}
	
	/**
	 * Whether the queue is clear.
	 * @return
	 */
	public boolean isClear(boolean printIfNot) {
		if (log) System.out.println("OUTPUT "+name+" cleared?");
		if (output.size() == 0) {
			if (log) System.out.println("OUTPUT "+name+" yes");
			return true;
		} else {
			if (log) System.out.println("OUTPUT "+name+" NO");
			if (printIfNot && log) {
				printOutput();
			}
			return false;
		}
	}

	/**
	 * Clear the queue.
	 */
	public void clear() {
		output.clear();		
	}
	
	public void printOutput() {
		for (String str : this.output) {
			System.out.println("OUTPUT " + name + ": " + str);
		}
	}

	/**
	 * Do we print the messages to the console (simple logging),
	 * @return
	 */
	public static boolean isLog() {
		return log;
	}

	/**
	 * Set simple console logging.
	 * @param log
	 */
	public static void setLog(boolean log) {
		TestOutput.log = log;
	}

}
