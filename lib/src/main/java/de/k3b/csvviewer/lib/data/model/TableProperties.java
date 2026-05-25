package de.k3b.csvviewer.lib.data.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

import de.k3b.csvviewer.lib.data.comparator.TableModelRowComparator;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public class TableProperties {
    public static final String PROPERTY_COLUMN_DEFINITION = "colDef";
    public static final String PROPERTY_SORT_ORDER = "colSort";
    public static final String PROPERTY_FILTER = "filter";

    /** @return formatter that belongs to model[columnNumber] */
    public static @Nullable FormatterApi<?> getColumnFormatter(@NonNull TableModelApi model, int columnNumber) {
        return model.getColumnProperty(columnNumber, PROPERTY_COLUMN_DEFINITION);

    }

    /** @return formatter that belongs to model[columnNumber] */
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
    public static @Nullable List<@Nullable TableModelRowFilterBase> getColumnFilterList(@NonNull TableModelApi model) {
        return model.getColumnProperty(-1, PROPERTY_FILTER);
    }

    /** @return filterList that belongs to model */
    public static void setColumnFilterList(@NonNull TableModelApi model, @Nullable List<@Nullable TableModelRowFilterBase>  filterList) {
        model.putColumnProperty(-1, PROPERTY_FILTER, filterList);
    }


    /** @return sorter that belongs to model */
    public static void setColumnSorterList(@NonNull TableModelApi model, TableModelRowComparator sorter) {
        model.putColumnProperty(-1, PROPERTY_SORT_ORDER, sorter);
    }

    /** @return filterList that belongs to model */
    public static @Nullable TableModelRowComparator  getColumnSorter(@NonNull TableModelApi model) {
        return model.getColumnProperty(-1, PROPERTY_SORT_ORDER);
    }
}
