package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.Competence;
import cz.cuni.amis.pogamut.sposh.elements.INamedElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import cz.cuni.pogamut.posh.explorer.IPaletteActions;
import cz.cuni.pogamut.shed.widget.ShedScene;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.model.ObjectState;
import org.netbeans.api.visual.widget.Widget;

/**
 * Factory for actions that toggle its {@link ObjectState#isObjectFocused() when user clicks on the widget.
 * }. When user clicks on the widget, actions can also show description of the
 * node in the palette.
 */
final class FocusActionFactory {

    /**
     * Create focus actions.
     */
    static WidgetAction createFocusAction() {
        return new FocusAction();
    }

    /**
     * Create focus actions that will also display description of the {@link Competence}
     * in the palette once focused.
     */
    static WidgetAction createCompetenceFocusAction(final Competence competence) {
        return new FocusAction() {

            @Override
            protected String getHtmlDescription() {
                return competence.getHtmlDescription();
            }
        };
    }

    /**
     * Create focus actions that will also display description of the {@link ActionPattern}
     * in the palette once focused.
     */
    static WidgetAction createActionPatternFocusAction(final ActionPattern actionPattern) {
        return new FocusAction() {

            @Override
            protected String getHtmlDescription() {
                return actionPattern.getHtmlDescription();
            }
        };
    }
    

    /**
     * Create focus actions that will also display description of the competence
     * in the palette once focused.
     */
    static WidgetAction createPrimitiveFocusAction(final INamedElement primitive, final ShedPresenter presenter) {
        return new FocusAction() {

            @Override
            protected String getHtmlDescription() {
                PrimitiveData metadata = presenter.getMetadata(primitive.getName());
                if (metadata == null) {
                    metadata = new PrimitiveData(primitive.getName());
                }
                return metadata.getHtmlDescription();
            }
        };
    }    

    private static class FocusAction extends WidgetAction.Adapter {

        @Override
        public final WidgetAction.State mouseClicked(Widget widget, WidgetAction.WidgetMouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 1) {
                ObjectState widgetState = widget.getState();
                boolean isFocused = widgetState.isObjectFocused();
                ObjectState newWidgetState = widgetState.deriveObjectFocused(!isFocused);

                unfocusAllWidgets(widget);
                widget.setState(newWidgetState);
                widget.revalidate();

                String htmlDescription = getHtmlDescription();
                if (htmlDescription != null) {
                    IPaletteActions paletteActions = ((ShedScene) widget.getScene()).getPaletteActions();
                    paletteActions.setHtmlDescription(htmlDescription);
                }
                return WidgetAction.State.CONSUMED;
            }
            return WidgetAction.State.REJECTED;
        }

        private void unfocusAllWidgets(Widget widget) {
            Set<Widget> allFocusedWidgets = getFocusedWidgets(widget.getScene());
            for (Widget focusedWidget : allFocusedWidgets) {
                ObjectState unfocusedState = focusedWidget.getState().deriveObjectFocused(false);
                focusedWidget.setState(unfocusedState);
            }
        }

        /**
         * Get all {@link ObjectState#isObjectFocused() focused} widgets in the
         * widget (eithger children or the widget itself).
         *
         * @param rootWidget Root widget whose tree we are searching
         */
        private Set<Widget> getFocusedWidgets(Widget rootWidget) {
            Set<Widget> focusedWidgets = new HashSet<Widget>();
            if (rootWidget.getState().isObjectFocused()) {
                focusedWidgets.add(rootWidget);
            }
            for (Widget child : rootWidget.getChildren()) {
                focusedWidgets.addAll(getFocusedWidgets(child));
            }
            return focusedWidgets;
        }

        protected String getHtmlDescription() {
            return null;
        }
    }
}
