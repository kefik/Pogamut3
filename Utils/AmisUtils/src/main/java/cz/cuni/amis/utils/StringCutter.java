package cz.cuni.amis.utils;

import java.util.Arrays;

/**
 * You will stuff strings into the object and it will check whether the 'limiter'
 * is present. If so, it will cut the string buffer along the 'limiter' and return
 * you the parts.
 * 
 * @author Jimmy
 */
public class StringCutter {
		
	private String limiter = "\r\n";
	
	private StringBuffer buffer = new StringBuffer(200);
	
	private String[] empty = new String[0];
	
	/**
	 * Default limiter is "\r\n" (windows new line).
	 */
	public StringCutter() {			
	}
	
	public StringCutter(String limiter) {
		this.limiter = limiter;
	}
	
	/**
	 * Adding string to the buffer, returning strings if 'limiter' is found.
	 * If no limiter is present, returns String[0].
	 * @param str
	 * @return
	 */
	public String[] add(String str) {
		buffer.append(str);
		if (buffer.indexOf(limiter) > 0) {
			String bufferString = buffer.toString();
			String[] strs = bufferString.split(limiter);
			String[] result;
			if (bufferString.endsWith(limiter)) {
				result = strs;
				buffer.delete(0, buffer.length());
			} else {
				result = Arrays.copyOf(strs, strs.length-1);
				buffer.delete(0, buffer.length());
				buffer.append(strs[strs.length-1]);
			}
			return result;
		}	
		return empty;			
	}
	
	/**
	 * Clear the string buffer.
	 */
	public void clear() {
		buffer.delete(0, buffer.length());
	}
	
}
