/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.utils.future;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Martin Cerny
 */
public interface IFutureWithListeners<RESULT> extends Future<RESULT> {

    /**
     * Adds a listener on a future status (using strong reference). Listeners are automatically
     * removed whenever the future gets its result (or is cancelled or an exception happens).
     * @param listener
     */
    void addFutureListener(IFutureListener<RESULT> listener);

    @Override
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     *
     * Informs the future that it can't be computed due to the exception.
     * <p><p>
     * Switches the status to EXCEPTION (notifying listeners along the way).
     * <p><p>
     * The result can be set only iff NOT {@link FutureWithListeners#isDone()}, i.e., status is {@link FutureStatus}:FUTURE_IS_BEING_COMPUTED.
     * @param e
     */
    void computationException(Exception e);

    @Override
    RESULT get();

    /**
     * Returns a result or waits for the computation till timeout.
     * <p><p>
     * Does not throw {@link TimeoutException}! It returns null instead - always examine status of the future
     * via {@link FutureWithListeners#getStatus()} if the null is returned to tell whether the 'null' is the
     * result of the computation (if the status is FUTURE_IS_READY than the 'null' is truly the result).
     * @param timeout
     * @param unit
     * @return
     */
    @Override
    RESULT get(long timeout, TimeUnit unit);

    /**
     * Contains an exception that has happened during the computation in the case of ({@link FutureWithListeners#getStatus()} == EXCEPTION).
     * @return
     */
    Exception getException();

    /**
     * Current status of the future computation.
     * @return
     */
    FutureStatus getStatus();

    @Override
    boolean isCancelled();

    @Override
    boolean isDone();

    /**
     * Whether some listener is listening on the future.
     * @param listener
     * @return
     */
    boolean isListening(IFutureListener<RESULT> listener);

    /**
     * Removes a listener from the future.
     * @param listener
     */
    void removeFutureListener(IFutureListener<RESULT> listener);

    /**
     * Sets the result of the future computation.
     * <p><p>
     * Switches the status to FUTURE_IS_READY (notifying listeners along the way).
     * <p><p>
     * The result can be set only iff NOT {@link FutureWithListeners#isDone()}, i.e., status is {@link FutureStatus}:FUTURE_IS_BEING_COMPUTED.
     * @param result
     */
    void setResult(RESULT result);
    
}
