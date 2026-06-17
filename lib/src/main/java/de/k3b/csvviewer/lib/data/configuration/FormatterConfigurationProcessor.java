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
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.FormatterDefinition;
import de.k3b.csvviewer.lib.data.formatter.FormatterDefinitionDto;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactory;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;
import de.k3b.csvviewer.lib.data.model.TableProperties;

/** {@link ConfigurationModel} interpreter for filters based on {@link TableModelRowFilterBase}  */
public class FormatterConfigurationProcessor extends ConfigurationProcessorBase<@Nullable FormatterDefinition[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);
    public static final String CONFIGURATION_TYPE = "columnDefinition";
    public FormatterConfigurationProcessor(@NonNull ConfigurationModel configurationModel) {
        super(configurationModel, CONFIGURATION_TYPE);
    }

    /** Transfer formatter info from {@link #configurationModel} to {@link ConfigurationModel}. */
    @Override
    public void addConfig(FormatterDefinition[] definitions) {
        if (definitions != null) {
            int colCount = definitions.length;
            if (colCount > 0) {
                for (int columnNumber = 0; columnNumber < colCount; columnNumber++) {
                    addConfig(columnNumber, definitions[columnNumber]);
                }
            }
        }
    }

    /** apply configuration of one column {columnNumber} from configRow to targetModel */
    @Override
    protected void applyConfiguration(@NonNull InMemoryTableModel targetModel, int columnNumber, @Nullable Object @NonNull [] configRow) {
        FormatterApi<?> formatter = FormatterFactory.createFormatter(getFormatterDefinition(configRow));
        if (formatter != null) {
            if (result == null) result = new FormatterDefinition[targetModel.getColumnCount()];
            result[columnNumber] = formatter;
            TableProperties.setColumnFormatter(targetModel, columnNumber, formatter);
        }
    }

    private static FormatterDefinition getFormatterDefinition(Object[] configRow) {
        String subType = (String) configRow[ConfigurationModel.DomainColumnModel.col_subType];
        String formatPattern = (String) configRow[ConfigurationModel.DomainColumnModel.col_parameter1];
        Boolean nullable = TableColumnType.Boolean.parseImpl(configRow[ConfigurationModel.DomainColumnModel.col_parameter2]);
        Integer maxStringLength = TableColumnType.Integer.parseImpl(configRow[ConfigurationModel.DomainColumnModel.col_parameter3]);
        return new FormatterDefinitionDto(subType, formatPattern, nullable, maxStringLength);
    }

    private void addConfig(int columnNumber, FormatterDefinition formatter) {
        if (formatter != null) {
            String description = formatter.toString();
            String subTyp = formatter.getElementClassName();
            Object formatPattern = formatter.getFormatPattern();
            Object nullAble = formatter.isNullable() ? null : "0";
            Integer maxStringLen = formatter.getMaxStringLength();
            addConfig(columnNumber, subTyp, description, formatPattern, nullAble, maxStringLen);
        }
    }
}
