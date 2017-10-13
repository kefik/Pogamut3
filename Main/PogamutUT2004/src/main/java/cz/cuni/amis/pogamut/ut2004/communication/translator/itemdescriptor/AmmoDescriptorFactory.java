package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

/**
 * An ammo factory takes care about all items which fall into a category Ammo.
 *
 * @author knight
 */
public class AmmoDescriptorFactory implements IDescriptorFactory<AmmoDescriptor> {

    public AmmoDescriptor getNewDescriptor(ItemTyped configMsg) {
        AmmoDescriptor desc = new AmmoDescriptor();
        desc.doReflexion(configMsg, AmmoDescriptor.class);
        return desc;
    }
}

