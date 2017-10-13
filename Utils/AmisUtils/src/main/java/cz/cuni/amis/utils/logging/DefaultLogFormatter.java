package cz.cuni.amis.utils.logging;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Pogamut custom formatter used as default.
 *
 * @author Jimmy
 */
public class DefaultLogFormatter extends Formatter {
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	
	private static String longestUnitName = "";
	
	private static String longestComponentName = "";
	
	private StringBuffer buffer = new StringBuffer(512);

	/**
	 * Whether to append {@link DefaultLogFormatter#lineEnd} after the log message.
	 */
	protected boolean lineEnds = false;

	protected String lineEnd = "\r\n";
	
	protected String name = null;

	public static final String[] whitespaces = new String[] {
		"        ",
		"       ",
		"      ",
		"     ",
		"    ",
		"   ",
		"  ",
		" ",
		""
	};
	
	public DefaultLogFormatter() {
		this(null, false);
	}
	
	public DefaultLogFormatter(String unitName) {
		this.name = unitName;
	}
	
	public DefaultLogFormatter(String unitName, boolean appendLineEnd) {
		this.name = unitName;
		this.lineEnds = appendLineEnd;
	}
	
	@Override
	public synchronized String format(LogRecord record) {
		buffer.delete(0, buffer.length());

		if (name != null) {
			String n = name;
			if (n == null) n = "null";
			if (n.length() > longestUnitName.length()) {
				StringBuffer longest = new StringBuffer();
				for (int i = 0; i < n.length(); ++i) {
					longest.append(" ");
				}
				longestUnitName = longest.toString();
			}
			buffer.append("(");
			buffer.append(n);
			buffer.append(") ");
			int count = longestUnitName.length() - n.length();
			for (int i = 0; i < count; ++i) {
				buffer.append(" ");
			}
		} else {
			if (longestUnitName.length() > 0) {
				buffer.append("()");
				buffer.append(longestUnitName);
			} else {
				buffer.append("() ");
			}
		}
		
		buffer.append("[");
		buffer.append(record.getLevel().toString());
		buffer.append("]");
		buffer.append(whitespaces[record.getLevel().toString().length()]);

		buffer.append(dateFormat.format(new Date(record.getMillis())));
		
		buffer.append(" ");
		
		String n = record.getLoggerName();
		if (n == null) n = "null";
		if (n.length() > longestComponentName.length()) {
			StringBuffer longest = new StringBuffer();
			for (int i = 0; i < n.length(); ++i) {
				longest.append(" ");
			}
			longestComponentName = longest.toString();
		}
		buffer.append("<");
		buffer.append(n);
		buffer.append("> ");
		int count = longestComponentName.length() - n.length();
		for (int i = 0; i < count; ++i) {
			buffer.append(" ");
		}
		
		Object[] params = record.getParameters();
		if (params == null || params.length <= 1) {
		    buffer.append(record.getMessage());
		} else {
		     Object[] passedParams = Arrays.copyOfRange(params, 1, params.length);
		     buffer.append(MessageFormat.format(record.getMessage(), passedParams));
		}
		
		if (lineEnds) {
			buffer.append(lineEnd);
		}
		
		return buffer.toString();
	}

}
