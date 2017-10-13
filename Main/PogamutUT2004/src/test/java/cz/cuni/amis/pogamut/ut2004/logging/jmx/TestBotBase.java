package cz.cuni.amis.pogamut.ut2004.logging.jmx;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotLogicController;

/**
 * Bot that will return result of its execution through get() method. The result
 * must be set through setResultAndTerminate() or setResult() methods.
 * @author ik
 */
@cz.cuni.amis.pogamut.base.utils.guice.AgentScoped
public class TestBotBase<T> extends UT2004BotLogicController implements Future<T> {

    T result = null;
    CountDownLatch latch = new CountDownLatch(1);

    protected void setResultAndTerminate(T res) {
        setResult(res);
        bot.stop();
    }

    protected void setResult(T res) {
        result = res;
        latch.countDown();
    }

    public T get() throws InterruptedException, ExecutionException {
        latch.await();
        return result;
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(timeout, unit);
        return result;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return latch.getCount() == 0;
    }
}

