package cz.cuni.amis.pogamut.ut2004.communication.messages;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;


/**
 * Translator String UnrealID <-> Int UnrealId.
 * <p><p>
 * It holds maps for translating string UnrealID into int id, every new string UnrealID is given a unique 
 * number and returned.
 */
public class UnrealIdTranslator {
	
	/**
	 * Returns UnrealId object for a specified string, creates a new one if none exists.
	 * 
	 * @param unrealId
	 * @return
	 */
	public UnrealId getId(String unrealId) {
		return UnrealId.get(unrealId);
	}
	
}
