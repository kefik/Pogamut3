package cz.cuni.amis.pogamut.base.communication.messages;

/**
 * This is marker class for all info messages that the world is producing.
 * 
 * @author Jimmy
 */
public abstract class InfoMessage {
	
	public String toString() {
		return "InfoMessage[" + getClass().getSimpleName() + "]";
	}
	
	/**
	 * To be refactored into UT2004 where it is used.
	 * @return
	 */
	public String toJsonLiteral() {		
		return null;
	}

}
