package de.k3b.csvviewer.lib.csv;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.io.Reader;

import de.k3b.csvviewer.lib.data.TableModelApi;

public class Csv2Html {
    @NonNull
    public static String toHtmlTable(String csvString, int options) throws IOException {
        try(Csv2TableModel parser = new Csv2TableModel(options)) {
            TableModelApi model = parser.toTableModel(csvString);
            return TableModel2Html.toHtmlTable(model, null, null, 0);
        }
    }

    @NonNull
    public static String toHtmlTable(Reader csvReader, int options) throws IOException {
        try(Csv2TableModel parser = new Csv2TableModel(options)) {
            TableModelApi model = parser.toTableModel(csvReader);
            return TableModel2Html.toHtmlTable(model, null, null, 0);
        }
    }

}
