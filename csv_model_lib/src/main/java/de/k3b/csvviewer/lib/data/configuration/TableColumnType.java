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

package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Comparator;

import de.k3b.csvviewer.lib.data.analyser.DateAnalyser;
import de.k3b.csvviewer.lib.data.formatter.BooleanFormatter;
import de.k3b.csvviewer.lib.data.formatter.DateFormatter;
import de.k3b.csvviewer.lib.data.formatter.DoubleFormatter;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.IntegerFormatter;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;
import de.k3b.csvviewer.lib.data.formatter.StringFormatter;

/**
 * Translates between Object and String.
 */
@SuppressWarnings("SimpleDateFormat")
public enum TableColumnType {
    Integer(new IntegerFormatter(true)),
    Long(new LongFormatter(true)),
    Boolean(new BooleanFormatter("1","0", true)),
    Date(new DateFormatter(DateAnalyser.ISO_DATE_TIME_PATTERN, new SimpleDateFormat(DateAnalyser.ISO_DATE_TIME_PATTERN), true)),
    String(new StringFormatter(true, 0)),
    Double(new DoubleFormatter(null, null, true))
    ;

    @NonNull private final FormatterApi<?> formatter;

    private TableColumnType(@NonNull FormatterApi<?> formatter) {
        this.formatter = formatter;
    }

    public @NonNull FormatterApi<?> getFormatter() {
        return formatter;
    }

    @Nullable public String toStringInternal(@Nullable Object value) {
        return formatter.formatObject(value);
    }

    @Nullable public <T> T parseImpl(@Nullable Object object) {
        if (object == null) return null;
        return (T) formatter.parse(object.toString());
    }

    /** parse a string to a native value */
    @Nullable Object parse(@Nullable String string) {
        return formatter.parse(string);
    }

    public static String toString(@Nullable Object value) {
        String result = null;
        if (value != null) {
            String typeName = value.getClass().getSimpleName();
            TableColumnType tableColumnType = valueOf(typeName);
            if (tableColumnType != null) {
                result = tableColumnType.toStringInternal(value);
            } else {
                result = tableColumnType +"[" + value.toString() + "]";
            }
        }
        return result;
    }

    public static @Nullable Comparator<Object> getComparator(@Nullable Object value) {
        Comparator<Object> result = null;
        if (value != null) {
            String typeName = value.getClass().getSimpleName();
            TableColumnType tableColumnType = valueOf(typeName);
            if (tableColumnType != null) {
                result = tableColumnType.formatter.getComparator();
            }
        }
        return result;
    }

}
