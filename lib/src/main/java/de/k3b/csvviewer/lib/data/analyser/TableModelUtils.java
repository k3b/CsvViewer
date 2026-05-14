package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.NonNull;

import de.k3b.csvviewer.lib.data.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.TableModelApi;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;

/** Utility methods for TableModelApi */
public class TableModelUtils {
    /**
     * Creates an {@link AnalyserReport} for a given TableModel.
     * @param modelToAnalyse - item to be analysed.
     * @param maxRows - maximum number of rows to be analysed. 0 means analyse all.
     * @return the report
     */
    public static AnalyserReport analyse(@NonNull TableModelApi modelToAnalyse, int maxRows) {

        int columnCount = modelToAnalyse.getColumnCount();
        int rowCount = modelToAnalyse.getRowCount();
        if (maxRows > 0 && rowCount > maxRows) rowCount = maxRows;

        int maxErrors = rowCount / 20;

        // create TableColumnAnalyser-s
        /** one analyser per modelToAnalyse column */
        TableColumnAnalyser[] analysers = new TableColumnAnalyser[columnCount];
        for (int col = 0; col < columnCount; col++) {
            analysers[col] = new TableColumnAnalyser(maxErrors);
        }

        // analyse rows.
        LongFormatter idFormatter = new LongFormatter();
        for (int row = 0; row < rowCount; row++) {
            @NonNull Object[] rowData = modelToAnalyse.getRow(row);
            for (int col = 0; col < columnCount; col++) {

                // Try to get rowid from column 0. if not found use row-number insted.
                Long id = getId(idFormatter, rowData);
                if (id == null) {
                    id = (long) row;
                    idFormatter = null;
                }

                analysers[col].analyse(id, rowData[col]);
            }
        }

        // create report.
        String[] columnNames = modelToAnalyse.getColumnNames();
        AnalyserReport report = new AnalyserReport(analysers);
        for (int col = 0; col < columnCount; col++) {
            report.defineColumn(col, columnNames[col]);
            analysers[col].addInfoRowsToReport(report);
        }

        return report;
    }

    /** try to extract id from column 0 */
    private static Long getId(LongFormatter idFormatter, @NonNull Object[] rowData) {
        Long id = null;
        if (idFormatter != null) {
            Object idObject = rowData[0];
            if (idObject != null) {
                id = idFormatter.parse(idObject.toString());
            }
        }
        return id;
    }

    /**
     * converts all String column values in {@link TableModelApi} to supported native typse via {@link TableColumnDefinition#getFormatter()}.
     * @param modelToConvert model to be converted
     * @param columnDefinitions used to determine the type
     */
    public static void convertColumns(@NonNull TableModelApi modelToConvert,@NonNull TableColumnDefinition[] columnDefinitions, boolean setNullIfError) {
        int rowCount = modelToConvert.getRowCount();
        int columnCount = modelToConvert.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            @NonNull Object[] rowData = modelToConvert.getRow(row);
            for (int col = 0; col < columnCount; col++) {
                TableColumnDefinition columnDefinition = columnDefinitions[col];
                Object oldValue = rowData[col];
                if (oldValue instanceof String && columnDefinition != null && columnDefinition.getFormatter() != null) {
                    try {
                        rowData[col] = columnDefinition.getFormatter().parse((String) oldValue);
                    } catch (Exception ex) {
                        //
                        if (setNullIfError) rowData[col] = null;
                    }
                }
            }
        }
    }
}
