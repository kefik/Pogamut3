package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * An ammo factory takes care about all items which fall into a {@link ItemType.Category#ARMOR}.
 *
 * @author Jimmy
 */
public class ArmorDescriptorFactory implements IDescriptorFactory<ArmorDescriptor> {


    public ArmorDescriptor getNewDescriptor(ItemTyped configMsg) {
        ArmorDescriptor desc = new ArmorDescriptor();
        desc.doReflexion(configMsg, ArmorDescriptor.class);
        return desc;
    }
}