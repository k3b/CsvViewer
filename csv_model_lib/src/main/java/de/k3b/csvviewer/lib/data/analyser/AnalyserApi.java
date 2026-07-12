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

/**
 * feed a lot of example data from one csv-column through this api via {@link #analyse(Long, Object)}
 * to infer table column datatype , nullable, columnWidth, ...
  */
public interface AnalyserApi<T>  {
    /** analyse content of columnValue */
    boolean analyse(Long rowId, @Nullable T columnValue);

    /** transfer the analyse-result of this to the {@link AnalyserReport} report */
    void addInfoRowsToReport(AnalyserReport report);
}
