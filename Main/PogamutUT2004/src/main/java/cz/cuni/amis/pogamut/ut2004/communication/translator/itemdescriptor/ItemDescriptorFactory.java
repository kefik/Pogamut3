package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategory;

/**
 * This factory handles default item category messages.
 *
 * @author Ondrej, knight
 */
public class ItemDescriptorFactory implements IDescriptorFactory<ItemDescriptor> {

   

    public ItemDescriptor getNewDescriptor(ItemTyped configMsg) {
        ItemDescriptor desc = new ItemDescriptor();
        desc.doReflexion(configMsg,ItemDescriptor.class);
        return desc;
    }
}

