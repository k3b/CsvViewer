package de.k3b.csvviewer.lib.data.comparator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.filter.TableModelColumnFilter;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;

public enum ComparatorTyp {
    GREATER_OR_EQUAL(0, ">=", (c, v1, v2) -> c.compare(v1, v2) >= 0),
    LESS_OR_EQUAL(1, "<=", (c, v1, v2) -> c.compare(v1, v2) <= 0),
    NOT_EQUALS(2, "!=", (c, v1, v2) -> c.compare(v1, v2) != 0),
    EQUALS(3, "=", (c, v1, v2) -> c.compare(v1, v2) == 0),
    LESS_THAN(4, "<", (c, v1, v2) -> c.compare(v1, v2) < 0),
    GREATER_THAN(5, ">", (c, v1, v2) -> c.compare(v1, v2) > 0),
    IS_NOT_NULL(6, "!empty", (c, v1, v2) -> v1 != null),
    IS_NULL(7, "empty", (c, v1, v2) -> v1 == null),
    ;

    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    private final int menuOffset;
    private final String code;
    private final TableModelColumnFilter.CompareResult compareResult;

    /** can be applied to null value */
    public static final List<ComparatorTyp> VALUE_NULL = Arrays.asList(IS_NOT_NULL,IS_NULL);

    /** can be applied to boolean and String */
    public static final List<ComparatorTyp> VALUE_SIMPLE = Arrays.asList(EQUALS,NOT_EQUALS, IS_NOT_NULL,IS_NULL);

    /** can be applied to Integer, Long, Double, Date */
    public static final List<ComparatorTyp> VALUE_COMPLEX = Arrays.asList(ComparatorTyp.values());

    private ComparatorTyp(int menuOffset, String code, TableModelColumnFilter.CompareResult compareResult) {
        this.menuOffset = menuOffset;
        this.code = code;
        this.compareResult = compareResult;
    }

    public boolean compareTo(@NonNull Comparator<Object> comparator, @Nullable Object fieldValue, @Nullable Object compareValue) {
        return compareResult.apply(comparator, fieldValue, compareValue);
    }

    @NonNull
    public String toString(@NonNull String fieldName, String compareValue) {
        if (compareValue == null || this.equals(IS_NOT_NULL) || this.equals(IS_NULL))
            return fieldName + " " + code;
        return fieldName + " " + code + " " + compareValue;
    }

    @NonNull
    public String toString(@NonNull String fieldName, Object compareValue, @Nullable FormatterApi<?> formatter) {
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

    @Nullable
    public String getFieldName(@Nullable String expression) {
        if (expression != null) {
            int found = expression.indexOf(code);
            if (found > 0) return expression.substring(0, found).trim();
        }
        LOGGER.warn("ComparatorTyp.getFieldName('{}') : not found", expression);
        return null;
    }

    @Nullable
    public String getCompareValue(@Nullable String expression) {
        if (expression != null) {
            int found = expression.indexOf(code);
            if (found > 0) return expression.substring(found + code.length()).trim();
        }
        return null;
    }

    @Nullable
    public Object getCompareValue(@Nullable String expression, @Nullable FormatterApi<?> formatter) {
        String compareString = getCompareValue(expression);
        if (compareString != null && formatter != null) {
            return formatter.parse(compareString);
        }
        return null;
    }

    /** creates a menu with id and title */
    @NonNull
    public static TreeMap<@NonNull Integer,@NonNull String> createMenu(@Nullable String compareString, List<ComparatorTyp> allowedComparatorTypes) {
        TreeMap<Integer, String> result = new TreeMap<>();

        if (compareString == null || compareString.isEmpty()) {
            allowedComparatorTypes = ComparatorTyp.VALUE_NULL;
        }

        for (ComparatorTyp comparatorTyp : allowedComparatorTypes) {
            result.put(comparatorTyp.menuOffset,comparatorTyp.toString("", compareString));
        }
        return result;
    }

    @Nullable
    public static ComparatorTyp parseExpression(String expression) {
        if (expression != null) {
            for (ComparatorTyp candidate : ComparatorTyp.values()) {
                if (expression.contains(candidate.code)) return candidate;
            }
        }
        LOGGER.warn("ComparatorTyp.parseExpression('{}') : no matching comparator found", expression);
        return null;
    }

    @Nullable
    public static ComparatorTyp getComparatorTyp(int menuOffset) {
        for (ComparatorTyp candidate : ComparatorTyp.values()) {
            if (candidate.menuOffset == menuOffset) return candidate;
        }
        LOGGER.warn("ComparatorTyp.getComparatorTyp('{}') : no matching comparator found", menuOffset);
        return null;
    }

    public int getMenuOffset() {
        return menuOffset;
    }
}
