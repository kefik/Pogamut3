package cz.cuni.amis.pogamut.base.utils.logging.network;

import java.util.logging.Level;

import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogClient;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogEnvelope;
import cz.cuni.amis.pogamut.base.utils.logging.NetworkLogManager;
import cz.cuni.amis.utils.Throughput;

public class CheckNetworkLogClient extends NetworkLogClient {

	private int myLogFinestLogs  = 0;
	private int myLogFinerLogs   = 0;
	private int myLogFineLogs    = 0;
	private int myLogInfoLogs    = 0;
	private int myLogWarningLogs = 0;
	private int myLogSevereLogs  = 0;
	
	private Throughput throughput = new Throughput("bytes");
	
	private int lastLogNumber = -1;
	
	private boolean firstLog = true;
	
	private ILogReadListener logReadListener = new ILogReadListener() {
		
		@Override
		public void notify(LogRead event) {
			if (firstLog) {
				firstLog = false;
				log.info("First log received: " + event.getRecord());
			}
			NetworkLogEnvelope log  = event.getRecord();
			//System.out.println("RECEIVED:" + log);
			throughput.add(log.toString().length());
			if (log.getCategory().equals("my-log") && log.getMessage().startsWith("!!!")) {
				int index = log.getMessage().lastIndexOf("-") + 2;
				int num = 0;
				try {
					num = Integer.parseInt(log.getMessage().substring(index));
				} catch (NumberFormatException e) {
					System.out.println("ouch");
					throw e;
				}
				if (num == 0) {
					lastLogNumber = 0;
				} else {
					if (lastLogNumber != num && lastLogNumber + 1 != num) {
						System.out.println("[ERROR] Last log number: " + lastLogNumber + ", current log number = " + num);
						throw new RuntimeException("[ERROR] Last log number: " + lastLogNumber + ", current log number = " + num);
					}
					lastLogNumber = num;
				}
				if (log.getLevel() == Level.FINEST) {
					++myLogFinestLogs;
				} else
				if (log.getLevel() == Level.FINER) {
					++myLogFinerLogs;
				} else
				if (log.getLevel() == Level.FINE) {
					++myLogFineLogs;
				} else
				if (log.getLevel() == Level.INFO) {
					++myLogInfoLogs;
				} else	
				if (log.getLevel() == Level.WARNING) {
					++myLogWarningLogs;
				} else	
				if (log.getLevel() == Level.SEVERE) {
					++myLogSevereLogs;
				}
			}
		}
	};
	private String name;
	
	public CheckNetworkLogClient(String name, String address, int port, String agentId) {
		super(address, port, agentId);
		addListener(logReadListener);
		getLogger().setLevel(Level.ALL);
		this.name = name;
		this.throughput.setReporting(true);
		this.throughput.setName(name + "-" + "Throughput");
	}
	
	public String getName() {
		return name;
	}

	public Throughput getThroughput() {
		return throughput;
	}
	
	public void checkLogNumber(int number) {
		if (myLogFinestLogs != number) {
			System.out.println(        "[ERROR] " + name + " finest logs number  = " + myLogFinestLogs  + " != " + number);
			throw new RuntimeException("[ERROR] " + name + " finest logs number  = " + myLogFinestLogs  + " != " + number);
		}
		if (myLogFinerLogs != number) {
			System.out.println(        "[ERROR] " + name + " finer logs number   = " + myLogFinerLogs   + " != " + number);
			throw new RuntimeException("[ERROR] " + name + " finer logs number   = " + myLogFinerLogs   + " != " + number);
		}
		if (myLogFineLogs != number) {
			System.out.println(        "[ERROR] " + name + " fine logs number    = " + myLogFineLogs    + " != " + number);
			throw new RuntimeException("[ERROR] " + name + " fine logs number    = " + myLogFineLogs    + " != " + number);
		}
		if (myLogInfoLogs != number) {
			System.out.println(        "[ERROR] " + name + " info logs number    = " + myLogInfoLogs    + " != " + number);
			throw new RuntimeException("[ERROR] " + name + " info logs number    = " + myLogInfoLogs    + " != " + number);
		}
		if (myLogWarningLogs != number) {
			System.out.println(        "[ERROR] " + name + " warning logs number = " + myLogWarningLogs + " != " + number);
			throw new RuntimeException("[ERROR] " + name + " warning logs number = " + myLogWarningLogs + " != " + number);
		}
		if (myLogSevereLogs != number) {
			System.out.println(        "[ERROR] " + name + " severe logs number  = " + myLogSevereLogs  + " != " + number);
			throw new RuntimeException("[ERROR] " + name + " severe logs number  = " + myLogSevereLogs  + " != " + number);
		}		
	}

}
