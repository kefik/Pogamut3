package cz.cuni.pogamut.posh.explorer;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 * Panel for showing the results of crawling.
 * @see Crawler
 * @author Honza
 * @param <T> Type that is being crawled and also the type that will be returned in selection.
 */
abstract class Explorer<T> extends javax.swing.JPanel implements CrawlerListener<T>, ListCellRenderer  {

    // Dynamic updater of resultList according to data user types into a search textfield
    private final DocumentListener searchTextListener = new DocumentListener() {

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateModel();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateModel();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateModel();
        }
    };
    // LIstener for change in selection.
    private final ListSelectionListener selectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            selectedIndex = resultList.getSelectedIndex();
        }
    };
    // transfer handler for drag and drop from the list to some other part of NB
    private final TransferHandler transferHandler = new TransferHandler() {

        @Override
        protected Transferable createTransferable(JComponent c) {
            if (selectedIndex != 0) {
                return createItemTransferable(getSelected());
            }
            // Create transferable for creation of new item
            return newItemTransferable;
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
    };
    /** Transfer handler that is used when user drags "new item" from list elewhere. */
    private final Transferable newItemTransferable;
    // Set containing all items crawler was able to gather so far.
    private final Set<T> cache = new HashSet<T>();
    // Crawler that refreshes the list of explorer when clicked on the refresh button.
    private final Crawler<T> refreshCrawler;
    // index of selected object
    private int selectedIndex;
    // List containing data from cache, filtered down by txtPrefix and case flag
    private final DefaultListModel listModel = new DefaultListModel();
    /** Comparator for two items, so the result is always shown in same order */
    private final Comparator<T> toStringComparator  = new Comparator<T>() {

        @Override
        public int compare(T o1, T o2) {
            return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
        }
    };
    /** Renderer of cells in {@link Explorer#resultList}. Labels are determined getRendererLabel */
    private final DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
    private static final Logger log = Logger.getLogger(Explorer.class.getSimpleName());


    public Explorer(Crawler<T> refreshCrawler) {
        this.refreshCrawler = refreshCrawler;
        newItemTransferable = createNewItemTransferable();

        initComponents();

        resultList.setDragEnabled(true);
        resultList.setTransferHandler(transferHandler);

        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.getSelectionModel().addListSelectionListener(selectionListener);

        resultList.setCellRenderer(listRenderer);

        updateModel();

        txtPrefix.getDocument().addDocumentListener(searchTextListener);
    }

    public T getSelected() {
        if (selectedIndex == 0) {
            return null;
        }
        return (T) listModel.get(selectedIndex);
    }

    /**
     * Get label for new item action.
     * @return String to be shown as first item in the list, basically synonym for add new item action.
     */
    protected abstract String getNewItemLabel();

    /**
     * Method to filter items in the {@link Explorer}. Whe user types something into
     * search box, explorer uses some implementation of this filter to determine
     * which items should be shown in the result window.
     * 
     * @param query String query, what user typed into search window
     * @param caseSensitive Should the filtering be case sensitive?
     * @param item Item that will be evaluated if it should be filtered out or not
     * @return True if item should not be included (i.e. it should be filtered out)
     *         False if item should be included (shown in the result)
     */
    protected abstract boolean filter(String query, boolean caseSensitive, T item);

    /**
     * Get label of an result list item to be rendered.
     * @param item Item that has survived filtration.
     * @return name of the item to be displayed in the list.
     */
    protected abstract String getRenderedLabel(T item);

    /**
     * Get description of an item (to be used in the tootltip text).
     * @param item Item in the result list
     * @return tooltip thaty should be shown when user hovers over the item.
     */
    protected abstract String getItemDescription(T item);

    /**
     * Create new {@link Transferable} from item data. Used in the
     * explorer for Drag and drop operation.
     * @param data data that will be transfered, can be null
     * @return new transferable or null if data isn't suitable for transfer.
     */
    protected abstract Transferable createItemTransferable(T data);

    /**
     * Create {@link Transferable} that is used when user drags and drops
     * node representing new item somewhere (presumably to scene). When
     * the transferable is asked for {@link Transferable#getData() data},
     * it queries the user for the data and returns new item.
     */
    protected abstract Transferable createNewItemTransferable();

    /**
     * Display item in the editor.
     * @param item Item to be displayed
     */
    protected abstract void displayItem(T item);


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String label;
        // First element in the list is new action/sense whatever...
        if (index == 0) {
            label = value.toString();
        } else {
            T item = (T) value;
            label = getRenderedLabel(item);
        }
        return listRenderer.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
    }

    /**
     * Filter the set using expression and flag.
     * @param set set to be filtered, won't be modified
     * @param expression expression used to filter the set
     * @param caseSensitive should expression be case sensitive
     * @return list TODO: sorted list, but that requires comparable or somthing
     */
    private List<T> filterSet(Set<T> set, String expression, boolean caseSensitive) {
        List<T> result = new ArrayList<T>();

        for (T item : set) {
            if (!filter(expression, caseSensitive, item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Take current crawled data and update the list
     */
    private void updateModel() {
        // Sort the list
        List<T> result = filterSet(cache, txtPrefix.getText(), flgCaseSensitive.isSelected());
        // TODO: It would be better to have T implement interface comparable
        Collections.sort(result, toStringComparator);
        // Set the model
        setModel(listModel, result);
    }

    /**
     * Set the content of the model to the list.
     * @param model 
     * @param list items that will be inserted into the model.
     */
    private void setModel(DefaultListModel model, List<T> list) {
        model.setSize(list.size() + 1);
        model.set(0, getNewItemLabel());
        for (int i = 0; i < list.size(); ++i) {
            model.set(i + 1, list.get(i));
        }
    }

    // listener for crawler
    @Override
    public void started(Crawler<T> crawler) {
        assert SwingUtilities.isEventDispatchThread();
        log.info("Started crawling, clear the cache " + cache.size() + " crawler " + crawler.getName());
        cache.clear();
        updateModel();
    }

    // listener for crawler
    @Override
    public void crawledData(Crawler<T> crawler, Collection<T> data) {
        assert SwingUtilities.isEventDispatchThread();
        log.info("Crawled data " + data.size());
        for (T item : data) {
            log.info("  * " + item);
        }

        cache.addAll(data);
        updateModel();
    }

    // listener for crawler
    @Override
    public void finished(Crawler<T> crawler, boolean error) {
        assert SwingUtilities.isEventDispatchThread();
        log.info("Finished crawling");

        crawler.removeListener(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblSearchTypeDescription = new javax.swing.JLabel();
        txtPrefix = new javax.swing.JTextField();
        lblStatesFound = new javax.swing.JLabel();
        flgCaseSensitive = new javax.swing.JCheckBox();
        resultContainer = new javax.swing.JScrollPane();
        resultList = new javax.swing.JList() {
            public String getToolTipText(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index > 0) {
                    T item = (T)getModel().getElementAt(index);
                    return getItemDescription(item);
                } else {
                    return null;
                }
            }
        };
        txtLocation = new javax.swing.JTextField();
        lblLocation = new javax.swing.JLabel();
        btnNew = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        lblSearchTypeDescription.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.lblSearchTypeDescription.text")); // NOI18N

        txtPrefix.setFont(new java.awt.Font("DialogInput", 0, 11));
        txtPrefix.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.txtPrefix.text")); // NOI18N
        txtPrefix.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblStatesFound.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.lblStatesFound.text")); // NOI18N

        flgCaseSensitive.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.flgCaseSensitive.text")); // NOI18N
        flgCaseSensitive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flgCaseSensitiveActionPerformed(evt);
            }
        });

        resultContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        resultList.setModel(listModel);
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultListMouseClicked(evt);
            }
        });
        resultContainer.setViewportView(resultList);

        txtLocation.setEditable(false);
        txtLocation.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.txtLocation.text")); // NOI18N

        lblLocation.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.lblLocation.text")); // NOI18N
        lblLocation.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        btnNew.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.btnNew.text")); // NOI18N

        btnRefresh.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.btnRefresh.text")); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(resultContainer, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefresh)
                        .addGap(18, 18, 18)
                        .addComponent(flgCaseSensitive))
                    .addComponent(txtPrefix, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .addComponent(lblSearchTypeDescription)
                    .addComponent(txtLocation, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                    .addComponent(lblLocation)
                    .addComponent(lblStatesFound))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSearchTypeDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNew)
                    .addComponent(btnRefresh)
                    .addComponent(flgCaseSensitive))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatesFound, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblLocation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void flgCaseSensitiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flgCaseSensitiveActionPerformed
        updateModel();
}//GEN-LAST:event_flgCaseSensitiveActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        refreshCrawler.addListener(this);
        refreshCrawler.crawl();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void resultListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultListMouseClicked
        // TODO: Make some kind of adapter?
        if (evt.getClickCount() == 2) {
            int index = resultList.locationToIndex(evt.getPoint());
            // FIXME: Ugly, reclass whole thing
            if (index > 0) {
                displayItem((T) listModel.get(index));
            }
        }
    }//GEN-LAST:event_resultListMouseClicked
    /**
     * Listener for changes on {{@link Explorer#flgCaseSensitive}}.
     * When changed, schedule new search.
     * @param e
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JCheckBox flgCaseSensitive;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblSearchTypeDescription;
    private javax.swing.JLabel lblStatesFound;
    private javax.swing.JScrollPane resultContainer;
    private javax.swing.JList resultList;
    private javax.swing.JTextField txtLocation;
    private javax.swing.JTextField txtPrefix;
    // End of variables declaration//GEN-END:variables
}
