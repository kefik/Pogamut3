package cz.cuni.amis.utils.simple_logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class SimpleLogging {
	
	public static void initLogging() {
		initLogging(false);
	}
	
	public static void initLogging(boolean time) {
		
		Logger log = Logger.getAnonymousLogger();
		while (log != null) {
			for (Handler handler : log.getHandlers()) {
				log.removeHandler(handler);
			}
			if (log.getParent() == null) {
				if (time) {
					log.addHandler(new TimeLogHandler());
				} else {
					log.addHandler(new SimpleLogHandler());
				}
			}
			log = log.getParent();
		}		
	}
	
	public static void addConsoleLogging() {
		Logger log = Logger.getAnonymousLogger();
		while (log != null) {
			if (log.getParent() == null) {
				log.addHandler(new Handler() {
					
					@Override
					public void close() throws SecurityException {
					}

					@Override
					public void flush() {
					}

					@Override
					public void publish(LogRecord record) {
						System.out.println("[" + record.getLevel() + "] " + record.getMessage());
					}
					
				});
			}
			log = log.getParent();
		}	
	}

	
	public static void addFileLogging(final String pathToFile) {
		Logger log = Logger.getAnonymousLogger();
		
		
		try {
			final PrintWriter writer = new PrintWriter(new FileWriter(new File(pathToFile)));
			
			while (log != null) {
				if (log.getParent() == null) {
					log.addHandler(new Handler() {
						
						@Override
						public void close() throws SecurityException {
							writer.close();
						}

						@Override
						public void flush() {
							writer.flush();
						}

						@Override
						public void publish(LogRecord record) {
							writer.println("[" + record.getLevel() + "] " + record.getMessage());
						}
						
					});
				}
				log = log.getParent();
			}	
			
		} catch (IOException e) {
			throw new RuntimeException("Could not open file " + new File(pathToFile).getAbsolutePath() + " for logging!", e);
		}
		
	}

}
