package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.Nullable;

import de.k3b.csvviewer.lib.data.analyser.AnalyserReport.ColumnDefinition;
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
            reportRow[ColumnDefinition.col_enabled] = enabled;
            reportRow[ColumnDefinition.col_min] = min;
            reportRow[ColumnDefinition.col_max] = max;
            reportRow[ColumnDefinition.col_success] = successCount;
            reportRow[ColumnDefinition.col_errorRowIds] = appendErrorInfo(new StringBuilder()).toString();
            reportRow[ColumnDefinition.col_result] = getResultColumnValueForReport();
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
