package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public class TableFilterFactory {
    /**
     * @return true if all filters inside filterList matches its condition.
     */
    public static boolean matchAll(@Nullable List<ITableModelRowFilter> filterList, Object[] row) {
        if (filterList != null) {
            for (int i = filterList.size() - 1; i >= 0; i--) {
                ITableModelRowFilter filter = filterList.get(i);
                if (!filter.match(row)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** @return filter for columnNumber, formatter, expression */
    public static ITableModelRowFilter create(String[] columnNames, @NonNull FormatterApi<?>[] formatters, @NonNull String expression) {
        return TableModelColumnFilter.createImpl(columnNames, formatters, expression);
    }

    /** @return filter for columnNumber, formatter, expression */
    public static ITableModelRowFilter create(int columnNumber, @NonNull FormatterApi<?> formatter, @NonNull String expression) {
        return TableModelColumnFilter.createImpl(columnNumber, formatter, expression);
    }
}
