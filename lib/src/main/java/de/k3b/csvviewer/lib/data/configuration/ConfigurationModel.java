package de.k3b.csvviewer.lib.data.configuration;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.InMemoryTableModel;

/**
 * Tablemodel to show the result of analyser run
 */
public class ConfigurationModel extends InMemoryTableModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    private final @NonNull Map<String, Integer> targetColumnName2ColumnNumber = new TreeMap<>();
    private final @NonNull String[] targetColumnNames;
    private int nextRowId = 0;

    public static class ColumnDefinition {
        public static final int col_id = 0;
        public static final int col_configType = 1;
        public static final int col_subType = 2;
        public static final int col_colName = 3;
        public static final int col_description = 4;
        public static final int col_parameter1 = 5;

        // col_Xxx corresponds to index in columnNames
        private static final String[] columnNames = new String[]{
                "id","configType", "subType", "colName","description","parameter1"};
    }

    public ConfigurationModel(@NonNull String[] targetColumnNames) {
        // columns of ConfigurationModel
        super(ColumnDefinition.columnNames);

        this.targetColumnNames = targetColumnNames;
        for(int i = 0; i < targetColumnNames.length;i++) {
            targetColumnName2ColumnNumber.put(targetColumnNames[i], i);
        }
    }

    /** translates target column name to column number or -1 if not found */
    public int getTargetColumnNumber(@NonNull String colName) {
        Integer columnNo = targetColumnName2ColumnNumber.get(colName);
        return columnNo == null ? -1 : columnNo;
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
        result[ColumnDefinition.col_id]=nextRowId++;
        return result;
    }
}

