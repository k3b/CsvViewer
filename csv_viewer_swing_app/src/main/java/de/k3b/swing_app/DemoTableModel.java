// src/main/java/example/
// Example implementation: DemoTableModel.java
package de.k3b.swing_app;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.model.TableModelApi;

/**
 * minimal implementation of demo data and for tests.
 */
public class DemoTableModel implements TableModelApi {

    private final String[] columns = {"ID", "Name", "Age"};

    private final Object[][] data = {
            {1, "Alice", 30},
            {2, "Bob", 25},
            {3, "Charlie", 40}
    };

    @Override
    public Object getValueAt(int row, int column) {
        return data[row][column];
    }

    @Override
    public Object[] getRow(int row) {
        return data[row];
    }

    /**
     * Sets the value for the cell in the table model at <code>rowNumber</code>
     * and <code>columnNumber</code>.
     * <p>
     * <b>Note</b>: The columnNumber is specified in the table view's display
     * order, and not in the <code>TableModel</code>'s columnNumber
     * order.  This is an important distinction because as the
     * user rearranges the columns in the table,
     * the columnNumber at a given index in the view will change.
     * Meanwhile the user's actions never affect the model's
     * column ordering.
     *
     * <code>aValue</code> is the new value.
     *
     * @param aValue       the new value
     * @param rowNumber    the rowNumber of the cell to be changed
     * @param columnNumber the columnNumber of the cell to be changed
     * @see #getValueAt
     */
    @Override
    public void setValueAt(@Nullable Object aValue, int rowNumber, int columnNumber) {

    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    /**
     * Returns the number of columns in the column model. Note that this may
     * be different from the number of columns in the table model.
     *
     * @return the number of columns in the table
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public @NonNull String[] getColumnNames() {
        return columns;
    }

    /**
     * @param rowCandidate
     * @return a valid version of rowCandidate.
     */
    @Override
    public @NonNull Object[] fixRow(@Nullable Object[] rowCandidate) {
        return new Object[0];
    }

    @Override
    public String getName() {
        return "Demo";
    }
}
