package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;

/**
 * This is a factory for creating a HTML description string for various nodes of
 * the Yaposh tree. Ideally, I would use some method on the {@link PoshElement},
 * but because of {@link TriggeredAction} and {@link Sense} require {@link PrimitiveData}
 * for human friendly description
 *
 * @author Honza
 */
public class ShedDescriptionFactory {
}
