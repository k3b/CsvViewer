// src/main/java/example/
// Adapter: JTableAdapter.java
package de.k3b.swing_app;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import de.k3b.csvviewer.lib.data.model.TableModelApi;

/** translates {@link TableModelApi} to Swing {@link AbstractTableModel}  */
public class JTableAdapter extends AbstractTableModel {

    private final TableModelApi api;
    private final String[] columnNames;
    private JTable table;

    public JTableAdapter(TableModelApi api) {
        this.api = api;
        this.columnNames = api.getColumnNames();
    }

    @Override
    public int getRowCount() {
        return api.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return api.getValueAt(rowIndex, columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (getRowCount() == 0) {
            return Object.class;
        }

        Object value = getValueAt(0, columnIndex);
        return value != null ? value.getClass() : Object.class;
    }


    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true; // or restrict per column
    }

    @Override
    public void setValueAt(Object aValue, int viewRow, int columnIndex) {

        if (table != null && table.getRowSorter() != null) {
            int modelRow = table.convertRowIndexToModel(viewRow);

            api.setValueAt(aValue, modelRow, columnIndex);

            fireTableCellUpdated(modelRow, columnIndex);
        }
    }

    public void setTable(JTable table) {
        this.table = table;
    }
}