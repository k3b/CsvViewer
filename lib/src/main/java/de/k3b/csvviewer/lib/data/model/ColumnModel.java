package de.k3b.csvviewer.lib.data.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;

public interface ColumnModel extends Serializable {
    @NonNull String[] getColumnNames();

    @Nullable default Object getDomainColumnValue(@Nullable Object[] row, int columnNumber) {
        if (columnNumber >= 0 && row != null && columnNumber < row.length) return row[columnNumber];
        return null;
    }

    default void setColumnValue(@Nullable Object[] row, int columnNumber, @Nullable Object value) {
        if (columnNumber >= 0 && row != null && columnNumber < row.length) row[columnNumber] = value;
    }

    default Object[] createRow() {
        return new Object[getColumnNames().length];
    }
}
