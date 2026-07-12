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

import java.util.List;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.filter.ITableModelRowFilter;
import de.k3b.csvviewer.lib.data.filter.TableModelRowFilterBase;
import de.k3b.csvviewer.lib.data.model.InMemoryTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** {@link ConfigurationModel} interpreter for filters based on {@link TableModelRowFilterBase}  */
public class FilterConfigurationProcessor extends ConfigurationProcessorBase<@Nullable List<ITableModelRowFilter>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_LIB);
    public FilterConfigurationProcessor(@NonNull ConfigurationModel target, @NonNull String configurationType) {
        super(target, configurationType);
    }

    /**
     * convert from T configValue in {@link #configurationModel} to Object[] configRow-s for {@link ConfigurationModel}
     *
     * @param configValue
     */
    @Override
    public void addConfig(@Nullable List<ITableModelRowFilter> configValue) {

    }

    /**
     * apply configuration of one column {columnNumber} from configRow to targetModel
     *
     * @param targetModel
     * @param columnNumber
     * @param configRow
     */
    @Override
    protected void applyConfiguration(@NonNull InMemoryTableModel targetModel, int columnNumber, @Nullable Object @NonNull [] configRow) {

    }
}
