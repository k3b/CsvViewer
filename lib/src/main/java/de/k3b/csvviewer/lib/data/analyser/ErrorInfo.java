package de.k3b.csvviewer.lib.data.analyser;

import java.util.ArrayList;
import java.util.List;

public class ErrorInfo {
    /** Each call to {@link #addError(Long, String)} will be remembered here. */

    final List<Long> errorRowIds = new ArrayList<>();
    final List<String> errorRowValues = new ArrayList<>();

    /** remember a parsing error for the {@link AnalyserReport} report. */
    public void addError(Long rowId, String stringValue) {
        errorRowIds.add(rowId);
        errorRowValues.add(stringValue);
    }

    public StringBuilder appendErrorInfo(StringBuilder result) {
        if (errorRowIds != null && !errorRowIds.isEmpty()) {
            for (int i = 0; i < errorRowIds.size(); i++) {
                result
                        .append(errorRowIds.get(i))
                        .append(":")
                        .append(errorRowValues.get(i))
                        .append(",")
                ;
            }
        }
        return result;
    }



}
