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

import org.jspecify.annotations.NonNull;

import java.io.Serializable;
import java.util.Comparator;

/** Defines a factory to create a TableColumnComparator that can be used to sort a table  */
public interface TableColumnComparatorFactory extends Serializable {

    /** Creates a null-save comparator for tableColumn. Nulls are last. */
    @NonNull
    Comparator<Object> getComparator();
}
