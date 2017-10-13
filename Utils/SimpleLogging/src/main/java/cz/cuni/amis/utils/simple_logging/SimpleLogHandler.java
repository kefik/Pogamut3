package cz.cuni.amis.utils.simple_logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class SimpleLogHandler extends Handler {

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

}
