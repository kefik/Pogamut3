package cz.cuni.amis.dash;

import cz.cuni.amis.pogamut.sposh.dbg.engine.IDebugEngine;
import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;

/**
 * Presenter used by Dash to present {@link Sense}.
 *
 * @see DashPrimitivePresenter Presenter with functionality common to {@link TriggeredAction}
 * and {@link Sense}.
 * @author Honza
 */
class DashSensePresenter extends DashPrimitivePresenter<Sense> {

    public DashSensePresenter(IDebugEngine engine, LapPath primitivePath, ShedScene scene, ShedPresenter presenter, Sense primitive, ShedWidget primitiveWidget, LapChain primitiveChain) {
        super(engine, primitivePath, scene, presenter, primitive, primitiveWidget, primitiveChain);
    }

    /**
     * Get title text to be displayed in ther widget. It uses mapped name, i.e.
     * it takes information from {@link PrimitiveInfo} of the presented {@link Sense}
     * and combines it with predicate and operand of the {@link Sense}.
     *
     * @return Mapped name, if exists, FQN otherwise
     */
    @Override
    protected String getTitleText() {
        String mappedName = presenter.getNameMapping(primitive.getName());
        if (mappedName == null) {
            mappedName = primitive.getName();
        }
        Object operand = primitive.getOperand();
        Sense.Predicate predicate = primitive.getPredicate();

        String senseRepresentation;
        boolean predicateIsEqual = predicate.equals(Sense.Predicate.DEFAULT) || predicate.equals(Sense.Predicate.EQUAL);
        if (predicateIsEqual && Boolean.TRUE.equals(operand)) {
            return mappedName;
        } else {
            String operandString = Result.toLap(operand);
            senseRepresentation = mappedName + predicate.toString() + operandString;
            return senseRepresentation;
        }
    }
}
