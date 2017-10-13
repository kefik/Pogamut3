package cz.cuni.amis.pogamut.ut2004.communication.messages;

public class UT2004ItemTypeTranslator implements ItemTypeTranslator {

	@Override
	public ItemType get(String utName) {
    	if (utName == null || utName.toLowerCase().equals("none")) return UT2004ItemType.NONE;
		return UT2004ItemType.getItemType(utName);
	}

}
