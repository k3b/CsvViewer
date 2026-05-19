package de.k3b.csvviewer.lib.csv;

import com.opencsv.CSVWriter;

import org.jspecify.annotations.NonNull;

import java.io.Writer;

import de.k3b.csvviewer.lib.data.model.TableModelApi;

public class TableModel2Csv {
    @NonNull
    private final CsvConfig config;

    CSVWriter csvWriter;


    /**
     * Export {@link TableModelApi} as csv to the resultWriter
     * @param resultWriter must be closed by the caller
     * @param model data to be exported
     */
    public static void write(@NonNull Writer resultWriter, @NonNull CsvConfig config, @NonNull TableModelApi model)
            throws Exception {
        TableModel2Csv csvExporter = new TableModel2Csv(resultWriter, config);
        csvExporter.writeCsvLine(model.getColumnNames());

        int rowCount = model.getRowCount();

        for (int row = 0; row < rowCount; row++) {
            csvExporter.writeCsvLine(toString(model.getRow(row)));
        }
        csvExporter.close();
    }

    public TableModel2Csv(Writer resultWriter, CsvConfig config) {
        this.config = config;
        /* requires com.opencsv:opencsv:5.7.1 that is not compatible with old java binary format JavaVersion.VERSION_1_8
        csvWriter = new CSVWriterBuilder(resultWriter)
                .withSeparator(CsvDefinitions.CSV_FIELD_DELIMITER_CHAR)
                .build();

         */
        // compatible with com.opencsv:opencsv:3.10
        csvWriter = new CSVWriter(resultWriter, config.getFieldDelimiterChar());
    }

    private void writeCsvLine(String... columns) {
        csvWriter.writeNext(columns, false);
        csvWriter.flushQuietly();
    }

    /**
     * nullsafe: converts object to string
     */
    private static String toString(Object o) {
        return o != null ? o.toString() : null;
    }

    /**
     * nullsafe: converts object to string
     */
    private static String[] toString(Object[] o) {
        String[] result = new String[o.length];
        for (int col = o.length - 1; col >= 0; col--) {
            result[col] = toString(o[col]);
        }
        return result;
    }

    // @Override implements AutoCloseable in android-4.4 and later
    public void close() throws Exception {
        csvWriter.close();
    }
}
