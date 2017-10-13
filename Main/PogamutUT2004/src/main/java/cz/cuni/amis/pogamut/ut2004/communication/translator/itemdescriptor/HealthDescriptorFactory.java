package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ItemCategory;

/**
 * A health factory takes care about all items which fall into a category Health.
 *
 * @author knight
 */
public class HealthDescriptorFactory implements IDescriptorFactory<HealthDescriptor> {

  
    public HealthDescriptor getNewDescriptor(ItemTyped configMsg) {
        HealthDescriptor desc = new HealthDescriptor();
        desc.doReflexion(configMsg, HealthDescriptor.class);
        return desc;
    }

}

