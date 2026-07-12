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

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k3b.csvviewer.lib.Global;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;
import de.k3b.csvviewer.lib.data.formatter.LongFormatter;

/** can string be converted to {@link Long} ? */
public class LongIntegerAnalyser extends AnalyserBase<Long, String> implements AnalyserApi<String>, FormatterFactoryApi<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Global.TAG_CONFIG);

    private boolean isDouble = false;

    public LongIntegerAnalyser(int maxErrors) {
        super(maxErrors);
    }

    public boolean analyse(Long rowId, String stringValue) {
        super.analyse(rowId, stringValue);
        boolean result = false;
        if (isEnabled()) {
            try {
                double d = Double.parseDouble(stringValue);
                long longValue = (long) d;
                if (d != longValue) isDouble = true;
                if (max == null || longValue > max) max = longValue;
                if (min == null || longValue < min) min = longValue;
                result = true;
            } catch (NumberFormatException ex) {
                LOGGER.info("LongIntegerAnalyser.analyse(rowId={},stringValue={}) exception: {}", rowId, stringValue, ex.getMessage());
                addError(rowId, stringValue);
            }
        }
        return result;
    }

    public boolean isDouble() {
        return isDouble;
    }

    @Override
    public @Nullable LongFormatter createFormatter() {
        if (!isEnabled()) return null;
        return new LongFormatter(true);
    }
}
