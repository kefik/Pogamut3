/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Usefull for queriing sets of objects with some common property.
 * Itarates all elements in the collection and returns 
 * @author Ik
 */
public abstract class Query<T> {

    protected abstract boolean filter(T o);

    public List<T> query(Collection<T> collection) {
        List<T> result = new ArrayList<T>();
        for (T o : collection) {
            if (filter(o)) {
                result.add(o);
            }
        }
        return result;
    }
}
