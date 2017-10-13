package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

/**
 * An interface for all descriptor factories. Descriptor factories are provided with an ITCMsg
 * and they return a configured corresponding ItemDescriptor.
 * 
 * @author Ondrej
 */
public interface IDescriptorFactory <T extends ItemDescriptor> {
	
    T getNewDescriptor(ItemTyped configMsg);
    
}