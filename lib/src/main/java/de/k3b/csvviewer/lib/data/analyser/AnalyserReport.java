package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.NonNull;

import de.k3b.csvviewer.lib.data.InMemoryTableModel;

/**
 * Tablemodel to show the result of analyser run
 */
public class AnalyserReport extends InMemoryTableModel {

    private int nextRowId = 0;
    private int colNo;
    private String colName;

    public static class ColumnDefinition {
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
    }

    public AnalyserReport() {
        super(ColumnDefinition.columnNames);
    }

    public void defineColumn(int colNo, String colName) {
        this.colNo = colNo;
        this.colName = colName;
    }

    public Object[] addReportRow(String parser, String subParser) {
        Object[] result = createEmptyRow();
        result[ColumnDefinition.col_parser]=parser;
        result[ColumnDefinition.col_subParser]=subParser;
        addRow(result);
        return result;
    }
    @Override @NonNull
    public Object[] createEmptyRow() {
        Object[] result = super.createEmptyRow();
        result[ColumnDefinition.col_id]=nextRowId++;
        result[ColumnDefinition.col_colNo]=this.colNo;
        result[ColumnDefinition.col_colName]=this.colName;
        return result;
    }
}

