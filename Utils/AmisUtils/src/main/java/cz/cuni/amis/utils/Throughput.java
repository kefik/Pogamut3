package cz.cuni.amis.utils;

public class Throughput {
	
	private long lastTime = -1;
	
	private long records = 0;
	
	private long data = 0;
	
	private long firstDataTime = -1;
	
	private long totalRecords = 0;
	
	private long totalData = 0;
	
	private String units;

	private double throughput = 0;
	
	private boolean reporting = false;
	
	private String name = "Throughput"; 
	
	public Throughput(String units) {
		this.units = units;
	}
	
	public boolean isReporting() {
		return reporting;
	}

	public void setReporting(boolean reporting) {
		this.reporting = reporting;
	}
	
	public double getThroughput() {
		return throughput;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void add(long data) {
		if (firstDataTime < 0) {
			firstDataTime = System.currentTimeMillis();
		}
		if (lastTime < 0) {
			lastTime = System.currentTimeMillis();
			return;
		}
		this.data += data;
		this.records += 1;
		this.totalData += data;
		this.totalRecords += 1;
		if (System.currentTimeMillis() - lastTime > 1000) {
			if (isReporting()) {
				System.out.println("[INFO]  " + name + ": " + this.data + " " + units + " / sec | " + this.records + " records / sec");
			}
			this.data = 0;
			this.records = 0;
			lastTime = System.currentTimeMillis();
		}
	}
	
	public long getTotalData() {
		return totalData;
	}
	
	public long getTotalRecords() {
		return totalRecords;
	}
	
	/**
	 * Return data / secs.
	 * @return
	 */
	public double getCurrentThroughput() {
		long time = System.currentTimeMillis() - firstDataTime;
		if (time < 1) return 0;
		return (double)totalData / ((double)time/1000);
	}
	
	public double getCheckThroughput() {
		return this.throughput;
	}

	public void check() {
		this.throughput = getCurrentThroughput();
		this.data = 0;
		this.records = 0;
		this.totalData = 0;
		this.totalRecords = 0;
		this.lastTime = -1;
		this.firstDataTime = -1;		
	}

}
