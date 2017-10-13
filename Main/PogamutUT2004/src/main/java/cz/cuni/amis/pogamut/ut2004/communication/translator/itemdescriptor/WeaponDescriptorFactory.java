package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;

/**
 * A weapon factory takes care about all items which fall into a category Weapon. 
 *
 * @author Ondrej
 */
public class WeaponDescriptorFactory implements IDescriptorFactory<WeaponDescriptor> {

	private final ItemTypeTranslator itemTypeTranslator;
	
	public WeaponDescriptorFactory(ItemTypeTranslator translator){
		this.itemTypeTranslator = translator;
	}

    @Override
    public WeaponDescriptor getNewDescriptor(ItemTyped configMsg) {
        WeaponDescriptor desc = new WeaponDescriptor(itemTypeTranslator);
        desc.doReflexion(configMsg, WeaponDescriptor.class);
        return desc;
    }
}

