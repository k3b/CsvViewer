package de.k3b.csvviewer.lib.data.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.analyser.TableColumnAnalyser;
import de.k3b.csvviewer.lib.data.comparator.TableModelRowComparator;
import de.k3b.csvviewer.lib.data.configuration.FilterConfigurationInterpreter;
import de.k3b.csvviewer.lib.data.configuration.FormatterConfigurationInterpreter;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
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
        LongFormatter idFormatter = new LongFormatter(true);
        for (int row = 0; row < rowCount; row++) {
            @NonNull Object[] rowData = modelToAnalyse.getRow(row);
            for (int col = 0; col < columnCount; col++) {

                // Try to get rowId from column 0. if not found use row-number instead.
                Long id = getId(idFormatter, rowData);
                if (id == null) {
                    id = (long) row;
                    idFormatter = null;
                }

                analysers[col].analyse(id, rowData[col]);
            }
        }

        // transfer analyser result to source modelToAnalyse
        for (int col = 0; col < columnCount; col++) {
            modelToAnalyse.putColumnProperty(
                    col, TableModelApi.PROPERTY_COLUMN_DEFINITION,
                    analysers[col].getFormatter());
        }

        // create report.
        String[] columnNames = modelToAnalyse.getColumnNames();
        AnalyserReport report = new AnalyserReport();
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
     * converts all String column values in {@link TableModelApi} to supported native types via {@link FormatterApi<?>#getFormatter()}.
     *
     * @param modelToConvert model to be converted
     */
    public static void convertColumns(@NonNull TableModelApi modelToConvert, boolean setNullIfError) {
        int rowCount = modelToConvert.getRowCount();
        int columnCount = modelToConvert.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            Object[] rowData = modelToConvert.getRow(row);
            for (int col = 0; col < columnCount; col++) {
                FormatterApi<?> columnDefinition = modelToConvert.getColumnProperty(
                        col, TableModelApi.PROPERTY_COLUMN_DEFINITION);
                Object oldValue = rowData[col];
                if (oldValue instanceof String && columnDefinition != null) {
                    try {
                        rowData[col] = columnDefinition.parse((String) oldValue);
                    } catch (Exception ex) {
                        LOGGER.error("TableModelUtils.convertColumns(row={},col={}, value='{}') exception: {}",
                                row, col, oldValue, ex.getMessage());
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
                result.addRow(row);
            }
        }
        result.putColumnProperty(-1, TableModelApi.PROPERTY_INCLUDE_FILTER, includeFilter);
        result.putColumnProperty(-1, TableModelApi.PROPERTY_INCLUDE_FILTER, excludeFilter);
        return result;
    }

    public static final String CONFIGURATION_FILTER_INCLUDE = "columnFilterInclude";
    public static final String CONFIGURATION_FILTER_EXCLUDE = "columnFilterExclude";
    public static final String CONFIGURATION_SORT = "columnSort";
    public static ConfigurationModel toConfigurationModel(@NonNull TableModelApi sourceModel) {

        ConfigurationModel result = new ConfigurationModel(sourceModel.getColumnNames());
        FormatterApi<?>[] tableColumnFormatters = sourceModel.getColumnProperties(
                new FormatterApi<?>[sourceModel.getColumnCount()], TableModelApi.PROPERTY_COLUMN_DEFINITION);
            if (tableColumnFormatters != null) {
                FormatterConfigurationInterpreter colDef = new FormatterConfigurationInterpreter(result);
                colDef.addConfig(tableColumnFormatters);
            }

        TableModelRowComparator sorter = sourceModel.getColumnProperty(-1, TableModelApi.PROPERTY_SORT_ORDER);
        if (sorter != null) {
            FilterConfigurationInterpreter filterInterpreter = new FilterConfigurationInterpreter(result, CONFIGURATION_FILTER_INCLUDE);
        }

        return result;
    }

}
