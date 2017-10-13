
package cz.cuni.amis.nb.pogamut.base.introspection;

/**
 * Object for performing some periodical task which can overload the CPU.
 * @author ik
 */
public abstract class PeriodicalTaskPerformer {
    
    /**
     * Creates a new instance of PeriodicalTaskPerformer
     */
    public PeriodicalTaskPerformer() {
    }
    
    /** Minimal gap between two calls of taskDefinition(). */
    protected static long MIN_UPDATE_GAP = 100;
    
    /**
     * Time of last call of taskDefinition().
     */
    protected long timeOfLastUpdate = -1;
    
    /**
     * Call this method if you want your task to be performed.
     * If you will call this method too often (see MIN_UPDATE_GAP)
     * the task won't be performed. This can save a lot of CPU time.
     */
    public void performTask() {
        long time = System.currentTimeMillis();
        if((time - timeOfLastUpdate) > MIN_UPDATE_GAP) {
            taskDefinition();
            timeOfLastUpdate = time;
        }
    }
    
    /**
     * Definition of task to be performed.
     */
    protected abstract void taskDefinition();
}
