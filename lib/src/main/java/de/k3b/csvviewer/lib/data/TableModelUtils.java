package de.k3b.csvviewer.lib.data;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.analyser.TableColumnAnalyser;
import de.k3b.csvviewer.lib.data.analyser.TableColumnDefinition;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;

/** Utility methods for TableModelApi */
public class TableModelUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);
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

        // create TableColumnAnalyser-s: one analyser per modelToAnalyse column
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
    public static void convertColumns(@NonNull TableModelApi modelToConvert, @NonNull List<TableColumnDefinition> columnDefinitions, boolean setNullIfError) {
        int rowCount = modelToConvert.getRowCount();
        int columnCount = modelToConvert.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            Object[] rowData = modelToConvert.getRow(row);
            for (int col = 0; col < columnCount; col++) {
                TableColumnDefinition columnDefinition = columnDefinitions.get(col);
                Object oldValue = rowData[col];
                if (oldValue instanceof String && columnDefinition != null && columnDefinition.getFormatter() != null) {
                    try {
                        rowData[col] = columnDefinition.getFormatter().parse((String) oldValue);
                    } catch (Exception ex) {
                        LOGGER.error("TableModelUtils.convertColumns(row={},col={}, value) exception: {}", row, col, oldValue, ex.getMessage());
                        if (setNullIfError) rowData[col] = null;
                    }
                }
            }
        }
    }

    public static @NonNull InMemoryTableModel filter(@NonNull InMemoryTableModel sourceModel,
                                            @Nullable List<TableModelRowFilterBase> includeFilter,
                                            @Nullable List<TableModelRowFilterBase> excludeFilter) {
        InMemoryTableModel result = sourceModel.createEmptyClone();
        int rowCount = sourceModel.getRowCount();
        boolean hasIncludes = includeFilter != null && !includeFilter.isEmpty();
        for(int rowNo = 0; rowNo < rowCount; rowNo++) {
            Object[] row = sourceModel.getRow(rowNo);
            if (TableModelRowFilterBase.match(excludeFilter, row) == null &&
                    (hasIncludes && TableModelRowFilterBase.match(excludeFilter, row) != null)) {
            }
        }
        return result;
    }

    public static ConfigurationModel toConfigurationModel(@NonNull InMemoryTableModel sourceModel) {
        ConfigurationModel result = new ConfigurationModel(sourceModel.getColumnNames());

        return result;
    }
}
