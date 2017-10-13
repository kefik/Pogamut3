package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.*;
import java.util.List;

/**
 * Base class that provides helper method for manipulation of events I got from
 * lap elements.
 *
 * TODO: Delete all stuff from {@link AbstractPresenter} that is duplicated here.
 *
 * @author Honza Havlicek
 */
abstract class AbstractLapElementListener<PARENT extends PoshElement> implements PoshElementListener<PARENT> {

    /**
     * Is passed element a {@link Sense}?
     *
     * @param element element to check.
     * @return true if element is sense, false if not.
     */
    protected final boolean isSense(PoshElement element) {
        return element instanceof Sense;
    }

    /**
     * Assert that sense is a part of the trigger and return it.
     *
     * @param trigger Trigger against which the method is testing.
     * @param sense tested sense
     * @return properly typed sense
     */
    protected final Sense extractSense(Trigger<?> trigger, PoshElement sense) {
        assert trigger.contains(sense);
        return (Sense) sense;
    }

    protected final boolean isDrive(PoshElement element) {
        return element instanceof DriveElement;
    }

    protected final DriveElement extractDrive(List<DriveElement> drives, PoshElement drive) {
        assert drives.contains(drive);
        return (DriveElement) drive;
    }

    /**
     * Is the element {@link CompetenceElement}?
     *
     * @param element Tested element
     * @return true if the @element is {@link CompetenceElement} or its
     * subclass, false otherwise.
     */
    protected final boolean isChoice(PoshElement element) {
        return element instanceof CompetenceElement;
    }

    /**
     * Assert that @choice is in the @choices and return it.
     *
     * @param choices List of choices that must contain the @choice.
     * @param choice Choice must be contained in the @choices
     * @return Properly casted @choice.
     */
    protected final CompetenceElement extractChoice(List<CompetenceElement> choices, PoshElement choice) {
        assert choices.contains(choice);
        return (CompetenceElement) choice;
    }

    /**
     * Is the element {@link ActionPattern}?
     *
     * @param element Tested element
     * @return true if the @element is {@link ActionPattern} or its subclass,
     * false otherwise.
     */
    protected final boolean isActionPattern(PoshElement element) {
        return element instanceof ActionPattern;
    }

    /**
     * Assert that @actionPattern is in the @actionPatterns and return it.
     * Basically a checked type conversion from {@link PoshElement} to {@link ActionPattern}.
     *
     * @param actionPatterns List of {@link ActionPattern}s that must contain
     * the @actionPattern.
     * @param actionPattern The {@link ActionPattern} that must be contained in
     * the @actionPatterns
     * @return Properly casted @actionPattern.
     */
    protected final ActionPattern extractActionPattern(List<ActionPattern> actionPatterns, PoshElement actionPattern) {
        assert actionPatterns.contains(actionPattern);
        return (ActionPattern) actionPattern;
    }

    /**
     * Is the element {@link Competence}?
     *
     * @param element Tested element
     * @return true if the @element is {@link Competence} or its subclass, false
     * otherwise.
     */
    protected final boolean isCompetence(PoshElement element) {
        return element instanceof Competence;
    }

    /**
     * Assert that @competence is in the @competences and return it. Basically a
     * checked type conversion from {@link PoshElement} to {@link Competence}.
     *
     * @param competences List of {@link Competence}s that must contain the
     * @competence.
     * @param competence The {@link Competence} that must be contained in the
     * @competences
     * @return Properly casted @competence.
     */
    protected final Competence extractCompetence(List<Competence> competences, PoshElement competence) {
        assert competences.contains(competence);
        return (Competence) competence;
    }

    /**
     * Get position of the element in the list.
     *
     * @param <T> Type of element
     * @param list list in which we determine position of the elemnt.
     * @param element The element for whic we want position in the list. Must be
     * there, otherwise assert error.
     * @return Found position.
     */
    protected final <T extends PoshElement> int getPosition(List<T> list, T element) {
        int position = list.indexOf(element);
        assert position != -1;
        return position;
    }
}
