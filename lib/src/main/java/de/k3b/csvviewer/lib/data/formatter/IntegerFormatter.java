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

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.ComparatorTyp;
import de.k3b.csvviewer.lib.data.comparator.TableColumnComparatorFactoryImpl;

public class IntegerFormatter extends FormatterBase<Integer> implements TableColumnComparatorFactoryImpl<Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    public IntegerFormatter(boolean nullable) {
        super(Integer.class,null, nullable, ComparatorTyp.VALUE_COMPLEX);
    }
    /**
     * format a native value to a string
     */
    @Override @Nullable
    public String format(@Nullable Integer nativeValue) {
        if (nativeValue == null) return null;
        return Integer.toString(nativeValue);
    }

    /**
     * parse a string to a native value
     */
    @Override @Nullable
    public Integer parse(@Nullable String string) {
        Integer result = null;
        if (string != null) {
            try {
                result = Integer.parseInt(string);
            } catch (NumberFormatException e) {
                LOGGER.error("IntegerFormatter.parse(string='{}') exception: {}", string,e.getMessage());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
