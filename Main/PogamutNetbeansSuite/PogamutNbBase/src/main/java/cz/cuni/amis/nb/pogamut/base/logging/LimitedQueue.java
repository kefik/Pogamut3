/*
 * LimitedQueue.java
 *
 * Created on 20. b�ezen 2007, 17:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.base.logging;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.LogRecord;

/**
 * Queue with limited capacity. If size of the queue is greater than the capacity 
 * then the oldest objects are removed.
 * @author kero
 */
public class LimitedQueue<T> {
    
    protected ArrayDeque<T> deque = new ArrayDeque<T>();
    
    /** Capacity of queue. */
    protected int capacity = 1000;
    
    /** Creates a new instance of LimitedQueue */
    public LimitedQueue() {
    }
    
    /** Sets new capacity of this queue. */
    public void setCapacity(int newCapacity) {
        capacity = newCapacity;
    }
    
    synchronized public void add(T lr) {
        deque.addFirst(lr);
        if(deque.size() > capacity) deque.removeLast();
    }
    
    synchronized public Deque<T> getAll() {
        return deque;
    }
    
    synchronized public void clear() {
        deque.clear();
    }
    
}
