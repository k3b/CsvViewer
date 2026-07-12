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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.k3b.csvviewer.lib.data.formatter.DateFormatter;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;

/** can string be converted to {@link Date} ? */
public class DateAnalyser extends AnalyserBase<Date, String> implements AnalyserApi<String>, FormatterFactoryApi<Date> {

    public static final String ISO_DATE_PATTERN = "yyyy-MM-dd";
    public static final String ISO_TIME_PATTERN = "HH:mm:ss";
    public static final String ISO_DATE_TIME_PATTERN = ISO_DATE_PATTERN + " " + ISO_TIME_PATTERN;
    public static final String FMT_INTERNAL_DATE = "Date";
    public static final String FMT_INTERNAL_TIME = "Time";
    public static final String FMT_INTERNAL_DATE_TIME = "DateTime";

    static private class FormatterInfo extends ErrorInfo {
        final String formatPattern;
        final DateFormat formatter;

        boolean enabled = true;
        Date min;
        Date max;
        @SuppressWarnings("SimpleDateFormat")
        FormatterInfo(String formatPattern) {
            this(formatPattern, new SimpleDateFormat(formatPattern));
        }
        FormatterInfo(String formatPattern,DateFormat formatter) {
            this.formatPattern = formatPattern;
            this.formatter = formatter;
        }

        public void addInfo(AnalyserReport report) {
            Object[] analyzeRow = report.addReportRow(DateAnalyser.class.getSimpleName(), formatPattern);
            analyzeRow[AnalyserReport.DomainColumnModel.col_enabled] = enabled;
            analyzeRow[AnalyserReport.DomainColumnModel.col_min] = min;
            analyzeRow[AnalyserReport.DomainColumnModel.col_max] = max;
            analyzeRow[AnalyserReport.DomainColumnModel.col_errorRowIds] = appendErrorInfo(new StringBuilder()).toString();
        }
    }

    private final FormatterInfo[] formatterInfos = {
            new FormatterInfo(ISO_DATE_PATTERN),
            new FormatterInfo(ISO_TIME_PATTERN),
            new FormatterInfo(ISO_DATE_TIME_PATTERN),
            new FormatterInfo(FMT_INTERNAL_DATE,SimpleDateFormat.getDateInstance()),
            new FormatterInfo(FMT_INTERNAL_TIME,SimpleDateFormat.getTimeInstance()),
            new FormatterInfo(FMT_INTERNAL_DATE_TIME,SimpleDateFormat.getDateTimeInstance()),
    };

    public DateAnalyser(int maxErrors) {
        super(maxErrors);
    }
    public boolean analyse(Long rowId, String stringValue) {
        super.analyse(rowId, stringValue);
        boolean anySuccess = false;
        if (isEnabled()) {
            for (FormatterInfo dateParser : formatterInfos) {
                if (dateParser.enabled) {
                    try {
                        Date date = dateParser.formatter.parse(stringValue);
                        if (date != null) {
                            if (dateParser.max == null || date.getTime() > dateParser.max.getTime())
                                dateParser.max = date;
                            if (dateParser.min == null || date.getTime() < dateParser.min.getTime())
                                dateParser.min = date;
                        }
                        anySuccess = true;
                    } catch (ParseException e) {
                        dateParser.addError(rowId, stringValue);
                        if (dateParser.errorCount() > getMaxErrors()) dateParser.enabled = false;
                    }
                }
            }
            if (!anySuccess) addError(rowId, stringValue);
        }
        return anySuccess;
    }

    @Override
    public @Nullable DateFormatter createFormatter() {

        // result get Formatter with least errors
        DateFormatter result = null;
        if (isEnabled()) {
            FormatterInfo resultParser = null;

            // use parser with the least number of errors.
            for (FormatterInfo dateParser : formatterInfos) {
                if (dateParser.enabled &&
                        (resultParser == null || dateParser.errorCount() < resultParser.errorCount())) {
                    resultParser = dateParser;
                }
            }
            if (resultParser != null) {
                result = new DateFormatter(resultParser.formatPattern, resultParser.formatter, true);
            }
        }
        return result;
    }

    @Override
    public void addInfoRowsToReport(AnalyserReport report) {
        for (FormatterInfo dateParser : formatterInfos) {
            dateParser.addInfo(report);
        }
        super.addInfoRowsToReport(report);
    }

}
