package cz.cuni.amis.nb.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Read only property that will be regularly updated.
 * @author ik
 */
public class PeriodicalyUpdatedProperty<T> extends AutoNamedProp<T> {

    Method getMethod = null;
    Object source = null;

    public PeriodicalyUpdatedProperty(
            Object source,
            String propName,
            Class<T> type,
            String displayName,
            String shortDescription) throws NoSuchMethodException {
        super(type, displayName, shortDescription, false);
        getMethod = source.getClass().getMethod("get" + propName);
        this.source = source;
    }

    @Override
    public T getValue() throws IllegalAccessException, InvocationTargetException {
        try {
            return (T) getMethod.invoke(source);
        } catch (RuntimeException e) {
            throw new InvocationTargetException(e);
        }
    }

    @Override
    public void setValue(T val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // only viewer
    }
}
