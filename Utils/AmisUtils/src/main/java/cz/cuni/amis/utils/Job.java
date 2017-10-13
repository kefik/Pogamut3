/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.utils;

import java.util.concurrent.TimeUnit;

import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.ImmutableFlag;
import cz.cuni.amis.utils.flag.WaitForFlagChange;

/**
 * This Job class represents and wraps one job you want to execute asynchronously. It is
 * a combination of the java.lang.Thread and java.util.concurrent.Future class from java.concurrency packaga
 * <p><p>
 * Usage:
 * <ol>
 * <li>Create your own job (anonymous class, inheritance, whatever)</li>
 * <li>Implement job() method - there you might need to use setResult() method to specify the result of the job</li>
 * <li>Start the job using startJob() method</li>
 * <li>Then the thread is launched and job is being crunched</li>
 * <li>Use await() / isFinished() / isFinishedOk() / isException() / getResult() to examine the results</li>
 * </ol>
 * Note that getResult() should be used only IFF isFinishedOk(), also notice that one instance of job
 * can't be started twice (even if isFinished(), the second startJob() will throw exception)!
 * <p>/p>
 * Thread-safe.
 * <p><p>
 * Example:<p>
 * MyJob myJob = new MyJob().startJob("my cool thread name");<p>
 * myJob.await();<p>
 * if (myJob.isFinishedOk()) {<p>
 * 	  switch(myJob.getResult()) {<p>
 *    // examine the result, act accordinally<p>
 *    }<p>
 * }<p>
 * 
 * @author Jimmy
 */
public abstract class Job<RESULT> {
    
	/**
	 * Exception that is thrown if you attempt to start one job twice.
	 * @author Jimmy
	 */
    @SuppressWarnings("serial")
	public static class JobWasAlreadyStartedException extends RuntimeException {
        
        public JobWasAlreadyStartedException(String text) {
            super(text);
        }
        
    }

    /**
     * Stores the result from the last setResult() call
     */
    private RESULT jobResult = null;
    
    /**
     * Thrown exception by the job() (if any).
     */
    private Exception thrownException = null;
 
    /**
     * Thread the job is using ... if == null the job was never started before.
     */
    private Thread thread = null;
    
    /**
     * Mutex for the whole class, access synchronization to all Job's fields.
     */
    private Object mutex = new Object();
    
    /**
     * Flag that tells you whether the job is running.
     */
    private Flag<Boolean> running = new Flag<Boolean>(false);
    
    /**
     * Returns object we used as a mutex for this class.
     * <p><p>
     * Do not use for your own jobs! If you screw up it will result in deadlock.
     * 
     * @return
     */
    protected Object getMutex() {
    	return mutex;
    }

    /**
     * If job is running (thread is not null and isAlive()) - interrupts the thread.
     */
    public void interrupt() {
        synchronized(mutex) {
            if (thread != null && thread.isAlive()) thread.interrupt();
        }
    }
    
    /**
     * If thread is null: returns false
     * <p>
     * If thread is NOT null: returns thread.isInterrupted().
     * @return
     */
    public boolean isInterrupted() {
        synchronized(mutex) {
            if (thread != null) return thread.isInterrupted();
            else return false;
        }
    }
    
    /**
     * Returns you a flag that is marking whether the job is running or not.
     * @return
     */
    public ImmutableFlag<Boolean> getRunningFlag() {
    	synchronized(mutex) {
    		return running.getImmutable();
    	}
    }
    
    /**
     * Immediately tells you whether the job is running.
     * @return
     */
    public boolean isRunning() {
    	synchronized(mutex) {
    		return running.getFlag();
    	}
    }
    
    /**
     * Tells you whether the job has ended ... it doesn't tell you whether it finished OK or KO,
     * use isException() to ask whether the job has finished OK.
     * @return
     */
    public boolean isFinished() {
    	synchronized(mutex) {
    		return isStarted() && !isRunning();
    	}
    }
    
    /**
     * True means: the job has finished correctly without throwing any exception...<p>
     * False means: job has not finished yet / was not even started / or exception has occured.    
     * @return
     */
    public boolean isFinishedOk() {
    	synchronized(mutex) {
    		return isFinished() && !isException();
    	}
    }
    
    /**
     * Whether the job was already (somewhere in the past) started. <p>
     * <b>If returns true it does not necessarily mean the job is running!</b>
     * @return
     */
    public boolean isStarted() {
    	synchronized(mutex) {
    		return thread != null;
    	}
    }
    
    /**
     * Use this protected method to set the result of the job. (Default result is null.)
     * @param result
     */
    protected void setResult(RESULT result) {
    	synchronized(mutex) {
    		jobResult = result;
    	}
    }
    
    /**
     * Returns job result - should be used 
     * @return
     */
    public RESULT getResult() {
    	synchronized(mutex) {
    		return jobResult;
    	}
    }
    
    /**
     * Whether the exception occurred during the job().
     * @return
     */
    public boolean isException() {
    	synchronized(mutex) {
    		return thrownException != null;
    	}
    }
    
    /**
     * If isException() this returns an exception that has occured.
     * @return
     */
    public Exception getException() {
    	synchronized(mutex) {
    		return thrownException;
    	}
    }
    
    /**
     * Do your job here.
     */
    protected abstract void job() throws Exception;
    
    /**
     * Wraps the call of the job() method - instantiated inside startJob().
     * @author Jimmy
     */
    private class RunJob implements Runnable {
	 
    	/**
    	 * Calls the job() method from the Job class, correctly sets running flag / thrownException.
    	 */
    	public void run() {	    	
	        try {
	            job();
	        } catch (Exception e) {
	        	synchronized(mutex) {
	        		thrownException = e;
	        	}
	        } finally {
	        	synchronized(mutex) {
	        		running.setFlag(false);
	        	}
	        }
	    }
    }
    
    /**
     * If isRunning(), this will await till the job finishes.
     * @throws InterruptedException 
     */
    public void await() throws InterruptedException  {
    	new WaitForFlagChange<Boolean>(running, false).await();    	
    }
    
    /**
     * If isRunning(), this will await till the job finishes (with specified timeout).
     * @throws InterruptedException 
     * @return {@code true} if the count reached zero and {@code false}
     *         if the waiting time elapsed before the count reached zero 
     */
    public boolean await(long timeoutMillis) throws InterruptedException {
    	return new WaitForFlagChange<Boolean>(running, false).await(timeoutMillis, TimeUnit.MILLISECONDS);
    }
        
    /**
     * Starts the job (only iff !isStarted()) in the new thread.
     * <p><p>
     * If isStarted() ... JobWasAlreadyStartedException is thrown.
     * @param job
     * @return this instance (you may immediately call await())
     */
    public Job<RESULT> startJob() {
    	synchronized(mutex) {
    		return startJob("JobRunnable[" + this + "]");
    	}
    }
     
    /**
     * Starts the job (only iff !isStarted()) in the new thread (with specific thread name).
     * <p><p>
     * If isStarted() ... JobWasAlreadyStartedException is thrown. 
     * @param threadName
     * @return this instance (you may immediately call await())
     */
    public Job<RESULT> startJob(String threadName) {
        synchronized(mutex) {
            if (thread != null) throw new JobWasAlreadyStartedException("Job was already started... can't run one job twice.");
            thread = new Thread(new RunJob(), threadName);           
    		running.setFlag(true);
            thread.start();
            return this;
        }
    }

}
