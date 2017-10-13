package cz.cuni.amis.pogamut.base.utils.jmx.flag;

import java.io.Serializable;

/**
 *
 * @author ik
 */
public interface JMXFlagDecoratorMBean {
    /**
     *
     * @return Current flag value.
     */
    Serializable getFlag();
}
