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

package de.k3b.csvviewer.lib.data.comparator;

import java.util.Comparator;

public class StringIgnoreCaseComparator implements Comparator<Object> {
    @Override
    public int compare(Object var1, Object var2) {
        if (var1 == null) {
            return var2 == null ? 0 : 1;
        } else if (var2 == null) {
            return -1;
        } else {
            return ((String)var1).compareToIgnoreCase((String) var2);
        }
    }

    public int indexOf(String[] values, String searchValue) {
        if (values != null && searchValue != null) {
            for (int i=0; i < values.length; i++) {
                if (compare(values[i], searchValue) == 0) return i;
            }
        }
        return -1;
    }
}
