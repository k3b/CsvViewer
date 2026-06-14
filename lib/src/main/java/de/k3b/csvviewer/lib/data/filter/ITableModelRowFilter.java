package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ITableModelRowFilter {
    boolean match(Object[] row);
    String toExpression(@Nullable String[] columnNames);
}
