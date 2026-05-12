package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/** a (base-)class to collect error infos */
public class ErrorInfo {
    /** Each call to {@link #addError(Long, String)} will be remembered here. */

    @NonNull private final List<Long> errorRowIds = new ArrayList<>();
    @NonNull private final List<String> errorRowValues = new ArrayList<>();

    /** remember a parsing error for the {@link AnalyserReport} report. */
    public void addError(Long rowId, String stringValue) {
        errorRowIds.add(rowId);
        errorRowValues.add(stringValue);
    }

    public StringBuilder appendErrorInfo(StringBuilder result) {
        if (!errorRowIds.isEmpty()) {
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

    public int errorCount() {
        return errorRowIds.size();
    }

}
