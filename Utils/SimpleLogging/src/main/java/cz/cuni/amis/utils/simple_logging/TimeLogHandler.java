package cz.cuni.amis.utils.simple_logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TimeLogHandler extends Handler {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	
	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		String time = dateFormat.format(new Date(record.getMillis()));
		System.out.println("[" + record.getLevel() + "] " + time + " " + record.getMessage());
	}

}
