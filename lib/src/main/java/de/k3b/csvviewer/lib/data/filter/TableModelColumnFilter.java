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

    /** @return filter for columnNumber, formatter, expression */
    public static TableModelColumnFilter create(String[] columnNames, @NonNull FormatterApi<?>[] formatters, @NonNull String expression) {
        TableModelColumnFilter result = null;

        ComparatorTyp typ = ComparatorTyp.parseExpression(expression);
        String fieldName = typ== null ? null : typ.getFieldName(expression);
        int columnNumber = fieldName == null ? -1 :  new StringIgnoreCaseComparator().indexOf(columnNames, fieldName);
        if (columnNumber >= 0) result = create(columnNumber, formatters[columnNumber], expression);
        return result;
    }

    /** @return filter for columnNumber, formatter, expression */
    public static TableModelColumnFilter create(int columnNumber, @NonNull FormatterApi<?> formatter, @NonNull String expression) {
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

    public @NonNull String toString(@Nullable String[] columnNames) {
        StringBuilder result = super.toStringBuilder(columnNames, getCompareValue());

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
