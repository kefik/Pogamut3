package cz.cuni.pogamut.posh.explorer;

/**
 * Actions used by all explorers in the palete. The {@link IExplorerActions} is
 * for actions that affect only one {@link Explorer}.
 */
public interface IPaletteActions {

    /**
     * Refresh all explorers in the palette
     */
    void refresh();

    /**
     * Set description panel in the palette to specified text.
     *
     * @param htmlDescription HTML description that is to be shown in info
     * panel.
     */
    void setHtmlDescription(String htmlDescription);
}
