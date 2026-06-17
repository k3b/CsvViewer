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

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/** a (base-)class to collect error infos */
public class ErrorInfo {
    /** Each call to {@link #addError(Long, String)} will be remembered here. */

    @NonNull private final List<Long> errorRowIds = new ArrayList<>();
    @NonNull private final List<String> errorRowValues = new ArrayList<>();

    /** remember a parsing error for the {@link AnalyserReport} report. */
    public void addError(Long rowId, String stringValue) {
        errorRowIds.add(rowId);
        errorRowValues.add(stringValue);
    }

    public StringBuilder appendErrorInfo(StringBuilder result) {
        if (!errorRowIds.isEmpty()) {
            for (int i = 0; i < errorRowIds.size(); i++) {
                result
                        .append(errorRowIds.get(i))
                        .append(":")
                        .append(errorRowValues.get(i))
                        .append(",")
                ;
            }
        }
        return result;
    }

    public int errorCount() {
        return errorRowIds.size();
    }

}
