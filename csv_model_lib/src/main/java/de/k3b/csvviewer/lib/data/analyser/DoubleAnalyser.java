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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import de.k3b.csvviewer.lib.data.formatter.DoubleFormatter;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;

/** can string be converted to {@link Double} ? */
public class DoubleAnalyser extends AnalyserBase<Double, String> implements AnalyserApi<String>, FormatterFactoryApi<Double> {

    static private class FormatterInfo extends ErrorInfo {
        final String formatPattern;
        final NumberFormat formatter;

        boolean enabled = true;
        private boolean isDouble = false;

        // @SuppressWarnings("SimpleDoubleFormat")
        FormatterInfo(String formatPattern) {
            this(formatPattern, new DecimalFormat(formatPattern));
        }
        FormatterInfo(String formatPattern,DecimalFormat formatter) {
            this.formatPattern = formatPattern;
            this.formatter = formatter;
        }

        public void addInfo(AnalyserReport report) {
            Object[] analyzeRow = report.addReportRow(DoubleAnalyser.class.getSimpleName(), formatPattern);
            analyzeRow[AnalyserReport.DomainColumnModel.col_enabled] = enabled;
            analyzeRow[AnalyserReport.DomainColumnModel.col_errorRowIds] = appendErrorInfo(new StringBuilder()).toString();
        }
    }

    private final FormatterInfo[] formatterInfos = {
            /*
            new FormatterInfo(ISO_DATE_PATTERN),
            new FormatterInfo(ISO_TIME_PATTERN),
            new FormatterInfo(ISO_DATE_TIME_PATTERN),
            new FormatterInfo(FMT_INTERNAL_DATE,SimpleDoubleFormat.getDoubleInstance()),
            new FormatterInfo(FMT_INTERNAL_TIME,SimpleDoubleFormat.getTimeInstance()),
            new FormatterInfo(FMT_INTERNAL_DATE_TIME,SimpleDoubleFormat.getDoubleTimeInstance()),

             */
    };

    public DoubleAnalyser(int maxErrors) {
        super(maxErrors);
    }
    public boolean analyse(Long rowId, String stringValue) {
        super.analyse(rowId, stringValue);
        boolean anySuccess = false;
        if (isEnabled()) {
            for (FormatterInfo doubleParser : formatterInfos) {
                if (doubleParser.enabled) {
                        /*
                    try {
                        Double double = doubleParser.formatter.parse(stringValue);
                        if (double != null && ((long) double) != double) {
                            this.isDouble = true;
                        }
                        anySuccess = true;
                    } catch (ParseException e) {
                        doubleParser.addError(rowId, stringValue);
                        if (doubleParser.errorCount() > getMaxErrors()) doubleParser.enabled = false;
                    }
                         */
                }
            }
            if (!anySuccess) addError(rowId, stringValue);
        }
        return anySuccess;
    }

    @Override
    public @Nullable DoubleFormatter createFormatter() {

        // result get Formatter with least errors
        DoubleFormatter result = null;
        if (isEnabled()) {
            FormatterInfo resultParser = null;

            // use parser with the least number of errors.
            for (FormatterInfo doubleParser : formatterInfos) {
                if (doubleParser.enabled &&
                        (resultParser == null || doubleParser.errorCount() < resultParser.errorCount())) {
                    resultParser = doubleParser;
                }
            }
            if (resultParser != null) {
                result = new DoubleFormatter(resultParser.formatPattern, resultParser.formatter, true);
            }
        }
        return result;
    }

    @Override
    public void addInfoRowsToReport(AnalyserReport report) {
        for (FormatterInfo doubleParser : formatterInfos) {
            doubleParser.addInfo(report);
        }
        super.addInfoRowsToReport(report);
    }

}
