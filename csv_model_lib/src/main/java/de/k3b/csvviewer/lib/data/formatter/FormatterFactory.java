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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.k3b.csvviewer.lib.data.analyser.DateAnalyser;

public class FormatterFactory {
    public static FormatterApi<?> createFormatter(@NonNull FormatterDefinition formatterDefinition) {
        return createFormatter(formatterDefinition.getElementClassName(), formatterDefinition.getFormatPattern(), formatterDefinition.isNullable(), formatterDefinition.getMaxStringLength());
    }

        @SuppressWarnings("SimpleDateFormat")
    @Nullable
    public static FormatterApi<?> createFormatter(String subType, String formatPattern, Boolean nullable, Integer maxStringLength) {
        FormatterApi<?> formatter = null;

        if (nullable == null) nullable = true;

        if (subType != null) {
            if (subType.compareToIgnoreCase(Integer.class.getSimpleName()) == 0) {
                formatter = new IntegerFormatter(nullable);
            } else if (subType.compareToIgnoreCase(Long.class.getSimpleName()) == 0) {
                formatter = new LongFormatter(nullable);
            } else if (subType.compareToIgnoreCase(Boolean.class.getSimpleName()) == 0 && formatPattern != null) {
                String[] trueFalse = formatPattern.split("\\|");
                if (trueFalse.length >= 2) {
                    formatter = new BooleanFormatter(trueFalse[0], trueFalse[1], nullable);
                }
            } else if (subType.compareToIgnoreCase(Date.class.getSimpleName()) == 0 && formatPattern != null) {
                DateFormat parser;

                if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_DATE) == 0) {
                    parser = SimpleDateFormat.getDateInstance();
                } else if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_TIME) == 0) {
                    parser = SimpleDateFormat.getTimeInstance();
                } else if (formatPattern.compareToIgnoreCase(DateAnalyser.FMT_INTERNAL_DATE_TIME) == 0) {
                    parser = SimpleDateFormat.getDateTimeInstance();
                } else {
                    parser = new SimpleDateFormat(formatPattern);
                }

                formatter = new DateFormatter(formatPattern, parser, nullable);
            }
        }

        if (formatter == null) {
            // assume String
            if (maxStringLength == null) maxStringLength = -1;
            formatter = new StringFormatter(nullable, maxStringLength);
        }
        return formatter;
    }
}
