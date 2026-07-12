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

import java.util.List;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;

abstract class FormatterBase<T> implements FormatterApi<T> {
    private @Nullable  final Class<?> elementClass;
    private @Nullable final String formatPattern;
    private final Boolean nullable;
    @NonNull
    private final List<@NonNull ComparatorTyp> allowedComparators;

    FormatterBase(@Nullable Class<?> elementClass, @Nullable String formatPattern,
                  boolean nullable, @NonNull List<@NonNull ComparatorTyp> allowedComparators) {
        this.elementClass = elementClass;
        this.formatPattern = formatPattern;
        this.nullable = nullable;
        this.allowedComparators = allowedComparators;
    }

    @Nullable
    public String getElementClassName() {
        return elementClass.getSimpleName();
    }

    @Nullable
    public String getFormatPattern() {
        return formatPattern;
    }

    @Nullable
    public Boolean isNullable() {
        return nullable;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " +FormatterDefinition.toString(this);
    }

    public @NonNull List<@NonNull ComparatorTyp> getAllowedComparators() {
        return allowedComparators;
    }

}
