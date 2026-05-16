package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

/** used to sort a TableModel by column numbers */
public abstract class TableModelRowFilterBase {
    protected final int columnNumber;

    TableModelRowFilterBase(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    abstract protected boolean matchImpl(@Nullable Object value);

    public int getColumnNumber() {
        return columnNumber;
    }

    public boolean match(Object[] row) {
        return matchImpl(row[columnNumber]);
    }

    /**
     * remove filter for column col
     * @return if filter was found and removed.
     */
    public static boolean remove(List<TableModelRowFilterBase> filterList, int col) {
        boolean found = false;
        for (int i=filterList.size() -1; i >= 0; i--) {
            TableModelRowFilterBase filter = filterList.get(i);
            if (filter.columnNumber == col) {
                filterList.remove(i);
                found = true;
            }
        }
        return found;
    }

    /**
     * @return first filter that matches its condition or null if not matching.
     */
    public static @Nullable TableModelRowFilterBase match(@Nullable List<TableModelRowFilterBase> filterList, Object[] row) {
        if (filterList != null) {
            for (int i = filterList.size() - 1; i >= 0; i--) {
                TableModelRowFilterBase filter = filterList.get(i);
                if (filter.match(row)) {
                    return filter;
                }
            }
        }
        return null;
    }

    public String getColumnName(@Nullable String[] columnNames) {
        String columnName;
        if (columnNames != null && columnNumber >= 0 && columnNumber < columnNames.length) {
            columnName = columnNames[columnNumber];
        } else {
            columnName = "???[col=" + columnNumber + "]";
        }
        return columnName;
    }

    protected @NonNull StringBuilder toStringBuilder(@Nullable String[] columnNames, Object exampleValue) {
        StringBuilder result = new StringBuilder()
                .append("Filter: ")
                .append(getColumnName(columnNames))
                .append("[")
                .append(columnNumber);
        if (exampleValue != null) {
            result.append(",").append(exampleValue.getClass().getSimpleName());
        }
        result.append("]: ");
        return result;
    }

    public abstract @NonNull String toString(@Nullable String[] columnNames);
}
