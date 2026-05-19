package de.k3b.csvviewer.lib.data.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import de.k3b.csvviewer.lib.data.comparator.TableModelRowComparator;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

/**
 * A {@link TableModelApi} implementation where all data is kept in memory.
 */
public class InMemoryTableModel extends TableModelBase {

    /** names of the columns  */
    private int[] columnWidths = null;

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
        super(columnNames);
        if (rowData != null) {
            for (Object[] row : rowData) {
                if (row != null) {
                    addRow(rowData);
                }
            }
        }
    }

    public InMemoryTableModel createEmptyClone() {
        InMemoryTableModel result = new InMemoryTableModel(getColumnNames());
        result.copyPropertiesFrom(this);
        return result;
    }

    /** implementation detail of {@link #createEmptyClone()} */
    protected void copyPropertiesFrom (@NonNull TableModelBase from) {
        super.copyPropertiesFrom(from);
        InMemoryTableModel to = this;
        if (from instanceof InMemoryTableModel) {
            InMemoryTableModel from_ = (InMemoryTableModel) from;
            if (from_.columnWidths != null) {
                to.columnWidths = new int[from_.columnWidths.length];
                System.arraycopy(from_.columnWidths, 0, to.columnWidths, 0, from_.columnWidths.length);
            }
        }
    }


    /**
     * @return names of the columns
     */
    @Override
    public int getColumnWidth(int column) {
        int result = -1;
        if (columnWidths != null && column >= 0 && column < getColumnCount()) {
            result =  columnWidths[column];
        }

        return result;
    }

    private int[] inferColumnWidths(int numberOfRowsToAnalyse) {
        int[] columnWidths = new int[getColumnCount()];

        for (int columnNumber = 0; columnNumber < getColumnCount(); columnNumber++) {
            int min = getColumnNames()[columnNumber].length();
            int sum = min;
            int max = min;

            int nonEmpty = 1;
            int count = getRowCount();
            if (count > numberOfRowsToAnalyse) count = numberOfRowsToAnalyse;

            for (int rowNumber = 0;rowNumber < count; rowNumber++) {
                Object value = getValueAt(rowNumber, columnNumber);
                if (value != null) {
                    int len = value.toString().length();
                        sum += len;
                        if (len > max) max = len;
                        if (len > 0) nonEmpty++;
                }

            }

            int average = sum / nonEmpty;

            columnWidths[columnNumber] = average;

        }
        return columnWidths;
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
     * Returns the cell value at <code>rowNumber</code> and <code>columnNumber</code>.
     * <p>
     * <b>Note</b>: The columnNumber is specified in the table view's display
     * order, and not in the <code>TableModel</code>'s columnNumber
     * order.  This is an important distinction because as the
     * user rearranges the columns in the table,
     * the columnNumber at a given index in the view will change.
     * Meanwhile the user's actions never affect the model's
     * columnNumber ordering.
     *
     * @param rowNumber    the rowNumber whose value is to be queried
     * @param columnNumber the columnNumber whose value is to be queried
     * @return the Object at the specified cell
     * @throws IllegalArgumentException if rowNumber or columnNumber is not valid. See {@link #check(int, int)} )}
     */
    @Override @Nullable
    public Object getValueAt(int rowNumber, int columnNumber) {
        check(rowNumber, columnNumber);
        return getRow(rowNumber)[columnNumber];
    }

    /**
     * @param rowNumber - rowNumber number to be retrieved.
     * @return the row data at specified rowNumber number
     */
    @Override
    public Object[] getRow(int rowNumber) {
        check(rowNumber, 0);
        return rows.get(rowNumber);
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
     * columnNumber ordering.
     * <p>
     * <code>aValue</code> is the new value.
     *
     * @param value the new value
     * @param rowNumber    the rowNumber of the cell to be changed
     * @param columnNumber the columnNumber of the cell to be changed
     * @see #getValueAt
     * @throws IllegalArgumentException if rowNumber or columnNumber is not valid. See {@link #check(int, int)} )}
     */
    @Override
    public void setValueAt(@Nullable Object value, int rowNumber, int columnNumber) {
        check(rowNumber, columnNumber);
        getRow(rowNumber)[columnNumber] = value;
    }

    public void sortBy(@NonNull List<Integer> columnNos) {
        FormatterApi<?>[] columnDefinitions = getColumnProperties(
                new FormatterApi<?>[getColumnCount()], TableModelApi.PROPERTY_COLUMN_DEFINITION);

        if (columnDefinitions != null && columnNos != null && !columnNos.isEmpty()) {
            TableModelRowComparator sorter = TableModelRowComparator.create(columnDefinitions, columnNos);

            if (sorter != null) {
                // rows.sort(sorter); // note List.sort requires android api 24
                sort(rows, sorter);
            }
            putColumnProperty(-1, TableModelApi.PROPERTY_SORT_ORDER, sorter);
            // TableModelRowComparator sorter =
        }
    }

    /** implements list.sort(comparator) which requires android api-24 (Android-7) */
    private void sort(List<Object[]> rowList, Comparator<Object[]> comparator) {
        Object[][] rowArray = rowList.toArray(new Object[0][getColumnCount()]);
        Arrays.sort(rowArray, comparator);
        ListIterator<Object[]> rowIterator = rowList.listIterator();

        for(Object[] row : rowArray) {
            rowIterator.next();
            rowIterator.set(row);
        }
    }

}
