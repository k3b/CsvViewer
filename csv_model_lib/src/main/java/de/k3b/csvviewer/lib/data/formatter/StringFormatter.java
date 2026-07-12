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

import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class StringFormatter extends FormatterBase<String> implements TableColumnComparatorFactoryImpl<String> {
    private final Integer maxStringLength;

    public StringFormatter(boolean nullable, int maxStringLength) {
        super(String.class,null, nullable, ComparatorTyp.VALUE_SIMPLE);
        this.maxStringLength = maxStringLength;
    }

    @Nullable @Override
    public String format(@Nullable String result) {
        return result;
    }

    @Override
    public String toString() {
        return "StringFormatter";
    }

    @Nullable @Override
    public String parse(@Nullable  String result) {
        return result;
    }

    /** Creates a null-save comparator for tableColumn. Nulls are last. */
    @NonNull
    public Comparator<Object> getComparator() {
        return new StringIgnoreCaseComparator();
    }

    @Override
    public Integer getMaxStringLength() {
        return maxStringLength;
    }
}
