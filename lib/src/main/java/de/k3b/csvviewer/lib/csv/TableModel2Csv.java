package de.k3b.csvviewer.lib.csv;

import com.opencsv.CSVWriter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Writer;

import de.k3b.csvviewer.lib.data.formatter.FormatterApi;
import de.k3b.csvviewer.lib.data.model.TableModelApi;
import de.k3b.csvviewer.lib.data.model.TableModelUtils;

public class TableModel2Csv {
    @NonNull
    private final CsvConfig config;
    private final @Nullable FormatterApi<?> @Nullable [] columnFormatters;

    CSVWriter csvWriter;


    /**
     * Export {@link TableModelApi} as csv to the resultWriter
     * @param resultWriter must be closed by the caller
     * @param model data to be exported
     */
    public static void write(@NonNull Writer resultWriter, @NonNull CsvConfig config, @NonNull TableModelApi model)
            throws Exception {
        TableModel2Csv csvExporter = new TableModel2Csv(resultWriter, config, TableModelUtils.getColumnFormatters(model));

        csvExporter.writeCsvLine(model.getColumnNames());

        int rowCount = model.getRowCount();

        for (int row = 0; row < rowCount; row++) {
            csvExporter.writeCsvLine(csvExporter.toString(model.getRow(row)));
        }
        csvExporter.close();
    }

    public TableModel2Csv(Writer resultWriter, CsvConfig config, @Nullable FormatterApi<?> @Nullable [] columnFormatters) {
        this.config = config;
        this.columnFormatters = columnFormatters;
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
    private String[] toString(Object[] o) {
        String[] result = new String[o.length];
        for (int col = o.length - 1; col >= 0; col--) {
            result[col] = getStringValue(o[col], col);
        }
        return result;
    }

    /**
     * nullsafe: converts object to string using formatter if available
     */
    private @Nullable String getStringValue(@Nullable Object object, int columnNumber) {
        FormatterApi<?> formatter = columnFormatters == null ? null : columnFormatters[columnNumber];
        String value;
        if (object == null) {
            value = "";
        } else if (formatter != null) {
            value = formatter.formatObject(object);
        } else {
            value = object.toString();
        }
        return value;
    }

    // @Override implements AutoCloseable in android-4.4 and later
    public void close() throws Exception {
        csvWriter.close();
    }
}
