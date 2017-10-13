package cz.cuni.amis.pogamut.base.utils.logging;

import java.util.logging.LogRecord;

/**
 * Java logging API relies on handlers for publishing records, we have
 * created one instance of this Handler (LogHandler instnace) and delegate
 * abstract methods from Handler on the publisher interface.
 * <p><p>
 * Default implementation LogPublisher exists which is using Formatter
 * to format the LogRecord - inside this LogPublisher you will find
 * public static inner class with default implementation for publishing
 * log records to Console or File (LogPublisher.ConsolePublisher and
 * LogPublisher.FilePublisher classes).
 * 
 * @author Jimmy
 */
public interface ILogPublisher {
	
	/**
	 * From JavaDoc API:
	 * <p>
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
     * <p>
     * The <tt>Handler</tt>  is responsible for formatting the message, when and
     * if necessary.  The formatting should include localization.
     * <hr>
     * 
     *
     * @param  record  description of the log event. A null record is
     *                 silently ignored and is not published
     */
    public void publish(LogRecord record);

    /**
     * From JavaDoc API:
	 * <p>
     * Flush any buffered output.
     */
    public void flush();

    /**
     * From JavaDoc API:
	 * <p>
     * Close the <tt>Handler</tt> and free all associated resources.
     * <p>
     * The close method will perform a <tt>flush</tt> and then close the
     * <tt>Handler</tt>.   After close has been called this <tt>Handler</tt>
     * should no longer be used.  Method calls may either be silently
     * ignored or may throw runtime exceptions.
     *
     * @exception  SecurityException  if a security manager exists and if
     *             the caller does not have <tt>LoggingPermission("control")</tt>.
     */
    public void close() throws SecurityException;

}
