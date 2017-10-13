package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;

/**
 * Interface marking the object as "typed" therefore being a subject for description
 * by {@link ItemDescriptor}.
 * 
 * @author Ondrej
 */
public interface ItemTyped {

    /**
     * Returns ItemType as injected by ItemTranslator.
     * @return
     */
    ItemType getType();
}
