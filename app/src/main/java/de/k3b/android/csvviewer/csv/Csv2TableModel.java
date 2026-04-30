package de.k3b.android.csvviewer.csv;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.ICSVParser;

import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import de.k3b.android.csvviewer.data.InMemoryTableModel;
import de.k3b.android.csvviewer.data.TableModelApi;

public class Csv2TableModel implements AutoCloseable {
    /** csv-header must be inside 0 .. BUFFER_SIZE to be detected.  */
    public static final int BUFFER_SIZE = 8096;

    /** parameter for constructor: if set make shure that there is a 0th column ID */
    public static final int OPTION_WITH_ID_COLUMN = 0x1;
    /** parameter for constructor: if set all options */
    public static final int OPTION_ALL = 0xffff;

    /** {@link #withIdColumn} and {@link #OPTION_WITH_ID_COLUMN}: if set every table model must have a numeric id column at position #0. If it does not exist it will be generated from csv line number */
    private static final String COL_ID = "ID";

    /** withIdColumn and {@link #OPTION_WITH_ID_COLUMN}: if set every table model must have a numeric id column at position #0. If it does not exist it will be generated from csv line number */
    private final boolean withIdColumn;

    private CSVReader m_csvReader;
    private int m_lineNumber = 0;

    public  Csv2TableModel(int options) {
        this.withIdColumn = (0 != (options & OPTION_WITH_ID_COLUMN));
    }

    public TableModelApi toTableModel(@NonNull String csvMarkup) {
        // parser cannot handle empty lines if they are not "\r\n"
        return toTableModel(new StringReader(csvMarkup.replace("\n", "\r\n")));
    }

    public TableModelApi toTableModel(@NonNull Reader csvMarkup) {
        InMemoryTableModel tableModel = null;
        try (BufferedReader bufferedReader = new BufferedReader(csvMarkup, BUFFER_SIZE)) {
            CsvConfig csvConfig = inferCsvConfiguration(bufferedReader);
            ICSVParser parser = new CSVParserBuilder()
                    .withSeparator(csvConfig.getFieldDelimiterChar())
                    .withQuoteChar(csvConfig.getQuoteChar())
                    .build();
            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(bufferedReader)
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .withKeepCarriageReturn(true);

            try (CSVReader csvReader = csvReaderBuilder.build()) {
                m_csvReader = csvReader;
                String[] headers = readNextCsvColumnLine();

                if (headers != null && headers.length > 0) {
                    int idColumn = 0;
                    if (withIdColumn) {
                        idColumn = find(headers, COL_ID);
                        if (idColumn < 0) {
                            // insert COL_ID at position 0
                            String[] newArray = new String[headers.length + 1];
                            System.arraycopy(headers, 0, newArray, 1, headers.length);
                            newArray[0] = COL_ID;
                            headers = newArray;
                        } else if (idColumn > 0) {
                            // swap position 0 and idColumn
                            headers[idColumn] = headers[0];
                            headers[0] = COL_ID;
                        }
                    }

                    tableModel = new InMemoryTableModel(headers);
                    String[] rawRow;
                    while (null != (rawRow = readNextCsvColumnLine())) {
                        Object[] row;
                        if (idColumn < 0) {
                            // insert COL_ID at position 0
                            row = new Object[headers.length + 1];
                            System.arraycopy(rawRow, 0, row, 1, rawRow.length);
                            row[0] = m_lineNumber;
                        } else {
                            row = new Object[headers.length];
                            System.arraycopy(rawRow, 0, row, 0, rawRow.length);
                            if (idColumn > 0) {
                                // swap position 0 and idColumn
                                Object temp = row[idColumn];
                                row[idColumn] = row[0];
                                row[0] = temp;
                            }
                        }

                        tableModel.addRow(tableModel.fixRow(row));
                    }
                } else {
                    throw new RuntimeException("CSV does not have header");
                }
            }

        } catch (Exception e) {
            // invalid csv format in editor should not crash the app when trying to display as html
            // openCsv-3.10 may throw IOException or RuntimeExcpetion,
            // openCsv-5.7 may throw IOException or CsvValidationException.
            e.printStackTrace();
        }
        m_csvReader = null;
        return tableModel;
    }

    /** @return 0 based position of colSearch inside columnNames or -1 if not found */
    private int find(String[] columnNames,@NonNull String colSearch) {
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i].toUpperCase();
            if (colSearch.compareToIgnoreCase(columnName) == 0) return i;
        }
        return -1;
    }

    private CsvConfig inferCsvConfiguration(BufferedReader bufferedReader) throws IOException {
        // remember where we started.
        bufferedReader.mark(BUFFER_SIZE);
        try {
            String line;
            while (null != (line = bufferedReader.readLine())) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    return CsvConfig.infer(line);
                }
            }
            return CsvConfig.DEFAULT;
        } finally {
            // go back to start of csv
            bufferedReader.reset();
        }
    }

    private String[] readNextCsvColumnLine() throws IOException {
        // openCsv-3.10 may throw IOException or RuntimeExcpetion,
        // openCsv-5.7 may throw IOException or CsvValidationException.
        String[] columns;
        do {
            m_lineNumber++;
            columns = m_csvReader.readNext();
        } while (columns != null && isComment(columns));
        if (columns != null && columns.length > 0 && columns[columns.length -1] != null) {
            // remove trailing "\r" in last column
            columns[columns.length -1] = columns[columns.length -1].trim();
        }
        return columns;
    }

    private boolean isComment(@NonNull String[] columns) {
        if (columns.length == 0) {
            return true;
        }

        // empty line without content
        if (columns.length == 1 && columns[0].trim().isEmpty()) {
            return true;
        }

        // comments start with "#" char
        return columns[0].startsWith("#");
    }

    @Override
    public void close() throws IOException {
        if (m_csvReader != null) m_csvReader.close();
    }


}
