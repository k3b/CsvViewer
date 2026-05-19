package de.k3b.csvviewer.lib.data.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvFileColumnModel implements ColumnModel {
    public static class Builder {
        @NonNull private final List<String> csvColumnNames = new ArrayList<>();
        @NonNull private final ColumnModel domainColumnModel;

        public Builder(@NonNull ColumnModel domainColumnModel) {
            this.domainColumnModel = domainColumnModel;
        }
        /** not ThreadSave */
        public Builder addColumnName(@NonNull String csvColumnName) {
            csvColumnNames.add(csvColumnName);
            return this;
        }

        public CsvFileColumnModel build() {
            String[] csvColumnNames = this.csvColumnNames.toArray(new String[0]);
            return new CsvFileColumnModel(domainColumnModel, csvColumnNames);
        }

    }
    @NonNull private final ColumnModel domainColumnModel;
    @NonNull private final int[] csv2Domain;
    @NonNull private final String[] csvColumnNames;

    // private int[] domain2Csv = null;

    /** must be created with {@link CsvFileColumnModel.Builder} */
    private CsvFileColumnModel(@NonNull ColumnModel domainColumnModel, @NonNull String[] csvColumnNames) {
        this.domainColumnModel = domainColumnModel;
        this.csvColumnNames = csvColumnNames;
        this.csv2Domain = createSrc2Dest(csvColumnNames, domainColumnModel.getColumnNames());
        // this.domain2Csv = createSrc2Dest(domainColumnModel.getColumnNames(), csvColumnNames);
    }

    @Override
    public @NonNull String[] getColumnNames() {
        return csvColumnNames;
    }

    @Override
    public @Nullable Object getDomainColumnValue(@Nullable Object[] row, int csvColumnNumber) {
        int domainColumnNumber = -1;
        if (csvColumnNumber >= 0 && csvColumnNumber < csv2Domain.length) domainColumnNumber = csv2Domain[csvColumnNumber];
        return domainColumnModel.getDomainColumnValue(row, domainColumnNumber);
    }

    private static int[] createSrc2Dest(@NonNull String[] sourceColumnNames, @NonNull String[] destinationColumnNames) {
        int[] src2Dest = new int[sourceColumnNames.length];
        Arrays.fill(src2Dest, -1);
        for (int destinationColumnNumber = destinationColumnNames.length - 1; destinationColumnNumber >= 0; destinationColumnNumber ++) {
            int sourceColumnNumber = getColumnNumber(sourceColumnNames, destinationColumnNames[destinationColumnNumber]);
            if (sourceColumnNumber >= 0) {
                src2Dest[sourceColumnNumber] = destinationColumnNumber;
            }
        }
        return src2Dest;
    }

    private static int getColumnNumber(@NonNull String[] columnNames, @NonNull String columnName) {
        for (int columnNumber = columnNames.length - 1; columnNumber >= 0; columnNumber ++) {
            if (columnNames[columnNumber].compareToIgnoreCase(columnName) == 0) return columnNumber;
        }
        return -1;
    }

}
