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

import de.k3b.csvviewer.lib.data.model.ColumnModel;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;

/**
 * Tablemodel to show the result of analyser run
 */
public class AnalyserReport extends InMemoryTableModel {

    private int nextRowId = 0;
    private int colNo;
    private String colName;

    public static class DomainColumnModel implements ColumnModel  {
        public static final int col_id = 0;
        public static final int col_colNo = 1;
        public static final int col_colName = 2;
        public static final int col_parser= 3;
        public static final int col_subParser= 4;
        public static final int col_result= 5;
        public static final int col_enabled= 6;
        public static final int col_min= 7;
        public static final int col_max= 8;
        public static final int col_success= 9;
        public static final int col_errorRowIds= 10;

        // col_Xxx corresponds to index in columnNames
        private static final String[] columnNames = new String[]{
                "id","colNo", "colName","parser","subParser",
                "result","enabled","min","max", "success",
                "errorRowIds"};
        @Override
        @NonNull public String[] getColumnNames() {
            return columnNames;
        }

    }

    public AnalyserReport(String name) {
        super(name, DomainColumnModel.columnNames);
    }

    public void defineColumn(int colNo, String colName) {
        this.colNo = colNo;
        this.colName = colName;
    }

    public Object[] addReportRow(String parser, String subParser) {
        Object[] result = createEmptyRow();
        result[DomainColumnModel.col_parser]=parser;
        result[DomainColumnModel.col_subParser]=subParser;
        addRow(result);
        return result;
    }
    @Override @NonNull
    public Object[] createEmptyRow() {
        Object[] result = super.createEmptyRow();
        result[DomainColumnModel.col_id]=nextRowId++;
        result[DomainColumnModel.col_colNo]=this.colNo;
        result[DomainColumnModel.col_colName]=this.colName;
        return result;
    }
}

