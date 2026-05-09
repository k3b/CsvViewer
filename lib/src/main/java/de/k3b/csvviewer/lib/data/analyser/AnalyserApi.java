package de.k3b.csvviewer.lib.data.analyser;

import org.jspecify.annotations.Nullable;

/**
 * feed a lot of example data from one csv-column through this api via {@link #analyse(Long, Object)}
 * to infer table column datatype , nullable, columnWidth, ...
  */
public interface AnalyserApi<T>  {
    /** analyse content of columnValue */
    boolean analyse(Long rowId, @Nullable T columnValue);

    /** transfer the analyse-result of this to the {@link AnalyserReport} report */
    void addInfoRowsToReport(AnalyserReport report);
}
