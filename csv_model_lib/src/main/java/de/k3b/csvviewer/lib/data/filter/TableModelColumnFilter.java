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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public class TableModelColumnFilter extends TableModelRowFilterBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    @NonNull private final ComparatorTyp comparatorTyp;
    @NonNull private final Comparator<Object> comparator;
    @Nullable private final Object compareValue;
    @Nullable private final String compareValueString;

    public static @Nullable TableModelColumnFilter createImpl(String[] columnNames, @NonNull FormatterApi<?>[] formatters, @NonNull String expression) {
        TableModelColumnFilter result = null;

        ComparatorTyp typ = ComparatorTyp.parseExpression(expression);
        String fieldName = typ== null ? null : typ.getFieldName(expression);
        int columnNumber = fieldName == null ? -1 :  new StringIgnoreCaseComparator().indexOf(columnNames, fieldName);
        if (columnNumber >= 0) result = createImpl(columnNumber, formatters[columnNumber], expression);
        return result;
    }

    public static @Nullable TableModelColumnFilter createImpl(int columnNumber, @NonNull FormatterApi<?> formatter, @NonNull String expression) {
        TableModelColumnFilter result = null;
        ComparatorTyp typ = ComparatorTyp.parseExpression(expression);
        if (typ != null) {
            String compareValueString = typ.getCompareValue(expression);
            Object compareValue = typ.getCompareValue(expression, formatter);
            result = new TableModelColumnFilter(columnNumber, formatter.getComparator(), compareValue, compareValueString, typ);
        }
        return result;
    }

    public TableModelColumnFilter(int columnNumber, @NonNull Comparator<Object> comparator, @Nullable Object compareValue,@Nullable  String compareValueString,@NonNull ComparatorTyp comparatorTyp) {
        super(columnNumber);
        this.comparator = comparator;
        this.compareValue = compareValue;
        this.compareValueString = compareValueString;
        this.comparatorTyp = comparatorTyp;
    }

    protected boolean matchImpl(@Nullable Object value) {
        return comparatorTyp.compareTo(this.comparator, value, this.compareValue);
    }

    public @NonNull String toExpression(@Nullable String[] columnNames) {
        StringBuilder result = super.toExpression(columnNames, getCompareValue());

        result
            .append(comparatorTyp.toExpression("", compareValueString))
            ;
        return result.toString();
    }

    public @Nullable  Object getCompareValue() {
        return compareValue;
    }

    @Override
    public String toString() {
        return "TableModelColumnFilter{" +
                comparatorTyp.toExpression("col[" + getColumnNumber() + "] ", compareValueString) +
                '}';
    }
}
