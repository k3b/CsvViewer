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

package de.k3b.csvviewer.lib.data.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import de.k3b.csvviewer.lib.data.comparator.TableModelRowComparator;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.filter.ITableModelRowFilter;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public class TableProperties {
    public static final String PROPERTY_COLUMN_DEFINITION = "colDef";
    public static final String PROPERTY_SORT_ORDER = "colSort";
    public static final String PROPERTY_FILTER = "filter";
    public static final String PROPERTY_MAX_WIDTH = "maxWidth";

    /** @return MaxWidth that belongs to model[columnNumber] */
    public static @Nullable Integer getColumnMaxWidth(@NonNull TableModelApi model, int columnNumber) {
        return model.getColumnProperty(columnNumber, PROPERTY_MAX_WIDTH);
    }

    /** set MaxWidth for model[columnNumber] */
    public static void setColumnMaxWidth(@NonNull TableModelApi model, int columnNumber, @Nullable Integer maxWidth) {
        model.putColumnProperty(columnNumber, PROPERTY_MAX_WIDTH, maxWidth);
    }

    /** @return formatter that belongs to model[columnNumber]. If not found return string-formatter */
    public static @NonNull FormatterApi<?> getColumnFormatter(@NonNull TableModelApi model, int columnNumber) {
        FormatterApi<?> formatter = model.getColumnProperty(columnNumber, PROPERTY_COLUMN_DEFINITION);
        if (formatter == null) formatter = TableColumnType.String.getFormatter();
        return formatter;

    }

    /** sets formatter for model[columnNumber] */
    public static void setColumnFormatter(@NonNull TableModelApi model, int columnNumber, @Nullable FormatterApi<?> formatter) {
        model.putColumnProperty(columnNumber, PROPERTY_COLUMN_DEFINITION, formatter);
    }

    /** @return formatters that belongs to model, one per column. */
    public static @Nullable FormatterApi<?>[]  getColumnFormatters(@NonNull TableModelApi model) {
        FormatterApi<?>[] tableColumnFormatters = model.getColumnProperties(
                new FormatterApi<?>[model.getColumnCount()], PROPERTY_COLUMN_DEFINITION);
        return tableColumnFormatters;
    }

    /** @return filterList that belongs to model */
    public static @Nullable List<@Nullable ITableModelRowFilter> getColumnFilterList(@NonNull TableModelApi model) {
        return model.getColumnProperty(-1, PROPERTY_FILTER);
    }

    /** sets filterList for model */
    public static void setColumnFilterList(@NonNull TableModelApi model, @Nullable List<@Nullable ITableModelRowFilter>  filterList) {
        model.putColumnProperty(-1, PROPERTY_FILTER, filterList);
    }

    /** set sorter for model */
    public static void setColumnSorterList(@NonNull TableModelApi model, TableModelRowComparator sorter) {
        model.putColumnProperty(-1, PROPERTY_SORT_ORDER, sorter);
    }

    /** @return filterList that belongs to model */
    public static @Nullable TableModelRowComparator  getColumnSorter(@NonNull TableModelApi model) {
        return model.getColumnProperty(-1, PROPERTY_SORT_ORDER);
    }
}
