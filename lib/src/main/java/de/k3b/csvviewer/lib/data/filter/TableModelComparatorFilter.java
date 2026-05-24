package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public class TableModelComparatorFilter extends TableModelRowFilterBase {
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
            return null;
        }
    }

    public static final int GREATER_OR_EQUAL = 0;
    public static final int LESS_OR_EQUAL = 1;
    public static final int NOT_EQUALS = 2; // not EQUALS
    public static final int EQUALS = 3;
    public static final int LESS_THAN = 4; // not GREATER_OR_EQUAL
    public static final int GREATER_THAN = 5; // not LESS_OR_EQUAL
    public static String[] toComparatorString = new String[]{">=","<=","!=","=","<",">"};

    @NonNull private final Comparator<Object> comparator;
    @NonNull private final Object compareValue;
    private final int comparatorMode;

    public TableModelComparatorFilter(int col, @NonNull Comparator<Object> comparator, @NonNull Object compareValue, int comparatorMode) {
        super(col);
        this.comparator = comparator;
        this.compareValue = compareValue;
        this.comparatorMode = comparatorMode;
    }

    public static int getModeByTypeString(@NonNull String subTypeName) {
        for (int i = toComparatorString.length-1; i >= 0; i--) {
            if (toComparatorString[i].compareToIgnoreCase(subTypeName) == 0) return i;
        }
        LOGGER.warn("TableModelComparatorFilter.getModeByTypeString('{}') : not in {}", subTypeName, toComparatorString);

        return -1;
    }

    protected boolean matchImpl(@Nullable Object value) {
        if (value == null) return false;
        int compareResult = comparator.compare(value, getCompareValue());
        if (compareResult == 0 && comparatorMode == EQUALS) return true;
        if (compareResult <= 0 && comparatorMode == LESS_OR_EQUAL) return true;
        if (compareResult >= 0 && comparatorMode == GREATER_OR_EQUAL) return true;
        return false;
    }

    public @NonNull String toString(@Nullable String[] columnNames) {
        StringBuilder result = super.toStringBuilder(columnNames, getCompareValue());

        result
            .append(getComparatorModeName())
            .append(" ")
            .append(TableColumnType.toString(getCompareValue()));
        return result.toString();
    }

    public String getComparatorModeName() {
        String op;
        if (comparatorMode >= 0 && comparatorMode < toComparatorString.length) {
            op = toComparatorString[comparatorMode];
        } else {
            op = "???[comparatorMode=" + comparatorMode + "]";
            LOGGER.warn("TableModelComparatorFilter.getComparatorModeName() : {}. Not in {}", op, toComparatorString);
        }
        return op;
    }

    public @NonNull Object getCompareValue() {
        return compareValue;
    }
}
