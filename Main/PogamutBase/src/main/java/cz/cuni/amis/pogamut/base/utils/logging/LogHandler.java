package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Handler for the messages - instantiated without publisher.
 * <p><p>
 * Use setPublisher() or constructor with ILogPublisher parameter.
 * <p><p>
 * See LogPublisher and it's public static inner classes ConsolePublisher and FilePublisher.
 *  
 * @author Jimmy
 *
 */
public class LogHandler extends Handler implements Cloneable {
	
	public static class ConsoleLogHandler extends LogHandler {
		
		public ConsoleLogHandler() {
			super(new LogPublisher.ConsolePublisher());
		}
		
	}
	
	protected ILogPublisher publisher = null;
	
	/**
	 * Creates empty log handler without any publisher.
	 */
	public LogHandler() {			
	}
	
	@Override
	public int hashCode() {
		return 12345678;
	}
	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof LogHandler)) return false;
		return publisher.equals(((LogHandler)obj).publisher);
	}
	
	/**
	 * Creates log handler with specific publisher.
	 * @param publisher
	 */
	public LogHandler(ILogPublisher publisher) {
		this.publisher = publisher;
	}
	
	/**
	 * Returns actual publisher of the hanlder.
	 * @return
	 */
	public synchronized ILogPublisher getPublisher() {
		return publisher;
	}

	/**
	 * Sets new publisher to the handler.
	 * @param publisher
	 */
	public synchronized void setPublisher(ILogPublisher publisher) {
		this.publisher = publisher;
	}
	
	@Override
	public synchronized void close() throws SecurityException {
		ILogPublisher actualPublisher = publisher;		
		if (actualPublisher != null) actualPublisher.close();		
	}

	@Override
	public synchronized void flush() {
		ILogPublisher actualPublisher = publisher;		
		if (actualPublisher != null) actualPublisher.flush();		
	}

	@Override
	public synchronized void publish(LogRecord record) {
		ILogPublisher actualPublisher = publisher;		
		if (actualPublisher != null) actualPublisher.publish(record);
	}

}
