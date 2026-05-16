package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TableModelNotNullFilter extends TableModelRowFilterBase {
    public static final String compareTyp =  "not null";

    public TableModelNotNullFilter(int col) {
        super(col);
    }

    protected boolean matchImpl(@Nullable Object value) {
        return value != null;
    }
    public @NonNull String toString(@Nullable String[] columnNames) {
        return super.toStringBuilder(columnNames, null)
                .append("not null")
                .toString();
    }
}
