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

package de.k3b.csvviewer.lib.csv;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.Reader;

import de.k3b.csvviewer.lib.data.model.TableModelApi;

public class Csv2Html {
    @NonNull
    public static String toHtmlTable(String name, String csvString, int options) throws IOException {
        try(Csv2TableModel parser = new Csv2TableModel(options)) {
            TableModelApi model = parser.toTableModel("", csvString);
            return TableModel2Html.toHtmlTable(model, null, null, 0);
        }
    }

    @NonNull
    public static String toHtmlTable(String name, Reader csvReader, int options) throws IOException {
        try(Csv2TableModel parser = new Csv2TableModel(options)) {
            TableModelApi model = parser.toTableModel(name, csvReader);
            return TableModel2Html.toHtmlTable(model, null, null, 0);
        }
    }

}
