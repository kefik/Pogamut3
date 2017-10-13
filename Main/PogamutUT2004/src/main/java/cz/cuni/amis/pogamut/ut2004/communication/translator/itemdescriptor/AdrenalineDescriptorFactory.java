package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * An adrenaline factory takes care about all items which fall into a {@link ItemType.Category#ADRENALINE}.
 *
 * @author Jimmy
 */
public class AdrenalineDescriptorFactory implements IDescriptorFactory<AdrenalineDescriptor> {

    public AdrenalineDescriptor getNewDescriptor(ItemTyped configMsg) {
        AdrenalineDescriptor desc = new AdrenalineDescriptor();
        desc.doReflexion(configMsg, AdrenalineDescriptor.class);
        return desc;
    }
}
