package cz.cuni.amis.pogamut.base.utils.logging.stub;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.LogRecord;

import junit.framework.Assert;
import cz.cuni.amis.pogamut.base.utils.logging.ILogPublisher;
import cz.cuni.amis.utils.Const;

public class CheckPublisher implements ILogPublisher {

	private Queue<String> expect = new LinkedList<String>();
	
	public CheckPublisher() {
	}
	
	public CheckPublisher(String[] lines) {
		expect(lines);
	}

	public void expect(String line) {
		expect.add(line);
	}
	
	public void expect(String[] lines) {
		for (String line : lines) {
			expect.add(line);
		}
	}
	
	public int getExpectSize() {
		return expect.size();
	}
	
	public void checkExpectEmpty() {
		Assert.assertTrue("should not expect any messages", getExpectSize() == 0);
	}
	
	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public synchronized void publish(LogRecord record) {
		System.out.println("CheckPublisher: " + record.getMessage());
		if (expect.size() == 0) throw new RuntimeException("Did not expect anything, got: " + record.getMessage());
		String line = expect.poll();
		if (line.equals(record.getMessage())) return;
		throw new RuntimeException("Wrong log record. " + Const.NEW_LINE + "Expected: '" + line + "'" + Const.NEW_LINE + "Got:      '" + record.getMessage() + "'.");
	}

}
