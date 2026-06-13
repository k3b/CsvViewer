package de.k3b.csvviewer.lib.data.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public abstract class TableModelBase implements TableModelApi {
    /** names of the columns */
    @NonNull
    private final String[] columnNames;

    /** has one element more than columnNames. the last element "-1" is used for globale values */
    @NonNull private final Map<?,?>[] columnProperties;

    public TableModelBase(@NonNull final String[] columnNames) {
        this.columnNames = columnNames;
        this.columnProperties = new Map[columnNames.length+1];
    }

    /** @return  names of the columns */
    @NonNull
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * Returns the number of columns in the column model. Note that this may
     * be different from the number of columns in the table model.
     *
     * @return  the number of columns in the table
     * @see #getRowCount
     */
    @Override
    public int getColumnCount() { return columnNames.length; }

    /** put column specific propery */
    @Override
    public void putColumnProperty(int column, @NonNull Object key, Object value) {
        getMap(column).put(key,value);
    }

    /** return column specific propery */
    @Override
    public @Nullable <VALUE> VALUE getColumnProperty(int column, @NonNull Object key) {
        return (VALUE) getMap(column).get(key);
    }

    private Map<Object, Object> getMap(int column) {
        if (column == -1) {
            column = columnProperties.length -1;
        } else {
            checkColumnNumber(column);
        }
        Map<Object,Object> properties = (Map<Object,Object>) columnProperties[column]; // .put(key,value);
        if (properties == null) {
            properties = new HashMap<>();
            columnProperties[column] = properties;
        }
        return properties;
    }

    /** implementation detail of createEmptyClone() */
    protected void copyPropertiesFrom (TableModelBase from) {
        System.arraycopy(from.columnProperties,0, this.columnProperties, 0, from.columnProperties.length);
    }

    /** throws IllegalArgumentException if row is not valid */
    protected void check(Object[] row) {
        if (row == null || row.length < getColumnCount()) {
            throw new IllegalArgumentException(String.format("Row %s must have at least %d columns", Arrays.toString(row), getColumnCount()));
        }
    }

    /** throws IllegalArgumentException if rowNumber or columnNumber is not valid */
    protected void check(Integer rowNumber, int columnNumber) {
        checkColumnNumber(columnNumber);
        if (rowNumber < 0  || rowNumber >= getRowCount()) {
            throw new IllegalArgumentException(String.format("Row %d must be between 0 and %d", rowNumber, getRowCount() - 1));
        }
    }

    /** throws IllegalArgumentException if rowNumber or columnNumber is not valid */
    private void checkColumnNumber(int columnNumber) {
        if (columnNumber < 0  || columnNumber >= getColumnCount()) {
            throw new IllegalArgumentException(String.format("Column %d must be between 0 and %d", columnNumber, getColumnCount() - 1));
        }
    }
}
