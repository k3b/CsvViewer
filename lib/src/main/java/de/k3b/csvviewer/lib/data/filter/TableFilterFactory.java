package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
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

    /** used to de-serialize filter-s from csv */
    public static List<ITableModelRowFilter> expressionsToFilterList(String[] columnNames, @NonNull FormatterApi<?>[] formatters, @NonNull String... expressions) {
        List<ITableModelRowFilter> result = new ArrayList<>(expressions.length);
        for (String expression : expressions) {
            result.add(create(columnNames, formatters, expression));
        }
        return result;
    }

    /** used to serialize filter-s to csv */
    public static String[] filterListToExpressions(@Nullable String[] columnNames, @NonNull List<ITableModelRowFilter> filterList) {
        String[] result = new String[filterList.size()];
        for (int i = filterList.size() - 1; i >= 0; i--) {
            result[i] = filterList.get(i).toExpression(columnNames);
        }
        return result;
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
