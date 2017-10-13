package cz.cuni.pogamut.shed.presenter;

import javax.swing.Action;
import org.netbeans.api.visual.action.WidgetAction;

/**
 * Presenter is responsible for presenting some widget in the scene. Its job is
 * to update the widget according to the stuff the widget should reflect. It
 * also provides context menu actions, edit action and accept actions.
 *
 * @author Honza Havlicek
 */
public interface IPresenter {

    /**
     * Register its listeners into the lap tree. Presenter must react to wide
     * array of events in the tree. E.g. expanded action must listen not only to
     * the name property of the action, but also to all competences and AP in
     * the tree.
     */
    void register();

    /**
     * Unregister its listeners that were registered in the {@link #register() }
     * method.
     */
    void unregister();

    /**
     * Get(create) actions that should be displayed in the context menu of the
     * widgets that use this presenter.
     *
     * @return Actions that will be in the menu or null for no menu.
     */
    Action[] getMenuActions();

    /**
     * Get array of accept providers of this
     *
     * @return Array of accept providers. If null, no accept provider.
     */
    AbstractAcceptAction[] getAcceptProviders();

    /**
     * Get edit action that will be invoked upon double click.
     *
     * @return Edit action for the widget that is being represented. If null, no
     * edit action.
     */
    WidgetAction getEditAction();
}
