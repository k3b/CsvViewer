package de.k3b.android.csvviewer.data;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link TableModelApi} implementation where all data is kept in memory.
 */
public class InMemoryTableModel implements TableModelApi {

    /** names of the columns */
    @NonNull private final String[] columnNames;

    /** A list of rows where each row is an array of columns  */
    @NonNull private final List<Object[]> rows = new ArrayList<>();

    /**
     * Constructs a <code>JTable</code> to display the values in the two dimensional array,
     * <code>rowData</code>, with column names, <code>columnNames</code>.
     * <code>rowData</code> is an array of rows, so the value of the cell at row 1,
     * column 5 can be obtained with the following code:
     *
     * <pre> rowData[1][5]; </pre>
     * <p>
     * All rows must be of the same length as <code>columnNames</code>.
     * <p>
     * @param columnNames       names of each column
     * @param rowData           the data for the new table
     */
    public InMemoryTableModel(@NonNull final String[] columnNames, final Object[]... rowData) {
        this.columnNames = columnNames;
        if (rowData != null) {
            for (Object[] row : rowData) {
                if (row != null) {
                    addRow(rowData);
                }
            }
        }
    }

    /** @return  names of the columns */
    @NonNull public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * Creates a row with no values. Use {@link #addRow(Object[])} to add it to this.
     */
    @NonNull public Object[] createEmptyRow() {
        return new Object[getColumnCount()];
    }

    /** @return a valid version of rowCandidate. */
    @NonNull @Override public Object[] fixRow(@Nullable Object[] rowCandidate) {
        Object[] result = rowCandidate;
        if (rowCandidate == null || rowCandidate.length < getColumnCount()) {
            result = createEmptyRow();
            if (rowCandidate != null) {
                System.arraycopy(rowCandidate, 0, result, 0, rowCandidate.length);
            }
        }
        check(result);
        return result;
    }

    /**
     * Adds row to internal row list.
     * @param row item to be added
     * @return true if success
     * @throws IllegalArgumentException if row is not valid. See {@link #check(Object[])}
     */
    public boolean addRow(@NonNull final Object[] row) {
        check(row);
        return this.rows.add(row);
    }

    /** throws IllegalArgumentException if row is not valid */
    private void check(Object[] row) {
        if (row == null || row.length < getColumnCount()) {
            throw new IllegalArgumentException(String.format("Row %s must have at least %d coulums", Arrays.toString(row), getColumnCount()));
        }
    }

    /** throws IllegalArgumentException if row or column is not valid */
    private void check(int row, int column) {
        if (column < 0  || column >= getColumnCount()) {
            throw new IllegalArgumentException(String.format("Column %d must be between 0 and %d", row, getColumnCount() - 1));
        }
        if (row < 0  || row >= getRowCount()) {
            throw new IllegalArgumentException(String.format("Row %d must be between 0 and %d", row, getRowCount() - 1));
        }
    }

    /**
     * Returns the number of rows that can be shown in the
     * <code>JTable</code>, given unlimited space.
     *
     * @return the number of rows shown in the <code>JTable</code>
     * @see #getColumnCount
     */
    @Override
    public int getRowCount() { return rows.size(); }

    /**
     * Returns the number of columns in the column model. Note that this may
     * be different from the number of columns in the table model.
     *
     * @return  the number of columns in the table
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() { return columnNames.length; }

    /**
     * Returns the cell value at <code>row</code> and <code>column</code>.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display
     * order, and not in the <code>TableModel</code>'s column
     * order.  This is an important distinction because as the
     * user rearranges the columns in the table,
     * the column at a given index in the view will change.
     * Meanwhile the user's actions never affect the model's
     * column ordering.
     *
     * @param row    the row whose value is to be queried
     * @param column the column whose value is to be queried
     * @return the Object at the specified cell
     * @throws IllegalArgumentException if row or column is not valid. See {@link #check(int, int)} )}
     */
    @Override @Nullable
    public Object getValueAt(int row, int column) {
        check(row, column);
        return getRow(row)[column];
    }

    /**
     * @param row
     * @return the row data at specified row number
     */
    @Override
    public @NonNull Object[] getRow(int row) {
        check(row, 0);
        return rows.get(row);
    }

    /**
     * Sets the value for the cell in the table model at <code>row</code>
     * and <code>column</code>.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display
     * order, and not in the <code>TableModel</code>'s column
     * order.  This is an important distinction because as the
     * user rearranges the columns in the table,
     * the column at a given index in the view will change.
     * Meanwhile the user's actions never affect the model's
     * column ordering.
     * <p>
     * <code>aValue</code> is the new value.
     *
     * @param value the new value
     * @param row    the row of the cell to be changed
     * @param column the column of the cell to be changed
     * @see #getValueAt
     * @throws IllegalArgumentException if row or column is not valid. See {@link #check(int, int)} )}
     */
    @Override
    public void setValueAt(@Nullable Object value, int row, int column) {
        check(row, column);
        getRow(row)[column] = value;
    }
}
