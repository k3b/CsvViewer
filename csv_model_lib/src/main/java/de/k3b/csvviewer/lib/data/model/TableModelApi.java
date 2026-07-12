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

package de.k3b.csvviewer.lib.data.model;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/** Defines the api to access the table.
 * <p>
 * A table has of a list of rows.
 * Each row has an array of row-columns.
 * Each row has as the same number of columns.
 * <p>
 * Inspired by java swing JTable */
public interface TableModelApi {

    /**
     * Returns the cell value at <code>rowNumber</code> and <code>columnNumber</code>.
     * <p>
     * <b>Note</b>: The columnNumber is specified in the table view's display
     *              order, and not in the <code>TableModel</code>'s columnNumber
     *              order.  This is an important distinction because as the
     *              user rearranges the columns in the table,
     *              the columnNumber at a given index in the view will change.
     *              Meanwhile the user's actions never affect the model's
     *              columnNumber ordering.
     *
     * @param   rowNumber             the rowNumber whose value is to be queried
     * @param   columnNumber          the columnNumber whose value is to be queried
     * @return  the Object at the specified cell
     */
    @Nullable Object getValueAt(int rowNumber, int columnNumber);

    /**
     * Returns the cell values at <code>rowNumber</code>.
     * <p>
     *
     * @param   rowNumber             the rowNumber whose value is to be queried
     * @return  the cell values at <code>rowNumber</code>.
     */
    @NonNull Object[] getRow(int rowNumber);

    /**
     * Sets the value for the cell in the table model at <code>rowNumber</code>
     * and <code>columnNumber</code>.
     * <p>
     * <b>Note</b>: The columnNumber is specified in the table view's display
     *              order, and not in the <code>TableModel</code>'s columnNumber
     *              order.  This is an important distinction because as the
     *              user rearranges the columns in the table,
     *              the columnNumber at a given index in the view will change.
     *              Meanwhile the user's actions never affect the model's
     *              column ordering.
     *
     * <code>aValue</code> is the new value.
     *
     * @param   aValue          the new value
     * @param   rowNumber             the rowNumber of the cell to be changed
     * @param   columnNumber          the columnNumber of the cell to be changed
     * @see #getValueAt
     */
    void setValueAt(@Nullable Object aValue, int rowNumber, int columnNumber);

    /**
     * Returns the number of rows that can be shown in the
     * <code>JTable</code>, given unlimited space.
     *
     * @return the number of rows shown in the <code>JTable</code>
     * @see #getColumnCount
     */
    int getRowCount();

    /**
     * Returns the number of columns in the column model. Note that this may
     * be different from the number of columns in the table model.
     *
     * @return  the number of columns in the table
     * @see #getRowCount
     */
    int getColumnCount();

    /** @return  names of the columns */
    @NonNull String[] getColumnNames();

    /**
     * @return width of given columnNumber in chars. 0 means hidden. -1 means automatic width
     */
    default Integer getColumnMaxWidth(int columnNumber) {
        return TableProperties.getColumnMaxWidth(this, columnNumber);
    }

    /** @return columnNumber specific property for key. */
    @Nullable default <VALUE> VALUE getColumnProperty(int columnNumber, @NonNull Object key)  { return null; }

    /** put columnNumber specific property for key. */
    default void putColumnProperty(int columnNumber, @NonNull Object key, Object value) {}

    /** return all column properties  for key. */
    @Nullable default <VALUE> VALUE[] getColumnProperties(VALUE[] result, @NonNull Object key)  {
        int columnCount = result.length;
        boolean allEmpty = true;
        for (int columnNumber = 0; columnNumber < columnCount; columnNumber++) {
            VALUE value = getColumnProperty(columnNumber, key);
            result[columnNumber] = value;
            if (value != null) {
                allEmpty = false;
            }
        }
        return allEmpty ? null : result;
    }

    /** @return a valid or fixed version of rowCandidate. */
    @NonNull
    Object[] fixRow(@Nullable Object[] rowCandidate);

    /** @return a human readable name of the data. */
    String getName();
}
