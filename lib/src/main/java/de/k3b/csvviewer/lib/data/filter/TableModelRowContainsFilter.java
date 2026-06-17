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

import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

/** {@link #match(Object[])} returns true if every token from {@link #lowerCaseSearchValues} is contained in
 * one cell of current row (case insensitive). */
public class TableModelRowContainsFilter implements ITableModelRowFilter {
    public static final String COMPARATOR_ID = ComparatorTyp.CONTAINS.getCode();
    private final FormatterApi<?>[] formatters;
    private final String[] lowerCaseSearchValues;
    private final String name;

    /**
     * create filter for seachValue.
     * @param formatters used to format the current row.
     * @param seachValue " " delimited list of tokens that must be in every cell of row.
     */
    public TableModelRowContainsFilter(@Nullable FormatterApi<?>[] formatters, String seachValue) {
        this.formatters = formatters;
        this.name = COMPARATOR_ID + seachValue;
        lowerCaseSearchValues = seachValue == null ? null :  seachValue.trim().toLowerCase().split(" ");
    }

    @Override
    public boolean match(Object[] row) {
        if (lowerCaseSearchValues != null && row != null) {
            String[] stringRow = toStringArrayLowerCase(row);
            for (String seachValue : lowerCaseSearchValues) {
                if (seachValue != null && !seachValue.isEmpty() && !contains(seachValue, stringRow)) return false;
            }
        }
        return true;
    }

    private boolean contains(String seachValue, String[] row) {
        for(String cell : row) {
            if (cell != null && cell.contains(seachValue)) return true;
        }
        return false;
    }

    private String[] toStringArrayLowerCase(Object[] row) {
        String[] result = new String[row.length];
        for (int columnNumber = 0; columnNumber < row.length; columnNumber++)
        {
            Object cell = row[columnNumber];
            if (cell != null && columnNumber < formatters.length) {
                FormatterApi<?> formatter = formatters[columnNumber];
                String stringValue = formatter == null ? cell.toString() : formatter.formatObject(cell);
                result[columnNumber] = stringValue == null ? null : stringValue.trim().toLowerCase();
            }
        }
        return result;
    }

    @Override
    public String toExpression(@Nullable String[] columnNames) {
        return toString();
    }

    @Override public String toString() {
        return name;
    }
}
