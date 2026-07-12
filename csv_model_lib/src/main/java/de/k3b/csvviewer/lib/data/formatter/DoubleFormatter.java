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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class DoubleFormatter extends FormatterBase<Double> implements TableColumnComparatorFactoryImpl<Double> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);
    private final String formatPattern;
    private final NumberFormat parser;


    public DoubleFormatter(@Nullable String formatPattern, @Nullable NumberFormat parser, Boolean nullable) {
        super(Double.class,formatPattern, nullable, ComparatorTyp.VALUE_COMPLEX);
        this.formatPattern = formatPattern;
        this.parser = parser;
    }
    /**
     * format a native value to a string
     */
    @Override @Nullable
    public String format(@Nullable Double nativeValue) {
        if (nativeValue == null) return null;
        if (parser != null) return parser.format(nativeValue);
        return Double.toString(nativeValue);
    }

    /**
     * parse a string to a native value
     */
    @Override @Nullable
    public Double parse(@Nullable String string) {
        Double result = null;
        if (string != null) {
            try {
                if (parser != null) {
                    result = parser.parse(string).doubleValue();
                } else {
                    result = Double.parseDouble(string);
                }
            } catch (Exception e) {
                LOGGER.error("DoubleFormatter.parse(string='{}', formatPattern='{}') exception: {}",
                        string,formatPattern, e.getMessage());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
