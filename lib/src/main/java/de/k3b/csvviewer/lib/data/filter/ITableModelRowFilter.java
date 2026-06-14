package de.k3b.csvviewer.lib.data.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;

public interface ITableModelRowFilter extends Serializable {
    boolean match(Object[] row);
    String toExpression(@Nullable String[] columnNames);
}
