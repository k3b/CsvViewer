package de.k3b.csvviewer.lib.data;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Defines the api to access the table.
 * <p>
 * A table has of a list of rows.
 * Each row has an array of row-columns.
 * Each row has as the same number of columns.
 * <p>
 * Inspired by java swing JTable */
public interface TableModelApi {
    static String PROPERTY_COLUMN_DEFINITION = "colDef";

    /**
     * Returns the cell value at <code>row</code> and <code>column</code>.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display
     *              order, and not in the <code>TableModel</code>'s column
     *              order.  This is an important distinction because as the
     *              user rearranges the columns in the table,
     *              the column at a given index in the view will change.
     *              Meanwhile the user's actions never affect the model's
     *              column ordering.
     *
     * @param   row             the row whose value is to be queried
     * @param   column          the column whose value is to be queried
     * @return  the Object at the specified cell
     */
    @Nullable Object getValueAt(int row, int column);

    @NonNull Object[] getRow(int row);

    /**
     * Sets the value for the cell in the table model at <code>row</code>
     * and <code>column</code>.
     * <p>
     * <b>Note</b>: The column is specified in the table view's display
     *              order, and not in the <code>TableModel</code>'s column
     *              order.  This is an important distinction because as the
     *              user rearranges the columns in the table,
     *              the column at a given index in the view will change.
     *              Meanwhile the user's actions never affect the model's
     *              column ordering.
     *
     * <code>aValue</code> is the new value.
     *
     * @param   aValue          the new value
     * @param   row             the row of the cell to be changed
     * @param   column          the column of the cell to be changed
     * @see #getValueAt
     */
    void setValueAt(@Nullable Object aValue, int row, int column);

    /**
     * Returns the number of rows that can be shown in the
     * <code>JTable</code>, given unlimited space.
     *
     * @return the number of rows shown in the <code>JTable</code>
     * @see #getColumnCount
     */

    int getRowCount();

    /**
     * Returns the number of columns in the column model. Note that this may
     * be different from the number of columns in the table model.
     *
     * @return  the number of columns in the table
     * @see #getRowCount
     */
    int getColumnCount();

    /** @return  names of the columns */
    @NonNull String[] getColumnNames();

    /**
     * @return width of given column in chars. 0 means hidden. -1 means automatic width
     */
    default int getColumnWidth(int column) {
        return -1;
    }

    /** return column specific propery */
    @Nullable default <VALUE> VALUE getColumnProperty(int column, @NonNull Object key)  { return null; }

    /** put column specific propery */
    default void putColumnProperty(int column, @NonNull Object key, Object value) {}

    @Nullable default <VALUE> VALUE[] getColumnProperties(VALUE[] result, @NonNull Object key)  {
        int columnCount = result.length;
        boolean allEmpty = true;
        for (int col = 0; col < columnCount; col++) {
            VALUE value = getColumnProperty(col, key);
            result[col] = value;
            if (value != null) {
                allEmpty = false;
            }
        }
        return allEmpty ? null : result;
    }

    /** @return a valid version of rowCandidate. */
    @NonNull
    Object[] fixRow(@Nullable Object[] rowCandidate);

}
