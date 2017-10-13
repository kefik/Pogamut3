package cz.cuni.pogamut.shed.widget.editor;

import cz.cuni.amis.pogamut.sposh.elements.EnumValue;
import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.exceptions.FubarException;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * Table that can represent yaposh variables. Currently it is normal {@link JTable}
 * that has custom editor and cell displayer for {@link EnumValue}.
 *
 * @author Honza
 */
class VariableTable extends JTable {

    private TableCellRenderer enumCellRenderer = new EnumCellRenderer();

    public VariableTable(TableModel dm) {
        super(dm);
        
        putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
    }
    
    Object mutex = new Object();
    boolean recursion = false;

    public void editingStopped(ChangeEvent e) {
        synchronized(mutex) {
            if (recursion) return;
            recursion = true;
        }
        try {
            // Take in the new value
            TableCellEditor editor = getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
                Object value = editor.getCellEditorValue();
                setValueAt(value, editingRow, editingColumn);
                removeEditor();
            }
        } finally {
            recursion = false;
        }
    }
    
    @Override
    public void editingCanceled(ChangeEvent e) {
        TableCellEditor editor = getCellEditor();
        if (editor != null) {
            editor.stopCellEditing();
            Object value = editor.getCellEditorValue();
            setValueAt(value, editingRow, editingColumn);
            removeEditor();
        }
    }
    
    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        Object stringValue = super.getValueAt(row, column);


        Object value;
        try {
            value = Result.parseValue((String) stringValue);
        } catch (ParseException ex) {
            return super.getCellRenderer(row, column);
        }

        if (value instanceof EnumValue) {
            return enumCellRenderer;
        }

        return super.getCellRenderer(row, column);
    }
    
    /**
     * Renderer that renders only simple name of the enum constant, not a fully
     * qualified name.
     */
    private static class EnumCellRenderer extends DefaultTableCellRenderer {

        @Override
        public void setValue(Object value) {
            EnumValue enumValue = new EnumValue((String) value);
            setText(enumValue.getSimpleName());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // passed value is a string representation of enum in the plan, i.e. 'cz.cuni.TestEnu.VALUE including apostrophe
            String stringValue = (String) value;
            try {
                EnumValue enumValue = (EnumValue) Result.parseValue(stringValue);
                String enumConst = enumValue.getSimpleName();
                setToolTipText(enumValue.getName());
                return super.getTableCellRendererComponent(table, enumConst, isSelected, hasFocus, row, column);
            } catch (ParseException ex) {
                throw new FubarException("Enum renderer should be used only for enum fields, got " + stringValue, ex);
            }
        }
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        String valueString = (String) getValueAt(row, column);
        ParamInfo info = getInfo(row);

        boolean paramIsEnum = (info != null && info.type == ParamInfo.Type.ENUM);
        if (paramIsEnum && valueString.isEmpty()) {
            return getEnumEditor(row, column, info.clsName);
        }

        try {
            Object value = Result.parseValue(valueString);
            boolean valueIsEnum = value instanceof EnumValue;

            if (valueIsEnum) {
                EnumValue enumValue = (EnumValue) value;
                return getEnumEditor(row, column, enumValue.getEnumFQN());
            }
        } catch (ParseException ex) {
            return super.getCellEditor(row, column);
        }
        return super.getCellEditor(row, column);
    }

    /**
     * Get {@link EnumCellEditor} for an enum in specified cell. If for some
     * reason that is not possible, return default editor for the cell.
     *
     * @param enumFQN FQN name of the enum in the cell.
     */
    private TableCellEditor getEnumEditor(int row, int column, String enumFQN) {
        List<String> enumConsts = EnumStructureFactory.getValues(enumFQN);
        if (enumConsts != null) {
            List<EnumValue> enumValues = new ArrayList<EnumValue>();
            for (String enumConst : enumConsts) {
                enumValues.add(new EnumValue(enumFQN + '.' + enumConst));
            }
            return new EnumCellEditor(enumValues);
        }
        return new CellEditorWrapper(super.getCellEditor(row, column));
        //return super.getCellEditor(row, column);
    }

    private ParamInfo getInfo(int row) {
        if (dataModel instanceof ArgumentsTableModel) {
            ArgumentsTableModel model = (ArgumentsTableModel) dataModel;
            return model.getArguments().get(row).getInfo();
        }
        return null;
    }
    
    private static class CellEditorWrapper implements TableCellEditor, ActionListener {
        
        private final TableCellEditor editor;
    
        public CellEditorWrapper(TableCellEditor editor) {
            this.editor = editor;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return editor.getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        @Override
        public Object getCellEditorValue() {
            return editor.getCellEditorValue();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return editor.isCellEditable(anEvent);
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return editor.shouldSelectCell(anEvent);
        }

        @Override
        public boolean stopCellEditing() {
            return editor.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
            editor.stopCellEditing();
            //editor.cancelCellEditing();
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            editor.addCellEditorListener(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            editor.removeCellEditorListener(l);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            editor.stopCellEditing();
        }
        
    }

    /**
     * Editor of cell with enum in it.
     */
    private static class EnumCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

        private JComboBox box;
        private static final String CLEAR_VALUE = "Clear the value";

        EnumCellEditor(List<EnumValue> enumValues) {
            this.box = new JComboBox(enumValues.toArray());
            this.box.addItem(CLEAR_VALUE);
            this.box.setRenderer(new CellRenderer());
            this.box.addActionListener(this);
        }

        /**
         * @return Value used to change the model through {@link ArgumentsTableModel#setValueAt(java.lang.Object, int, int)
         * } once the {@link #fireEditingStopped() }.
         */
        @Override
        public Object getCellEditorValue() {
            if (box.getSelectedItem().equals(CLEAR_VALUE)) {
                return "";
            }
            EnumValue selectedItem = (EnumValue) box.getSelectedItem();
            return Result.toLap(selectedItem);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object editedValueFromModel, boolean isSelected, int row, int column) {
            try {
                String enumConstFQN = (String) editedValueFromModel;
                if (!enumConstFQN.isEmpty()) {
                    Object editedEnumConst = Result.parseValue(enumConstFQN);
                    box.setSelectedItem(editedEnumConst);
                } else {
                    box.setSelectedIndex(0);
                }
            } catch (ParseException ex) {
                throw new FubarException("Unable to parse the " + editedValueFromModel, ex);
            }
            return box;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped();
        }

        private static class CellRenderer extends DefaultListCellRenderer {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value.equals(CLEAR_VALUE)) {
                    return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                }
                EnumValue enumValue = (EnumValue) value;
                String enumConst = enumValue.getSimpleName();
                setToolTipText(enumValue.getName());

                return super.getListCellRendererComponent(list, enumConst, index, isSelected, cellHasFocus);
            }
        }
    }
}
