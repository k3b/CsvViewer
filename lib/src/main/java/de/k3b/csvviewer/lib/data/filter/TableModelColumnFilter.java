package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public class TableModelColumnFilter extends TableModelRowFilterBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    /** to define compare result as lamda expression */
    @FunctionalInterface
    public interface CompareResult {
        boolean apply(Comparator<Object> comparator, Object fieldValue, Object compareValue);
    }

    public enum ComparatorTyp {
        IS_NOT_NULL("!empty",(c, v1, v2) -> v1 != null),
        IS_NULL("empty",(c, v1, v2) -> v1 == null),
        GREATER_OR_EQUAL(">=",(c, v1, v2) -> c.compare(v1,v2) >= 0),
        LESS_OR_EQUAL("<=",(c, v1, v2) -> c.compare(v1,v2) <= 0),
        NOT_EQUALS("!=",(c, v1, v2) -> c.compare(v1,v2) != 0),
        EQUALS("=" ,(c, v1, v2) -> c.compare(v1,v2) == 0),
        LESS_THAN("<",(c, v1, v2) -> c.compare(v1,v2) < 0),
        GREATER_THAN(">",(c, v1, v2) -> c.compare(v1,v2) > 0),
        ;

        private final String code;
        private final CompareResult compareResult;

        private ComparatorTyp(String code, CompareResult compareResult) {
            this.code = code;
            this.compareResult = compareResult;
        }

        public boolean compareTo(@NonNull Comparator<Object> comparator, @Nullable Object fieldValue, @Nullable Object compareValue) {
            return compareResult.apply(comparator, fieldValue, compareValue);
        }

        @NonNull public String toString(@NonNull String fieldName, String compareValue) {
            if (compareValue == null || this.equals(IS_NOT_NULL) || this.equals(IS_NULL)) return fieldName + " " + code;
            return fieldName + " " + code + " " + compareValue;
        }

        @NonNull public String toString(@NonNull String fieldName, Object compareValue, @Nullable FormatterApi<?> formatter) {
            return toString(fieldName, getString(compareValue, formatter));
        }

        private static String getString(Object compareValue, FormatterApi<?> formatter) {
            String stringValue = null;
            if (compareValue != null) {
                if (formatter != null) {
                    stringValue = formatter.formatObject(compareValue);
                } else {
                    stringValue = compareValue.toString();
                }
            }
            return stringValue;
        }

        @Nullable public String getFieldName(@Nullable String expression) {
            if (expression != null) {
                int found = expression.indexOf(code);
                if (found > 0) return expression.substring(0,found).trim();
            }
            LOGGER.warn("TableModelColumnFilter.ComparatorTyp.getFieldName('{}') : not found", expression);
            return null;
        }

        @Nullable public String getCompareValue(@Nullable String expression) {
            if (expression != null) {
                int found = expression.indexOf(code);
                if (found > 0) return expression.substring(found + code.length()).trim();
            }
            return null;
        }

        @Nullable public Object getCompareValue(@Nullable String expression, @Nullable FormatterApi<?> formatter) {
            String compareString = getCompareValue(expression);
            if (compareString != null && formatter != null) {
                return formatter.parse(compareString);
            }
            return null;
        }

        @Nullable public static ComparatorTyp parseExpression(String expression) {
            if (expression != null) {
                for (ComparatorTyp candidate : ComparatorTyp.values()) {
                    if (expression.contains(candidate.code)) return candidate;
                }
            }
            LOGGER.warn("TableModelColumnFilter.ComparatorTyp.parseExpression('{}') : no matching comparator found", expression);
            return null;
        }
    }

    public static final int EQUALS = 3;
    public static String[] toComparatorString = new String[]{">=","<=","!=","=","<",">"};

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
            String fieldName = typ.getFieldName(expression);
            String compareValueString = typ.getCompareValue(expression);
            Object compareValue = typ.getCompareValue(expression, formatter);
            result = new TableModelColumnFilter(columnNumber, formatter.getComparator(), compareValue, compareValueString, typ);
        }
        return result;
    }

    public TableModelColumnFilter(int columnNumber, @NonNull Comparator<Object> comparator, @Nullable Object compareValue, String compareValueString,@NonNull ComparatorTyp comparatorTyp) {
        super(columnNumber);
        this.comparator = comparator;
        this.compareValue = compareValue;
        this.compareValueString = compareValueString;
        this.comparatorTyp = comparatorTyp;
    }

    @Deprecated
    public static int getModeByTypeString(@NonNull String subTypeName) {
        for (int i = toComparatorString.length-1; i >= 0; i--) {
            if (toComparatorString[i].compareToIgnoreCase(subTypeName) == 0) return i;
        }
        LOGGER.warn("TableModelColumnFilter.getModeByTypeString('{}') : not in {}", subTypeName, toComparatorString);

        return -1;
    }

    protected boolean matchImpl(@Nullable Object value) {
        return comparatorTyp.compareTo(this.comparator, value, this.compareValue);
    }

    public @NonNull String toString(@Nullable String[] columnNames) {
        StringBuilder result = super.toStringBuilder(columnNames, getCompareValue());

        result
            .append(comparatorTyp.toString("", compareValueString))
            ;
        return result.toString();
    }

    public @NonNull Object getCompareValue() {
        return compareValue;
    }

    @Override
    public String toString() {
        return "TableModelColumnFilter{" +
                comparatorTyp.toString("col[" + getColumnNumber() + "] ", compareValueString) +
                '}';
    }
}
