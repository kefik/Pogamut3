package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * A shield factory takes care about all items which fall into a {@link ItemType.Category#SHIELD}.
 *
 * @author Jimmy
 */
public class ShieldDescriptorFactory implements IDescriptorFactory<ShieldDescriptor> {


    public ShieldDescriptor getNewDescriptor(ItemTyped configMsg) {
        ShieldDescriptor desc = new ShieldDescriptor();
        desc.doReflexion(configMsg, ShieldDescriptor.class);
        return desc;
    }
}