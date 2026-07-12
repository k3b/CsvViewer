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
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.comparator.StringIgnoreCaseComparator;
import de.k3b.csvviewer.lib.data.configuration.ConfigurationModel.DomainColumnModel;
import de.k3b.csvviewer.lib.data.filter.ITableModelRowFilter;
import de.k3b.csvviewer.lib.data.filter.TableFilterFactory;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;

/**
 * Use {@link #addConfig(Object)} to add infos from targetModel to this-ConfigurationModel
 * Use {@link #applyConfiguration(InMemoryTableModel)} to apply infos from ConfigurationModel to targetModel
 * @param <T> configuration data to be processed.
 */
public abstract class ConfigurationProcessorBase<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);

    @NonNull protected final ConfigurationModel configurationModel;
    @NonNull protected final String configurationType;
    protected @Nullable T result = null;

    public ConfigurationProcessorBase(@NonNull ConfigurationModel configurationModel, @NonNull String configurationType) {
        this.configurationModel = configurationModel;
        this.configurationType = configurationType;
    }

    /** Transfer filters based on {@link TableModelRowFilterBase} from {@link #configurationModel} to {@link ConfigurationModel}. */
    protected Object[] addConfig(int columnNumber, String subType, String description, Object parameter1, Object parameter2, Object parameter3) {
        String columnName = configurationModel.getTargetColumnName(columnNumber);

        Object[] row = configurationModel.createEmptyRow();
        row[ConfigurationModel.DomainColumnModel.col_colName]=columnName;
        row[DomainColumnModel.col_configType] = configurationType;
        row[ConfigurationModel.DomainColumnModel.col_subType] = subType;
        row[DomainColumnModel.col_description] = description;
        row[DomainColumnModel.col_parameter1] = parameter1;
        row[DomainColumnModel.col_parameter2] = parameter2;
        row[ConfigurationModel.DomainColumnModel.col_parameter3] = parameter3;

        configurationModel.addRow(row);
        return row;
    }

    /** convert from T configValue in {@link #configurationModel} to Object[] configRow-s for {@link ConfigurationModel}*/
    public abstract void addConfig(@Nullable T configValue);

    /** apply configuration to targetModel */
    public T applyConfiguration(@NonNull InMemoryTableModel targetModel) {
        int col_configType = ConfigurationModel.DomainColumnModel.col_configType;
        String expression = this.configurationModel.getColumnNames()[col_configType] + "=" + configurationType;
        ITableModelRowFilter filter = TableFilterFactory.create(col_configType, TableColumnType.String.getFormatter(),
                expression);

        InMemoryTableModel configurationRows = TableModelUtils.filter(this.configurationModel, filter);

        int rowCount = configurationRows.getRowCount();

        String[] targetColumnNames = targetModel.getColumnNames();
        StringIgnoreCaseComparator stringComparator = new StringIgnoreCaseComparator();

        for(int rowNumber = 0; rowNumber < rowCount; rowNumber ++) {
            Object[] configRow = configurationRows.getRow(rowNumber);
            String columnName = (String) configRow[ConfigurationModel.DomainColumnModel.col_colName];
            int columnNumber = stringComparator.indexOf(targetColumnNames, columnName);
            if (columnNumber >= 0) {
                applyConfiguration(targetModel, columnNumber, configRow);
            } else {
                LOGGER.info("applyConfiguration(columnName='{}'): not found in TargetModel {}", columnName, targetColumnNames);
            }
        }
        return result;
    }

    /** apply configuration of one column {columnNumber} from configRow to targetModel */
    protected abstract void applyConfiguration(@NonNull InMemoryTableModel targetModel, int columnNumber, @Nullable Object @NonNull [] configRow);
}
