package cz.cuni.amis.utils;

/**
 * Used for adapting APIs throwing exceptions to usecases where this is not desired.
 * @author ik
 */
public abstract class ExceptionDiscarder<T> {

    /**
     * Specifies the task that should be computed. the task can throw exception but
     * it will be wrapped by run method to a RuntimeException.
     * @return
     * @throws java.lang.Exception
     */
   protected abstract T task() throws Exception;

    public T run() {
        try {
            return task();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
