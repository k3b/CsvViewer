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

/** simple csv test data */
public class DemoData {
    public static final String demoCsvName = "DemoData";
    public static final String demoCsv = "# some comment\n" +
            // ' is optional string delimiter
            "'name',greeting,html,birthday\n" +
            "peter,hello peter,0,1970-01-07\n" +
            "susi,hello susi,0,2001-12-25\n" +
            // is not a comment because surrounded by delimiter
            "' #world#',hello #world#,0\n" +
            // html - escaping
            "<b>nobody</b>,hello <b>nobody</b>,1\n" +
            ",hello ,0\n";
}
