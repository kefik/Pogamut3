/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import java.util.ArrayList;

/**
 * Record of one variable during time.
 * 
 * Variable is stored in pairs (timestamp, value) and is changed only
 * if two subsequent values are not equal to keep small memory footprint.
 * 
 * T has to have a properly implemented equal() method because
 * otherwise the record could row quite large.
 * 
 * @author Honza
 */
public class TLRecord<T> {
	class TLVarInfo {}
	/**
	 * Tmestamp when first record was added.
	 */
	private long startTimestamp;
	/**
	 * Tmestamp when last record was added.
	 */
	private long endTimestamp;

	TLRecord(TLVarInfo info, long timestamp) {
		this.varInfo = info;
		
		this.startTimestamp = timestamp;
		this.endTimestamp = timestamp;
	}

	public TLVarInfo getInfo() {
		return varInfo;
	}
	
	/**
	 * 
	 * fixme: lookat class overload and possibly try to 
	 * @param <T>
	 */
	public class Record<T> {
		long timestamp;
		T value;
		
		Record(long timestamp,T value) {
			this.timestamp = timestamp;
			this.value = value;
			
//			System.out.println(" ### New record in " + (timestamp - startTimestamp) + ": " + value);
		}
	}
	
	private TLVarInfo varInfo;
	private ArrayList<Record<T>> records;
	
	protected synchronized ArrayList<Record<T>> getRecords() {
		if (records == null) {
			records = new ArrayList<Record<T>>();
		}
		return records;
	}
	
	
	/**
	 * Add value to the record.
	 * 
	 * @param timestamp Number of ms since 1970
	 * @param value Current value of recorded variable. Can't be null.
	 */
	public synchronized Record addRecord(long timestamp, T value) {
		Record<T> record = null;
		
		if (getRecords().isEmpty()) {
			record = new Record<T>(timestamp, value);
			getRecords().add(record);
			this.startTimestamp = timestamp;
		} else {
			int size = getRecords().size(); 
			Record<T> tailRecord = getRecords().get(size - 1);
			
			if (tailRecord.timestamp > timestamp) {
				throw new RuntimeException("Invalid addition: Passed timestamp (" + timestamp + ") is younger than lastly added record (" + tailRecord.timestamp + ")");
			}
			
			if ( value != tailRecord.value || 
			    ( (tailRecord.value!=null) && (! tailRecord.value.equals(value))) ||
			    ( (value != null) && (! value.equals(tailRecord.value)))
			    ) {
				record = new Record<T>(timestamp,value);
				getRecords().add(record);
			}
		 }
		 this.endTimestamp = timestamp;
		 return record;
	}
	
}
