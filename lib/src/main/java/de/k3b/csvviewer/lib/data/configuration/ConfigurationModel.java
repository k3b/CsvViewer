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

package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.model.ColumnModel;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;

/**
 * Tablemodel to show the result of analyser run
 */
public class ConfigurationModel extends InMemoryTableModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    private final @NonNull Map<String, Integer> targetColumnName2ColumnNumber = new TreeMap<>();
    private final @NonNull String[] targetColumnNames;
    private int nextRowId = 0;

    public static class DomainColumnModel implements ColumnModel {
        public static final int col_id = 0;
        public static final int col_configType = 1;
        public static final int col_subType = 2;
        public static final int col_colName = 3;
        public static final int col_description = 4;
        public static final int col_parameter1 = 5;
        public static final int col_parameter2 = 6;
        public static final int col_parameter3 = 7;

        // col_Xxx corresponds to index in columnNames
        private static final String[] columnNames = new String[]{
                "id",
                "configType",
                "subType",
                "colName",
                "description"
                ,"parameter1",
                "parameter2",
                "parameter3"};
        /* !!! TODO
        private static final FormatterApi<?>[] formatters = new FormatterApi<?>[]{
                TableColumnType.Integer,
                TableColumnType.String,
                TableColumnType.String, // "subType",
                TableColumnType.String, // "colName",
                TableColumnType.String, // "description"
                TableColumnType.String, // ,"parameter1",
                TableColumnType.String, // "parameter2",
                TableColumnType.String, // "parameter3"};
*/

        @Override
        @NonNull public String[] getColumnNames() {
            return columnNames;
        }

    }

    public ConfigurationModel(String name, @NonNull String[] targetColumnNames) {
        // columns of ConfigurationModel
        super(name, DomainColumnModel.columnNames);

        this.targetColumnNames = targetColumnNames;
        for(int i = 0; i < targetColumnNames.length;i++) {
            targetColumnName2ColumnNumber.put(targetColumnNames[i], i);
        }
    }

    /** translates target column name to column number or -1 if not found */
    public int getTargetColumnNumber(@NonNull String colName) {
        Integer columnNo = targetColumnName2ColumnNumber.get(colName);
        if (columnNo == null) {
            LOGGER.warn("ConfigurationModel.getTargetColumnNumber(colName='{}') error: not found in {}", colName, targetColumnNames);
            columnNo = -1;
        }
        return columnNo;
    }

    public @NonNull String[] getTargetColumnNames() {
        return targetColumnNames;
    }

    public @NonNull String getTargetColumnName(int colNo) {
        String result;
        if (colNo >= 0 && colNo < targetColumnNames.length) {
            result = targetColumnNames[colNo];
        } else {
            result = "???[colNo=" + colNo + "]";
            LOGGER.warn("ConfigurationModel.getTargetColumnName(colNo='{}') :  not in {}", colNo, targetColumnNames);
        }
        return result;
    }

    @Override @NonNull
    public Object[] createEmptyRow() {
        Object[] result = super.createEmptyRow();
        result[DomainColumnModel.col_id]=nextRowId++;
        return result;
    }
}

