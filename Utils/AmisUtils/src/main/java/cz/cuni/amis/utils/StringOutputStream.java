package cz.cuni.amis.utils;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * OutputStream for the String, writes to StringBuffer.
 * <p><p>
 * Based on: http://kickjava.com/src/jodd/util/StringOutputStream.java.htm
 * Thank you!
 */
public class StringOutputStream extends OutputStream implements Serializable {

	/**
	 * The internal destination StringBuffer.
	 */
	private StringBuffer buffer = null;
	
	private String resultString = null;

	/**
	 * Creates new StringOutputStream, makes a new internal StringBuffer.
	 */
	public StringOutputStream() {
		super();
		buffer = new StringBuffer();
	}

	/**
	 * Returns string from the underlying buffer
	 * @return
	 */
	public String getString() {
		if (resultString != null) return resultString;
		return buffer.toString();
		
	}

	@Override
	public void close() {
		resultString = buffer.toString();
		buffer = null;
	}

	@Override
	public void write(byte[] b) {
		buffer.append(toCharArray(b));
	}

	private char[] toCharArray(byte[] barr) {
		if (barr == null) {
			return null;
		}
		char[] carr = new char[barr.length];
		for (int i = 0; i < barr.length; i++) {
			carr[i] = (char) barr[i];
		}
		return carr;
	}

	@Override
	public void write(byte[] b, int off, int len) {
		if ((off < 0) || (len < 0) || (off + len) > b.length) {
			throw new IndexOutOfBoundsException("StringOutputStream.write: Parameters out of bounds.");
		}
		byte[] bytes = new byte[len];
		for (int i = 0; i < len; i++) {
			bytes[i] = b[off];
			off++;
		}
		buffer.append(toCharArray(bytes));
	}

	@Override
	public void write(int b) {
		buffer.append((char) b);
	}

}