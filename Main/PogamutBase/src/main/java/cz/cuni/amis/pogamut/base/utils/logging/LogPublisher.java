package cz.cuni.amis.pogamut.base.utils.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.utils.FilePath;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutIOException;

/**
 * Implementation for the ILogPublisher interface that contains a Formatter for the LogRecords.
 * <p>
 * As default SimpleFormatter from the java.logging API is used.
 * <p><p>
 * Contains two default implementations ConsolePublisher and FilePublisher.
 * 
 * @author Jimmy
 */
public abstract class LogPublisher implements ILogPublisher {
	
	protected Formatter formatter = null;
	
	public LogPublisher() {
		formatter = new LogFormatter();
	}
	
	public LogPublisher(IAgentId name) {
		formatter = new LogFormatter(name);
	}
	
	public LogPublisher(Formatter formatter) {
		this.formatter = formatter;
		if (this.formatter == null) {
			formatter = new LogFormatter();
		}
	}
	
	public Formatter getFormatter() {
		return formatter;
	}
	
	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public abstract void close() throws SecurityException;

	@Override
	public abstract void flush();
	
	public abstract void publish(LogRecord record, String formattedMsg);

	@Override
	public synchronized void publish(LogRecord record) {
		Formatter actualFormatter = formatter;
		if (actualFormatter != null) publish(record, actualFormatter.format(record));
	}
	
	//
	// Follows simple implementation of publishers
	//
	
	public static class ConsolePublisher extends LogPublisher {

		public ConsolePublisher() {
			super();
		}
		
		public ConsolePublisher(IAgentId name) {
			super(name);
		}
		
		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(LogRecord record, String formattedMsg) {
			System.out.println(formattedMsg);
		}
		
	}
	
	public static class FilePublisher extends LogPublisher {
		
		protected File file;
		protected FileOutputStream fileOut;
		protected PrintWriter fileWriter;
		protected boolean immediateFlush = false;
		
		public FilePublisher(File file) throws PogamutIOException {
			this(file, null);
		}
		
		public FilePublisher(File file, Formatter formatter) throws PogamutIOException {
			super(formatter);
			NullCheck.check(file, "file");
			this.file = file;
			FilePath.makeDirsToFile(file);
			try {
				fileOut = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				throw new PogamutIOException("Can't open file '" + file.getAbsolutePath() + "' for logging.", e);
			}
			fileWriter = new PrintWriter(fileOut);
		}
		
		public boolean isImmediateFlush() {
			return immediateFlush;
		}

		public void setImmediateFlush(boolean immediateFlush) {
			this.immediateFlush = immediateFlush;
		}

		public File getFile() {
			return file;
		}

		@Override
		public void close() throws SecurityException {
			try {
				fileWriter.close();
			} catch (Exception e) {				
			}
			try {
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void flush() {
			fileWriter.flush();
			try {
				fileOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public synchronized void publish(LogRecord record, String formattedMsg) {
			fileWriter.println(formattedMsg);		
			if (immediateFlush) flush();
		}
		
	}

}
