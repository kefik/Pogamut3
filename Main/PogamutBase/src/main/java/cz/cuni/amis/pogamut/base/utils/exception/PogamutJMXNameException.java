package cz.cuni.amis.pogamut.base.utils.exception;

import javax.management.ObjectName;

import cz.cuni.amis.utils.exception.PogamutJMXException;

/**
 * Thrown whenever the {@link ObjectName} instance can't be obtained.
 * @author Jimmy
 */
public class PogamutJMXNameException extends PogamutJMXException {

	public PogamutJMXNameException(String jmxName, Throwable cause) {
		super("JMXName: " + jmxName, cause, null);
	}
	
}
