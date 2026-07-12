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

import java.util.Comparator;
import java.util.List;

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;

public class ObjectFormatter implements FormatterApi<Object> {
    @NonNull private final FormatterApi<?> formatterImpl;

    public ObjectFormatter(@NonNull FormatterApi<?> formatterImpl) {
        this.formatterImpl = formatterImpl;
    }

    @Nullable @Override
    public String format(@Nullable Object nativeValue) {
        String result = null;
        if (nativeValue != null) {
            if (formatterImpl != null) {
                result = formatterImpl.formatObject(nativeValue);
            } else {
                result = nativeValue.toString().trim();
            }
        }
        return result;
    }

    @Nullable @Override
    public Object parse(@Nullable  String nativeValue) {
        Object result = nativeValue;
        if (formatterImpl != null) {
            result = formatterImpl.parse(nativeValue);
        }
        return result;
    }

    @Override
    public String getElementClassName() {
        return formatterImpl == null ? null :formatterImpl.getElementClassName();
    }

    @Override
    public String getFormatPattern() {
        return formatterImpl == null ? null :formatterImpl.getFormatPattern();
    }

    @Override
    public Boolean isNullable() {
        return formatterImpl == null ? true :formatterImpl.isNullable();
    }

    @Override
    public Integer getMaxStringLength() {
        return formatterImpl == null ? null :formatterImpl.getMaxStringLength();
    }

    @Override
    public @NonNull List<@NonNull ComparatorTyp> getAllowedComparators() {
        return formatterImpl == null
                ? ComparatorTyp.VALUE_SIMPLE
                : formatterImpl.getAllowedComparators();
    }

    @Override
    public String toString() {
        return "ObjectFormatter{" +
                "formatter=" + formatterImpl +
                '}';
    }

    /**
     * Creates a null-save comparator for tableColumn. Nulls are last.
     */
    @Override
    public @NonNull Comparator<Object> getComparator() {
        return formatterImpl == null ? null : formatterImpl.getComparator();
    }
}
