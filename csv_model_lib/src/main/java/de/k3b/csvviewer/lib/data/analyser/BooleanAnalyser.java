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

package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.formatter.BooleanFormatter;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;

/** can string be converted to {@link Boolean} ? */
public class BooleanAnalyser extends AnalyserBase<Boolean,String> implements AnalyserApi<String>, FormatterFactoryApi<Boolean> {
    //-------------
    // true: can be convertet to boolean (there are only 2 different string values ie Yes/No or true/false)
    private String value1 = null;
    private String value2 = null;

    public BooleanAnalyser() {
        super(1);
    }

    public boolean analyse(Long rowId, String stringValue) {
        super.analyse(rowId, stringValue);
        boolean isBoolean = false;
        if (isEnabled()) {
            isBoolean = true;
            if (value1 == null) {
                value1 = stringValue.toLowerCase();
            } else if (value1.compareToIgnoreCase(stringValue) != 0) {
                if (value2 == null) {
                    value2 = stringValue.toLowerCase();
                } else if (value2.compareToIgnoreCase(stringValue) != 0) {
                    // third value found: cannot be boolean
                    isBoolean = false;
                    addError(rowId,stringValue);
                }
            }
        }
        return isBoolean;
    }

    @Nullable
    public BooleanFormatter createFormatter() {
        if (!isEnabled()) return null;
        return BooleanFormatter.createBooleanFormatter(value1, value2);
    }


}

