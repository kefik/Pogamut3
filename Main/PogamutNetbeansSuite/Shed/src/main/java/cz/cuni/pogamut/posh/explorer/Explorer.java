package cz.cuni.pogamut.posh.explorer;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Panel for showing the results of crawling.
 *
 * @see Crawler
 * @author Honza
 * @param <T> Type that is being crawled and also the type that will be returned
 * in selection.
 */
public final class Explorer<T> extends javax.swing.JPanel implements CrawlerListener<T>, ListCellRenderer {

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
                return actions.createTransferable(getSelected());
            }
            // Create transferable for creation of new item
            return actions.createNewTransferable();
        }

        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
    };
    // Set containing all items crawler was able to gather so far.
    private final Set<T> cache = new HashSet<T>();
    // Actions on the palette that are available in the explorer.
    private final IPaletteActions paletteActions;
    // Actions that the explorer can perform, i.e. delete item.
    private final IExplorerActions<T> actions;
    // index of selected object
    private int selectedIndex;
    // List containing data from cache, filtered down by txtPrefix and case flag
    private final DefaultListModel listModel = new DefaultListModel();
    /**
     * Comparator for two items, so the result is always shown in same order
     */
    private final Comparator<T> toStringComparator = new Comparator<T>() {

        @Override
        public int compare(T o1, T o2) {
            return o1.toString().toLowerCase().compareTo(o2.toString().toLowerCase());
        }
    };
    /**
     * Renderer of cells in {@link Explorer#resultList}. Labels are determined
     * getRendererLabel
     */
    private final DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
    private static final Logger log = Logger.getLogger(Explorer.class.getSimpleName());

    Explorer(IPaletteActions paletteActions, IExplorerActions<T> actions) {
        this.paletteActions = paletteActions;
        this.actions = actions;

        initComponents();

        resultList.setDragEnabled(true);
        resultList.setTransferHandler(transferHandler);

        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.getSelectionModel().addListSelectionListener(selectionListener);

        resultList.setCellRenderer(listRenderer);

        updateModel();

        txtPrefix.getDocument().addDocumentListener(searchTextListener);
    }

    /**
     * Get actions object that can be used to perform actions on the explorer.
     */
    public IExplorerActions<T> getActions() {
        return actions;
    }
    
    private T getSelected() {
        if (selectedIndex == 0) {
            return null;
        }
        return (T) listModel.get(selectedIndex);
    }


    /**
     * Get cell renderer, if index is 0, return new element cell renderer,
     * otherwise return renderer for {@link #getItemDisplayName(java.lang.Object) getItemDisplayName(value)}.
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        String label;
        // First element in the list is new action/sense whatever...
        if (index == 0) {
            label = value.toString();
        } else {
            T item = (T) value;
            label = actions.getDisplayName(item);
        }
        return listRenderer.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
    }

    /**
     * Filter the set using expression and flag.
     *
     * @param set set to be filtered, won't be modified
     * @param expression expression used to filter the set
     * @param caseSensitive should expression be case sensitive
     * @return list TODO: sorted list, but that requires comparable or something
     */
    private List<T> filterSet(Set<T> set, String expression, boolean caseSensitive) {
        List<T> result = new ArrayList<T>();

        for (T item : set) {
            if (!actions.filter(expression, caseSensitive, item)) {
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
        boolean caseSensitive = false;
        List<T> result = filterSet(cache, txtPrefix.getText(), caseSensitive);
        // TODO: It would be better to have T implement interface comparable
        Collections.sort(result, toStringComparator);
        // Set the model
        setModel(listModel, result);
    }

    /**
     * Set the content of the model to the list.
     *
     * @param model
     * @param list items that will be inserted into the model.
     */
    private void setModel(DefaultListModel model, List<T> list) {
        model.setSize(list.size() + 1);
        model.set(0, actions.getNewItemLabel());
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblSearchTypeDescription = new javax.swing.JLabel();
        txtPrefix = new javax.swing.JTextField();
        lblStatesFound = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        resultContainer = new javax.swing.JScrollPane();
        resultList = new javax.swing.JList() {
            public String getToolTipText(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index > 0) {
                    T item = (T)getModel().getElementAt(index);
                    return actions.getDescription(item);
                } else {
                    return null;
                }
            }
        };

        lblSearchTypeDescription.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.lblSearchTypeDescription.text")); // NOI18N

        txtPrefix.setFont(new java.awt.Font("DialogInput", 0, 11)); // NOI18N
        txtPrefix.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.txtPrefix.text")); // NOI18N
        txtPrefix.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblStatesFound.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.lblStatesFound.text")); // NOI18N

        btnRefresh.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.btnRefresh.text")); // NOI18N
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnDelete.setText(org.openide.util.NbBundle.getMessage(Explorer.class, "Explorer.btnDelete.text")); // NOI18N
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        resultContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        resultList.setModel(listModel);
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultListMouseClicked(evt);
            }
        });
        resultList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                resultListValueChanged(evt);
            }
        });
        resultContainer.setViewportView(resultList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtPrefix)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblSearchTypeDescription)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnRefresh)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete))
                    .addComponent(lblStatesFound))
                .addContainerGap())
            .addComponent(resultContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblSearchTypeDescription)
                .addGap(5, 5, 5)
                .addComponent(txtPrefix, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDelete)
                    .addComponent(btnRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatesFound, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultContainer, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        paletteActions.refresh();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void resultListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultListMouseClicked
        // TODO: Make some kind of adapter?
        if (evt.getClickCount() == 2) {
            int index = resultList.locationToIndex(evt.getPoint());
            // FIXME: Ugly, reclass whole thing
            if (index > 0) {
                actions.openEditor((T) listModel.get(index));
            }
        }
    }//GEN-LAST:event_resultListMouseClicked

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int index = resultList.getSelectedIndex();
        if (index == -1) {
            PGSupport.message("You have to select an item.");
            return;
        }
        if (index == 0) {
            PGSupport.message("Not a valid item");
            return;
        }
        boolean itemDeleted = actions.delete((T) listModel.get(index));
        if (itemDeleted) {
            actions.refresh(this);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void resultListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_resultListValueChanged
        int index = resultList.getSelectedIndex();
        if (index == -1 || index == 0) {
            paletteActions.setHtmlDescription("<html><body>Click on item in the list or in the scene</body></html>");
        } else {
            T selectedItem = (T) listModel.get(index);
            String description = actions.getDescription(selectedItem);
            paletteActions.setHtmlDescription(description);
        }
    }//GEN-LAST:event_resultListValueChanged
    /**
     * Listener for changes on {{@link Explorer#flgCaseSensitive}}. When
     * changed, schedule new search.
     *
     * @param e
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JLabel lblSearchTypeDescription;
    private javax.swing.JLabel lblStatesFound;
    private javax.swing.JScrollPane resultContainer;
    private javax.swing.JList resultList;
    private javax.swing.JTextField txtPrefix;
    // End of variables declaration//GEN-END:variables
}
