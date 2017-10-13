package cz.cuni.amis.utils.future;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.utils.exception.PogamutCancellationException;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.exception.PogamutTimeoutException;

public interface IFuture<RESULT> extends Future<RESULT> {
	
	/**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return the computed result
     * @throws PogamutInterruptedException if the current thread was interrupted while waiting
     */
	@Override
    public RESULT get() throws PogamutInterruptedException;
    
    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return the computed result
     * @throws PogamutCancellationException if the computation was cancelled
     * @throws PogamutInterruptedException if the current thread was interrupted while waiting
     * @throws PogamutTimeoutException if the wait timed out
     */
	@Override
    public RESULT get(long timeout, TimeUnit unit) throws PogamutInterruptedException, PogamutCancellationException, PogamutTimeoutException;
	
}
