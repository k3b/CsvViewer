package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.configuration.TableColumnType;

public class TableModelNullFilter extends TableModelRowFilterBase {
    public static final String compareTyp = "null";

    public TableModelNullFilter(int col) {
        super(col);
    }

    protected boolean matchImpl(@Nullable Object value) {
        return value == null;
    }

    public @NonNull String toString(@Nullable String[] columnNames) {
        return super.toStringBuilder(columnNames, null)
                .append("null")
                .toString();
    }

}
