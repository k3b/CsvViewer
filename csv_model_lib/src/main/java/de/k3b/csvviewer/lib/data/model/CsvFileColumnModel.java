/*
 * Copyright (c) 2026 by k3b.
 *
 * This file is part of https://github.com/k3b/CsvViewer.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 */

package de.k3b.csvviewer.lib.data.model;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvFileColumnModel implements Serializable,ColumnModel {
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
