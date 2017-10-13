package cz.cuni.amis.pogamut.ut2004.communication.translator.shared.events;

import cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor.ItemDescriptor;

public class ItemDescriptorObtained extends TranslatorEvent {
	
	private ItemDescriptor desc;

	public ItemDescriptorObtained(ItemDescriptor obtained, long simTime) {
		super(simTime);
		this.desc = obtained;
	}

	public ItemDescriptor getItemDescriptor() {
		return this.desc;
	}
	
	@Override
	public String toString() {
		return "ItemDescriptorObtained[desc=" + this.desc + "]";
	}

}
