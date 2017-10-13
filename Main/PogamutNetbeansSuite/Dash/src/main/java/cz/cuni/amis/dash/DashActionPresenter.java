package cz.cuni.amis.dash;

import cz.cuni.amis.pogamut.sposh.dbg.engine.IDebugEngine;
import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import cz.cuni.pogamut.shed.presenter.ShedPresenter;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;

/**
 * Presenter used by Dash to present {@link TriggeredAction}.
 *
 * @see DashPrimitivePresenter Presenter with functionality common to {@link TriggeredAction}
 * and {@link Sense}.
 * @author Honza
 */
class DashActionPresenter extends DashPrimitivePresenter<TriggeredAction> {

    public DashActionPresenter(IDebugEngine engine, LapPath primitivePath, ShedScene scene, ShedPresenter presenter, TriggeredAction primitive, ShedWidget primitiveWidget, LapChain primitiveChain) {
        super(engine, primitivePath, scene, presenter, primitive, primitiveWidget, primitiveChain);
    }

    /**
     * Get title text to be displayed in ther widget. It uses mapped name, i.e.
     * it takes information from {@link PrimitiveInfo} of the presented {@link TriggeredAction}.
     *
     * @return Mapped name, if exists, FQN otherwise
     */
    @Override
    protected String getTitleText() {
        String mappedActionName = presenter.getNameMapping(primitive.getName());
        if (mappedActionName == null) {
            mappedActionName = primitive.getName();
        }
        return mappedActionName;
    }
}
