/*
 * Copyright (c) 2026 by k3b.
 *
 * This file is part of https://github.com/k3b/CsvViewer.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

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
