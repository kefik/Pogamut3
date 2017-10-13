package cz.cuni.pogamut.posh.explorer;

import java.awt.datatransfer.Transferable;

/**
 * Interface for actions that can be performed on the {@link Explorer}.
 *
 * @author Honza
 */
public interface IExplorerActions<T> {

    /**
     * Get text for @item that will be displayed in the explorer.
     *
     * @param item Item that has survived filtration.
     * @return name of the item to be displayed in the list.
     */
    String getDisplayName(T item);

    /**
     * Get label for new item action.
     *
     * @return String to be shown as first item in the list, basically synonym
     * for add new item action.
     */
    String getNewItemLabel();

    /**
     * Get description of an item (to be used in the tootltip text).
     *
     * @param item Item in the result list
     * @return tooltip thaty should be shown when user hovers over the item.
     */
    String getDescription(T item);

    /**
     * Method to filter items in the {@link Explorer}. Whe user types something
     * into search box, explorer uses some implementation of this filter to
     * determine which items should be shown in the result window.
     *
     * @param query String query, what user typed into search window
     * @param caseSensitive Should the filtering be case sensitive?
     * @param item Item that will be evaluated if it should be filtered out or
     * not
     * @return True if item should not be included (i.e. it should be filtered
     * out) False if item should be included (shown in the result)
     */
    public boolean filter(String query, boolean caseSensitive, T item);

    /**
     * Refresh the explorer.
     */
    void refresh(Explorer<T> explorer);

    /**
     * Delete item (e.g. {@link Competence}) from the plan, that may include
     * some questions to the user ("Are you sure?") .
     *
     * @param item Item to be deleted.
     * @return Was item deleted? User could abort, file trouble...
     */
    boolean delete(T item);

    /**
     * Open editor for item.
     *
     * @param item Item to be displayed
     */
    void openEditor(T item);

    /**
     * Create new {@link Transferable} from item data. Used in the explorer for
     * Drag and drop operation.
     *
     * @param data data that will be transfered, can be null
     * @return new transferable or null if data isn't suitable for transfer.
     */
    Transferable createTransferable(T data);

    /**
     * Create {@link Transferable} that is used when user drags and drops node
     * representing new item somewhere (presumably to scene). When the
     * transferable is asked for {@link Transferable#getData() data}, it queries
     * the user for the data and returns new item.
     */
    Transferable createNewTransferable();
}
