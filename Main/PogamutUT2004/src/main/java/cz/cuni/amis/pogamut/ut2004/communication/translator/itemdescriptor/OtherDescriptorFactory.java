package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * An 'other item type' factory takes care about all items which fall into a {@link ItemType.Category#OTHER}.
 *
 * @author Jimmy
 */
public class OtherDescriptorFactory implements IDescriptorFactory<OtherDescriptor> {


    public OtherDescriptor getNewDescriptor(ItemTyped configMsg) {
        OtherDescriptor desc = new OtherDescriptor();
        desc.doReflexion(configMsg, OtherDescriptor.class);
        return desc;
    }
}
