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

import de.k3b.csvviewer.lib.data.analyser.AnalyserReport.DomainColumnModel;
import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;

/** implements global Error handling, basic properties and report in {@link AnalyserReport}. */
public class AnalyserBase<T,API> extends ErrorInfo implements AnalyserApi<API> {

    /** if there are more than maxErrors then {@link #isEnabled()} will 
     be set to false so that analysing stops. */
    private final int maxErrors;
    
    /** count the  successfully calls to {@link #analyse(Long, Object)} with data != null. {@link ErrorInfo#addError(Long, String)} will decrease it. */
    private int successCount =0;
    
    /** true means {@link #analyse(Long, Object)} is active.
        if there are more than maxErrors then {@link #isEnabled()} will 
     be set to false so that analysing stops. */    
    protected boolean enabled = true;
    
    /** the smallest or minimal value found */
    protected T min = null;
    /** the biggest or max value found */
    protected T max = null;

    /** if there are more than maxErrors then {@link #isEnabled()} will be set to false so that analysing stops. */
    public AnalyserBase(int maxErrors) {
        this.maxErrors = maxErrors;
    }

    /** true means {@link #analyse(Long, Object)} is active.
     if there are more than maxErrors then {@link #enabled} will
     be set to false so that analysing stops. */
    public boolean isEnabled() {
        return enabled;
    }

    /** if there are more than maxErrors then {@link #isEnabled()} will 
     be set to false so that analysing stops. */
    public int getMaxErrors() {
        return maxErrors;
    }

    /** the smallest or minimal value found */
    public T getMin() {
        return min;
    }

    /** the biggest or max value found */
    public T getMax() {
        return max;
    }

    /** remember a parsing error for the {@link AnalyserReport} report.
     If there are more than maxErrors then {@link #isEnabled()} will
     be set to false so that analysing stops. */
    public void addError(Long rowId, String stringValue) {
        successCount--;
        super.addError(rowId, stringValue);
        if (errorCount() >= maxErrors) enabled = false;
    }

    /** successCount++ if objectValue != null */
    @Override
    public boolean analyse(Long rowId, @Nullable API objectValue) {
        if (objectValue != null) {
            successCount++;
        }
        return true;
    }

    /**
     * add one report row for this Analyser to {@link AnalyserReport} report
     */
    protected void addInfoRowToReport(AnalyserReport report, String subParser) {
        Object[] reportRow = report.addReportRow(this.getClass().getSimpleName(), subParser);
        if (subParser == null) {
            reportRow[AnalyserReport.DomainColumnModel.col_enabled] = enabled;
            reportRow[DomainColumnModel.col_min] = min;
            reportRow[AnalyserReport.DomainColumnModel.col_max] = max;
            reportRow[AnalyserReport.DomainColumnModel.col_success] = successCount;
            reportRow[AnalyserReport.DomainColumnModel.col_errorRowIds] = appendErrorInfo(new StringBuilder()).toString();
            reportRow[AnalyserReport.DomainColumnModel.col_result] = getResultColumnValueForReport();
        }
    }

    /** get data for the Result column in of {@link AnalyserReport} report. */
    public String getResultColumnValueForReport() {
        String result = null;
        if (this instanceof FormatterFactoryApi) {
            FormatterApi formatter = ((FormatterFactoryApi) this).createFormatter();
            if (formatter != null) result = formatter.toString();
        }
        return result;
    }

    /** add one or more info rows for this Analyser to {@link AnalyserReport} report. */
    @Override
    public void addInfoRowsToReport(AnalyserReport report) {
        addInfoRowToReport(report, null);
    }
}
