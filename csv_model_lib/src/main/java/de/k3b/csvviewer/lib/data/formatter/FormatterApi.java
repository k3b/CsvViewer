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

package de.k3b.csvviewer.lib.data.formatter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactory;

/**
 * FormatterApi can format a native value to a string or parse a string to a native value.
 * @param <T> - the native type
 */
public interface FormatterApi<T> extends FormatterDefinition, TableColumnComparatorFactory, Serializable {
    /** format a native value to a string */
    @Nullable String format(@Nullable T nativeValue);
    default @Nullable String formatObject(@Nullable Object nativeValue) {
        return format((T) nativeValue);
    }

    /** parse a string to a native value */
    @Nullable T parse(@Nullable String string);

    default Integer getMaxStringLength() {return null;}

    @NonNull List<@NonNull ComparatorTyp> getAllowedComparators();
}
