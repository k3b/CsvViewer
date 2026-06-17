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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.csv.CsvConfig;
import de.k3b.csvviewer.lib.csv.TableModel2Csv;
import de.k3b.csvviewer.lib.data.analyser.AnalyserReport;
import de.k3b.csvviewer.lib.data.analyser.TableColumnAnalyser;
import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.comparator.TableModelRowComparator;
import de.k3b.csvviewer.lib.data.configuration.FormatterConfigurationProcessor;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel;
import de.k3b.csvviewer.lib.data.configuration.TableColumnType;
import de.k3b.csvviewer.lib.data.filter.ITableModelRowFilter;
import de.k3b.csvviewer.lib.data.filter.TableFilterFactory;
import de.k3b.csvviewer.lib.data.filter.TableModelColumnFilter;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.FormatterDefinition;
import de.k3b.csvviewer.lib.data.formatter.FormatterDefinitionDto;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactory;
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
            TableProperties.setColumnFormatter(modelToAnalyse,col,
                    analysers[col].getFormatter());
            TableProperties.setColumnMaxWidth(modelToAnalyse, col, analysers[col].getMaxStringLength());
        }

        // create report.
        String[] columnNames = modelToAnalyse.getColumnNames();
        AnalyserReport report = new AnalyserReport(
                modelToAnalyse.getName() +"->AnalyserReport");
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

    //             putColumnProperty(-1, TableModelApi.PROPERTY_SORT_ORDER, sorter);

    /**
     * converts all String column values in {@link TableModelApi} to supported native types via {@link FormatterApi<>#getFormatter()}.
     *
     * @param modelToConvert model to be converted
     */
    public static void convertColumns(@NonNull TableModelApi modelToConvert, boolean setNullIfError) {
        int rowCount = modelToConvert.getRowCount();
        int columnCount = modelToConvert.getColumnCount();

        for (int row = 0; row < rowCount; row++) {
            Object[] rowData = modelToConvert.getRow(row);
            for (int col = 0; col < columnCount; col++) {
                FormatterApi<?> columnDefinition = TableProperties.getColumnFormatter(modelToConvert,col);
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
                                                     @Nullable ITableModelRowFilter filter) {
        return TableModelUtils.filter(sourceModel, Collections.singletonList(filter));
    }

    public static @NonNull InMemoryTableModel filter(@NonNull InMemoryTableModel sourceModel,
                                                     @Nullable List<@Nullable ITableModelRowFilter> filterList) {
        String name = sourceModel.getName() + "->Filtered";
        if (filterList != null && filterList.size() == 1) {
            ITableModelRowFilter filter = filterList.get(0);
            if (filter != null) name += "[" + filter +"]";
        }
        InMemoryTableModel result = sourceModel.createEmptyClone(name);
        int rowCount = sourceModel.getRowCount();
        if (filterList != null && !filterList.isEmpty()) {

            for (int rowNo = 0; rowNo < rowCount; rowNo++) {
                Object[] row = sourceModel.getRow(rowNo);
                if (TableFilterFactory.matchAll(filterList, row)) {
                    result.addRow(row);
                }
            }
            TableProperties.setColumnFilterList(result, filterList);
        } else {
            result = sourceModel;
        }
        return result;
    }

    public static void applyConfiguration(@NonNull InMemoryTableModel targetModel, @NonNull ConfigurationModel configModel) {
        int col_configType = ConfigurationModel.DomainColumnModel.col_configType;
        ITableModelRowFilter filter = TableFilterFactory.create(col_configType, TableColumnType.String.getFormatter(),
                FormatterConfigurationProcessor.CONFIGURATION_TYPE);
        InMemoryTableModel columnDefinitions = TableModelUtils.filter(configModel, filter);

        int rowCount = columnDefinitions.getRowCount();

        String[] targetColumnNames = targetModel.getColumnNames();
        StringIgnoreCaseComparator stringComparator = new StringIgnoreCaseComparator();

        for(int rowNumber = 0; rowNumber < rowCount; rowNumber ++) {
            Object[] configColumnDefinition = columnDefinitions.getRow(rowNumber);
            String columnName = (String) configColumnDefinition[ConfigurationModel.DomainColumnModel.col_colName];
            int columnNumber = stringComparator.indexOf(targetColumnNames, columnName);
            if (columnNumber >= 0) {
                FormatterApi<?> formatter = FormatterFactory.createFormatter(getFormatterDefinition(configColumnDefinition));
            }
        }
    }

    private static FormatterDefinition getFormatterDefinition(Object[] configRow) {
        String subType = (String) configRow[ConfigurationModel.DomainColumnModel.col_subType];
        String formatPattern = (String) configRow[ConfigurationModel.DomainColumnModel.col_parameter1];
        Boolean nullable = TableColumnType.Boolean.parseImpl(configRow[ConfigurationModel.DomainColumnModel.col_parameter2]);
        Integer maxStringLength = TableColumnType.Integer.parseImpl(configRow[ConfigurationModel.DomainColumnModel.col_parameter3]);
        return new FormatterDefinitionDto(subType, formatPattern, nullable, maxStringLength);
    }

    public static ConfigurationModel toConfigurationModel(@NonNull TableModelApi sourceModel) {

        ConfigurationModel result = new ConfigurationModel(
                sourceModel.getName() + "->Config",
                sourceModel.getColumnNames());
        FormatterApi<?>[] tableColumnFormatters = TableProperties.getColumnFormatters(sourceModel);
            if (tableColumnFormatters != null) {
                FormatterConfigurationProcessor colDef = new FormatterConfigurationProcessor(result);
                colDef.addConfig(tableColumnFormatters);
            }

/*     TODO !!!
        TableModelRowComparator sorter = sourceModel.getColumnProperty(-1, TableModelApi.PROPERTY_SORT_ORDER);
        if (sorter != null) {
            FilterConfigurationProcessor filterInterpreter = new FilterConfigurationProcessor(result, CONFIGURATION_FILTER_INCLUDE);
        }
*/

        return result;
    }

    public static void printDebug2Console(String header, @NonNull TableModelApi model) {
        System.out.println("# '" + model.getName() + "' " + header);

        List<ITableModelRowFilter> filterList = TableProperties.getColumnFilterList(model);
        if (filterList != null) {
            for(ITableModelRowFilter filter : filterList) {
                System.out.println("# filter '" + filter + "' " + header);
            }
        }

        TableModelRowComparator sorter = TableProperties.getColumnSorter(model);
        if (sorter != null) {
            System.out.println("# sorter '" + sorter + "'");
        }

        int columnCount = model.getColumnCount();

        for (int col = 0; col < columnCount; col++) {
            FormatterApi<?> columnDefinition = TableProperties.getColumnFormatter(model, col);
            if (columnDefinition != null) {
                System.out.println("# " + model.getColumnNames()[col] + ": "
                        + columnDefinition
                        + " width " + model.getColumnMaxWidth(col));
            }
        }

        StringWriter resultWriter = new StringWriter();
        try {
            TableModel2Csv.write(resultWriter, CsvConfig.DEFAULT, model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(resultWriter);
        System.out.println("------------------");
    }

}
