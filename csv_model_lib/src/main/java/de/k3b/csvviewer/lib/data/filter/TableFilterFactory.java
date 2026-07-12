/*
 * Copyright (c) 2026 by k3b.
 *
 * This file is part of https://github.com/k3b/CsvViewer.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

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
