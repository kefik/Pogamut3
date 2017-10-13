package cz.cuni.pogamut.shed.widget;

import cz.cuni.pogamut.shed.presenter.IPresenter;

/**
 * Interface for widgets that are being presented by some {@link IPresenter}.
 * Not all widgets are presented.
 *
 * @author Honza
 */
public interface IPresentedWidget {

    /**
     * Get presenter of the widget.
     */
    IPresenter getPresenter();
}
