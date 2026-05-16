package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;

public class TableModelComparatorFilter extends TableModelRowFilterBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    public static final int EQUALS = 0;
    public static final int GREATER_OR_EQUAL = 1;
    public static final int LESS_OR_EQUAL = 2;
    public static String[] toComparatorString = new String[]{"=",">=","<="};

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
