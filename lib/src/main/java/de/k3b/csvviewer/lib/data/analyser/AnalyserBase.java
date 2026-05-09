package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.formatter.FormatterFactoryApi;

/** implements global Errorhandling, basic properties and report in {@link AnalyserReport}. */
public class AnalyserBase<T,API> implements AnalyserApi<API> {

    /** if there are more than maxErrors then {@link #isEnabled()} will 
     be set to false so that analysing stops. */
    private final int maxErrors;
    
    /** Each call to {@link #addError(Long)} will be remembered here. */
    private final List<Long> errorRowIds = new ArrayList<>();

    /** count the  successfull calls to {@link #analyse(Long, Object)} with data != null. {@link #addError(Long)} will decrease it. */
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
     if there are more than maxErrors then {@link #isEnabled()} will 
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
    public void addError(Long rowId) {
        successCount--;
        errorRowIds.add(rowId);
        if (errorRowIds.size() >= maxErrors) enabled = false;
    }

    /** successCount++ if objectValue != null */
    @Override
    public boolean analyse(Long rowId, @Nullable API objectValue) {
        if (objectValue != null) {
            successCount++;
        }
        return true;
    }

    /** add one report row for this Analyser to {@link AnalyserReport} report */
    protected Object[] addInfoRowToReport(AnalyserReport report, String subParser) {
        Object[] reportRow = report.addReportRow(this.getClass().getSimpleName(), subParser);
        if (subParser != null) {
            reportRow[AnalyserReport.col_enabled] = enabled;
            reportRow[AnalyserReport.col_min] = min;
            reportRow[AnalyserReport.col_max] = max;
            reportRow[AnalyserReport.col_success] = successCount;
            addErrorRowIdsToReport(reportRow, errorRowIds);
            reportRow[AnalyserReport.col_result] = getResultColumnValueForReport();
        }
        return reportRow;
    }

    /** get data for the Result column in of {@link AnalyserReport} report. */
    public String getResultColumnValueForReport() {
        String result = null;
        if (this instanceof FormatterFactoryApi) {
            @Nullable FormatterApi formatter = ((FormatterFactoryApi) this).createFormatter();
            if (formatter != null) result = formatter.toString();
        }
        return result;
    }

    /** add one or more info rows for this Analyser to {@link AnalyserReport} report. */
    @Override
    public void addInfoRowsToReport(AnalyserReport report) {
        addInfoRowToReport(report, null);
    }

    /** get data for the ErrorRowIds column in of {@link AnalyserReport} */
    protected static void addErrorRowIdsToReport(Object[] reportRow, List<Long> errorRowIds) {
        String result = null;
        if (errorRowIds != null && !errorRowIds.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for(Long errorRowId : errorRowIds) {
                stringBuilder.append(errorRowId).append(",");
            }
            result = stringBuilder.toString();
        }
        reportRow[AnalyserReport.col_errorRowIds] = result;
    }

}
