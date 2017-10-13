package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategory;

/**
 *
 *
 * @author Ondrej
 */
public class GeneralDescriptorFactory implements IDescriptorFactory<GeneralDescriptor> {
	
	
	@Override
	public GeneralDescriptor getNewDescriptor(ItemTyped configMsg) {
		return new GeneralDescriptor(configMsg);
	}

}

